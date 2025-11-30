@echo off
REM ChatPTT Frontend Build Script - Package JAR to EXE

echo ========================================
echo ChatPTT Frontend Build Tool
echo ========================================
echo.

REM Check Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java not found
    pause
    exit /b 1
)

REM Check jpackage
jpackage --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] jpackage not found
    pause
    exit /b 1
)

REM Change to script directory
cd /d "%~dp0"

REM Clean old build
echo Cleaning old build...
if exist target rmdir /s /q target
if exist dist rmdir /s /q dist
echo.

REM Build JAR
echo Building JAR...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Maven build failed
    pause
    exit /b 1
)
echo.

REM Find JAR file
set JAR_FILE=
for %%f in (target\chatptt-frontend-*.jar) do (
    if not "%%~nf"=="chatptt-frontend-1.0.0-original" (
        set JAR_FILE=%%~nxf
        goto :found_jar
    )
)

:found_jar
if not defined JAR_FILE (
    echo [ERROR] JAR file not found
    pause
    exit /b 1
)

echo Found JAR: %JAR_FILE%
echo.

REM Package with jpackage
echo Packaging with jpackage...
jpackage --input target --name ChatPTT --main-jar %JAR_FILE% --main-class main.Main --type app-image --dest dist --app-version 1.0.0 --description "ChatPTT Application" --vendor "ChatPTT"

REM Check if EXE was created
if not exist "dist\ChatPTT\ChatPTT.exe" (
    echo [ERROR] EXE was not created
    pause
    exit /b 1
)

echo.
echo [OK] Application created successfully
echo.

REM Copy config file
if exist "config.properties" (
    copy /Y "config.properties" "dist\ChatPTT\config.properties" >nul
    echo [OK] Config file copied
)

echo.
echo ========================================
echo [SUCCESS] Build completed!
echo ========================================
echo.
echo Application: dist\ChatPTT\ChatPTT.exe
echo Config file: dist\ChatPTT\config.properties
echo.
echo To run: Navigate to dist\ChatPTT and double-click ChatPTT.exe
echo.

pause
