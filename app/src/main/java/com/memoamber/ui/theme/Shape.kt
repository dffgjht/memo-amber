package com.memoamber.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Apple Squircle — 卡片统一使用连续曲率大圆角
val AppShapes = Shapes(
    // 小型组件 — 标签、芯片
    extraSmall = RoundedCornerShape(10.dp),
    // 小型组件 — 按钮（胶囊由 CircleShape 处理）
    small = RoundedCornerShape(14.dp),
    // 中型组件 — 输入框、对话框
    medium = RoundedCornerShape(20.dp),
    // 大型组件 — Bento 卡片
    large = RoundedCornerShape(28.dp),
    // 超大组件 — 全屏对话框
    extraLarge = RoundedCornerShape(36.dp)
)
