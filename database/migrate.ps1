# MiniForce Database Migration Runner
# Deterministic, safe, idempotent database initialization and migration engine.
[CmdletBinding()]
param(
    [string]$HostName = $(if ($env:MINIFORCE_DB_HOST) { $env:MINIFORCE_DB_HOST } else { "127.0.0.1" }),
    [string]$Port = $(if ($env:MINIFORCE_DB_PORT) { $env:MINIFORCE_DB_PORT } else { "3306" }),
    [string]$User = $(if ($env:MINIFORCE_DB_USER) { $env:MINIFORCE_DB_USER } else { "root" }),
    [string]$Password = $env:MINIFORCE_DB_PASSWORD,
    [string]$Database = $(if ($env:MINIFORCE_DB_NAME) { $env:MINIFORCE_DB_NAME } else { "miniforce" })
)

$ErrorActionPreference = "Stop"

# Locate mysql.exe
$mysqlExe = (Get-Command mysql.exe -ErrorAction SilentlyContinue).Source
if (-not $mysqlExe) {
    $searchPaths = @(
        "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe"
    )
    foreach ($p in $searchPaths) {
        if (Test-Path $p) { $mysqlExe = $p; break }
    }
    if (-not $mysqlExe) {
        $found = Get-ChildItem -Path "C:\Program Files\MySQL", "C:\Program Files (x86)\MySQL" -Recurse -Filter "mysql.exe" -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found) { $mysqlExe = $found.FullName }
    }
}

if (-not $mysqlExe) {
    Write-Error "mysql.exe could not be found. Please ensure MySQL Server 8.0+ is installed and on your PATH."
    exit 1
}

Write-Host "Using MySQL client: $mysqlExe" -ForegroundColor Cyan

function Invoke-MySql([string]$query, [string]$dbName = "") {
    $env:MYSQL_PWD = $Password
    $argsList = @(
        "-h", $HostName,
        "-P", $Port,
        "-u", $User,
        "--default-character-set=utf8mb4"
    )
    if ($dbName) {
        $argsList += $dbName
    }
    $argsList += @("-e", $query)

    $output = & $mysqlExe @argsList 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "MySQL execution error: $output"
    }
    return $output
}

function Invoke-MySqlFile([string]$filePath, [string]$dbName = "") {
    $env:MYSQL_PWD = $Password
    $argsList = @(
        "-h", $HostName,
        "-P", $Port,
        "-u", $User,
        "--default-character-set=utf8mb4"
    )
    if ($dbName) {
        $argsList += $dbName
    }

    $processInfo = New-Object System.Diagnostics.ProcessStartInfo
    $processInfo.FileName = $mysqlExe
    $processInfo.Arguments = ($argsList -join " ")
    $processInfo.UseShellExecute = $false
    $processInfo.RedirectStandardInput = $true
    $processInfo.RedirectStandardOutput = $true
    $processInfo.RedirectStandardError = $true
    $processInfo.CreateNoWindow = $true

    $p = [System.Diagnostics.Process]::Start($processInfo)
    $content = Get-Content -Raw -Encoding utf8 $filePath
    if ($dbName -and $dbName -ne "miniforce") {
        $content = $content -replace "CREATE DATABASE IF NOT EXISTS miniforce", "CREATE DATABASE IF NOT EXISTS $dbName"
        $content = $content -replace "USE miniforce;", "USE $dbName;"
        $content = $content -replace "FROM miniforce\.", "FROM $dbName."
    }
    $p.StandardInput.Write($content)
    $p.StandardInput.Close()

    $stdout = $p.StandardOutput.ReadToEnd()
    $stderr = $p.StandardError.ReadToEnd()
    $p.WaitForExit()

    if ($p.ExitCode -ne 0) {
        throw "Failed to execute SQL file '$filePath': $stderr"
    }
    return $stdout
}

# 1. Verify connectivity
try {
    Invoke-MySql "SELECT 1;" | Out-Null
    Write-Host "MySQL connection verified on $HostName`:$Port as user '$User'." -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Could not connect to MySQL: $_" -ForegroundColor Red
    exit 1
}

# 2. Ensure database exists
Invoke-MySql "CREATE DATABASE IF NOT EXISTS $Database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" | Out-Null

# 3. Check if base tables exist
$tableCheck = Invoke-MySql "SHOW TABLES LIKE 'problems';" $Database
$hasProblemsTable = ($tableCheck -match "problems")

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent $scriptDir
$baseSqlPath = Join-Path $projectRoot "database\miniforce.sql"

if (-not $hasProblemsTable) {
    Write-Host "Empty database detected. Initializing base schema from database\miniforce.sql..." -ForegroundColor Yellow
    if (-not (Test-Path $baseSqlPath)) {
        Write-Error "Base SQL file not found at: $baseSqlPath"
        exit 1
    }
    Invoke-MySqlFile $baseSqlPath $Database | Out-Null
    Write-Host "Base schema and seed data loaded successfully." -ForegroundColor Green
}

