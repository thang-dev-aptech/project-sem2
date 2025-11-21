@echo off
REM GymPro Launcher Script for Windows

cd /d "%~dp0"

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java is not installed or not in PATH
    pause
    exit /b 1
)

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Maven is not installed or not in PATH
    pause
    exit /b 1
)

REM Run with Maven (recommended)
echo Starting GymPro...
call mvn clean compile javafx:run

pause

