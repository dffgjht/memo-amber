<p align="center">
  <h1 align="center">🪵 记忆琥珀 · Memo Amber</h1>
  <p align="center"><strong>Secure, private digital legacy management app for Android & Desktop</strong></p>
  <p align="center">
    <a href="#english">
      <img src="https://img.shields.io/badge/Language-English-blue" alt="English" />
    </a>
    <a href="#中文">
      <img src="https://img.shields.io/badge/Language-中文-orange" alt="中文" />
    </a>
    <img src="https://img.shields.io/badge/version-v1.6.0-blue" alt="Version" />
    <img src="https://img.shields.io/badge/API-24%2B-green" alt="Android API" />
    <img src="https://img.shields.io/badge/license-MIT-orange" alt="License" />
    <img src="https://img.shields.io/badge/language-Kotlin-7F52FF" alt="Kotlin" />
    <img src="https://img.shields.io/badge/PRs-welcome-brightgreen" alt="PRs Welcome" />
  </p>
</p>

---

<a id="english"></a>
# 🇺🇸 English

## 📛 The Name: Why "Memo Amber"?

> **Amber** — nature's oldest memory keeper.
>
> Tens of millions of years ago, a drop of tree resin accidentally wrapped a leaf or a tiny insect, quietly solidifying over the ages and preserving that exact moment, perfectly intact, to this day.
>
> **Memo Amber** does exactly the same thing: it seals your diary, digital will, passwords, and precious photos inside your phone with AES-256 encryption and hardware-backed keys from the Android Keystore. No network, no upload, no leakage — protected forever, like life trapped in amber. 🪵✨

---

## ✨ Features

| Feature | Description |
|:--------|:------------|
| 🔐 **Biometric Authentication** | Fingerprint / face unlock, blocks unauthorized access |
| 🛡️ **AES-256 Encrypted Storage** | All sensitive data encrypted with AES-256-GCM, keys managed by Android Keystore |
| 📔 **Diary System** | Rich-text diary with mood / weather / tags / photos, automatic timestamps |
| ✏️ **Diary Editing** | Edit or delete existing entries anytime |
| 🔑 **Password Vault** | Securely store account credentials, one-tap copy |
| 📜 **Digital Will ("Unsent Letters")** | Write letters to loved ones, optionally scheduled for delivery via SMS / email |
| 👥 **Contact Profiles** | Profiles of important people — could serve as will contacts or quick recipients |
| 📸 **Encrypted Gallery** | Private album for treasured photos & memories |
| 💬 **Community Message Board** | Leave messages and farewells for family |
| 💾 **Backup / Restore** | Local encrypted backup export & restore, no data loss |
| ⏱️ **Auto-Lock** | Auto-locks 5 minutes after leaving the app |
| 🖥️ **Desktop Edition** | Cross-platform build with Compose for Desktop (Windows / macOS / Linux) |

## 📸 Screenshots

<p align="center">
  <img src="screenshot.png" alt="Memo Amber main screen" width="360" />
</p>

> 📌 More screenshots can be added under `docs/screenshots/`.

## 🛠️ Tech Stack

| Category | Technology |
|:---------|:-----------|
| Language | Kotlin 1.9.20 |
| UI Framework | Jetpack Compose (Material 3) |
| Local Database | Room Database |
| Encryption | AES-256-GCM |
| Key Management | Android Keystore |
| Authentication | Android Biometric API |
| Min SDK | Android 7.0 (API 24) |
| Target SDK | Android 14 (API 34) |

## 🔒 Security First

Memo Amber treats security as the top priority from the ground up:

- **End-to-end encryption** — diaries, passwords, wills and other sensitive data are encrypted with AES-256-GCM
- **Hardware-backed keys** — encryption keys live in the Android Keystore, protected by TEE / StrongBox, never exportable
- **Biometric gate** — launching the app requires fingerprint or face verification
- **Auto-lock** — locks after 5 minutes of inactivity, re-authentication required on return
- **Zero network dependency** — core data is stored fully locally, nothing leaves your device

## 🚀 Quick Start

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android SDK including API 34

### From Android Studio

1. **Clone the repo**
   ```bash
   git clone https://github.com/dffgjht/memo-amber.git
   ```
2. Open the project directory in Android Studio
3. Wait for Gradle sync to finish
4. Connect a device or start an emulator (API 24+)
5. Hit **Run** ▶️

### From the command line (Android)

```bash
git clone https://github.com/dffgjht/memo-amber.git
cd memo-amber

# Debug build (use gradlew.bat on Windows)
./gradlew assembleDebug

# Install to a connected device
./gradlew installDebug
```

