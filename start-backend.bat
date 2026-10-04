@echo off
title MiniForce Backend Server
cd /d "%~dp0backend"
echo ============================================================
echo Starting MiniForce Backend Server (Port 8080)...
echo ============================================================
for /f "tokens=*" %%i in ('powershell -Command "[Environment]::GetEnvironmentVariable('MINIFORCE_DB_PASSWORD', 'User')"') do set MINIFORCE_DB_PASSWORD=%%i
for /f "tokens=*" %%i in ('powershell -Command "[Environment]::GetEnvironmentVariable('MINIFORCE_JWT_SECRET', 'User')"') do set MINIFORCE_JWT_SECRET=%%i

if "%MINIFORCE_JWT_SECRET%"=="" (
    echo [ERROR] MINIFORCE_JWT_SECRET is not set.
    pause
    exit /b 1
)

mvn spring-boot:run
pause
