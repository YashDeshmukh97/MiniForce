@echo off
setlocal
cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -Command "$script = (Get-Content '%~f0' | Select-Object -Skip 5) -join \"`n\"; Invoke-Expression $script"
exit /b %ERRORLEVEL%

Write-Host '========================================='
Write-Host 'MiniForce Master Launcher'
Write-Host '========================================='

# The batch wrapper has changed into the directory that contains this file.
# This cannot accidentally use the caller's working directory.
$currentPath = (Get-Location).ProviderPath.TrimEnd('\')
$backendPath = Join-Path $currentPath 'backend'
$browserUrl = 'http://127.0.0.1:5500/index.html'

function Fail([string]$message) {
    throw [System.InvalidOperationException]::new($message)
}

function Get-PortOwner([int]$port) {
    $listeners = @(Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue)
    $owners = @($listeners | Select-Object -ExpandProperty OwningProcess -Unique)
    if ($owners.Count -eq 0) { return $null }
    # A port with several owners is not safely attributable to one instance.
    if ($owners.Count -ne 1) { return -1 }
    return [int]$owners[0]
}

function Get-Ancestry([int]$processId) {
    $seen = @{}
    $id = $processId
    for ($depth = 0; $depth -lt 20; $depth++) {
        if ($id -le 0 -or $seen.ContainsKey($id)) { break }
        $seen[$id] = $true
        $p = Get-CimInstance Win32_Process -Filter "ProcessId = $id" -ErrorAction SilentlyContinue
        if (-not $p) { break }
        $p
        if (-not $p.ParentProcessId -or $p.ParentProcessId -eq $id) { break }
        $id = [int]$p.ParentProcessId
    }
}

# CIM exposes neither working directory nor environment for another process.
# This inspects only the target JVM's PEB fields needed to prove identity:
# current directory and MINIFORCE_JUDGE_REAL_ENABLED. It never reads, retains,
# or prints the DB password or JWT secret.
Add-Type -TypeDefinition @'
using System;
using System.ComponentModel;
using System.Runtime.InteropServices;
using System.Text;

public static class MFProcess
{
    [StructLayout(LayoutKind.Sequential)]
    private struct PBI {
        public IntPtr R1, Peb, R2a, R2b, Pid, ParentPid;
    }
    [DllImport("kernel32.dll", SetLastError=true)]
    private static extern IntPtr OpenProcess(uint access, bool inherit, int pid);
    [DllImport("kernel32.dll", SetLastError=true)]
    private static extern bool ReadProcessMemory(IntPtr h, IntPtr address, [Out] byte[] buffer, int size, out IntPtr read);
    [DllImport("kernel32.dll", SetLastError=true)]
    private static extern bool CloseHandle(IntPtr h);
    [DllImport("ntdll.dll")]
    private static extern int NtQueryInformationProcess(IntPtr h, int infoClass, ref PBI info, int length, out int returned);

    private static IntPtr Add(IntPtr p, int n) { return new IntPtr(p.ToInt64() + n); }
    private static byte[] Read(IntPtr h, IntPtr p, int size) {
        byte[] b = new byte[size]; IntPtr n;
        if (!ReadProcessMemory(h, p, b, size, out n) || n.ToInt64() != size)
            throw new Win32Exception(Marshal.GetLastWin32Error());
        return b;
    }
    private static IntPtr Ptr(IntPtr h, IntPtr p) {
        if (IntPtr.Size != 8) throw new NotSupportedException("Only 64-bit JVM inspection is supported.");
        return new IntPtr(BitConverter.ToInt64(Read(h, p, 8), 0));
    }
    private static IntPtr Params(IntPtr h) {
        PBI pbi = new PBI(); int returned;
        if (NtQueryInformationProcess(h, 0, ref pbi, Marshal.SizeOf(typeof(PBI)), out returned) != 0)
            throw new InvalidOperationException("Unable to inspect process parameters.");
        // PEB.ProcessParameters on x64.
        return Ptr(h, Add(pbi.Peb, 0x20));
    }
    public static string Cwd(int pid) {
        IntPtr h = OpenProcess(0x0410, false, pid);
        if (h == IntPtr.Zero) throw new Win32Exception(Marshal.GetLastWin32Error());
        try {
            IntPtr p = Params(h);
            // RTL_USER_PROCESS_PARAMETERS.CurrentDirectory.DosPath on x64.
            ushort length = BitConverter.ToUInt16(Read(h, Add(p, 0x38), 2), 0);
            IntPtr buffer = Ptr(h, Add(p, 0x40));
            return length == 0 || buffer == IntPtr.Zero ? String.Empty : Encoding.Unicode.GetString(Read(h, buffer, length));
        } finally { CloseHandle(h); }
    }
    public static string Env(int pid, string name) {
        IntPtr h = OpenProcess(0x0410, false, pid);
        if (h == IntPtr.Zero) throw new Win32Exception(Marshal.GetLastWin32Error());
        try {
            IntPtr p = Params(h);
            IntPtr env = Ptr(h, Add(p, 0x80)); // ProcessParameters.Environment on x64.
            if (env == IntPtr.Zero) return null;
            // The maximum Windows environment block is 32,767 characters.
            string block = Encoding.Unicode.GetString(Read(h, env, 65534));
            int start = 0;
            while (start < block.Length) {
                int end = block.IndexOf('\0', start);
                if (end <= start) break;
                string entry = block.Substring(start, end - start);
                int equals = entry.IndexOf('=');
                if (equals > 0 && String.Equals(entry.Substring(0, equals), name, StringComparison.OrdinalIgnoreCase))
                    return entry.Substring(equals + 1);
                start = end + 1;
            }
            return null;
        } finally { CloseHandle(h); }
    }
}
'@

function Test-Cwd([int]$processId, [string]$expected) {
    try { return ([MFProcess]::Cwd($processId).TrimEnd('\') -ieq $expected.TrimEnd('\')) }
    catch { return $false } # Unprovable is unsafe to reuse.
}

function Test-RealJudgeEnv([int]$processId) {
    try {
        # Only the non-secret boolean is compared; its value is never output.
        return ([MFProcess]::Env($processId, 'MINIFORCE_JUDGE_REAL_ENABLED') -ceq 'true')
    } catch { return $false }
}

function Test-LauncherAncestry(
    [int]$listenerPid,
    [ValidateSet('Backend','Frontend')][string]$role,
    [string]$expectedDirectory,
    [int]$expectedHostPid = 0
) {
    $chain = @(Get-Ancestry $listenerPid)
    if ($chain.Count -eq 0) { return $false }
    if ($expectedHostPid -gt 0 -and -not ($chain.ProcessId -contains $expectedHostPid)) { return $false }

    $escapedDirectory = [regex]::Escape($expectedDirectory)
    foreach ($p in $chain) {
        if ($p.Name -notmatch '(?i)^cmd\.exe$' -or [string]::IsNullOrWhiteSpace($p.CommandLine)) { continue }
        $line = $p.CommandLine
        $hasTitle = $line -match "(?i)\btitle\s+MiniForce\s+$role\b"
        $hasDirectory = $line -match ("(?i)\bcd\s+/d\s+[""]?" + $escapedDirectory + "[""]?")
        if ($role -eq 'Backend') {
            $hasCommand = $line -match '(?i)\bset\s+[""]?MINIFORCE_JUDGE_REAL_ENABLED=true[""]?\s*&&.*\bmvn(?:\.cmd)?\s+spring-boot:run\b'
        } else {
            $hasCommand = $line -match '(?i)\bjava(?:\.exe)?\s+-m\s+jdk\.httpserver\s+-p\s+5500\s+-b\s+127\.0\.0\.1\s+-d\s+\.(?:\s|$)'
        }
        if ($hasTitle -and $hasDirectory -and $hasCommand) { return $true }
    }
    return $false
}

function Test-CurrentBackend([int]$processId, [int]$expectedHostPid = 0) {
    $p = Get-CimInstance Win32_Process -Filter "ProcessId = $processId" -ErrorAction SilentlyContinue
    if (-not $p -or [IO.Path]::GetFileName($p.ExecutablePath) -notmatch '(?i)^java\.exe$') { return $false }
    return (
        (Test-Cwd $processId $backendPath) -and
        (Test-RealJudgeEnv $processId) -and
        (Test-LauncherAncestry $processId Backend $backendPath $expectedHostPid)
    )
}

function Test-CurrentFrontend([int]$processId, [int]$expectedHostPid = 0) {
    $p = Get-CimInstance Win32_Process -Filter "ProcessId = $processId" -ErrorAction SilentlyContinue
    if (-not $p -or [IO.Path]::GetFileName($p.ExecutablePath) -notmatch '(?i)^java\.exe$') { return $false }
    if ([string]::IsNullOrWhiteSpace($p.CommandLine) -or
        $p.CommandLine -notmatch '(?i)-m\s+jdk\.httpserver\s+-p\s+5500\s+-b\s+127\.0\.0\.1\s+-d\s+\.(?:\s|$)') { return $false }
    return (
        (Test-Cwd $processId $currentPath) -and
        (Test-LauncherAncestry $processId Frontend $currentPath $expectedHostPid)
    )
}

function Stop-StartedTree([int]$hostPid) {
    if ($hostPid -le 0) { return }
    try {
        # This exact cmd.exe PID was returned by Start-Process in this invocation.
        # /T scopes cleanup to that process and its child tree, never all Java/Maven.
        & "$env:SystemRoot\System32\taskkill.exe" /PID $hostPid /T /F *> $null
    } catch {
        Write-Host "[WARN] Could not clean up service tree rooted at PID $hostPid." -ForegroundColor Yellow
    }
}

function Wait-Backend([int]$expectedHostPid = 0) {
    for ($i = 0; $i -lt 45; $i++) {
        $ownerPid = Get-PortOwner 8080
        if ($ownerPid -is [int] -and $ownerPid -gt 0 -and (Test-CurrentBackend $ownerPid $expectedHostPid)) {
            $status = $null
            try {
                $response = Invoke-WebRequest -Uri 'http://127.0.0.1:8080/api/auth/me' -UseBasicParsing -TimeoutSec 1 -ErrorAction Stop
                $status = [int]$response.StatusCode
            } catch {
                if ($_.Exception.Response) { $status = [int]$_.Exception.Response.StatusCode }
            }
            if ($status -eq 401 -and (Test-CurrentBackend $ownerPid $expectedHostPid)) { return $true }
        }
        Start-Sleep -Seconds 2
    }
    return $false
}

function Wait-Frontend([int]$expectedHostPid = 0) {
    for ($i = 0; $i -lt 30; $i++) {
        $ownerPid = Get-PortOwner 5500
        if ($ownerPid -is [int] -and $ownerPid -gt 0 -and (Test-CurrentFrontend $ownerPid $expectedHostPid)) {
            try {
                $response = Invoke-WebRequest -Uri $browserUrl -UseBasicParsing -TimeoutSec 1 -ErrorAction Stop
                if ($response.StatusCode -eq 200 -and (Test-CurrentFrontend $ownerPid $expectedHostPid)) { return $true }
            } catch { }
        }
        Start-Sleep -Seconds 1
    }
    return $false
}

$mutex = $null
$mutexHeld = $false
$backendHostPid = 0
$frontendHostPid = 0

try {
    # A second double-click cannot race through the free-port check.
    $mutex = New-Object Threading.Mutex($false, 'Local\MiniForce.Startup.Launcher')
    try { $mutexHeld = $mutex.WaitOne(0) } catch { $mutexHeld = $false }
    if (-not $mutexHeld) { Fail 'MiniForce startup is already in progress. Please wait for it to finish, then try again.' }

    # Preserve the original safe per-user environment-variable retrieval.
    $dbPass = [Environment]::GetEnvironmentVariable('MINIFORCE_DB_PASSWORD', 'User')
    $jwtSecret = [Environment]::GetEnvironmentVariable('MINIFORCE_JWT_SECRET', 'User')
    if ([string]::IsNullOrWhiteSpace($dbPass)) { Fail 'MINIFORCE_DB_PASSWORD is not set.' }
    if ([string]::IsNullOrWhiteSpace($jwtSecret)) { Fail 'MINIFORCE_JWT_SECRET is not set.' }
    if ($jwtSecret.Length -lt 32) { Fail 'MINIFORCE_JWT_SECRET must be at least 32 characters.' }

    Write-Host '[1/4] Checking Docker...'
    try {
        $null = docker ps 2>&1
        if ($LASTEXITCODE -ne 0) { throw }
        Write-Host 'Docker is running.' -ForegroundColor Green
    } catch { Fail 'Docker is not running. Please start Docker Desktop first.' }

    Write-Host '[2/4] Checking MySQL...'
    $mysqlPath = 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'
    if (Test-Path -LiteralPath $mysqlPath) {
        try {
            $mysql = Start-Process -FilePath $mysqlPath -ArgumentList "-u root -p$dbPass -e `"SELECT 1;`"" -NoNewWindow -Wait -PassThru
            if ($mysql.ExitCode -ne 0) { throw }
            Write-Host 'MySQL is reachable.' -ForegroundColor Green
        } catch { Fail 'MySQL is not reachable or the configured password is incorrect.' }
    } else {
        Write-Host '[WARN] MySQL CLI not found; skipping the explicit MySQL check.' -ForegroundColor Yellow
    }

    Write-Host '[3/4] Checking ports and verifying process identity...'
    $backendPid = Get-PortOwner 8080
    $backendAlreadyRunning = $false
    if ($backendPid -ne $null) {
        if ($backendPid -gt 0 -and (Test-CurrentBackend $backendPid)) {
            $backendAlreadyRunning = $true
            Write-Host "Current MiniForce backend (PID $backendPid) is already running." -ForegroundColor Green
        } else {
            Fail 'Port 8080 is occupied by another/legacy process. Please run STOP-MINIFORCE.bat and start MiniForce again.'
        }
    }

    $frontendPid = Get-PortOwner 5500
    $frontendAlreadyRunning = $false
    if ($frontendPid -ne $null) {
        if ($frontendPid -gt 0 -and (Test-CurrentFrontend $frontendPid)) {
            $frontendAlreadyRunning = $true
            Write-Host "Current MiniForce frontend (PID $frontendPid) is already running." -ForegroundColor Green
        } else {
            Fail 'Port 5500 is occupied by another/legacy process. Please run STOP-MINIFORCE.bat and start MiniForce again.'
        }
    }

    # Every newly launched child inherits these values. The command wrapper
    # explicitly sets the critical real-judge flag as well.
    $env:MINIFORCE_DB_PASSWORD = $dbPass
    $env:MINIFORCE_JWT_SECRET = $jwtSecret
    $env:MINIFORCE_JUDGE_REAL_ENABLED = 'true'

    Write-Host '[4/4] Starting missing services...'
    if (-not $backendAlreadyRunning) {
        Write-Host 'Starting MiniForce Backend...'
        $command = 'title MiniForce Backend && cd /d "{0}" && set "MINIFORCE_JUDGE_REAL_ENABLED=true" && mvn spring-boot:run' -f $backendPath
        $backendHostPid = (Start-Process -FilePath $env:ComSpec -ArgumentList @('/d', '/c', $command) -PassThru).Id
    }
    Write-Host 'Waiting for a verified backend to become ready...'
    if (-not (Wait-Backend $backendHostPid)) { Fail 'Backend did not become ready as the verified current MiniForce backend.' }
    Write-Host 'Backend is READY and verified.' -ForegroundColor Green

    if (-not $frontendAlreadyRunning) {
        Write-Host 'Starting MiniForce Frontend...'
        $command = 'title MiniForce Frontend && cd /d "{0}" && java -m jdk.httpserver -p 5500 -b 127.0.0.1 -d .' -f $currentPath
        $frontendHostPid = (Start-Process -FilePath $env:ComSpec -ArgumentList @('/d', '/c', $command) -PassThru).Id
    }
    Write-Host 'Waiting for a verified frontend to become ready...'
    if (-not (Wait-Frontend $frontendHostPid)) { Fail 'Frontend did not become ready as the verified current MiniForce frontend.' }
    Write-Host 'Frontend is READY and verified.' -ForegroundColor Green

    Write-Host "Opening browser to $browserUrl"
    Start-Process $browserUrl
    Write-Host '========================================='
    Write-Host 'Launcher finished successfully.'
    Write-Host 'To stop services, run STOP-MINIFORCE.bat'
    Write-Host '========================================='
}
catch {
    Write-Host "[ERROR] $($_.Exception.Message)" -ForegroundColor Red
    if ($frontendHostPid -gt 0) { Stop-StartedTree $frontendHostPid }
    if ($backendHostPid -gt 0) { Stop-StartedTree $backendHostPid }
    exit 1
}
finally {
    if ($mutexHeld -and $mutex) { $mutex.ReleaseMutex() }
    if ($mutex) { $mutex.Dispose() }
}
