@echo off
rem MiniForce Database Migration Launcher
setlocal EnableDelayedExpansion

set SCRIPT_DIR=%~dp0
powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%migrate.ps1"
if errorlevel 1 (
    echo.
    echo [ERROR] Database migration failed.
    exit /b 1
)

echo [SUCCESS] Database migrations completed.
exit /b 0
