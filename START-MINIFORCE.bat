@echo off
setlocal EnableExtensions DisableDelayedExpansion

rem MiniForce local launcher
rem Starts the current React/Vite frontend, Spring Boot API, local MySQL-backed
rem development profile, and the real Docker judge. Secrets are read only from
rem the current/user environment and are never printed or written by this script.

cd /d "%~dp0"
set "PROJECT_DIR=%CD%"
set "BACKEND_DIR=%PROJECT_DIR%\backend"
set "FRONTEND_DIR=%PROJECT_DIR%\frontend\react"
set "FRONTEND_URL=http://127.0.0.1:5173"
set "BACKEND_URL=http://127.0.0.1:8080/api/auth/me"

echo.
echo =========================================
echo            MiniForce Launcher
echo =========================================
echo.

if not exist "%BACKEND_DIR%\pom.xml" (
    echo [ERROR] The backend folder is missing or incomplete.
    goto :fatal
)
if not exist "%FRONTEND_DIR%\package.json" (
    echo [ERROR] The React frontend folder is missing or incomplete.
    goto :fatal
)
if not exist "%FRONTEND_DIR%\vite.config.ts" (
    echo [ERROR] The Vite frontend configuration is missing.
    goto :fatal
)
if not exist "%PROJECT_DIR%\Dockerfile" (
    echo [ERROR] The Docker judge Dockerfile is missing.
    goto :fatal
)

echo [1/7] Checking required tools...
where java >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java 17 or newer is required but was not found.
    goto :fatal
)
where mvn >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is required but was not found.
    goto :fatal
)
where node >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Node.js is required but was not found.
    goto :fatal
)
where npm >nul 2>&1
if errorlevel 1 (
    echo [ERROR] npm is required but was not found.
    goto :fatal
)
where docker >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Desktop is required for the real judge but was not found.
    goto :fatal
)
docker version --format "{{.Server.Version}}" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Desktop is installed but its engine is not running. Start it first.
    goto :fatal
)
echo Tools are ready.

echo [2/7] Loading private configuration...
rem Allow DB password to be empty if it's intentionally empty for local dev
call :loadUserVariable MINIFORCE_DB_PASSWORD
call :requireUserVariable MINIFORCE_JWT_SECRET || goto :fatal
powershell -NoProfile -Command "if ($env:MINIFORCE_JWT_SECRET.Length -ge 32) { exit 0 } exit 1" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] MINIFORCE_JWT_SECRET must contain at least 32 characters.
    goto :fatal
)

set "SPRING_PROFILES_ACTIVE=dev"
set "MINIFORCE_JUDGE_REAL_ENABLED=true"
set "MINIFORCE_CORS_ORIGINS=http://127.0.0.1:5173,http://localhost:5173"
echo Private configuration is available.

echo [3/7] Checking MySQL and service ports...
call :testTcpPort 3306
if errorlevel 1 (
    echo [ERROR] MySQL is not reachable on 127.0.0.1:3306. Start your existing MiniForce MySQL service first.
    goto :fatal
)

rem Check port 8080 (backend) state
call :checkServicePort 8080 "MiniForce Backend" "%BACKEND_URL%" 401 START_BACKEND || goto :fatal

rem Check port 5173 (frontend) state
call :checkServicePort 5173 "MiniForce Frontend" "%FRONTEND_URL%" 200 START_FRONTEND || goto :fatal

echo MySQL is reachable and ports are validated.

echo [4/7] Preparing the React frontend...
if exist "%FRONTEND_DIR%\node_modules\" (
    echo React dependencies are already installed.
) else (
    echo Installing React dependencies. This may take a moment...
    pushd "%FRONTEND_DIR%" || (echo [ERROR] Could not open frontend folder. & goto :fatal)
    call npm ci
    if errorlevel 1 (
        popd
        echo [ERROR] React dependencies could not be installed.
        goto :fatal
    )
    popd
)

echo [5/7] Preparing the real Docker judge...
docker image inspect miniforce-judge:latest >nul 2>&1
if errorlevel 1 (
    echo Building the required miniforce-judge:latest image...
    docker build --tag miniforce-judge:latest --file "%PROJECT_DIR%\Dockerfile" "%PROJECT_DIR%"
    if errorlevel 1 (
        echo [ERROR] The Docker judge image could not be built.
        goto :fatal
    )
) else (
    echo The real judge image is ready.
)

if "%START_BACKEND%"=="1" (
    echo [6/7] Starting the Spring Boot backend...
    start "MiniForce Backend" /D "%BACKEND_DIR%" cmd /d /k "title MiniForce Backend && mvn spring-boot:run"
    call :waitForHttpStatus "%BACKEND_URL%" 401 60
    if errorlevel 1 (
        echo.
        echo [ERROR] The backend did not become ready on port 8080.
        echo Check the "MiniForce Backend" window for the exact message.
        echo Run STOP-MINIFORCE.bat after reviewing the message.
        exit /b 1
    )
    echo Backend is ready.
) else (
    echo [6/7] Spring Boot backend is already running on port 8080.
)

