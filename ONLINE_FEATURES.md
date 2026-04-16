# 🌐 在线功能说明

## ✅ 已添加的在线功能

### 1. 用户系统
- ✅ 用户注册（用户名 + 邮箱 + 密码）
- ✅ 用户登录（JWT Token 认证）
- ✅ 用户资料管理
- ✅ 在线状态显示

### 2. 社区功能
- ✅ 发布帖子到服务器
- ✅ 实时加载远程帖子
- ✅ 点赞/取消点赞（同步到服务器）
- ✅ 评论功能
- ✅ 分类浏览（推荐、关注、热门）
- ✅ 标签系统

### 3. 数据同步
- ✅ 上传本地数据到服务器
- ✅ 下载远程数据
- ✅ 增量同步（基于时间戳）
- ✅ 离线缓存（本地优先）

---

## 🔧 技术实现

### API 服务
```
基础 URL: https://api.deathdiary.com/v1
认证方式: Bearer Token (JWT)
```

### 主要接口

#### 用户认证
```
POST /auth/register    # 注册
POST /auth/login       # 登录
POST /auth/logout      # 登出
GET  /auth/me          # 获取当前用户信息
```

#### 社区帖子
```
GET    /posts          # 获取帖子列表
GET    /posts/{id}     # 获取单个帖子
POST   /posts          # 创建帖子
PUT    /posts/{id}     # 更新帖子
DELETE /posts/{id}     # 删除帖子
POST   /posts/{id}/like   # 点赞
DELETE /posts/{id}/like   # 取消点赞
```

#### 评论
```
GET    /posts/{postId}/comments  # 获取评论列表
POST   /posts/{postId}/comments  # 创建评论
DELETE /comments/{id}             # 删除评论
```

#### 数据同步
```
GET /sync/data    # 下载数据
POST /sync/data   # 上传数据
```

---

## 📱 使用方式

### 1. 离线模式（默认）
- 所有数据存储在本地
- 无需网络连接
- 完全私密和安全

### 2. 在线模式（可选）
- 注册/登录账号
- 数据同步到云端
- 社区互动功能
- 跨设备访问

---

## 🔐 安全性

### 数据加密
- 本地数据：AES-256 加密
- 传输数据：HTTPS 加密
- 密码：BCrypt 哈希
- Token：JWT 认证

### 隐私保护
- 敏感数据不上传（如密码保险箱）
- 用户可控制同步内容
- 符合数据保护法规

---

## 🚀 如何使用在线功能

### 方法一：配置后端服务器（需要部署）

#### 1. 部署 API 服务器
```bash
# 克隆后端项目
git clone https://github.com/your-repo/death-diary-server.git

# 安装依赖
cd death-diary-server
npm install

# 配置数据库
# 修改 .env 文件

# 启动服务器
npm start
```

#### 2. 修改 API 地址
```kotlin
// RetrofitClient.kt
private const val BASE_URL = "https://your-api-server.com/v1/"
```

#### 3. 重新构建 APK
```bash
./gradlew assembleDebug
```

---

### 方法二：使用 Mock 数据（测试用）

如果暂时没有服务器，可以使用 Mock 数据测试 UI：

```kotlin
// CommunityViewModel.kt
fun loadPosts() {
    // 使用 Mock 数据
    _posts.value = listOf(
        CommunityPost(
            id = 1,
            authorName = "测试用户",
            content = "这是一条测试帖子",
            timestamp = System.currentTimeMillis(),
            likes = 10
        )
    )
}
```

---

### 方法三：使用第三方 BaaS（推荐）

#### Firebase 示例
1. 创建 Firebase 项目
2. 添加 Android 应用
3. 下载 google-services.json
4. 集成 Firebase SDK

#### Supabase 示例
1. 创建 Supabase 项目
2. 获取 API URL 和 Key
3. 修改 RetrofitClient 配置

---

## 📊 数据同步流程

### 上传数据
```
1. 用户创建帖子
2. 保存到本地数据库
3. 上传到服务器
4. 服务器返回同步时间戳
5. 更新本地同步时间
```

### 下载数据
```
1. 请求服务器数据
2. 携带上次同步时间戳
3. 服务器返回增量数据
4. 合并到本地数据库
```

---

## ⚠️ 注意事项

### 网络要求
- 需要稳定的网络连接
- 支持 Wi-Fi 和移动网络
- 自动检测网络状态

### 数据冲突
- 服务器数据优先
- 本地数据作为备份
- 提供冲突解决机制

### 离线支持
- 无网络时自动切换到离线模式
- 网络恢复后自动同步
- 用户可手动触发同步

---

## 🎯 未来计划

### 短期
- [ ] 实时推送通知
- [ ] 图片上传功能
- [ ] 私信功能
- [ ] 用户关注系统

### 长期
- [ ] 端到端加密
- [ ] 多人协作
- [ ] AI 内容推荐
- [ ] 数据分析

---

## 📞 技术支持

如有问题：
1. 检查网络连接
2. 查看 API 日志
3. 验证 Token 有效性
4. 查看服务器状态

---

**API 文档**: [待补充]
**服务器代码**: [待提供]

---

**版本**: 1.2.0 (在线功能)
**更新日期**: 2025-01-15
