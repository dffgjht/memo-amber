package com.deathdiary.ui.screens

import java.text.SimpleDateFormat
import java.util.*

/**
 * 将毫秒时间戳格式化为完整日期时间字符串（yyyy-MM-dd HH:mm）
 */
fun formatTimestampFull(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * 将毫秒时间戳格式化为相对时间描述（刚刚/X分钟前/X小时前/X天前/日期）
 */
fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "刚刚"
        diff < 3600000 -> "${diff / 60000}分钟前"
        diff < 86400000 -> "${diff / 3600000}小时前"
        diff < 604800000 -> "${diff / 86400000}天前"
        else -> {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

/**
 * 将毫秒时间戳格式化为完整日期字符串（yyyy年MM月dd日）
 */
fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * 将毫秒时间戳格式化为完整日期时间字符串（yyyy年MM月dd日 HH:mm）
 */
fun formatDateTimeFull(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * 将毫秒时间戳格式化为短日期字符串（yyyy-MM-dd）
 */
fun formatDateShort(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * 解析 yyyy-MM-dd 格式字符串为毫秒时间戳，失败返回 null
 */
fun parseDate(dateStr: String): Long? {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.parse(dateStr)?.time
    } catch (e: Exception) {
        null
    }
}
