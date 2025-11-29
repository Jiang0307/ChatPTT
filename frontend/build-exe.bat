@echo off
REM ChatPTT 前端打包腳本 - 將 JAR 打包成 EXE
REM 使用 jpackage 工具（Java 14+）

echo ========================================
echo ChatPTT 前端打包工具
echo ========================================
echo.

REM 檢查 Java 是否安裝
echo 檢查 Java 版本...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [錯誤] 找不到 Java，請先安裝 Java 21
    pause
    exit /b 1
)
echo [成功] Java 已安裝
echo.

REM 檢查 jpackage 是否可用
echo 檢查 jpackage 工具...
jpackage --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [錯誤] 找不到 jpackage 工具
    echo 請確保使用 Java 14+ 版本
    pause
    exit /b 1
)
echo [成功] jpackage 可用
echo.

REM 切換到腳本所在目錄
cd /d "%~dp0"

REM 步驟 1: 清理舊的構建
echo 步驟 1: 清理舊的構建...
if exist target (
    rmdir /s /q target
    echo [成功] 已清理 target 目錄
)
echo.

REM 步驟 2: 構建 Fat JAR
echo 步驟 2: 構建 Fat JAR...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo [錯誤] Maven 構建失敗
    pause
    exit /b 1
)
echo [成功] JAR 構建成功
echo.

REM 查找生成的 JAR 文件（排除 original）
for %%f in (target\chatptt-frontend-*.jar) do (
    if not "%%~nf"=="chatptt-frontend-1.0.0-original" (
        set JAR_FILE=%%~nxf
        set JAR_NAME=%%~nf
        goto :found_jar
    )
)

:found_jar
if not defined JAR_FILE (
    echo [錯誤] 找不到生成的 JAR 文件
    pause
    exit /b 1
)

echo 找到 JAR 文件: %JAR_FILE%
echo.

REM 步驟 3: 使用 jpackage 打包成應用程序映像（不需要 WiX）
echo 步驟 3: 使用 jpackage 打包成應用程序映像...

REM 清理舊的輸出目錄
if exist dist rmdir /s /q dist

REM jpackage 命令
set APP_NAME=ChatPTT
set MAIN_CLASS=main.Main
set APP_VERSION=1.0.0

echo 應用名稱: %APP_NAME%
echo 主類: %MAIN_CLASS%
echo 輸入 JAR: %JAR_FILE%
echo 輸出目錄: dist
echo.
echo 正在創建應用程序映像（這可能需要幾分鐘）...
echo.

jpackage ^
    --input target ^
    --name %APP_NAME% ^
    --main-jar %JAR_FILE% ^
    --main-class %MAIN_CLASS% ^
    --type app-image ^
    --dest dist ^
    --app-version %APP_VERSION% ^
    --description "ChatPTT - 聊天論壇應用程式" ^
    --vendor "ChatPTT"

if %errorlevel% equ 0 (
    echo.
    echo [成功] 應用程序映像創建成功
    echo.
    echo 步驟 4: 創建啟動器...
    
    REM 創建 VBS 啟動器（隱藏控制台窗口）
    set APP_DIR=dist\%APP_NAME%
    set EXE_PATH=%APP_DIR%\%APP_NAME%.exe
    set VBS_PATH=dist\%APP_NAME%.vbs
    set BAT_PATH=dist\%APP_NAME%.bat
    
    REM 創建 VBS 文件
    (
        echo Set WshShell = CreateObject^("WScript.Shell"^)
        echo WshShell.Run """%EXE_PATH%""", 0, False
        echo Set WshShell = Nothing
    ) > "%VBS_PATH%"
    
    REM 創建 BAT 文件（用於調試）
    (
        echo @echo off
        echo cd /d "%%~dp0"
        echo "%EXE_PATH%"
    ) > "%BAT_PATH%"
    
    echo [成功] 啟動器創建成功
    echo.
    echo ========================================
    echo [成功] 打包成功！
    echo ========================================
    echo.
    echo 應用程序位置: %APP_DIR%
    echo 主程序: %EXE_PATH%
    echo 啟動器（推薦）: %VBS_PATH%
    echo 調試啟動器: %BAT_PATH%
    echo.
    echo 提示:
    echo 1. 雙擊 %APP_NAME%.vbs 運行應用（無控制台窗口）
    echo 2. 或雙擊 %APP_NAME%.bat 運行應用（顯示控制台，用於調試）
    echo 3. 將 config.properties 放在應用程序目錄下（%APP_DIR%）
    echo 4. 如果沒有 config.properties，程序會自動創建預設配置
    echo.
    echo 分發說明:
    echo - 可以將整個 %APP_NAME% 文件夾打包分發
    echo - 或者只分發 %APP_NAME%.vbs 和 %APP_NAME% 文件夾
    echo.
) else (
    echo.
    echo [錯誤] jpackage 打包失敗
    echo 提示: 如果遇到問題，可以嘗試只打包 JAR 文件
    pause
    exit /b 1
)

pause

