@echo off
echo Stopping MiniForce Backend...
taskkill /FI "WindowTitle eq MiniForce Backend*" /T /F >nul 2>&1

echo Stopping MiniForce Frontend...
taskkill /FI "WindowTitle eq MiniForce Frontend*" /T /F >nul 2>&1

echo All MiniForce services stopped.
