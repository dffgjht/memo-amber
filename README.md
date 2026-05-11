<<<<<<< HEAD
# 📔 Death Diary - 数字化死亡日记

一款专注于个人反思、情感表达和生命记录的私密应用。记录你的日常、珍藏回忆、编写遗嘱，让每一段时光都有迹可循。

## ✨ 核心功能

- **📔 日记模块** - 轻松记录生活点滴，支持位置标记、照片预览
- **🔐 密码保险箱** - 安全存储重要信息，加密保护隐私
- **📜 遗嘱遗言** - 提前规划身后事宜，自动发送重要信息
- **📷 回忆相册** - 珍藏美好时刻，智能分类管理
- **⚙️ 设置优化** - 移除不稳定的生物识别功能

## 🚀 特性亮点

- **隐私保护** - 所有数据本地存储，端到端加密
- **离线可用** - 无网络连接也能正常使用
- **多平台支持** - Android 8.0+ 完美运行
- **智能提醒** - 自动发送遗嘱信息给指定联系人

## 📱 版本说明

当前版本：**v1.2.5**  
更新日志：
- 修复日记照片预览问题
- 修复密码保险箱保存闪退
- 添加遗嘱自动发送功能
- 优化相册持久化存储
- 移除不稳定的生物识别

## 🛠️ 技术架构

- **前端框架**: Kotlin + Android Compose
- **数据库**: Room Database
- **安全加密**: AES-256
- **推送服务**: Firebase Cloud Messaging

## 📦 安装说明

1. 下载最新 APK 安装包
2. 启用 USB 调试模式（如需从电脑安装）
3. 在安装页面选择"允许"
4. 首次启动需授予存储权限

> ⚠️ 安全提示：请谨慎共享您的账户信息，应用支持本地加密备份功能

## 🔄 版本迁移指南

从 v1.2.0 升级时：
- 所有日记、遗嘱数据自动保留
- 保险箱密码无需重新设置
- 相册内容完整无损

## 📄 许可协议

Copyright © 2024 Death Diary. All rights reserved.
=======
# 存证纪 (Cunzhengji) - 安卓应用

一个安全、私密的数字遗产管理安卓应用，帮助您记录珍贵的回忆、保存重要的数字资产、制定

## 📥 下载安装

**最新版本：** [v1.2.4 回忆录](https://github.com/dffgjht/death-diary/releases/tag/v1.2.4)

点击上方链接下载 APK，直接安装即可使用。

## ✨ 功能特性

### 核心功能
- 🔐 **生物识别认证** - 指纹/面容快速解锁
- 🔐 **AES-256 加密存储** - 军事级数据保护
- 📔 **日记系统** - 记录每一天的心情与感悟（含 GPS 位置）
- 🔑 **密码保险箱** - 安全保管重要账号密码
- 📜 **数字遗嘱** - 规划身后事的数字遗产
- 📸 **回忆相册** - 保存珍贵的照片和视频
- 💬 **社区留言板** - 与亲友分享心声

### 更多特性
- 🎨 Material Design 3 美观界面
- 📱 纯本地存储，数据完全由您掌控
- 🔒 隐私优先，无强制联网

## 🚀 快速开始

### 使用 Android Studio 构建

1. 打开 Android Studio
2. File → Open → 选择项目文件夹
3. 等待 Gradle 同步完成（首次 5-10 分钟）
4. Build → Build Bundle(s) / APK(s) → Build APK(s)
5. 在 `app/build/outputs/apk/debug/app-debug.apk` 获取 APK

详细步骤：[CONTRIBUTING.md](CONTRIBUTING.md)

### 使用命令行构建

```bash
./gradlew assembleDebug
```

## 📱 系统要求

- Android 7.0 (API 24) 或更高版本
- 推荐使用 Android 10+ 以获得最佳体验

## 🔧 技术栈

| 技术 | 说明 |
|------|------|
| Kotlin 1.9.20 | 编程语言 |
| Jetpack Compose | 现代 UI 框架 |
| Room Database | 本地数据库 |
| Material Design 3 | 设计语言 |
| AES-256 加密 | 数据加密 |
| BCrypt | 密码哈希 |
| 生物识别 API | 身份认证 |

## 📁 项目结构

```
回忆录/
├── app/
│   ├── src/main/
│   │   ├── java/com/deathdiary/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/           # 数据层
│   │   │   ├── security/       # 安全加密
│   │   │   └── ui/screens/     # 界面组件
│   │   └── res/                # 资源文件
│   └── build.gradle.kts
├── gradle/wrapper/             # Gradle 包装器
├── .github/workflows/          # CI/CD 流程
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

## 🔒 安全特性

- **AES-256-GCM 加密** - 军事级数据保护
- **BCrypt 密码哈希** - 防彩虹表攻击，自适应成本因子 (cost=12)
- **Android Keystore** - 硬件级密钥存储
- **生物识别认证** - 指纹/面容解锁
- **Room 数据库迁移** - 安全的数据版本升级
- **完全离线** - 数据仅存储在您的设备

## 📄 许可证

MIT License

## 📌 版本历史

| 版本 | 更新内容 |
|------|----------|
| [v1.2.4](https://github.com/dffgjht/death-diary/releases/tag/v1.2.4) | 修复数据持久化、备份对话框、版本号显示、生物识别优先指纹、相册图片预览、应用图标更新 |
| [v1.2.3](https://github.com/dffgjht/death-diary/releases/tag/v1.2.3) | 新增备份/恢复功能、自动锁定机制、文档精简 |
| [v1.2.2](https://github.com/dffgjht/death-diary/releases/tag/v1.2.2) | 安全增强: BCrypt密码哈希、数据库迁移策略、CI/CD完善 |
| [v1.2.1](https://github.com/dffgjht/death-diary/releases/tag/v1.2.1) | 修复后台重登闪退、编辑删除功能、GPS定位、隐私政策 |
| [v1.2](https://github.com/dffgjht/death-diary/releases/tag/v1.2) | 修复闪退问题，优化 UI，精确日期到时分秒，本地相册 |
| [v1.1](https://github.com/dffgjht/death-diary/releases/tag/v1.1) | 新增社区留言板功能 |
| v1.0 | 初始版本发布 |

---

**开发者**: dffgjht  
**项目地址**: https://github.com/dffgjht/death-diary  
**最后更新**: 2026-04-16
>>>>>>> 1a7d53639726a97e98f95a9d105d210b3b6457fd
