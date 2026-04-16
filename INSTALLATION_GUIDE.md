# 📱 死亡日记 APK 获取指南

## 🎯 您的目标：获得安装包 (APK)

由于当前环境没有安装 Android 开发工具，我为您准备了**3种获取 APK 的方法**，从最简单到最灵活。

---

## 🌟 方法一：使用 Android Studio（最推荐，最简单）

### 为什么选择这个方法？
✅ **零配置** - 一键安装，自动配置所有环境
✅ **可视化** - 图形界面，操作直观
✅ **速度快** - 首次构建后只需1-2分钟
✅ **官方工具** - Google 官方 IDE，稳定可靠

### 步骤（仅需5分钟）

#### Windows 用户
1. **下载 Android Studio**
   - 访问：https://developer.android.com/studio
   - 点击 "Download Android Studio for Windows"
   - 文件大小：约 1 GB

2. **安装**
   - 运行下载的 `.exe` 文件
   - 全部选择"下一步"（使用默认设置）
   - 等待安装完成（约5-10分钟）

3. **首次启动**
   - 启动 Android Studio
   - 选择 "Standard" 安装类型
   - 点击 "Finish" 下载 Android SDK（自动）
   - 等待下载完成（约10-15分钟，只需一次）

4. **打开项目**
   - 点击 "Open"
   - 选择文件夹：`/vol1/@apphome/trim.openclaw/data/workspace/death-diary`
   - 等待 Gradle 同步（首次约5-10分钟）

5. **构建 APK**
   - 顶部菜单：`Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
   - 等待构建完成（1-2分钟）
   - 点击弹出的通知中的 "locate"
   - 找到 `app-debug.apk` 文件

6. **完成！**
   - APK 位置：`app/build/outputs/apk/debug/app-debug.apk`
   - 复制到手机安装即可

#### macOS 用户
1. **下载 Android Studio**
   ```bash
   # 使用 Homebrew（推荐）
   brew install --cask android-studio
   ```
   - 或访问：https://developer.android.com/studio
   - 下载 macOS 版本

2. **安装和配置**
   - 打开下载的 `.dmg` 文件
   - 将 Android Studio 拖到 Applications
   - 启动并按照提示安装 SDK

3. **打开项目**
   - `File` → `Open` → 选择项目文件夹
   - 等待同步完成

4. **构建 APK**
   - `Build` → `Build APK(s)`
   - 完成后在通知中查看 APK 位置

#### Linux 用户
1. **下载 Android Studio**
   ```bash
   # 访问：https://developer.android.com/studio
   # 下载 Linux 版本 (.tar.gz)
   ```

2. **解压和安装**
   ```bash
   tar -xzf android-studio-*.tar.gz
   sudo mv android-studio /opt/
   /opt/android-studio/bin/studio.sh
   ```

3. **后续步骤同 Windows**

### 预计时间
- 下载安装：15-30 分钟（首次）
- 构建时间：1-2 分钟
- **总计：约 20 分钟（首次），之后只需 2 分钟**

---

## ☁️ 方法二：GitHub Actions 云端构建（无需安装工具）

### 为什么选择这个方法？
✅ **无需安装** - 不需要在本地安装任何工具
✅ **自动化** - 推送代码后自动构建
✅ **云端** - 使用 GitHub 的服务器
✅ **免费** - GitHub 提供免费的构建时间

### 步骤（仅需3分钟）

#### 前提条件
- 您需要有一个 GitHub 账号（免费注册）
- 项目文件夹需要是 Git 仓库（或初始化为仓库）

#### 操作步骤

1. **初始化 Git 仓库**（如果还不是）
   ```bash
   cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary
   git init
   git add .
   git commit -m "Initial commit"
   ```

2. **推送到 GitHub**
   - 在 GitHub 创建新仓库（免费）
   - 添加远程仓库并推送：
   ```bash
   git remote add origin https://github.com/YOUR_USERNAME/death-diary.git
   git branch -M main
   git push -u origin main
   ```

3. **触发构建**
   - 在 GitHub 仓库页面
   - 点击 "Actions" 标签
   - 选择 "Build Android APK" 工作流
   - 点击 "Run workflow" → "Run workflow"

4. **等待构建**
   - 构建过程约 5-10 分钟
   - 您可以看到实时进度

5. **下载 APK**
   - 构建完成后，进入工作流详情页
   - 在 "Artifacts" 部分下载：
     - `app-debug` (Debug 版本)
     - `app-release` (Release 版本，未签名)

### 预计时间
- 首次设置：5 分钟
- 构建时间：5-10 分钟
- **总计：约 10 分钟**

---

## 💻 方法三：命令行构建（适合开发者）

### 为什么选择这个方法？
✅ **灵活** - 完全控制构建过程
✅ **脚本化** - 可以集成到 CI/CD
✅ **快速** - 环境配置好后，构建最快

### 环境要求
- Java JDK 17 或更高版本
- Android SDK
- Android Build Tools 34.0.0

### 步骤（约30分钟）

#### 1. 安装 Java JDK 17

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
java -version  # 验证安装
```

