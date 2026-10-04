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

echo [3/7] Checking MySQL and local ports...
call :testTcpPort 3306
if errorlevel 1 (
    echo [ERROR] MySQL is not reachable on 127.0.0.1:3306. Start your existing MiniForce MySQL service first.
    goto :fatal
)
call :requireFreePort 8080 || goto :fatal
call :requireFreePort 5173 || goto :fatal
echo MySQL is reachable and the MiniForce ports are free.

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

echo [6/7] Starting the Spring Boot backend...
start "MiniForce Backend" /D "%BACKEND_DIR%" cmd /d /k "title MiniForce Backend && mvn spring-boot:run"
call :waitForHttpStatus "%BACKEND_URL%" 401 45
if errorlevel 1 (
    echo.
    echo [ERROR] The backend did not become ready on port 8080.
    echo Check the "MiniForce Backend" window for the exact message.
    echo Run STOP-MINIFORCE.bat after reviewing the message.
    exit /b 1
)
echo Backend is ready.

echo [7/7] Starting the React frontend...
start "MiniForce Frontend" /D "%FRONTEND_DIR%" cmd /d /k "title MiniForce Frontend && npm run dev"
call :waitForHttpStatus "%FRONTEND_URL%" 200 30
if errorlevel 1 (
    echo.
    echo [ERROR] The React frontend did not become ready on port 5173.
    echo Check the "MiniForce Frontend" window for the exact message, then run STOP-MINIFORCE.bat.
    exit /b 1
)

rem Verify that the Vite frontend can proxy an API request
call :waitForHttpStatus "%FRONTEND_URL%/api/auth/me" 401 5
if errorlevel 1 (
    echo.
    echo [ERROR] The React frontend started, but it cannot reach the MiniForce API through its local proxy.
    echo Check both MiniForce windows, then run STOP-MINIFORCE.bat.
    exit /b 1
)

echo Frontend and API connection are ready.
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

:requireFreePort
powershell -NoProfile -Command "$listener = Get-NetTCPConnection -State Listen -LocalPort %~1 -ErrorAction SilentlyContinue; if ($listener) { exit 1 } exit 0" >nul 2>&1
if not errorlevel 1 exit /b 0
echo [ERROR] Port %~1 is already in use. Use STOP-MINIFORCE.bat or close the program using the port.
exit /b 1

:waitForHttpStatus
for /L %%A in (1,1,%~3) do (
    powershell -NoProfile -Command "$status = 0; try { $response = Invoke-WebRequest -Uri '%~1' -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop; $status = [int]$response.StatusCode } catch { if ($_.Exception.Response) { $status = [int]$_.Exception.Response.StatusCode } }; if ($status -eq %~2) { exit 0 } exit 1" >nul 2>&1
    if not errorlevel 1 exit /b 0
    timeout /t 2 /nobreak >nul
)
exit /b 1
