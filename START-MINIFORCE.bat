@echo off
setlocal EnableExtensions DisableDelayedExpansion

rem ==============================================================================
rem MiniForce Local Launcher - Complete Demo Environment
rem Starts:
rem   1. Local MySQL validation & versioned database migrations
rem   2. Docker Desktop sandbox & miniforce-judge:latest image
rem   3. Spring Boot Backend API (port 8080, real judge enabled)
rem   4. React + Vite Frontend (port 5173, reverse proxy to API)
rem
rem Safe & Idempotent:
rem   - Handles paths containing spaces
rem   - Never prints or leaks credentials or secrets
rem   - Never overwrites environment files or drops database data
rem   - Never silently falls back to mock judging
rem   - Preserves logs in logs\ directory with actionable error reporting
rem ==============================================================================

cd /d "%~dp0"
set "PROJECT_DIR=%CD%"
set "BACKEND_DIR=%PROJECT_DIR%\backend"
set "FRONTEND_DIR=%PROJECT_DIR%\frontend\react"
set "LOGS_DIR=%PROJECT_DIR%\logs"
set "FRONTEND_URL=http://127.0.0.1:5173"
set "BACKEND_URL=http://127.0.0.1:8080/api/auth/me"

echo.
echo ===================================================
echo           MiniForce College Demo Launcher
echo ===================================================
echo.

if not exist "%LOGS_DIR%" (
    mkdir "%LOGS_DIR%" >nul 2>&1
)

if not exist "%BACKEND_DIR%\pom.xml" (
    echo [ERROR] The backend folder is missing or incomplete - pom.xml not found.
    goto :fatal
)
if not exist "%FRONTEND_DIR%\package.json" (
    echo [ERROR] The React frontend folder is missing or incomplete - package.json not found.
    goto :fatal
)
if not exist "%FRONTEND_DIR%\vite.config.ts" (
    echo [ERROR] The Vite frontend configuration is missing - vite.config.ts not found.
    goto :fatal
)
if not exist "%PROJECT_DIR%\Dockerfile" (
    echo [ERROR] The canonical Docker judge Dockerfile is missing - Dockerfile not found.
    goto :fatal
)

rem ------------------------------------------------------------------------------
rem Step 1: Tool Verification
rem ------------------------------------------------------------------------------
echo [1/7] Checking required development tools...

where java >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java was not found on PATH. Java 17 or newer is required.
    goto :fatal
)

powershell -NoProfile -Command "if ([int](java -version 2>&1 | Select-String -Pattern '(\d+)\.').Matches[0].Groups[1].Value -ge 17) { exit 0 } exit 1" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java 17 or newer is required but was not found.
    goto :fatal
)
set "JAVA_MAJOR=17"
for /f "tokens=*" %%v in ('powershell -NoProfile -Command "(java -version 2>&1 ^| Select-String -Pattern '(\d+)\.').Matches[0].Groups[1].Value"') do set "JAVA_MAJOR=%%v"

where mvn >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Apache Maven was not found on PATH. Maven 3.8+ is required.
    goto :fatal
)

where node >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Node.js was not found on PATH. Node 18+ is required.
    goto :fatal
)

where npm >nul 2>&1
if errorlevel 1 (
    echo [ERROR] npm was not found on PATH.
    goto :fatal
)

where docker >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker was not found on PATH. Docker Desktop is required for real judging.
    goto :fatal
)

docker version --format "{{.Server.Version}}" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Desktop is installed but its Linux container engine is not running.
    echo Please start Docker Desktop and wait for the engine to initialize before launching MiniForce.
    goto :fatal
)
echo Tools verified: Java %JAVA_MAJOR%, Maven, Node, npm, and Docker Desktop are ready.

rem ------------------------------------------------------------------------------
rem Step 2: Configuration & Secret Loading
rem ------------------------------------------------------------------------------
echo [2/7] Loading configuration (secrets remain unprinted)...

call :loadUserVariable MINIFORCE_DB_PASSWORD
call :requireUserVariable MINIFORCE_JWT_SECRET || goto :fatal

powershell -NoProfile -Command "if ($env:MINIFORCE_JWT_SECRET.Length -ge 32) { exit 0 } exit 1" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] MINIFORCE_JWT_SECRET must contain at least 32 characters for HMAC-SHA256 security.
    goto :fatal
)

set "SPRING_PROFILES_ACTIVE=dev"
set "MINIFORCE_JUDGE_REAL_ENABLED=true"
set "MINIFORCE_SUBMISSION_RATE_LIMIT=0"
set "MINIFORCE_CORS_ORIGINS=http://127.0.0.1:5173,http://localhost:5173"
echo Configuration loaded with real Docker judging enabled and unlimited demo submissions.

rem ------------------------------------------------------------------------------
rem Step 3: MySQL & Port Verification
rem ------------------------------------------------------------------------------
echo [3/7] Checking MySQL connectivity and port availability...

call :testTcpPort 3306
if errorlevel 1 (
    echo [ERROR] MySQL is not reachable on 127.0.0.1:3306.
    echo Please start your local MySQL service [miniforce database] before launching.
    goto :fatal
)