### Desktop build

The desktop edition is built with Kotlin + Jetpack Compose for Desktop (Material3) and runs on Windows / macOS / Linux.

```bash
# Build a fat jar (bundles all dependencies)
./gradlew :desktop:fatJar

# Output: desktop/build/libs/desktop-1.5.0-all.jar
```

> ⚠️ **Signature note**: the `fatJar` task excludes signature files (`.SF`/`.DSA`/`.RSA`/`.EC`) of libraries such as BouncyCastle to avoid `SecurityException: Invalid signature file digest`. If you package a fat jar manually, make sure to exclude those files too.

## 📁 Project Structure

```
├── app/                        # Android app
│   └── src/main/
│       ├── java/com/memoamber/
│       │   ├── data/           # Data layer — Room database, DAOs, entities
│       │   ├── network/        # Network layer — Retrofit client & API (community feature)
│       │   ├── security/       # Encryption, Keystore, biometrics
│       │   ├── ui/
│       │   │   ├── screens/    # Compose screens (diary/vault/will/gallery/community/settings)
│       │   │   ├── theme/      # Material 3 theme
│       │   │   └── viewmodels/ # ViewModel layer
│       │   └── utils/          # Utilities (backup/restore, media files)
│       └── res/                # Resources
├── desktop/                    # Desktop edition (Compose for Desktop, Kotlin JVM)
│   └── src/main/kotlin/com/memoamber/desktop/
│       ├── data/               # SQLite + Argon2id encrypted storage
│       └── ui/                 # Desktop Compose UI
├── build.gradle.kts
├── gradlew / gradlew.bat       # Gradle Wrapper (Windows / Unix)
└── CHANGELOG.md
```

## 🗺️ Roadmap — Help Shape the Future

Ideas we'd love community help with:

- [ ] Localization (i18n) — English / 中文 / more languages
- [ ] Cloud backup with end-to-end encryption (your key, your data)
- [ ] Auto-delivery of "Unsent Letters" via scheduled SMS / email
- [ ] Widgets & quick capture for diary
- [ ] More vault categories (bank cards, identity documents, notes)
- [ ] Community board moderation & anti-spam
- [ ] Unit / UI test coverage