**macOS:**
```bash
brew install openjdk@17
# 设置环境变量
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

**Windows:**
1. 下载 JDK 17：https://adoptium.net/
2. 安装并设置环境变量 `JAVA_HOME`

#### 2. 安装 Android SDK

**方法 A：通过 Android Studio**（推荐）
- 安装 Android Studio（方法一）
- SDK 会自动安装到：
  - Windows: `C:\Users\YourName\AppData\Local\Android\Sdk`
  - macOS: `~/Library/Android/sdk`
  - Linux: `~/Android/Sdk`

**方法 B：命令行工具**
```bash
# 下载命令行工具
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip

# 解压并设置
unzip commandlinetools-*.zip
mkdir -p ~/Android/sdk/cmdline-tools
mv cmdline-tools ~/Android/sdk/cmdline-tools/latest

# 设置环境变量
export ANDROID_HOME=~/Android/sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools

# 接受许可证
yes | sdkmanager --licenses

# 安装必需组件
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

#### 3. 构建 APK

```bash
# 进入项目目录
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 赋予执行权限
chmod +x gradlew

# 运行构建脚本
./build-apk.sh

# 或手动构建
./gradlew assembleDebug

# APK 位置
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

### 预计时间
- 环境配置：20-30 分钟（首次）
- 构建时间：1-2 分钟
- **总计：约 30 分钟（首次），之后只需 2 分钟**

---

## 📊 方法对比

| 方法 | 难度 | 时间 | 推荐度 | 适合人群 |
|------|------|------|--------|----------|
| **Android Studio** | ⭐ 简单 | 20 分钟 | ⭐⭐⭐⭐⭐ | 所有人，特别是新手 |
| **GitHub Actions** | ⭐⭐ 中等 | 10 分钟 | ⭐⭐⭐⭐⭐ | 不想安装工具的人 |
| **命令行** | ⭐⭐⭐ 复杂 | 30 分钟 | ⭐⭐⭐ | 开发者 |

---

## 🎯 我的推荐

### 如果您是第一次构建
👉 **使用方法一（Android Studio）**
- 最简单
- 最可靠
- 一次性配置，以后都用

### 如果您不想安装工具
👉 **使用方法二（GitHub Actions）**
- 无需安装
- 云端构建
- 自动化

### 如果您是开发者
👉 **使用方法三（命令行）**
- 完全控制
- 可脚本化
- 适合 CI/CD

---

## 📱 安装 APK 到手机

### 方法一：USB 安装
```bash
# 启用手机 USB 调试后
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 方法二：直接安装
1. 将 APK 文件复制到手机
2. 在文件管理器中点击 APK
3. 允许"未知来源"安装
4. 点击"安装"

---

## ❓ 常见问题

**Q1: 构建失败怎么办？**
A: 查看 `BUILD_GUIDE.md` 和 `BUILD_ENVIRONMENT.md` 中的故障排除部分

**Q2: 需要多长时间？**
A: 首次20分钟，之后2分钟

**Q3: APK 有多大？**
A: 约 8-12 MB（Debug 版本）

**Q4: 可以直接发给别人吗？**
A: 可以，APK 文件可以直接分享和安装

**Q5: 需要签名吗？**
A: Debug 版本已自动签名，可以直接安装

---

## 📞 需要帮助？

### 详细文档
- `BUILD_GUIDE.md` - 详细构建步骤
- `BUILD_ENVIRONMENT.md` - 环境配置指南
- `QUICKREF.md` - 快速参考
- `BUILD_STATUS.md` - 当前状态

---

## 🎉 快速开始

### 最简单的方式（推荐）

1. **下载 Android Studio**
   ```
   https://developer.android.com/studio
   ```

2. **安装并打开项目**

3. **点击 "Build APK"**

4. **完成！**

---

**项目位置**: `/vol1/@apphome/trim.openclaw/data/workspace/death-diary`

**祝您构建顺利！** 🚀
