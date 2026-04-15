# ☁️ GitHub Actions 云端构建 - 详细步骤

## 🎯 目标
通过 GitHub Actions 自动构建 APK，无需在本地安装任何开发工具。

---

## 📋 前提条件

### 您需要：
- ✅ 一个 GitHub 账号（免费注册：https://github.com/signup）
- ✅ 项目已准备好（✅ 已完成）

### 您不需要：
- ❌ 安装 Android Studio
- ❌ 安装 Java JDK
- ❌ 安装 Android SDK
- ❌ 任何本地开发环境

---

## 🚀 步骤一：在 GitHub 创建新仓库

### 方式 A：通过网页创建（推荐）

1. **登录 GitHub**
   - 访问：https://github.com
   - 登录您的账号

2. **创建新仓库**
   - 点击右上角的 **"+"** → **"New repository"**
   - 填写信息：
     ```
     Repository name: death-diary
     Description: 死亡日记 - 数字遗产管理安卓应用
     Public / Private: 选择 Public（公开）或 Private（私有）
     ```
   - **重要**：不要勾选任何选项（Add a README file 等）
   - 点击 **"Create repository"**

3. **记录仓库地址**
   - 创建后会显示仓库地址
   - 格式：`https://github.com/您的用户名/death-diary.git`

### 方式 B：通过 GitHub CLI（如果已安装 gh）

```bash
gh repo create death-diary --public --description "死亡日记 - 数字遗产管理安卓应用"
```

---

## 📤 步骤二：推送代码到 GitHub

### 在项目目录执行以下命令：

```bash
# 进入项目目录
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 添加远程仓库（替换 YOUR_USERNAME 为您的 GitHub 用户名）
git remote add origin https://github.com/YOUR_USERNAME/death-diary.git

# 推送到 GitHub
git push -u origin main
```

### 如果需要认证：

**方式 1：使用 Personal Access Token（推荐）**
1. 生成 Token：
   - GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
   - 点击 "Generate new token (classic)"
   - 勾选 `repo` 权限
   - 点击 "Generate token"
   - 复制 Token（只显示一次！）

2. 推送时使用 Token 作为密码：
   ```bash
   git push -u origin main
   # 用户名：YOUR_USERNAME
   # 密码：粘贴 Token
   ```

**方式 2：使用 SSH Key**
```bash
# 生成 SSH Key
ssh-keygen -t ed25519 -C "your_email@example.com"

# 添加到 GitHub
# Settings → SSH and GPG keys → New SSH key
# 粘贴 ~/.ssh/id_ed25519.pub 的内容

# 修改远程地址为 SSH
git remote set-url origin git@github.com:YOUR_USERNAME/death-diary.git

# 推送
git push -u origin main
```

---

## ⚙️ 步骤三：触发 GitHub Actions 构建

### 方式 A：自动触发（推送后自动开始）

当代码推送到 GitHub 后，GitHub Actions 会**自动**开始构建（如果工作流配置为在推送时触发）。

- 在仓库页面点击 **"Actions"** 标签
- 您应该看到正在运行或已完成的构建工作流

### 方式 B：手动触发（更灵活）

1. **进入 Actions 页面**
   - 在仓库页面点击 **"Actions"** 标签

2. **选择工作流**
   - 在左侧选择 **"Build Android APK"** 工作流

3. **手动触发**
   - 点击右侧的 **"Run workflow"** 按钮
   - 确认分支为 **"main"**
   - 点击 **"Run workflow"** 确认

---

## ⏳ 步骤四：等待构建完成

### 构建过程

1. **运行时间**：约 5-10 分钟
   - 下载依赖：2-3 分钟
   - 编译代码：2-3 分钟
   - 构建 APK：1-2 分钟
   - 上传产物：1 分钟

2. **查看进度**
   - 在 Actions 页面点击正在运行的工作流
   - 可以实时看到构建日志
   - 绿色 ✅ = 成功
   - 红色 ❌ = 失败

