# 📱 死亡日记 - 快速参考卡片

## 🚀 一键构建

```bash
# 进入项目目录
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 赋予执行权限
chmod +x gradlew

# 构建 APK
./gradlew assembleDebug

# APK 位置
app/build/outputs/apk/debug/app-debug.apk
```

## 📂 核心文件速查

### 🎯 主要功能文件
| 文件 | 功能 | 行数估算 |
|------|------|---------|
| `MainActivity.kt` | 主入口 + 导航 | ~60 |
| `LockScreen.kt` | 锁定/认证界面 | ~200 |
| `HomeScreen.kt` | 主页导航 | ~150 |
| `DiaryScreen.kt` | 日记功能 | ~280 |
| `VaultScreen.kt` | 密码保险箱 | ~320 |
| `WillScreen.kt` | 遗嘱管理 | ~400 |
| `GalleryScreen.kt` | 回忆相册 | ~220 |
| `SettingsScreen.kt` | 设置界面 | ~280 |

### 🔐 安全模块
| 文件 | 功能 |
|------|------|
| `SecurityManager.kt` | AES-256加密 + 密码管理 |
| `BiometricAuthManager.kt` | 指纹/面容认证 |

### 💾 数据层
| 文件 | 功能 |
|------|------|
| `Database.kt` | Room数据库配置 |
| `entities/DiaryEntry.kt` | 日记数据实体 |
| `entities/VaultItem.kt` | 保险箱数据实体 |
| `entities/Will.kt` | 遗嘱数据实体 |
| `entities/MediaItem.kt` | 媒体数据实体 |

### 🎨 UI主题
| 文件 | 功能 |
|------|------|
| `Theme.kt` | Material Design 3 主题 |
| `Type.kt` | 字体排版配置 |

## 🎯 功能模块对应文件

```
用户流程          对应文件            主要功能
────────────────────────────────────────────
启动应用      →  MainActivity.kt    →  初始化、导航
                    LockScreen.kt    →  主密码/生物识别

主页导航      →  HomeScreen.kt     →  功能入口

写日记        →  DiaryScreen.kt     →  创建、查看、心情标签

密码保险箱    →  VaultScreen.kt     →  账号密码管理

遗嘱          →  WillScreen.kt      →  创建、设定发布条件

回忆相册      →  GalleryScreen.kt   →  照片视频管理

设置          →  SettingsScreen.kt  →  安全、备份、系统设置
```

## 📱 主要界面组件

### LockScreen (锁定界面)
- 主密码输入
- 密码确认 (首次)
- 生物识别按钮
- 错误提示

### HomeScreen (主页)
- 欢迎卡片
- 功能网格 (5个功能)
- Material Design 3 风格

### DiaryScreen (日记)
- 日记列表 (时间倒序)
- 心情标签 (5种)
- 新建/编辑对话框
- 空状态提示

### VaultScreen (保险箱)
- 搜索框
- 分类卡片
- 密码字段 (隐藏显示)
- 多字段支持

### WillScreen (遗嘱)
- 警告提示卡片
- 收件人设置
- 发布条件 (日期/手动)
- 已发布状态

### GalleryScreen (相册)
- 图片预览
- 标题和描述
- 时间线显示
- 类型图标

### SettingsScreen (设置)
- 生物识别开关
- 自动锁定开关
- 数据备份/恢复
- 清除数据选项

## 🔐 安全特性实现

### 加密流程
```
用户输入主密码
    ↓
SecurityManager.hashPassword() → 哈希存储
    ↓
用户数据 → SecurityManager.encryptData() → AES-256-GCM 加密
    ↓
加密数据存储到数据库
```

### 认证流程
```
应用启动
    ↓
检查是否设置主密码
    ↓
┌─ 是 ───────────────────┐
│                       │
│  显示生物识别按钮      │
│         或             │
│  主密码输入界面        │
│                       │
└───────┬───────────────┘
        ↓
    验证成功
        ↓
    进入主页
```

