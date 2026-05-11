# 🔧 修复 "Unexpected pluginManagement block" 错误

## 错误信息
```
Unexpected `pluginManagement` block found. Only one `pluginManagement` block is allowed per script
```

## 原因
这个错误通常是由于以下原因之一：
1. Gradle 缓存损坏
2. Android Studio 缓存问题
3. 项目中存在多个 `settings.gradle.kts` 文件
4. `.idea` 文件夹中的配置冲突

---

## ✅ 解决方案

### 方案一：清理 Gradle 缓存（推荐）

#### 步骤 1：关闭 Android Studio

#### 步骤 2：删除缓存文件
```bash
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 删除 .gradle 文件夹
rm -rf .gradle

# 删除 build 文件夹
rm -rf build
rm -rf app/build

# 删除 .idea 文件夹（Android Studio 配置）
rm -rf .idea
```

#### 步骤 3：重新打开项目
1. 启动 Android Studio
2. File → Open → 选择项目文件夹
3. 等待 Gradle 同步

---

### 方案二：使 Gradle Wrapper 脱机工作

#### 步骤 1：清理项目
在 Android Studio 中：
1. **Build** → **Clean Project**

#### 步骤 2：删除 Gradle 缓存
```bash
rm -rf ~/.gradle/caches/
```

#### 步骤 3：重新同步
在 Android Studio 中：
1. **File** → **Sync Project with Gradle Files**

---

### 方案三：检查是否有多个 settings 文件

```bash
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 查找所有 settings 文件
find . -name "settings.gradle*"

# 如果有多个，删除多余的
# 只保留 settings.gradle.kts
```

---

### 方案四：使用命令行构建（绕过 Android Studio）

```bash
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 清理
./gradlew clean

# 构建
./gradlew assembleDebug --stacktrace
```

---

### 方案五：重新生成 Gradle Wrapper

```bash
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 删除现有的 wrapper
rm -rf gradle/wrapper

# 重新生成（需要安装 gradle）
gradle wrapper --gradle-version 8.5
```

---

## 🎯 推荐的完整修复流程

### 1. 完全清理项目
```bash
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 删除所有缓存和构建产物
rm -rf .gradle
rm -rf .idea
rm -rf build
rm -rf app/build
rm -rf ~/.gradle/caches/
```

### 2. 重新打开项目
1. 关闭 Android Studio
2. 重新启动 Android Studio
3. File → Open → 选择项目文件夹
4. 选择 **"Trust Project"**

### 3. 等待同步
- 首次同步会下载依赖
- 等待 "Gradle sync finished"

### 4. 构建 APK
- Build → Build APK(s)

---

## 📝 预防措施

### 1. 不要手动修改 .idea 文件夹
Android Studio 会自动管理这个文件夹

### 2. 定期清理缓存
```bash
# 每月清理一次
./gradlew clean
rm -rf ~/.gradle/caches/
```

### 3. 使用 Git 忽略缓存
确保 `.gitignore` 包含：
```
.gradle/
build/
.idea/
```

---

## 🔍 如果问题仍然存在

### 检查 Gradle 版本
```bash
./gradlew --version
```

应该输出：
```
Gradle 8.5
```

### 检查 Java 版本
```bash
java -version
```

应该输出：
```
java version "17.x.x"
```

### 检查环境变量
```bash
echo $JAVA_HOME
echo $ANDROID_HOME
```

---

## 📞 获取帮助

如果上述方案都不行：

1. **查看完整错误日志**
   - Build → View Build Logs
   - 查看 `--stacktrace` 输出

2. **尝试命令行构建**
   ```bash
   ./gradlew assembleDebug --stacktrace --info
   ```

3. **检查项目文件**
   - 确保 `settings.gradle.kts` 没有重复
   - 确保没有多个 settings 文件

---

## ✅ 快速修复（一行命令）

```bash
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary && rm -rf .gradle .idea build app/build && ./gradlew clean
```

然后在 Android Studio 中重新打开项目。

---

**项目位置**: `/vol1/@apphome/trim.openclaw/data/workspace/death-diary`

**希望这能解决您的问题！** 🚀
