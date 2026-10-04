@echo off
setlocal
powershell -NoProfile -ExecutionPolicy Bypass -Command "$script = (Get-Content '%~f0' | Select-Object -Skip 4) -join \"`n\"; Invoke-Expression $script"
exit /b %ERRORLEVEL%

Write-Host "This is pure PowerShell!"
$env_var = [Environment]::GetEnvironmentVariable('PATH', 'User')
if ($env_var) { Write-Host "Env works!" }
