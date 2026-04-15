# ✅ GitHub Actions 构建清单

## 🎯 您需要做的事情

### 步骤 1：创建 GitHub 仓库 ☑️

- [ ] 访问 https://github.com
- [ ] 登录账号（如无账号先注册）
- [ ] 点击右上角 "+" → "New repository"
- [ ] 仓库名称：`death-diary`
- [ ] 选择 Public 或 Private
- [ ] **不要**勾选任何选项（README 等）
- [ ] 点击 "Create repository"

**预计时间**: 2 分钟

---

### 步骤 2：推送代码 ☑️

在项目目录执行以下命令：

```bash
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 添加远程仓库（替换 YOUR_USERNAME）
git remote add origin https://github.com/YOUR_USERNAME/death-diary.git

# 推送
git push -u origin main
```

**需要认证**：
- 用户名：您的 GitHub 用户名
- 密码：使用 Personal Access Token（不是 GitHub 密码！）

**预计时间**: 3 分钟

---

### 步骤 3：触发构建 ☑️

**方式 A：自动触发**
- 推送代码后会自动开始构建
- 在仓库页面点击 "Actions" 标签查看

**方式 B：手动触发**
1. 在仓库页面点击 "Actions" 标签
2. 选择 "Build Android APK" 工作流
3. 点击 "Run workflow" → "Run workflow"

**预计时间**: 1 分钟

---

### 步骤 4：等待构建 ☑️

- 构建时间：5-10 分钟
- 在 Actions 页面可以实时查看进度
- 绿色 ✅ = 成功
- 红色 ❌ = 失败（查看日志）

**预计时间**: 5-10 分钟

---

### 步骤 5：下载 APK ☑️

1. 在 Actions 页面点击已完成的工作流
2. 滚动到页面底部的 "Artifacts" 部分
3. 点击 "app-debug" 下载
4. 解压 zip 文件
5. 获得 `app-debug.apk` 文件

**预计时间**: 2 分钟

---

### 步骤 6：安装到手机 ☑️

**方法 A：USB 安装**
```bash
adb install app-debug.apk
```

**方法 B：直接安装**
1. 传输 APK 到手机
2. 在文件管理器中点击 APK
3. 允许"未知来源"安装
4. 点击"安装"

**预计时间**: 2 分钟

---

## 📊 总时间预估

| 步骤 | 时间 |
|------|------|
| 创建仓库 | 2 分钟 |
| 推送代码 | 3 分钟 |
| 触发构建 | 1 分钟 |
| 等待构建 | 5-10 分钟 |
| 下载 APK | 2 分钟 |
| 安装到手机 | 2 分钟 |
| **总计** | **15-20 分钟** |

---

## 🎯 一键命令（复制粘贴）

```bash
# 1. 进入项目目录
cd /vol1/@apphome/trim.openclaw/data/workspace/death-diary

# 2. 添加远程仓库（替换 YOUR_USERNAME）
git remote add origin https://github.com/YOUR_USERNAME/death-diary.git

# 3. 推送代码
git push -u origin main
```

推送后：
1. 访问 GitHub 仓库
2. 点击 "Actions" 标签
3. 等待构建完成
4. 下载 APK

---

## ⚠️ 重要提示

### 认证问题
❌ 不要使用 GitHub 密码
✅ 使用 Personal Access Token

**生成 Token**：
1. GitHub → Settings → Developer settings
2. Personal access tokens → Tokens (classic)
3. Generate new token (classic)
4. 勾选 `repo` 权限
5. Generate token → 复制（只显示一次）

推送时：
- 用户名：GitHub 用户名
- 密码：粘贴 Token

---

## 📝 需要替换的内容

在执行命令前，请将 `YOUR_USERNAME` 替换为您的实际 GitHub 用户名。

例如：
```bash
# 如果您的用户名是 zhangsan
git remote add origin https://github.com/zhangsan/death-diary.git
```

---

## ✅ 完成后

您将获得：
- ✅ 一个 GitHub 仓库
- ✅ 自动化的 APK 构建流程
- ✅ 可直接安装的 APK 文件
- ✅ 后续更新自动构建

---

## 📞 需要帮助？

详细步骤：`GITHUB_ACTIONS_GUIDE.md`
故障排除：`BUILD_GUIDE.md`

---

**项目位置**: `/vol1/@apphome/trim.openclaw/data/workspace/death-diary`

**祝您成功！** 🚀
