@echo off
REM Build Native Application Script for Windows

echo Building GymPro Native Application...

REM Step 1: Clean and package (creates JAR with all dependencies)
echo Step 1: Cleaning and packaging...
call mvn clean package

REM Step 2: Build native application with jpackage
echo Step 2: Building native application...
call mvn jpackage:jpackage

echo.
echo Build complete! Native application is in: target\dist\
echo.
echo Windows: target\dist\GymPro.exe

pause