Have a feature in mind? Open an [issue](https://github.com/dffgjht/memo-amber/issues) or submit a PR!

## 🤝 Contributing

Contributions of all kinds are welcome — code, docs, translations, design, bug reports, and feature ideas!

1. **Fork** the repo
2. Create your feature branch: `git checkout -b feat/your-feature`
3. Commit your changes: `git commit -am 'feat: add something awesome'`
4. Push to the branch: `git push origin feat/your-feature`
5. Open a **Pull Request** 🚀

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details. Every contributor counts — let's preserve memories together.

## 📋 Version History

| Version | Date | Notes |
|:--------|:-----|:------|
| [v1.6.0](https://github.com/dffgjht/memo-amber/releases/tag/v1.6.0) | 2026-08-12 | Apple-inspired UI refresh, contacts profiles, diary editing & swipe-to-delete |
| [v1.5.0](https://github.com/dffgjht/memo-amber/releases/tag/v1.5.0) | 2026-08-10 | Diary DB integration, PBKDF2 password hashing, unified package `com.memoamber` |
| [v1.4.0](https://github.com/dffgjht/memo-amber/releases/tag/v1.4.0) | 2026-04-30 | Fixed desktop packaging signature conflicts |
| [v1.3.0](https://github.com/dffgjht/memo-amber/releases/tag/v1.3.0) | — | First public release |

> Full changelog on the [Releases](https://github.com/dffgjht/memo-amber/releases) page.

## 📄 License

This project is open-sourced under the [MIT License](LICENSE).

---
---

<a id="中文"></a>
# 🇨🇳 中文

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
| 📔 **日记系统** | 富文本日记，支持心情 / 天气 / 标签与照片，自动记录时间戳 |
| ✏️ **日记编辑** | 随时修改或删除已有日记 |
| 🔑 **密码保险箱** | 安全存储账号密码，一键复制 |
| 📜 **未寄出的信（数字遗嘱）** | 写给重要之人的信，支持通过短信 / 邮件定时送达 |
| 👥 **关系人档案** | 重要人物档案，可作遗言联络人或快速发送对象 |
| 📸 **回忆相册** | 加密相册，保存珍贵照片与回忆 |
| 💬 **社区留言板** | 为亲人留下寄语与留言 |
| 💾 **数据备份 / 恢复** | 本地备份导出与恢复，数据不丢失 |
| ⏱️ **自动锁定** | 离开应用 5 分钟自动锁定，防止泄露 |
| 🖥️ **桌面端** | 基于 Compose for Desktop 的跨平台版本（Windows / macOS / Linux） |

## 📸 应用截图

<p align="center">
  <img src="screenshot.png" alt="记忆琥珀主界面" width="360" />
</p>

> 📌 更多截图可放置于 `docs/screenshots/` 目录后补充。

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

### 命令行（Android）

```bash
# 克隆仓库
git clone https://github.com/dffgjht/memo-amber.git
cd memo-amber

# Debug 构建（Windows 请使用 gradlew.bat）
./gradlew assembleDebug

# 安装到连接的设备
./gradlew installDebug
```

### 桌面端构建

桌面端基于 Kotlin + Jetpack Compose for Desktop（Material3），支持 Windows / macOS / Linux。

```bash
# 构建 fat jar（包含所有依赖）
./gradlew :desktop:fatJar

# 输出位置：desktop/build/libs/desktop-1.5.0-all.jar
```

> ⚠️ **fat jar 签名说明**：`fatJar` 任务已配置排除 BouncyCastle 等库的签名文件（`.SF`/`.DSA`/`.RSA`/`.EC`），避免 `SecurityException: Invalid signature file digest` 错误。如果手动打包 fat jar，请确保排除这些文件。

## 📁 项目结构

```
├── app/                        # Android 端
│   └── src/main/
│       ├── java/com/memoamber/
│       │   ├── data/           # 数据层 — Room 数据库、DAO、实体
│       │   ├── network/        # 网络层 — Retrofit 客户端与 API（社区功能）
│       │   ├── security/       # 加密、Keystore、生物识别
│       │   ├── ui/
│       │   │   ├── screens/    # Compose 页面（日记/保险箱/遗嘱/相册/社区/设置）
│       │   │   ├── theme/      # Material3 主题
│       │   │   └── viewmodels/ # ViewModel 层
│       │   └── utils/          # 工具类（备份恢复、媒体文件）
│       └── res/                # 资源文件
├── desktop/                    # 桌面端（Compose for Desktop，Kotlin JVM）
│   └── src/main/kotlin/com/memoamber/desktop/
│       ├── data/               # SQLite + Argon2id 加密存储
│       └── ui/                 # Compose 桌面端 UI
├── build.gradle.kts
├── gradlew / gradlew.bat       # Gradle Wrapper（Windows / Unix）
└── CHANGELOG.md
```

## 🗺️ 路线图 — 一起塑造未来

期待社区一起实现的方向：

- [ ] 国际化（i18n）— 英文 / 中文 / 更多语言
- [ ] 端到端加密的云备份（密钥只属于你）
- [ ] "未寄出的信"定时自动送达（短信 / 邮件）
- [ ] 日记小组件与快速记录
- [ ] 更多保险箱分类（银行卡、证件、笔记）
- [ ] 社区留言板审核与反垃圾
- [ ] 单元测试 / UI 测试覆盖

有想法？开 [Issue](https://github.com/dffgjht/memo-amber/issues) 或直接提 PR！

## 🤝 贡献指南

欢迎任何形式的贡献 —— 代码、文档、翻译、设计、Bug 报告与功能建议！

1. **Fork** 仓库
2. 创建功能分支：`git checkout -b feat/your-feature`
3. 提交改动：`git commit -am 'feat: add something awesome'`
4. 推送分支：`git push origin feat/your-feature`
5. 发起 **Pull Request** 🚀

详见 [CONTRIBUTING.md](CONTRIBUTING.md)。每一位贡献者都重要 —— 一起守护记忆。

## 📋 版本历史

| 版本 | 日期 | 说明 |
|:-----|:-----|:-----|
| [v1.6.0](https://github.com/dffgjht/memo-amber/releases/tag/v1.6.0) | 2026-08-12 | Apple 质感 UI 重构、关系人档案、日记编辑与左滑删除 |
| [v1.5.0](https://github.com/dffgjht/memo-amber/releases/tag/v1.5.0) | 2026-08-10 | 日记接入数据库、密码哈希升级 PBKDF2、包名统一为 com.memoamber |
| [v1.4.0](https://github.com/dffgjht/memo-amber/releases/tag/v1.4.0) | 2026-04-30 | 修复桌面端打包签名冲突 |
| [v1.3.0](https://github.com/dffgjht/memo-amber/releases/tag/v1.3.0) | — | 首个公开发布版本 |

> 完整版本记录见 [Releases](https://github.com/dffgjht/memo-amber/releases)。

## 📄 许可证

本项目基于 [MIT License](LICENSE) 开源。
