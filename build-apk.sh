#!/bin/bash

# 死亡日记 APK 自动构建脚本
#
# 使用方法：
# 1. 确保已安装 Android SDK 和 JDK 17+
# 2. 设置 ANDROID_HOME 环境变量
# 3. 运行此脚本: bash build-apk.sh

set -e  # 遇到错误立即退出

echo "======================================"
echo "  死亡日记 APK 构建脚本"
echo "======================================"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查环境
echo "🔍 检查构建环境..."

# 检查 Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ 未找到 Java${NC}"
    echo "请安装 JDK 17 或更高版本"
    echo "Ubuntu/Debian: sudo apt install openjdk-17-jdk"
    echo "macOS: brew install openjdk@17"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
echo -e "${GREEN}✅ Java 版本: $(java -version 2>&1 | head -n 1)${NC}"

# 检查 ANDROID_HOME
if [ -z "$ANDROID_HOME" ]; then
    echo -e "${YELLOW}⚠️  ANDROID_HOME 未设置${NC}"
    echo "尝试自动检测 Android SDK..."

    # 常见路径
    POSSIBLE_PATHS=(
        "$HOME/Android/Sdk"
        "$HOME/android-sdk"
        "/usr/lib/android-sdk"
        "/opt/android-sdk"
    )

    for path in "${POSSIBLE_PATHS[@]}"; do
        if [ -d "$path" ]; then
            export ANDROID_HOME="$path"
            echo -e "${GREEN}✅ 找到 Android SDK: $ANDROID_HOME${NC}"
            break
        fi
    done

    if [ -z "$ANDROID_HOME" ]; then
        echo -e "${RED}❌ 未找到 Android SDK${NC}"
        echo "请设置 ANDROID_HOME 环境变量"
        exit 1
    fi
else
    echo -e "${GREEN}✅ ANDROID_HOME: $ANDROID_HOME${NC}"
fi

# 检查 Android SDK 版本
if [ -f "$ANDROID_HOME/platform-tools/adb" ]; then
    ADB_VERSION=$("$ANDROID_HOME/platform-tools/adb" version | head -n 1)
    echo -e "${GREEN}✅ $ADB_VERSION${NC}"
fi

# 清理之前的构建
echo ""
echo "🧹 清理之前的构建..."
./gradlew clean || {
    echo -e "${RED}❌ 清理失败${NC}"
    exit 1
}

# 构建 Debug APK
echo ""
echo "🔨 开始构建 Debug APK..."
echo "这可能需要几分钟，请耐心等待..."
echo ""

./gradlew assembleDebug --stacktrace || {
    echo ""
    echo -e "${RED}❌ 构建失败${NC}"
    echo ""
    echo "可能的解决方案："
    echo "1. 检查网络连接（需要下载依赖）"
    echo "2. 清理 Gradle 缓存: rm -rf ~/.gradle/caches"
    echo "3. 使用 VPN 或配置国内镜像源"
    echo ""
    echo "查看构建日志以获取更多信息"
    exit 1
}

# 检查构建结果
echo ""
echo "🔍 检查构建结果..."

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo -e "${GREEN}✅ 构建成功！${NC}"
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo -e "  📦 APK 信息"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "  📄 路径: $APK_PATH"
    echo "  📏 大小: $APK_SIZE"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""

    # 询问是否安装到设备
    if command -v adb &> /dev/null; then
        echo "检测到 ADB，是否安装到连接的设备？"
        echo "  输入 y 安装，其他键跳过"
        read -r -p "> " INSTALL

        if [ "$INSTALL" = "y" ] || [ "$INSTALL" = "Y" ]; then
            echo ""
            echo "📱 正在安装到设备..."

            # 检查设备连接
            DEVICES=$(adb devices | grep -v "List" | wc -l)

            if [ "$DEVICES" -eq 0 ]; then
                echo -e "${YELLOW}⚠️  未检测到连接的设备${NC}"
                echo "请确保："
                echo "  1. 设备已通过 USB 连接"
                echo "  2. 已启用 USB 调试"
                echo "  3. 已授权计算机调试"
            else
                adb install -r "$APK_PATH" && {
                    echo -e "${GREEN}✅ 安装成功！${NC}"
                    echo ""
                    echo "现在您可以在设备上找到'死亡日记'应用并启动它"
                } || {
                    echo -e "${RED}❌ 安装失败${NC}"
                    echo "请尝试手动安装 APK 文件"
                }
            fi
        fi
    fi

    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "  ✨ 构建完成！"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "APK 文件位置:"
    echo "  $PWD/$APK_PATH"
    echo ""
    echo "下一步："
    echo "  1. 将 APK 传输到手机安装"
    echo "  2. 或使用: adb install $APK_PATH"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

else
    echo -e "${RED}❌ 未找到 APK 文件${NC}"
    echo "预期路径: $APK_PATH"
    exit 1
fi

echo ""
