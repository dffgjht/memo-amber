# 死亡日记 (Death Diary) - 构建说明

## 项目简介
这是一个数字遗产管理安卓应用，用于记录和保存生前的重要数据、记忆、遗言等。

## 核心功能
1. **日记记录** - 带心情标签的日记系统
2. **密码保险箱** - 加密保存账号密码和重要信息
3. **遗嘱/遗言** - 数字遗嘱功能，可设定发布条件
4. **回忆相册** - 珍贵照片和视频管理
5. **生物识别** - 指纹/面容解锁
6. **AES-256加密** - 所有敏感数据加密存储

## 技术栈
- Kotlin + Jetpack Compose (现代化UI框架)
- Room Database (本地数据库)
- Android Keystore (硬件级加密)
- BiometricPrompt (生物识别)
- Material Design 3 (UI设计)
- Coroutines + Flow (异步处理)

## 构建要求

### 系统要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17 或更高版本
- Android SDK API 34
- Gradle 8.2

### 环境配置
```bash
# 确保已安装 Android SDK
# 设置 ANDROID_HOME 环境变量
export ANDROID_HOME=/path/to/android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

## 构建步骤

### 方法一：使用 Android Studio (推荐)

1. **打开项目**
   ```bash
   # 在 Android Studio 中选择 "Open"
   # 导航到并选择 death-diary 文件夹
   ```

2. **同步 Gradle**
   - Android Studio 会自动提示同步 Gradle
   - 点击 "Sync Now" 等待依赖下载完成

3. **构建 APK**
   - 选择菜单: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
   - 等待构建完成
   - 点击通知中的 "locate" 查找生成的 APK
   - APK 位置: `app/build/outputs/apk/debug/app-debug.apk`

4. **构建发布版 APK**
   - 选择菜单: `Build` → `Generate Signed Bundle / APK`
   - 选择 "APK"
   - 创建或选择密钥库 (keystore)
   - 选择 "release" 构建变体
   - 发布版 APK 位置: `app/build/outputs/apk/release/app-release.apk`

### 方法二：使用命令行

1. **构建 Debug APK**
   ```bash
   cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary
   ./gradlew assembleDebug
   ```

   生成的 APK: `app/build/outputs/apk/debug/app-debug.apk`

2. **构建 Release APK** (需要签名配置)
   ```bash
   ./gradlew assembleRelease
   ```

   生成的 APK: `app/build/outputs/apk/release/app-release.apk`

3. **安装到设备**
   ```bash
   # 通过 USB 安装
   adb install app/build/outputs/apk/debug/app-debug.apk

   # 或卸载后重新安装
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

## 首次运行

1. **设置主密码**
   - 应用首次启动会要求设置主密码
   - 密码至少 6 个字符
   - 请妥善保管，忘记密码无法恢复数据

2. **生物识别**
   - 如果设备支持，可以启用指纹/面容解锁
   - 生物识别作为主密码的补充

3. **开始使用**
   - 日记：记录日常想法和回忆
   - 保险箱：安全保存账号密码
   - 遗嘱：创建遗言和最后的信息
   - 相册：珍藏珍贵照片视频

## 安全特性

### 加密存储
- 使用 Android Keystore 系统
- AES-256-GCM 加密算法
- 密钥存储在硬件支持的 TEE (可信执行环境)
- 主密码使用哈希存储

### 隐私保护
- 所有数据仅保存在本地设备
- 无网络请求，无数据上传
- 自动锁定机制
- 备份文件完全加密

## 数据备份

### 创建备份
- 在"设置"中选择"备份数据"
- 备份文件会保存到设备的 Download 文件夹
- 备份文件使用 AES 加密

### 恢复数据
- 在"设置"中选择"恢复数据"
- 选择之前创建的备份文件
- 输入主密码解密

## 注意事项

⚠️ **重要提示**:
1. **主密码无法找回** - 请务必记住主密码，忘记密码将导致数据永久丢失
2. **数据清除** - 卸载应用会删除所有数据，请先备份
3. **备份安全** - 备份文件包含加密数据，请妥善保管
4. **测试设备** - 首次使用建议在测试设备上体验

## 故障排除

### 构建失败
```bash
# 清理构建缓存
./gradlew clean

# 重新构建
./gradlew assembleDebug
```

### 依赖下载慢
- 在 `build.gradle.kts` 中添加国内镜像源
- 或使用 VPN 加速

### 设备不兼容
- 最低要求 Android 7.0 (API 24)
- 建议使用 Android 10+ 以获得最佳体验

## 开发路线图

### 已完成
- ✅ 基础 UI 框架
- ✅ 安全认证系统
- ✅ 数据库设计
- ✅ 各功能模块界面

### 待实现
- ⏳ 实际数据库集成 (替换 Mock 数据)
- ⏳ 相机拍照功能
- ⏳ 文件导入导出
- ⏳ 定时发布功能
- ⏳ 云端备份选项
- ⏳ 多语言支持

## 开源协议
MIT License

## 联系方式
如有问题或建议，请通过 GitHub Issues 反馈。
