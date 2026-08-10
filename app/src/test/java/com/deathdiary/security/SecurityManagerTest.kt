package com.deathdiary.security

import at.favre.lib.crypto.bcrypt.BCrypt
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * SecurityManager 单元测试
 * 
 * 测试 BCrypt 密码哈希和验证功能
 */
class SecurityManagerTest {

    private lateinit var securityManager: TestableSecurityManager

    /**
     * 可测试的 SecurityManager 包装类
     * 避免依赖 Android Context 和 Keystore
     */
    private class TestableSecurityManager {
        private var storedHash: String? = null
        private var passwordVersion: Int = 1

        companion object {
            private const val PASSWORD_VERSION_BCRYPT = 2
        }

        fun setMasterPassword(password: String) {
            val hash = BCrypt.withDefaults().hashToString(12, password.toCharArray())
            storedHash = hash
            passwordVersion = PASSWORD_VERSION_BCRYPT
        }

        fun verifyMasterPassword(password: String): Boolean {
            val hash = storedHash ?: return false
            
            return when (passwordVersion) {
                PASSWORD_VERSION_BCRYPT -> {
                    val result = BCrypt.verifyer().verify(password.toCharArray(), hash)
                    result.verified
                }
                else -> {
                    // 旧版 hashCode() 格式
                    val legacyHash = password.hashCode().toString()
                    legacyHash == hash
                }
            }
        }

        fun isLegacyPasswordHash(): Boolean {
            return passwordVersion < PASSWORD_VERSION_BCRYPT
        }

        fun migratePasswordToBcrypt(oldPassword: String): Boolean {
            val hash = storedHash ?: return false
            
            // 验证旧密码（模拟旧版 hashCode）
            val legacyHash = oldPassword.hashCode().toString()
            if (legacyHash != hash) {
                return false
            }
            
            // 迁移到 BCrypt
            val newHash = BCrypt.withDefaults().hashToString(12, oldPassword.toCharArray())
            storedHash = newHash
            passwordVersion = PASSWORD_VERSION_BCRYPT
            
            return true
        }

        fun setLegacyPassword(password: String) {
            // 模拟旧版 hashCode() 格式
            storedHash = password.hashCode().toString()
            passwordVersion = 1
        }

        fun hasMasterPassword(): Boolean {
            return storedHash != null
        }
    }

    @Before
    fun setUp() {
        securityManager = TestableSecurityManager()
    }

    @Test
    fun `test BCrypt password hashing and verification`() {
        val password = "MySecurePassword123!"
        
        // 设置密码
        securityManager.setMasterPassword(password)
        
        // 验证正确密码
        assertTrue("Correct password should verify", securityManager.verifyMasterPassword(password))
        
        // 验证错误密码
        assertFalse("Wrong password should not verify", securityManager.verifyMasterPassword("WrongPassword"))
        assertFalse("Empty password should not verify", securityManager.verifyMasterPassword(""))
        assertFalse("Different case should not verify", securityManager.verifyMasterPassword("mysecurepassword123!"))
    }

    @Test
    fun `test BCrypt with special characters`() {
        val passwords = listOf(
            "p@ssw0rd!",
            "密码测试123",
            "🔒 secure 🔑",
            "a".repeat(72),  // BCrypt 最大长度
            ""  // 空密码
        )
        
        for (password in passwords) {
            securityManager.setMasterPassword(password)
            assertTrue("Password '${password.take(10)}...' should verify", 
                securityManager.verifyMasterPassword(password))
        }
    }

    @Test
    fun `test legacy password hash detection`() {
        // 新密码应该是 BCrypt 格式
        securityManager.setMasterPassword("test123")
        assertFalse("New password should not be legacy", securityManager.isLegacyPasswordHash())
        
        // 旧密码应该是 hashCode 格式
        val legacyManager = TestableSecurityManager()
        legacyManager.setLegacyPassword("oldpassword")
        assertTrue("Legacy password should be detected", legacyManager.isLegacyPasswordHash())
    }

    @Test
    fun `test password migration from legacy to BCrypt`() {
        val oldPassword = "OldPassword123"
        
        // 设置旧格式密码
        securityManager.setLegacyPassword(oldPassword)
        assertTrue("Should have legacy hash", securityManager.isLegacyPasswordHash())
        
        // 迁移到 BCrypt
        val migrated = securityManager.migratePasswordToBcrypt(oldPassword)
        assertTrue("Migration should succeed", migrated)
        
        // 验证迁移后可以正常登录
        assertFalse("Should no longer be legacy", securityManager.isLegacyPasswordHash())
        assertTrue("Should verify with new BCrypt hash", securityManager.verifyMasterPassword(oldPassword))
        
        // 验证错误密码不能迁移
        val anotherManager = TestableSecurityManager()
        anotherManager.setLegacyPassword("correct_password")
        assertFalse("Wrong password should fail migration", 
            anotherManager.migratePasswordToBcrypt("wrong_password"))
    }

    @Test
    fun `test hasMasterPassword`() {
        assertFalse("Should not have password initially", securityManager.hasMasterPassword())
        
        securityManager.setMasterPassword("test123")
        assertTrue("Should have password after setting", securityManager.hasMasterPassword())
    }

    @Test
    fun `test empty password handling`() {
        securityManager.setMasterPassword("")
        assertTrue("Empty password should verify", securityManager.verifyMasterPassword(""))
        assertFalse("Non-empty should not verify empty", securityManager.verifyMasterPassword("notempty"))
    }

    @Test
    fun `test BCrypt cost factor`() {
        val password = "TestPassword"
        
        // 测量哈希生成时间（cost=12 应该在 200-500ms）
        val startTime = System.currentTimeMillis()
        securityManager.setMasterPassword(password)
        val hashTime = System.currentTimeMillis() - startTime
        
        // BCrypt cost=12 大约需要 200-500ms
        assertTrue("Hash generation should take reasonable time", hashTime in 100..1000)
        
        // 验证时间应该很快（< 100ms）
        val verifyStartTime = System.currentTimeMillis()
        securityManager.verifyMasterPassword(password)
        val verifyTime = System.currentTimeMillis() - verifyStartTime
        
        assertTrue("Verification should be fast", verifyTime < 100)
    }
}
