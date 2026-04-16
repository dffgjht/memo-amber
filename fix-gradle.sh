#!/bin/bash

# 快速修复 Gradle 同步问题

echo "🔧 开始修复 Gradle 同步问题..."

# 进入项目目录
cd "$(dirname "$0")"

echo "📁 项目目录: $(pwd)"

# 删除缓存
echo "🧹 清理缓存..."
rm -rf .gradle
rm -rf .idea
rm -rf build
rm -rf app/build

echo "✅ 缓存已清理"

# 提示下一步
echo ""
echo "============================================"
echo "  ✅ 修复完成！"
echo "============================================"
echo ""
echo "下一步："
echo "1. 关闭 Android Studio"
echo "2. 重新启动 Android Studio"
echo "3. File → Open → 选择项目文件夹"
echo "4. 等待 Gradle 同步完成"
echo ""
echo "或使用命令行构建："
echo "  ./gradlew assembleDebug"
echo ""
echo "============================================"
