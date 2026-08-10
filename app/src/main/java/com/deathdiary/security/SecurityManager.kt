package com.deathdiary.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import at.favre.lib.crypto.bcrypt.BCrypt
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecurityManager(context: Context) {

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    private val sharedPreferences = context.getSharedPreferences("death_diary_secure", Context.MODE_PRIVATE)

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "death_diary_master_key"
        private const val PREF_MASTER_PASSWORD = "master_password_hash"
        private const val PREF_PASSWORD_VERSION = "password_hash_version"
        private const val PASSWORD_VERSION_BCRYPT = 2
    }

    init {
        ensureKeyExists()
    }

    private fun ensureKeyExists() {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )

            val keyGenSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(30)
                .build()

            keyGenerator.init(keyGenSpec)
            keyGenerator.generateKey()
        }
    }

    fun encryptData(data: String, iv: ByteArray): Pair<ByteArray, ByteArray> {
        val cipher = getCipher()
        val secretKey = getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val actualIv = cipher.iv
        val encryptedData = cipher.doFinal(data.toByteArray())
        return Pair(actualIv, encryptedData)
    }

    fun decryptData(encryptedData: ByteArray, iv: ByteArray): String {
        val cipher = getCipher()
        val secretKey = getSecretKey()
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData)
    }

    fun setMasterPassword(password: String) {
        // 使用 BCrypt 生成安全的密码哈希（cost factor = 12）
        val hash = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        sharedPreferences.edit()
            .putString(PREF_MASTER_PASSWORD, hash)
            .putInt(PREF_PASSWORD_VERSION, PASSWORD_VERSION_BCRYPT)
            .apply()
    }

    fun verifyMasterPassword(password: String): Boolean {
        val storedHash = sharedPreferences.getString(PREF_MASTER_PASSWORD, null)
            ?: return false
        
        val passwordVersion = sharedPreferences.getInt(PREF_PASSWORD_VERSION, 1)
        
        return when (passwordVersion) {
            PASSWORD_VERSION_BCRYPT -> {
                // BCrypt 格式哈希验证
                val result = BCrypt.verifyer().verify(password.toCharArray(), storedHash)
                result.verified
            }
            else -> {
                // 旧版 hashCode() 格式（不安全，仅用于兼容）
                // 如果匹配，提示用户重新设置密码
                val legacyHash = password.hashCode().toString()
                legacyHash == storedHash
            }
        }
    }

    /**
     * 检测是否使用旧版密码哈希格式
     * @return true 表示使用旧格式，需要提示用户重新设置密码
     */
    fun isLegacyPasswordHash(): Boolean {
        val passwordVersion = sharedPreferences.getInt(PREF_PASSWORD_VERSION, 1)
        return passwordVersion < PASSWORD_VERSION_BCRYPT
    }

    /**
     * 将旧版 hashCode() 哈希迁移为 BCrypt
     * 需要用户提供旧密码进行验证后调用
     */
    fun migratePasswordToBcrypt(oldPassword: String): Boolean {
        val storedHash = sharedPreferences.getString(PREF_MASTER_PASSWORD, null)
            ?: return false
        
        // 验证旧密码
        val legacyHash = oldPassword.hashCode().toString()
        if (legacyHash != storedHash) {
            return false
        }
        
        // 生成新的 BCrypt 哈希
        val newHash = BCrypt.withDefaults().hashToString(12, oldPassword.toCharArray())
        sharedPreferences.edit()
            .putString(PREF_MASTER_PASSWORD, newHash)
            .putInt(PREF_PASSWORD_VERSION, PASSWORD_VERSION_BCRYPT)
            .apply()
        
        return true
    }

    fun hasMasterPassword(): Boolean {
        return sharedPreferences.contains(PREF_MASTER_PASSWORD)
    }

    private fun getSecretKey(): SecretKey {
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/" +
            KeyProperties.BLOCK_MODE_GCM + "/" +
            KeyProperties.ENCRYPTION_PADDING_NONE
        )
    }
}
