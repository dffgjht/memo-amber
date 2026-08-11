package com.memoamber.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    // 小型组件 — 标签、芯片
    extraSmall = RoundedCornerShape(8.dp),
    // 小型组件 — 按钮、输入框
    small = RoundedCornerShape(12.dp),
    // 中型组件 — 卡片、对话框
    medium = RoundedCornerShape(16.dp),
    // 大型组件 — 底部表单、大卡片
    large = RoundedCornerShape(24.dp),
    // 超大组件 — 全屏对话框
    extraLarge = RoundedCornerShape(32.dp)
)
