package com.memoamber.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 关系人档案
 * 记录重要人物信息，可作为遗言联络人或快速发送对象
 */
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,               // 姓名
    val basicInfo: String = "",     // 基本信息（生日、职业等）
    val phone: String = "",         // 手机号
    val email: String = "",         // 邮箱
    val relationship: String = "",  // 双方关系（家人/朋友/同事等）
    val notes: String = "",         // 备注
    val avatarPath: String = "",    // 头像（content:// URI 或本地路径）
    val timestamp: Long
)