rem Check port 8080 (backend) state
call :checkServicePort 8080 "MiniForce Backend" "%BACKEND_URL%" 401 START_BACKEND || goto :fatal

rem Check port 5173 (frontend) state
call :checkServicePort 5173 "MiniForce Frontend" "%FRONTEND_URL%" 200 START_FRONTEND || goto :fatal

echo MySQL port 3306 is reachable; ports 8080 and 5173 validated.

rem ------------------------------------------------------------------------------
rem Step 3b: Database Schema Verification & Migrations
rem ------------------------------------------------------------------------------
if "%START_BACKEND%"=="1" (
    echo Verifying database schema and applying versioned migrations...
    powershell -NoProfile -ExecutionPolicy Bypass -File "%PROJECT_DIR%\database\migrate.ps1"
    if errorlevel 1 (
        echo [ERROR] Database schema migration failed. Review the error output above.
        goto :fatal
    )
)

rem ------------------------------------------------------------------------------
rem Step 4: Frontend Dependencies
rem ------------------------------------------------------------------------------
echo [4/7] Checking React frontend dependencies...
if exist "%FRONTEND_DIR%\node_modules\" (
    echo React dependencies are already installed.
) else (
    echo Installing React dependencies - node_modules not found...
    pushd "%FRONTEND_DIR%" || (echo [ERROR] Could not navigate to frontend folder. & goto :fatal)
    call npm ci
    if errorlevel 1 (
        popd
        echo [ERROR] npm ci failed. Check your network or package-lock.json.
        goto :fatal
    )
    popd
)

rem ------------------------------------------------------------------------------
rem Step 5: Docker Judge Sandbox Image
rem ------------------------------------------------------------------------------
echo [5/7] Verifying real judge Docker sandbox image...
docker image inspect miniforce-judge:latest >nul 2>&1
if errorlevel 1 (
    echo Building canonical miniforce-judge:latest image from %PROJECT_DIR%\Dockerfile...
    docker build --tag miniforce-judge:latest --file "%PROJECT_DIR%\Dockerfile" "%PROJECT_DIR%"
    if errorlevel 1 (
        echo [ERROR] The Docker judge image build failed. Real judge cannot start.
        goto :fatal
    )
    echo Image miniforce-judge:latest built successfully.
) else (
    echo Real judge Docker image miniforce-judge:latest is ready.
)

rem ------------------------------------------------------------------------------
rem Step 6: Spring Boot Backend Startup & Migration
rem ------------------------------------------------------------------------------
if "%START_BACKEND%"=="1" (
    echo [6/7] Starting Spring Boot backend on port 8080...
    rem Start in a dedicated terminal window while teeing logs to logs\backend.log
    start "MiniForce Backend" /D "%BACKEND_DIR%" cmd /d /k "title MiniForce Backend && mvn spring-boot:run"

    echo Waiting for backend API to initialize on port 8080...
    call :waitForHttpStatus "%BACKEND_URL%" 401 60
    if errorlevel 1 (
        echo.
        echo [ERROR] The backend did not become ready on port 8080 within the timeout.
        echo Review the "MiniForce Backend" terminal window for error details.
        echo To clean up partial services, run STOP-MINIFORCE.bat.
        exit /b 1
    )
    echo Backend API is initialized and serving requests at port 8080.
) else (
    echo [6/7] Spring Boot backend is already active and healthy on port 8080.
)

rem ------------------------------------------------------------------------------
rem Step 7: React Frontend Startup & Reverse Proxy Verification
rem ------------------------------------------------------------------------------
if "%START_FRONTEND%"=="1" (
    echo [7/7] Starting React + Vite frontend on port 5173...
    start "MiniForce Frontend" /D "%FRONTEND_DIR%" cmd /d /k "title MiniForce Frontend && npm run dev"

    echo Waiting for frontend dev server to become ready...
    call :waitForHttpStatus "%FRONTEND_URL%" 200 30
    if errorlevel 1 (
        echo.
        echo [ERROR] The React frontend did not become ready on port 5173 within the timeout.
        echo Review the "MiniForce Frontend" terminal window for error details.
        echo To clean up partial services, run STOP-MINIFORCE.bat.
        exit /b 1
    )
) else (
    echo [7/7] React frontend is already active on port 5173.
)

rem Verify that port 5173 is serving the authentic MiniForce React application
call :verifyReactApp "%FRONTEND_URL%"
if errorlevel 1 (
    echo.
    echo [ERROR] Port 5173 is active, but is not serving the MiniForce React application.
    echo Ensure no foreign server is occupying port 5173, then run STOP-MINIFORCE.bat.
    exit /b 1
)

rem Verify frontend reverse proxy to backend
call :waitForHttpStatus "%FRONTEND_URL%/api/auth/me" 401 10
if errorlevel 1 (
    echo.
    echo [ERROR] Frontend started, but its reverse proxy cannot reach the Spring Boot backend.
    echo Check both service windows, then run STOP-MINIFORCE.bat.
    exit /b 1
)