# 4. Create schema_migrations tracking table
$createMigrationsTableSql = @"
CREATE TABLE IF NOT EXISTS schema_migrations (
    version VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
"@
Invoke-MySql $createMigrationsTableSql $Database | Out-Null

# 5. Baseline reconciliation for pre-existing databases (prevents duplicate column errors)
$hasClaimedBy = Invoke-MySql "SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '$Database' AND TABLE_NAME = 'submissions' AND COLUMN_NAME = 'claimed_by';"
if ($hasClaimedBy -match "1") {
    Invoke-MySql "INSERT IGNORE INTO schema_migrations (version, description) VALUES ('V7_0_2', 'add async judge fields');" $Database | Out-Null
}

$hasFailingTestCase = Invoke-MySql "SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '$Database' AND TABLE_NAME = 'submissions' AND COLUMN_NAME = 'failing_test_case_id';"
if ($hasFailingTestCase -match "1") {
    Invoke-MySql "INSERT IGNORE INTO schema_migrations (version, description) VALUES ('V7_0_3', 'add judge execution fields');" $Database | Out-Null
}

$hasAvatarKey = Invoke-MySql "SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '$Database' AND TABLE_NAME = 'users' AND COLUMN_NAME = 'avatar_key';"
if ($hasAvatarKey -match "1") {
    Invoke-MySql "INSERT IGNORE INTO schema_migrations (version, description) VALUES ('V7_0_4', 'add user avatar');" $Database | Out-Null
}

$hasIsPublic = Invoke-MySql "SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '$Database' AND TABLE_NAME = 'problems' AND COLUMN_NAME = 'is_public';"
if ($hasIsPublic -match "1") {
    Invoke-MySql "INSERT IGNORE INTO schema_migrations (version, description) VALUES ('V7_2_1', 'add is public and python code');" $Database | Out-Null
}

$tc16Count = Invoke-MySql "SELECT COUNT(*) FROM test_cases WHERE problem_id = 16;" $Database
if ($tc16Count -match "[1-9]") {
    Invoke-MySql "INSERT IGNORE INTO schema_migrations (version, description) VALUES ('V7_0_1', 'restore test cases lost to legacy id collision');" $Database | Out-Null
}

$hasPythonCodeCol = Invoke-MySql "SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '$Database' AND TABLE_NAME = 'problems' AND COLUMN_NAME = 'starter_code_python';"
if ($hasPythonCodeCol -match "1") {
    $hasP1Py = Invoke-MySql "SELECT COUNT(*) FROM problems WHERE id = 1 AND starter_code_python IS NOT NULL;" $Database
    if ($hasP1Py -match "1") {
        Invoke-MySql "INSERT IGNORE INTO schema_migrations (version, description) VALUES ('V7_2_3', 'populate python starter code');" $Database | Out-Null
        Invoke-MySql "INSERT IGNORE INTO schema_migrations (version, description) VALUES ('V7_2_4', 'populate all 50 python starter codes');" $Database | Out-Null
    }
}

$hasP4Java = Invoke-MySql "SELECT COUNT(*) FROM problems WHERE id = 4 AND starter_code LIKE '%int[][] merge%';" $Database
if ($hasP4Java -match "1") {
    Invoke-MySql "INSERT IGNORE INTO schema_migrations (version, description) VALUES ('V7_2_5', 'populate all 50 java starter codes');" $Database | Out-Null
}

# 6. Apply pending migrations in numerical/alphabetical order
$migrationsDir = Join-Path $projectRoot "database\migrations"
$migrationFiles = Get-ChildItem -Path $migrationsDir -Filter "V*.sql" | Sort-Object Name

$appliedCount = 0
$skippedCount = 0

foreach ($file in $migrationFiles) {
    $version = ($file.Name -split "__")[0]
    $description = ($file.Name -split "__")[1] -replace "\.sql$", "" -replace "_", " "

    $checkSql = "SELECT COUNT(*) FROM schema_migrations WHERE version = '$version';"
    $result = Invoke-MySql $checkSql $Database
    $isApplied = ($result -match "\b1\b")

    if ($isApplied) {
        $skippedCount++
    } else {
        Write-Host "Applying migration: $($file.Name)..." -ForegroundColor Yellow
        Invoke-MySqlFile $file.FullName $Database | Out-Null
        Invoke-MySql "INSERT INTO schema_migrations (version, description) VALUES ('$version', '$description');" $Database | Out-Null
        Write-Host "Applied migration: $($file.Name)" -ForegroundColor Green
        $appliedCount++
    }
}

Write-Host "Database migration check complete: $appliedCount applied, $skippedCount verified up to date." -ForegroundColor Green
exit 0