3. **构建步骤**
   - ✅ Checkout code（检出代码）
   - ✅ Set up JDK 17（设置 Java）
   - ✅ Build Debug APK（构建 Debug 版本）
   - ✅ Build Release APK（构建 Release 版本）
   - ✅ Upload artifacts（上传构建产物）

---

## 📥 步骤五：下载 APK

### 下载 Debug APK（推荐用于测试）

1. **进入工作流详情页**
   - 在 Actions 页面点击已完成的工作流

2. **找到 Artifacts 部分**
   - 滚动到页面底部
   - 找到 **"Artifacts"** 部分

3. **下载 APK**
   - 点击 **"app-debug"** 右侧的下载图标
   - 文件名：`app-debug.zip`
   - 大小：约 10 MB

4. **解压**
   - 下载后解压 zip 文件
   - 里面包含：`app-debug.apk`

### 下载 Release APK（用于发布）

- 流程同上，下载 **"app-release"**
- 注意：Release 版本未签名，需要额外签名才能发布

---

## 📱 步骤六：安装到手机

### 方法一：通过 USB

```bash
# 启用手机 USB 调试后
adb install app-debug.apk
```

### 方法二：直接安装

1. **传输 APK 到手机**
   - 通过 USB、微信、QQ 等方式

2. **在手机上安装**
   - 打开文件管理器
   - 找到 APK 文件
   - 点击安装
   - 允许"未知来源"安装（如需要）

3. **完成**
   - 安装后在桌面找到"死亡日记"图标
   - 启动并设置主密码

---

## 🎉 完成！

恭喜！您已经成功通过 GitHub Actions 构建了 APK。

---

## 📊 项目仓库信息

### 仓库地址（需要您创建后替换）
```
https://github.com/YOUR_USERNAME/death-diary
```

### Actions 工作流
```
.github/workflows/build-apk.yml
```

### 构建产物
```
Artifacts:
  - app-debug (Debug APK)
  - app-release (Release APK, 未签名)
```

---

## 🔧 故障排除

### 问题 1：推送失败

**错误**: `fatal: origin does not appear to be a git repository`

**解决**:
```bash
# 添加远程仓库（替换您的用户名）
git remote add origin https://github.com/YOUR_USERNAME/death-diary.git
```

---

### 问题 2：认证失败

**错误**: `Support for password authentication was removed`

**解决**: 使用 Personal Access Token 而不是密码

1. 生成 Token：GitHub Settings → Developer settings → Personal access tokens
2. 推送时使用 Token 作为密码

---

### 问题 3：Actions 构建失败

**错误**: 工作流显示红色 ❌

**解决**:
1. 点击失败的工作流查看日志
2. 常见原因：
   - 依赖下载失败（网络问题）→ 重试工作流
   - 代码语法错误 → 检查代码
   - Gradle 版本问题 → 已配置，一般不会出现

---

### 问题 4：无法下载 Artifacts

**错误**: Artifacts 部分没有下载按钮

**解决**:
- 确保工作流已完成（绿色 ✅）
- 刷新页面
- 检查是否有权限访问仓库

---

## 📝 命令速查

```bash
# 推送到 GitHub
git push -u origin main

# 查看远程仓库
git remote -v

# 修改远程仓库地址
git remote set-url origin https://github.com/YOUR_USERNAME/death-diary.git

# 查看推送状态
git status
```

---

## 🎯 下一步

### 构建成功后
1. ✅ 下载 Debug APK
2. ✅ 安装到手机
3. ✅ 体验应用功能
4. ✅ 收集反馈
5. ✅ 迭代改进

### 后续更新
```bash
# 修改代码后
git add .
git commit -m "Update: 描述您的更改"
git push
# Actions 会自动重新构建
```

---

## 📞 需要帮助？

- **GitHub 官方文档**: https://docs.github.com/en/actions
- **项目文档**: `INSTALLATION_GUIDE.md`
- **构建指南**: `BUILD_GUIDE.md`

---

**祝您构建顺利！** 🚀

---

**项目位置**: `/vol1/@apphome/trim.openclaw/data/workspace/death-diary`
**创建时间**: 2025-01-15
**版本**: 1.0.0 Beta
