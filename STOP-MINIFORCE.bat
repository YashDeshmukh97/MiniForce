@echo off
setlocal EnableExtensions
echo =========================================
echo       Stopping MiniForce Services
echo =========================================
echo.

echo [1/4] Stopping MiniForce Backend and Frontend windows...
taskkill /FI "WindowTitle eq MiniForce Backend*" /T /F >nul 2>&1
taskkill /FI "WindowTitle eq MiniForce Frontend*" /T /F >nul 2>&1

echo [2/4] Terminating any remaining backend/frontend processes...
powershell -NoProfile -Command "$conns = Get-NetTCPConnection -State Listen -LocalPort 8080, 5173 -ErrorAction SilentlyContinue; foreach ($c in $conns) { $proc = Get-Process -Id $c.OwningProcess -ErrorAction SilentlyContinue; if ($proc) { if ($c.LocalPort -eq 8080 -and ($proc.ProcessName -match 'java' -or $proc.ProcessName -match 'cmd')) { Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue } if ($c.LocalPort -eq 5173 -and ($proc.ProcessName -match 'node' -or $proc.ProcessName -match 'cmd')) { Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue } } }" >nul 2>&1

echo [3/4] Cleaning up any lingering judge containers...
powershell -NoProfile -Command "$containers = docker ps -q --filter 'name=miniforce_exec_' 2>$null; if ($containers) { docker rm -f $containers >$null 2>&1 }" >nul 2>&1

echo [4/4] Cleaning up temporary judge workspaces...
powershell -NoProfile -Command "Get-ChildItem -Path $env:TEMP -Filter 'miniforce_exec_*' -Directory -ErrorAction SilentlyContinue | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue" >nul 2>&1

echo.
echo All MiniForce services, judge containers, and temporary workspaces stopped.
echo =========================================
exit /b 0
