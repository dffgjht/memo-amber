# 贡献指南

感谢你对 **记忆琥珀（MemoAmber）** 项目的关注！我们欢迎任何形式的贡献。

## 快速开始

### 1. Fork 并克隆

```bash
git clone https://github.com/<your-username>/memo-amber.git
cd memo-amber

# 添加上游仓库
git remote add upstream https://github.com/dffgjht/memo-amber.git
```

### 2. 同步最新代码

```bash
git fetch upstream
git checkout main
git merge upstream/main
```

### 3. 创建分支

```bash
git checkout -b feature/your-feature-name
```

**分支命名规范：**

| 类型 | 格式 | 示例 |
|:-----|:-----|:-----|
| 新功能 | `feature/<描述>` | `feature/community-board` |
| Bug 修复 | `fix/<描述>` | `fix/login-crash` |
| 文档 | `docs/<描述>` | `docs/update-readme` |
| 重构 | `refactor/<描述>` | `refactor/database-layer` |
| 测试 | `test/<描述>` | `test/viewmodel-unit` |

### 4. 使用 Android Studio 打开

使用 Android Studio 打开项目根目录，等待 Gradle 同步完成。

## Commit 消息格式

采用 [Conventional Commits](https://www.conventionalcommits.org/zh-hans/) 规范：

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type 类型：**

| 类型 | 说明 |
|:-----|:-----|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `docs` | 文档变更 |
| `style` | 代码格式（不影响功能） |
| `refactor` | 代码重构 |
| `perf` | 性能优化 |
| `test` | 测试相关 |
| `chore` | 构建/工具链变更 |
| `ci` | CI/CD 配置变更 |

**示例：**

```
feat(community): 新增留言板点赞功能

- 实现点赞 API 调用
- 添加点赞动画效果
- 支持取消点赞

Closes #42
```

## 提交 Pull Request

### PR 前检查清单

- 代码通过编译，无警告
- 通过所有现有测试
- 新功能已添加对应测试
- 遵循项目代码规范
- Commit 消息符合 Conventional Commits 格式

### 提交步骤

1. 确保分支是最新的：
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```
2. 推送到你的 Fork：
   ```bash
   git push origin feature/your-feature-name
   ```
3. 在 GitHub 上创建 Pull Request，填写：
   - **标题**：简洁描述变更，遵循 Conventional Commits 格式
   - **描述**：变更原因、内容和测试方式
   - **关联 Issue**：使用 `Closes #<issue-number>` 关联

## 代码规范

遵循 [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)。

### 基本原则

- 命名使用有意义的变量和方法名，避免缩写（除公认的如 `id`、`url`）
- 单个函数不超过 40 行，超过时应拆分
- 公共 API 必须添加 KDoc 注释
- 在逻辑分组之间使用空行分隔

### Kotlin 风格

```kotlin
// 类名：大驼峰
class UserProfileViewModel { }

// 函数和变量：小驼峰
fun calculateHash() { }
val userName = "test"

// 常量：全大写下划线分隔
const val MAX_RETRY_COUNT = 3

// Composable 函数：大驼峰
@Composable
fun UserProfileScreen() { }
```

### Jetpack Compose 规范

- Composable 函数使用大驼峰命名
- 使用 `Modifier` 参数作为第一个可选参数
- 状态提升（State Hoisting）：将状态提升到调用者
- 避免在 Composable 中进行副作用操作，使用 `LaunchedEffect`

### 架构规范

- **MVVM 架构**：View → ViewModel → Repository → Data Source
- **单向数据流**：UI 通过事件通知 ViewModel，ViewModel 通过 State 更新 UI

## 测试要求

- 新增业务逻辑应包含单元测试
- Bug 修复应添加回归测试
- 关键工具类（加密、哈希等）必须有完整测试覆盖

```bash
# 运行所有单元测试
./gradlew test

# 运行 Android 仪器测试
./gradlew connectedAndroidTest
```

## 问题反馈

### Bug 反馈

使用 [GitHub Issues](https://github.com/dffgjht/memo-amber/issues) 提交，包含：

1. Bug 描述
2. 复现步骤
3. 预期行为 vs 实际行为
4. 环境信息（Android 版本、设备型号、应用版本）
5. 相关日志或截图

### 功能建议

同样通过 Issues 提交，使用 `enhancement` 标签，说明需求描述和使用场景。

---

感谢你的贡献！每一次参与都让记忆琥珀变得更好。🎉
