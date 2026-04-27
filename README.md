<p align="center">
  <h1 align="center">🪵 记忆琥珀</h1>
  <p align="center"><strong>安全、私密的数字遗产管理安卓应用</strong></p>
  <p align="center">
    <img src="https://img.shields.io/badge/version-v1.3.0-blue" alt="Version" />
    <img src="https://img.shields.io/badge/API-24%2B-green" alt="Android API" />
    <img src="https://img.shields.io/badge/license-MIT-orange" alt="License" />
    <img src="https://img.shields.io/badge/language-Kotlin-7F52FF" alt="Kotlin" />
  </p>
</p>

---

## 📛 命名寓意

> **琥珀**（Amber）—— 大自然最古老的记忆守护者。
>
> 数千万年前，一滴树脂不经意间包裹住一片落叶、一只振翅的昆虫，在漫长岁月中悄然凝固，将那个瞬间完好无损地封存至今。
>
> **记忆琥珀** 做的正是同样的事：用 AES-256 加密和 Android Keystore 硬件级密钥保护，将你的日记、遗嘱、密码和珍贵照片安全地封存在手机里。不联网，不上传，不泄露 —— 就像琥珀中的生命，被永久而私密地守护着。
>
> 每一条记录，都是凝固在时间里的琥珀。🪵✨

---

## ✨ 功能特性

| 功能 | 说明 |
|:-----|:-----|
| 🔐 **生物识别认证** | 指纹 / 面容解锁，拒绝未授权访问 |
| 🛡️ **AES-256 加密存储** | 全部敏感数据使用 AES-256-GCM 加密，密钥由 Android Keystore 管理 |
| 📔 **日记系统** | 富文本日记，自动记录 GPS 位置与时间戳 |
| 🔑 **密码保险箱** | 安全存储账号密码，一键复制 |
| 📜 **数字遗嘱** | 编写遗嘱，支持通过短信 / 邮件定时发送 |
| 📸 **回忆相册** | 加密相册，保存珍贵照片与回忆 |
| 💬 **社区留言板** | 为亲人留下寄语与留言 |
| 💾 **数据备份 / 恢复** | 本地备份导出与恢复，数据不丢失 |
| ⏱️ **自动锁定** | 5 分钟无操作自动锁定，防止泄露 |

## 📸 应用截图

<p align="center">
<kbd>
<br/>
&nbsp;&nbsp;&nbsp;&nbsp;┌─────────────────┐&nbsp;&nbsp;&nbsp;&nbsp;┌─────────────────┐&nbsp;&nbsp;&nbsp;&nbsp;┌─────────────────┐<br/>
&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;🔐 生物识别解锁&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;📔 日记列表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;📜 数字遗嘱&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│<br/>
&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│<br/>
&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;*(截图占位)*&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;*(截图占位)*&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;*(截图占位)*&nbsp;&nbsp;&nbsp;&nbsp;│<br/>
&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;│<br/>
&nbsp;&nbsp;&nbsp;&nbsp;└─────────────────┘&nbsp;&nbsp;&nbsp;&nbsp;└─────────────────┘&nbsp;&nbsp;&nbsp;&nbsp;└─────────────────┘<br/>
<br/>
</kbd>
</p>

> 📌 将实际截图放置于 `docs/screenshots/` 目录，替换上述占位区域。

## 🛠️ 技术栈

| 类别 | 技术 |
|:-----|:-----|
| 语言 | Kotlin 1.9.20 |
| UI 框架 | Jetpack Compose |
| 设计规范 | Material Design 3 |
| 本地数据库 | Room Database |
| 加密算法 | AES-256-GCM |
| 密钥管理 | Android Keystore |
| 身份认证 | Android Biometric API |
| 最低版本 | Android 7.0 (API 24) |
| 目标版本 | Android 14 (API 34) |

## 🔒 安全特性

记忆琥珀从底层架构就将安全作为第一优先级：

- **端到端加密** — 所有日记、密码、遗嘱等敏感数据使用 AES-256-GCM 对称加密
- **硬件级密钥保护** — 加密密钥存储于 Android Keystore，由 TEE / StrongBox 保护，不可导出
- **生物识别门禁** — 启动应用必须通过指纹或面容验证，充分利用设备硬件安全能力
- **自动锁定机制** — 5 分钟无操作自动锁定，返回需重新认证
- **零网络依赖** — 核心数据完全本地存储，不联网，不泄露

## 🚀 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17+
- Android SDK，包含 API 34

### Android Studio

1. **Clone 项目**
   ```bash
   git clone https://github.com/dffgjht/memo-amber.git
   ```
2. 用 Android Studio 打开项目目录
3. 等待 Gradle Sync 完成
4. 连接设备或启动模拟器（API 24+）
5. 点击 **Run** ▶️

### 命令行

```bash
# 克隆仓库
git clone https://github.com/dffgjht/memo-amber.git
cd memo-amber

# Debug 构建
./gradlew assembleDebug

# 安装到连接的设备
./gradlew installDebug
```

## 📁 项目结构

```
app/
├── src/main/
│   ├── java/com/memoamber/
│   │   ├── data/              # 数据层 — Room 数据库、DAO、实体
│   │   ├── di/                # 依赖注入模块
│   │   ├── ui/                # Compose UI — 页面与组件
│   │   ├── viewmodel/         # ViewModel 层
│   │   ├── security/          # 加密、Keystore、生物识别
│   │   └── util/              # 工具类与扩展函数
│   └── res/                   # 资源文件
├── build.gradle.kts
└── proguard-rules.pro
```

## 📋 版本历史

| 版本 | 日期 | 说明 |
|:-----|:-----|:-----|
| [v1.3.0](https://github.com/dffgjht/memo-amber/releases/tag/v1.3.0) | — | 首个公开发布版本 |

> 完整版本记录见 [Releases](https://github.com/dffgjht/memo-amber/releases)。

## 🤝 贡献

欢迎贡献！请阅读 [CONTRIBUTING.md](CONTRIBUTING.md) 了解详情。

## 📄 许可证

本项目基于 [MIT License](LICENSE) 开源。
