@echo off
REM 死亡日记 APK 自动构建脚本 (Windows)
REM
REM 使用方法：
REM 1. 确保已安装 Android SDK 和 JDK 17+
REM 2. 设置 ANDROID_HOME 环境变量
REM 3. 双击运行此脚本

echo ======================================
echo   死亡日记 APK 构建脚本
echo ======================================
echo.

REM 检查 Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Java
    echo 请安装 JDK 17 或更高版本
    echo 下载地址: https://adoptium.net/
    pause
    exit /b 1
)

echo [√] Java 已安装
java -version

echo.

REM 检查 ANDROID_HOME
if "%ANDROID_HOME%"=="" (
    echo [警告] ANDROID_HOME 未设置
    echo 请设置环境变量 ANDROID_HOME 指向 Android SDK 安装路径
    pause
    exit /b 1
)

echo [√] ANDROID_HOME: %ANDROID_HOME%
echo.

REM 清理之前的构建
echo [1/3] 清理之前的构建...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo [错误] 清理失败
    pause
    exit /b 1
)

echo.

REM 构建 Debug APK
echo [2/3] 开始构建 Debug APK...
echo 这可能需要几分钟，请耐心等待...
echo.

call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo.
    echo [错误] 构建失败
    echo.
    echo 可能的解决方案：
    echo 1. 检查网络连接（需要下载依赖）
    echo 2. 清理 Gradle 缓存
    echo 3. 使用 VPN 或配置国内镜像源
    pause
    exit /b 1
)

echo.

REM 检查构建结果
echo [3/3] 检查构建结果...

set APK_PATH=app\build\outputs\apk\debug\app-debug.apk

if exist "%APK_PATH%" (
    echo.
    echo ======================================
    echo   构建成功！
    echo ======================================
    echo.
    echo APK 文件位置:
    echo   %CD%\%APK_PATH%
    echo.

    REM 检查是否连接了设备
    where adb >nul 2>&1
    if %errorlevel% equ 0 (
        echo 检测到 ADB，是否安装到连接的设备？
        echo   输入 Y 安装，其他键跳过
        set /p INSTALL=

        if /i "%INSTALL%"=="Y" (
            echo.
            echo 正在安装到设备...
            adb install -r "%APK_PATH%"
            if %errorlevel% equ 0 (
                echo [√] 安装成功！
                echo 现在您可以在设备上找到'死亡日记'应用并启动它
            ) else (
                echo [错误] 安装失败
                echo 请尝试手动安装 APK 文件
            )
        )
    )

    echo.
    echo ======================================
    echo   完成
    echo ======================================
    echo.
    echo 下一步：
    echo   1. 将 APK 传输到手机安装
    echo   2. 或使用: adb install %APK_PATH%
    echo.

) else (
    echo [错误] 未找到 APK 文件
    echo 预期路径: %APK_PATH%
    pause
    exit /b 1
)

pause
