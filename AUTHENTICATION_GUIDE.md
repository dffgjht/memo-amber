# 🔐 GitHub 身份验证指南

## 推送代码需要认证

由于 GitHub 已不再支持密码认证，您需要使用以下方式之一：

---

## 方法一：使用 Personal Access Token（推荐）

### 步骤 1：生成 Token

1. **登录 GitHub**
   - 访问：https://github.com
   - 登录您的账号（dffgjht）

2. **生成 Token**
   - 点击右上角头像 → **Settings**
   - 左侧菜单最下方 → **Developer settings**
   - **Personal access tokens** → **Tokens (classic)**
   - 点击 **Generate new token (classic)**

3. **配置 Token**
   - Note: 输入 `death-diary-token`
   - Expiration: 选择 `90 days` 或更长
   - 勾选权限：
     - ✅ **repo** (勾选此选项下的所有子选项)
     - ✅ **workflow** (可选，用于 GitHub Actions)
   - 滚动到底部 → 点击 **Generate token**

4. **复制 Token**
   - ⚠️ Token 只显示一次，请立即复制保存
   - 格式类似：`ghp_xxxxxxxxxxxxxxxxxxxx`

### 步骤 2：使用 Token 推送

在项目目录执行以下命令：

```bash
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 推送代码
git push -u origin main
```

**提示输入时**：
- **Username**: `dffgjht` (您的 GitHub 用户名)
- **Password**: 粘贴刚才复制的 Token（不是 GitHub 密码！）

---

## 方法二：使用 SSH Key（推荐给开发者）

### 步骤 1：生成 SSH Key

```bash
# 生成 SSH Key
ssh-keygen -t ed25519 -C "dffgjht@github.com"

# 按回车使用默认路径
# 可以设置密码或直接回车（无密码）
```

### 步骤 2：添加到 GitHub

1. **复制公钥**
   ```bash
   cat ~/.ssh/id_ed25519.pub
   ```

2. **添加到 GitHub**
   - GitHub → Settings → **SSH and GPG keys**
   - 点击 **New SSH key**
   - Title: `death-diary-key`
   - Key: 粘贴刚才复制的内容
   - 点击 **Add SSH key**

### 步骤 3：修改远程地址为 SSH

```bash
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 修改远程地址
git remote set-url origin git@github.com:dffgjht/death-diary.git

# 推送代码
git push -u origin main
```

---

## 方法三：使用 GitHub CLI（gh）

### 安装 GitHub CLI

**Ubuntu/Debian:**
```bash
sudo apt install gh
```

**macOS:**
```bash
brew install gh
```

**Windows:**
```bash
winget install --id GitHub.cli
```

### 登录并推送

```bash
# 登录 GitHub
gh auth login

# 选择选项：
# 1. GitHub.com
# 2. HTTPS
# 3. Login with a web browser (推荐)

# 推送代码
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary
git push -u origin main
```

---

## 🎯 推荐方案

### 如果您是第一次使用 Git
👉 **方法一：Personal Access Token**
- 最简单
- 不需要额外工具
- 适合快速使用

### 如果您经常使用 Git
👉 **方法二：SSH Key**
- 一次配置，永久使用
- 更安全
- 适合开发者

### 如果您想自动化
👉 **方法三：GitHub CLI**
- 命令行工具
- 功能强大
- 适合高级用户

---

## ⚠️ 常见问题

### Q: Token 推送时提示错误？
A: 确保勾选了 `repo` 权限，重新生成 Token

### Q: SSH 推送时提示权限拒绝？
A: 检查 SSH key 是否正确添加到 GitHub

### Q: 忘记保存 Token？
A: 删除旧 Token，重新生成一个新的

---

## 📝 快速参考

### Token 认证
```bash
git push -u origin main
# Username: dffgjht
# Password: <粘贴 Token>
```

### SSH 认证
```bash
git remote set-url origin git@github.com:dffgjht/death-diary.git
git push -u origin main
```

### GitHub CLI
```bash
gh auth login
git push -u origin main
```

---

**项目位置**: `/vol1/@apphome/trim.openclaw/data/workspace/death-diary`
**仓库地址**: https://github.com/dffgjht/death-diary

**下一步**: 选择一种认证方式，然后推送代码 🚀