## 🎨 主题颜色

### Light Theme
```kotlin
primary = 0xFF6200EE      // 紫色
secondary = 0xFF03DAC6    // 青色
tertiary = 0xFFB00020     // 红色
```

### Dark Theme
```kotlin
primary = 0xFFBB86FC      // 浅紫色
secondary = 0xFF03DAC6    // 青色
tertiary = 0xFFCF6679     // 浅红色
```

## 📊 数据库表结构

### diary_entries
```sql
id          INTEGER PRIMARY KEY
title       TEXT
content     TEXT
mood        TEXT  -- happy, sad, neutral, etc.
timestamp   INTEGER
isEncrypted BOOLEAN
mediaPaths  TEXT  -- JSON array
```

### vault_items
```sql
id          INTEGER PRIMARY KEY
title       TEXT
category    TEXT  -- accounts, documents, notes
content     TEXT
username    TEXT
password    TEXT  -- encrypted
url         TEXT
timestamp   INTEGER
isEncrypted BOOLEAN
```

### wills
```sql
id               INTEGER PRIMARY KEY
title            TEXT
content          TEXT
recipientName    TEXT
recipientContact TEXT
releaseCondition TEXT  -- date, manual
releaseDate      INTEGER
isReleased       BOOLEAN
timestamp        INTEGER
isEncrypted      BOOLEAN
```

### media_items
```sql
id        INTEGER PRIMARY KEY
title     TEXT
description TEXT
filePath  TEXT
type      TEXT  -- image, video, audio
timestamp INTEGER
tags      TEXT
```

## 🛠️ 常用Gradle命令

```bash
# 清理构建
./gradlew clean

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease

# 安装到设备
./gradlew installDebug

# 运行测试
./gradlew test

# 查看依赖树
./gradlew app:dependencies
```

## 📝 关键配置

### minSdk / targetSdk
```kotlin
minSdk = 24      // Android 7.0
targetSdk = 34   // Android 14
```

### Java版本
```kotlin
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
```

### Compose版本
```kotlin
compose-bom: 2023.10.01
kotlinCompilerExtensionVersion: 1.5.4
```

## ⚠️ 重要注意事项

### 主密码
```
• 最少6个字符
• 不可逆哈希存储
• 无法找回
• 妥善保管
```

### 数据安全
```
• 卸载 = 删除所有数据
• 更换设备前先备份
• 备份文件受密码保护
• 无云端备份 (完全本地)
```

### 权限
```
• CAMERA: 拍照
• RECORD_AUDIO: 录音
• READ/WRITE_EXTERNAL_STORAGE: 访问照片和备份
• USE_BIOMETRIC: 生物识别
• 无INTERNET权限 (完全离线)
```

## 📚 文档索引

| 文档 | 用途 |
|------|------|
| `README.md` | 项目介绍、功能说明 |
| `BUILD_GUIDE.md` | 详细构建步骤 |
| `PROJECT.md` | 项目概述 |
| `DELIVERY.md` | 交付总结 |
| `QUICKREF.md` | 本文档 - 快速参考 |

## 🎯 下一步开发

### 立即可做
- [ ] 打开 Android Studio
- [ ] 同步 Gradle
- [ ] 构建 APK
- [ ] 安装到设备

### 短期任务
- [ ] 实现 DAO 接口
- [ ] 集成真实数据库
- [ ] 替换 Mock 数据
- [ ] 添加相机功能

### 长期规划
- [ ] 添加单元测试
- [ ] 多语言支持
- [ ] 性能优化
- [ ] 发布应用商店

---

**快速命令**:
```bash
# 一键构建 (在项目目录)
chmod +x gradlew && ./gradlew assembleDebug

# 一键安装 (连接设备后)
adb install app/build/outputs/apk/debug/app-debug.apk
```

**项目位置**:
```
/vol1/@apphome/trim.openclaw/data/workspace/death-diary
```

**版本**: 1.0.0 Beta
**更新**: 2025-01-15
