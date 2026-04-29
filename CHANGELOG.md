# Changelog

本项目的所有重要变更都会记录在此文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，
版本遵循 [Semantic Versioning](https://semver.org/lang/zh-CN/)。

## [1.4.0] - 2026-04-30

### Fixed
- 修复桌面端 fat jar 签名冲突导致 exe 无法启动的问题（SecurityException: Invalid signature file digest）
- 在 fatJar 构建任务中排除 META-INF 签名文件（.SF/.DSA/.RSA/.EC）

### Changed
- desktop/build.gradle.kts 优化 fat jar 打包流程

## [1.3.1] - 2026-04-27

### Changed
- 项目更名为「记忆琥珀 MemoAmber」（原 death-diary / 存证纪）
- 包名由 com.deathdiary 更名为 com.memoamber
- 主题由 Theme.DeathDiary 更名为 Theme.MemoAmber
- README 新增命名寓意章节

## [1.3.0] - 2026-04-17

### Fixed
- 修复 Room 数据库实体与列名映射问题
- 优化社区功能性能

## [1.2.5] - 2026-04-15

### Fixed
- 修复密码保险箱编辑闪退问题
- 修复相册预览异常

### Added
- 遗嘱支持短信/邮件发送
- 生物识别完善（指纹 + 人脸）

## [1.2.4] - 2026-04-13

### Fixed
- 修复数据持久化问题
- 修复备份对话框显示异常
- 修复版本号显示错误
- 修复相册图片预览问题

### Changed
- 生物识别优先使用指纹
- 更新应用图标

## [1.2.3] - 2026-04-11

### Added
- 新增备份/恢复功能
- 新增自动锁定机制

### Changed
- 精简文档结构

## [1.2.2] - 2026-04-09

### Security
- 密码哈希升级为 BCrypt
- 完善数据库迁移策略

### Changed
- CI/CD 流程完善

## [1.2.1] - 2026-04-07

### Fixed
- 修复后台重登闪退
- 修复编辑删除功能异常

### Added
- 日记支持 GPS 定位
- 添加隐私政策

## [1.2] - 2026-04-05

### Fixed
- 修复多项闪退问题

### Changed
- 优化 UI 界面
- 日期精确到时分秒

### Added
- 本地相册功能

## [1.1] - 2026-04-03

### Added
- 新增社区留言板功能

## [1.0] - 2026-04-01

### Added
- 初始版本发布
- 生物识别认证
- AES-256 加密存储
- 日记系统
- 密码保险箱
- 数字遗嘱
- 回忆相册

[1.4.0]: https://github.com/dffgjht/memo-amber/releases/tag/v1.4.0
[1.3.1]: https://github.com/dffgjht/memo-amber/releases/tag/v1.3.1
[1.3.0]: https://github.com/dffgjht/memo-amber/releases/tag/v1.3.0
[1.2.5]: https://github.com/dffgjht/memo-amber/releases/tag/v1.2.5
[1.2.4]: https://github.com/dffgjht/memo-amber/releases/tag/v1.2.4
[1.2.3]: https://github.com/dffgjht/memo-amber/releases/tag/v1.2.3
[1.2.2]: https://github.com/dffgjht/memo-amber/releases/tag/v1.2.2
[1.2.1]: https://github.com/dffgjht/memo-amber/releases/tag/v1.2.1
[1.2]: https://github.com/dffgjht/memo-amber/releases/tag/v1.2
[1.1]: https://github.com/dffgjht/memo-amber/releases/tag/v1.1
[1.0]: https://github.com/dffgjht/memo-amber/releases/tag/v1.0