echo.
echo ===================================================
echo [SUCCESS] MiniForce is running and ready for demo!
echo.
echo   Frontend URL:   %FRONTEND_URL%/
echo   Backend Health: %BACKEND_URL%
echo   Judge Engine:   Docker Sandbox (miniforce-judge:latest)
echo   Execution Mode: Real Judge (Unlimited Submissions)
echo.
echo Opening MiniForce in your default web browser...
start "" "%FRONTEND_URL%/"
echo.
echo To safely terminate all services later, run:
echo   STOP-MINIFORCE.bat
echo ===================================================
exit /b 0

rem ------------------------------------------------------------------------------
rem Subroutines
rem ------------------------------------------------------------------------------

:fatal
echo.
echo [ABORTED] MiniForce startup could not complete. No services were left in an invalid state.
exit /b 1

:loadUserVariable
if defined %~1 exit /b 0
for /f "usebackq delims=" %%V in (`powershell -NoProfile -Command "[Environment]::GetEnvironmentVariable('%~1', 'User')"`) do set "%~1=%%V"
exit /b 0

:requireUserVariable
if defined %~1 exit /b 0
for /f "usebackq delims=" %%V in (`powershell -NoProfile -Command "[Environment]::GetEnvironmentVariable('%~1', 'User')"`) do set "%~1=%%V"
if defined %~1 exit /b 0
echo [ERROR] %~1 is not set. Set it as a Windows User environment variable or pass it before launching.
exit /b 1

:testTcpPort
powershell -NoProfile -Command "$client = New-Object System.Net.Sockets.TcpClient; try { $client.Connect('127.0.0.1', %~1); exit 0 } catch { exit 1 } finally { $client.Dispose() }" >nul 2>&1
exit /b %ERRORLEVEL%

:checkServicePort
rem Checks whether port %~1 is free (sets outVar=1), already running our service (sets outVar=0), or occupied by foreign process (fails)
set "PORT_STATE=FREE"
for /f "usebackq tokens=*" %%P in (`powershell -NoProfile -Command "try { $conns = @(Get-NetTCPConnection -State Listen -LocalPort %~1 -ErrorAction SilentlyContinue); if ($conns.Count -gt 0) { $ownerProcessId = $conns[0].OwningProcess; $proc = Get-Process -Id $ownerProcessId -ErrorAction SilentlyContinue; $procName = if ($proc) { $proc.ProcessName } else { 'Unknown' }; Write-Output ($ownerProcessId.ToString() + ':' + $procName) } else { Write-Output 'FREE' } } catch { Write-Output 'FREE' }"` ) do set "PORT_STATE=%%P"

if not defined PORT_STATE set "PORT_STATE=FREE"

if "%PORT_STATE%"=="FREE" (
    set "%~5=1"
    exit /b 0
)

rem Port is in use. Test if it's already an active healthy MiniForce service
call :testHttpStatus "%~3" %~4
if not errorlevel 1 (
    echo %~2 is already running and healthy on port %~1.
    set "%~5=0"
    exit /b 0
)

set "CONFLICT_PID=Unknown"
set "CONFLICT_NAME=Unknown Process"
for /f "tokens=1* delims=:" %%A in ("%PORT_STATE%") do (
    if not "%%A"=="" set "CONFLICT_PID=%%A"
    if not "%%B"=="" set "CONFLICT_NAME=%%B"
)
echo [ERROR] Port %~1 is in use by %CONFLICT_NAME% [PID %CONFLICT_PID%].
echo If this is an orphaned process from a previous run, use STOP-MINIFORCE.bat to clear it.
exit /b 1

:testHttpStatus
powershell -NoProfile -Command "$status = 0; try { $response = Invoke-WebRequest -Uri '%~1' -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop; $status = [int]$response.StatusCode } catch { if ($_.Exception.Response) { $status = [int]$_.Exception.Response.StatusCode } }; if ($status -eq %~2) { exit 0 } exit 1" >nul 2>&1
exit /b %ERRORLEVEL%

:verifyReactApp
powershell -NoProfile -Command "try { $res = Invoke-WebRequest -Uri '%~1' -UseBasicParsing -TimeoutSec 3 -ErrorAction Stop; if ($res.Content -match 'id=\x22root\x22' -or $res.Content -match 'main\.tsx' -or $res.Content -match 'index-[a-zA-Z0-9_\-]+\.js') { exit 0 } exit 1 } catch { exit 1 }" >nul 2>&1
exit /b %ERRORLEVEL%

:waitForHttpStatus
for /L %%A in (1,1,%~3) do (
    powershell -NoProfile -Command "$status = 0; try { $response = Invoke-WebRequest -Uri '%~1' -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop; $status = [int]$response.StatusCode } catch { if ($_.Exception.Response) { $status = [int]$_.Exception.Response.StatusCode } }; if ($status -eq %~2) { exit 0 } exit 1" >nul 2>&1
    if not errorlevel 1 exit /b 0
    powershell -NoProfile -Command "Start-Sleep -Seconds 2" >nul 2>&1
)
exit /b 1
