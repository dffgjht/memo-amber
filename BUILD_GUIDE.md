# 死亡日记 APK 构建指南

## 🚀 快速开始

### 前置要求
- Android Studio (推荐) 或 Android SDK + 命令行工具
- JDK 17+
- Android SDK API 34

### 方法一：使用 Android Studio (最简单)

1. **打开项目**
   ```
   打开 Android Studio → Open → 选择 death-diary 文件夹
   ```

2. **等待 Gradle 同步完成**
   - 首次打开会自动下载依赖，请耐心等待
   - 如遇网络问题，可配置国内镜像源

3. **构建 APK**
   ```
   菜单栏 → Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```
   - 构建完成后点击通知中的 "locate"
   - APK 位于: `app/build/outputs/apk/debug/app-debug.apk`

### 方法二：命令行构建

```bash
# 进入项目目录
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 赋予执行权限
chmod +x gradlew

# 构建 Debug APK
./gradlew assembleDebug

# 生成的 APK 位置
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

### 安装到设备

```bash
# 通过 USB 连接设备后
adb install app/build/outputs/apk/debug/app-debug.apk

# 或者直接传输 APK 文件到手机安装
```

## 📱 应用功能

1. **日记系统**
   - 记录日常想法和回忆
   - 心情标签 (开心😊、难过😢、平静😐等)
   - 支持添加照片和媒体

2. **密码保险箱**
   - 安全保存账号密码
   - 分类管理 (账号、文档、笔记)
   - AES-256 加密存储

3. **遗嘱/遗言**
   - 创建数字遗嘱
   - 指定收件人
   - 设定发布条件 (日期/手动)

4. **回忆相册**
   - 珍藏照片和视频
   - 添加描述和标签
   - 按时间线浏览

5. **安全特性**
   - 生物识别 (指纹/面容)
   - 主密码保护
   - AES-256 加密
   - 自动锁定

## 🔐 安全说明

- 所有数据存储在本地，不上传云端
- 使用 Android Keystore 硬件级加密
- 主密码使用哈希存储，无法找回
- 建议定期备份数据

## ⚠️ 重要提示

1. **主密码管理**
   - 首次使用需设置主密码
   - 主密码无法找回，请务必记住
   - 建议设置6位以上强密码

2. **数据备份**
   - 卸载应用会删除所有数据
   - 请在设置中定期创建备份
   - 备份文件会保存到 Download 文件夹

3. **隐私保护**
   - 应用无网络权限
   - 所有数据仅保存在本地
   - 不会收集任何用户信息

## 🛠️ 开发信息

- **技术栈**: Kotlin + Jetpack Compose
- **最低版本**: Android 7.0 (API 24)
- **目标版本**: Android 14 (API 34)
- **架构**: MVVM + Room Database
- **UI框架**: Material Design 3

## 📂 项目结构

```
death-diary/
├── app/
│   ├── src/main/
│   │   ├── java/com/deathdiary/
│   │   │   ├── MainActivity.kt          # 主入口
│   │   │   ├── data/                    # 数据层
│   │   │   │   ├── Database.kt
│   │   │   │   └── entities/            # 数据实体
│   │   │   ├── security/                # 安全模块
│   │   │   │   ├── SecurityManager.kt
│   │   │   │   └── BiometricAuthManager.kt
│   │   │   └── ui/                      # UI层
│   │   │       ├── screens/             # 各功能界面
│   │   │       └── theme/               # 主题配置
│   │   └── res/                         # 资源文件
│   └── build.gradle.kts                 # 应用级构建配置
├── build.gradle.kts                     # 项目级构建配置
├── settings.gradle.kts                  # Gradle设置
└── gradlew                              # Gradle包装脚本
```

## 🔧 故障排除

### Gradle 同步失败
```bash
# 清理并重新构建
./gradlew clean
./gradlew assembleDebug
```

### 依赖下载慢
在 `build.gradle.kts` 中添加国内镜像:
```kotlin
repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    google()
    mavenCentral()
}
```

### 找不到 Java
```bash
# 检查 Java 版本
java -version

# 设置 JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

## 📝 许可证

MIT License - 自由使用和修改

---

**开发者**: OpenClaw AI Assistant
**版本**: 1.0.0
**更新日期**: 2025-01-15
