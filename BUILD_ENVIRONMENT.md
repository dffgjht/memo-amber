# 🔧 构建环境配置指南

## 📋 当前环境问题

检测到您的系统缺少以下必需组件：

### ❌ 缺失组件
- **Java JDK** - 未安装
- **Android SDK** - 未安装
- **Gradle** - 需要通过 Gradle Wrapper 自动下载

---

## 🚀 快速安装指南

### 方法一：使用 Android Studio (推荐，最简单)

#### Windows
1. **下载 Android Studio**
   - 访问: https://developer.android.com/studio
   - 下载 Windows 版本 (约 1GB)

2. **安装**
   - 运行安装程序
   - 使用默认设置安装
   - 安装完成后会自动下载 Android SDK

3. **构建 APK**
   - 启动 Android Studio
   - 选择 "Open" → 选择项目文件夹
   - 等待 Gradle 同步完成
   - 菜单: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`

#### macOS
1. **下载 Android Studio**
   ```bash
   # 使用 Homebrew 安装
   brew install --cask android-studio
   ```

2. **安装和配置**
   - 启动 Android Studio
   - 首次启动会引导安装 Android SDK
   - 接受所有默认选项

3. **构建**
   - 打开项目后等待同步
   - `Build` → `Build APK(s)`

#### Linux (Ubuntu/Debian)
1. **安装 Java JDK**
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk
   ```

2. **下载 Android Studio**
   - 访问: https://developer.android.com/studio
   - 下载 Linux 版本

3. **解压并安装**
   ```bash
   tar -xzf android-studio-*.tar.gz
   sudo mv android-studio /opt/
   /opt/android-studio/bin/studio.sh
   ```

4. **构建**
   - 在 Android Studio 中打开项目
   - 等待同步后构建 APK

---

### 方法二：命令行构建 (需要手动配置)

#### 1. 安装 Java JDK 17

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk

# 验证安装
java -version
```

**macOS:**
```bash
# 使用 Homebrew
brew install openjdk@17

# 设置环境变量（添加到 ~/.zshrc 或 ~/.bash_profile）
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

**Windows:**
1. 下载 JDK 17: https://adoptium.net/
2. 运行安装程序
3. 设置环境变量:
   - 系统属性 → 高级 → 环境变量
   - 新建 `JAVA_HOME`，值为 JDK 安装路径
   - 编辑 `Path`，添加 `%JAVA_HOME%\bin`

#### 2. 安装 Android SDK

**方法 A: 使用命令行工具 (推荐)**

**Linux/macOS:**
```bash
# 下载命令行工具
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
# 或 macOS:
# wget https://dl.google.com/android/repository/commandlinetools-mac-9477386_latest.zip

# 解压
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

**Windows:**
```powershell
# 下载并解压命令行工具
# 下载地址: https://developer.android.com/studio#command-tools

# 设置环境变量（ PowerShell）
[System.Environment]::SetEnvironmentVariable('ANDROID_HOME', 'C:\Android\sdk', 'User')
[System.Environment]::SetEnvironmentVariable('Path', $env:Path + ';C:\Android\sdk\cmdline-tools\latest\bin;C:\Android\sdk\platform-tools', 'User')

# 接受许可证
sdkmanager --licenses

# 安装组件
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

**方法 B: 使用 Android Studio**

安装 Android Studio 后，SDK 会自动安装到:
- Windows: `C:\Users\YourName\AppData\Local\Android\Sdk`
- macOS: `~/Library/Android/sdk`
- Linux: `~/Android/Sdk`

#### 3. 设置环境变量

**Linux/macOS (添加到 ~/.bashrc 或 ~/.zshrc):**
```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools

# 重新加载配置
source ~/.bashrc  # 或 source ~/.zshrc
```

**Windows (系统环境变量):**
```
ANDROID_HOME=C:\Users\YourName\AppData\Local\Android\Sdk
Path=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\emulator
```

#### 4. 验证安装

```bash
# 检查 Java
java -version

# 检查 Android SDK
echo $ANDROID_HOME  # Linux/macOS
echo %ANDROID_HOME% # Windows

# 检查 ADB
adb version
```

---

## 🏗️ 构建步骤

### Linux/macOS

```bash
# 进入项目目录
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 赋予执行权限
chmod +x gradlew
chmod +x build-apk.sh

# 运行构建脚本
./build-apk.sh

# 或手动构建
./gradlew assembleDebug
```

### Windows

```cmd
# 进入项目目录
cd C:\path\to\death-diary

# 运行构建脚本
build-apk.bat

# 或手动构建
gradlew.bat assembleDebug
```

---

## 📦 APK 位置

构建成功后，APK 文件位于:

```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔧 故障排除

### 问题 1: Java 版本不兼容

**错误:** `Unsupported class file major version`

**解决:**
```bash
# 确保使用 Java 17
java -version

# 如果版本不对，切换到 Java 17
sudo update-alternatives --config java  # Linux
```

### 问题 2: Gradle 下载慢

**解决:** 配置国内镜像源

编辑 `gradle.properties`:
```properties
systemProp.https.proxyHost=maven.aliyun.com
systemProp.https.proxyPort=443
```

或在 `build.gradle.kts` 中添加:
```kotlin
repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    google()
    mavenCentral()
}
```

### 问题 3: Android SDK 找不到

**错误:** `SDK location not found`

**解决:**
```bash
# 设置 ANDROID_HOME
export ANDROID_HOME=/path/to/Android/Sdk

# 或在 local.properties 中指定
echo "sdk.dir=/path/to/Android/Sdk" > local.properties
```

### 问题 4: 权限错误 (Linux/macOS)

**解决:**
```bash
chmod +x gradlew
./gradlew assembleDebug
```

---

## 📱 安装到设备

### 通过 USB

```bash
# 启用 USB 调试后
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 无线安装

1. 将 APK 传输到手机
2. 在手机上打开文件管理器
3. 找到 APK 文件并点击安装

---

## 🎯 推荐方案

**如果您不想配置复杂的环境:**

1. ✅ **使用 Android Studio** (最简单)
   - 一站式安装，无需手动配置
   - 可视化界面，易于使用
   - 自动处理依赖下载

2. ✅ **使用 GitHub Actions** (无需本地环境)
   - 在云端自动构建
   - 自动下载生成的 APK

3. ✅ **使用在线构建服务**
   - GitHub Actions
   - GitLab CI/CD
   - Bitrise

---

## 📞 需要帮助？

如果遇到问题:

1. 查看 `BUILD_GUIDE.md` - 详细构建说明
2. 查看 `QUICKREF.md` - 快速参考
3. 检查构建日志 (`build.log`)

---

**当前环境:**
- 系统: Linux (Docker 容器)
- Java: 未安装 ❌
- Android SDK: 未安装 ❌
- 建议: 使用 Android Studio 在本地构建

---

**下一步:**
1. 安装 Android Studio
2. 打开项目
3. 点击 "Build APK"
4. 等待完成