if "%START_FRONTEND%"=="1" (
    echo [7/7] Starting the React frontend...
    start "MiniForce Frontend" /D "%FRONTEND_DIR%" cmd /d /k "title MiniForce Frontend && npm run dev"
    call :waitForHttpStatus "%FRONTEND_URL%" 200 30
    if errorlevel 1 (
        echo.
        echo [ERROR] The React frontend did not become ready on port 5173.
        echo Check the "MiniForce Frontend" window for the exact message, then run STOP-MINIFORCE.bat.
        exit /b 1
    )
) else (
    echo [7/7] React frontend is already running on port 5173.
)

rem Verify that port 5173 is serving the authentic React application (not a legacy static HTML page)
call :verifyReactApp "%FRONTEND_URL%"
if errorlevel 1 (
    echo.
    echo [ERROR] Port 5173 is active, but is not serving the MiniForce React application.
    echo Please make sure no legacy static server is holding port 5173, then run STOP-MINIFORCE.bat.
    exit /b 1
)

rem Verify that the Vite frontend proxy can reach the backend
call :waitForHttpStatus "%FRONTEND_URL%/api/auth/me" 401 10
if errorlevel 1 (
    echo.
    echo [ERROR] The React frontend started, but it cannot reach the MiniForce API through its local proxy.
    echo Check both MiniForce windows, then run STOP-MINIFORCE.bat.
    exit /b 1
)

echo Frontend, API connection, and Jolly Roger branding are ready.
echo.
echo Opening MiniForce in your browser: %FRONTEND_URL%/
start "" "%FRONTEND_URL%/"
echo.
echo =========================================
echo MiniForce is running.
echo To stop it later, run STOP-MINIFORCE.bat
echo =========================================
exit /b 0

:fatal
echo No MiniForce service was started by this launcher.
exit /b 1

:loadUserVariable
if defined %~1 exit /b 0
for /f "usebackq delims=" %%V in (`powershell -NoProfile -Command "[Environment]::GetEnvironmentVariable('%~1', 'User')"`) do set "%~1=%%V"
exit /b 0

:requireUserVariable
if defined %~1 exit /b 0
for /f "usebackq delims=" %%V in (`powershell -NoProfile -Command "[Environment]::GetEnvironmentVariable('%~1', 'User')"`) do set "%~1=%%V"
if defined %~1 exit /b 0
echo [ERROR] %~1 is not set. Set it as a User environment variable and start a new Command Prompt before running MiniForce.
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
    echo %~2 is already running and ready on port %~1.
    set "%~5=0"
    exit /b 0
)

set "CONFLICT_PID=Unknown"
set "CONFLICT_NAME=Unknown Process"
for /f "tokens=1* delims=:" %%A in ("%PORT_STATE%") do (
    if not "%%A"=="" set "CONFLICT_PID=%%A"
    if not "%%B"=="" set "CONFLICT_NAME=%%B"
)
echo [ERROR] Port %~1 is in use by %CONFLICT_NAME% (PID %CONFLICT_PID%).
echo If this is an orphaned process from a previous run, use STOP-MINIFORCE.bat to clear it.
exit /b 1

:testHttpStatus
powershell -NoProfile -Command "$status = 0; try { $response = Invoke-WebRequest -Uri '%~1' -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop; $status = [int]$response.StatusCode } catch { if ($_.Exception.Response) { $status = [int]$_.Exception.Response.StatusCode } }; if ($status -eq %~2) { exit 0 } exit 1" >nul 2>&1
exit /b %ERRORLEVEL%

:verifyReactApp
powershell -NoProfile -Command "try { $res = Invoke-WebRequest -Uri '%~1' -UseBasicParsing -TimeoutSec 3 -ErrorAction Stop; if ($res.Content -match 'id=\x22root\x22' -or $res.Content -match 'main\.tsx') { exit 0 } exit 1 } catch { exit 1 }" >nul 2>&1
exit /b %ERRORLEVEL%

:waitForHttpStatus
for /L %%A in (1,1,%~3) do (
    powershell -NoProfile -Command "$status = 0; try { $response = Invoke-WebRequest -Uri '%~1' -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop; $status = [int]$response.StatusCode } catch { if ($_.Exception.Response) { $status = [int]$_.Exception.Response.StatusCode } }; if ($status -eq %~2) { exit 0 } exit 1" >nul 2>&1
    if not errorlevel 1 exit /b 0
    powershell -NoProfile -Command "Start-Sleep -Seconds 2" >nul 2>&1
)
exit /b 1
