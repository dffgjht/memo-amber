package com.memoamber.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec

/**
 * 安全管理器
 *
 * - 主密码使用 PBKDF2WithHmacSHA256 加盐哈希存储（与桌面端 Argon2id 对齐的安全强度）
 * - 数据加密使用 Android Keystore 中的 AES-256-GCM 密钥
 * - 兼容旧版（String.hashCode）哈希，老用户升级后无需重新设置密码
 */
class SecurityManager(context: Context) {

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    private val sharedPreferences = context.getSharedPreferences("death_diary_secure", Context.MODE_PRIVATE)

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "death_diary_master_key"
        private const val PREF_MASTER_PASSWORD = "master_password_hash"

        // PBKDF2 参数
        private const val PBKDF2_ITERATIONS = 120_000
        private const val SALT_LENGTH_BYTES = 16
        private const val HASH_LENGTH_BITS = 256
        private const val HASH_PREFIX = "pbkdf2$"
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
                // 注意：仅在用户解锁设备后 30 秒内允许使用密钥，
                // 保证加密数据不会在锁屏状态下被读取
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(30)
                .build()

            keyGenerator.init(keyGenSpec)
            keyGenerator.generateKey()
        }
    }

    /**
     * 使用 Keystore AES-GCM 密钥加密数据
     * @return Pair(IV, 密文)
     */
    fun encryptData(data: String): Pair<ByteArray, ByteArray> {
        val cipher = getCipher()
        val secretKey = getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedData = cipher.doFinal(data.toByteArray())
        return Pair(cipher.iv, encryptedData)
    }

    /**
     * 使用 Keystore AES-GCM 密钥解密数据
     */
    fun decryptData(encryptedData: ByteArray, iv: ByteArray): String {
        val cipher = getCipher()
        val secretKey = getSecretKey()
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData)
    }

    /**
     * 设置主密码（PBKDF2 加盐哈希）
     */
    fun setMasterPassword(password: String) {
        val salt = ByteArray(SALT_LENGTH_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = pbkdf2(password, salt)
        val encoded = HASH_PREFIX + PBKDF2_ITERATIONS + "$" +
                Base64.encodeToString(salt, Base64.NO_WRAP) + "$" +
                Base64.encodeToString(hash, Base64.NO_WRAP)
        sharedPreferences.edit()
            .putString(PREF_MASTER_PASSWORD, encoded)
            .apply()
    }

    /**
     * 验证主密码
     * 优先使用 PBKDF2 校验；若存储的是旧版 hashCode 哈希则兼容校验
     */
    fun verifyMasterPassword(password: String): Boolean {
        val storedHash = sharedPreferences.getString(PREF_MASTER_PASSWORD, null)
            ?: return false

        if (storedHash.startsWith(HASH_PREFIX)) {
            return verifyPbkdf2(password, storedHash)
        }
        // 兼容旧版实现：String.hashCode 存储
        return storedHash == password.hashCode().toString()
    }

    fun hasMasterPassword(): Boolean {
        return sharedPreferences.contains(PREF_MASTER_PASSWORD)
    }

    private fun verifyPbkdf2(password: String, stored: String): Boolean {
        return try {
            val parts = stored.removePrefix(HASH_PREFIX).split("$")
            if (parts.size != 3) return false
            val iterations = parts[0].toIntOrNull() ?: return false
            val salt = Base64.decode(parts[1], Base64.NO_WRAP)
            val expectedHash = Base64.decode(parts[2], Base64.NO_WRAP)
            val actualHash = pbkdf2(password, salt, iterations)
            actualHash.contentEquals(expectedHash)
        } catch (e: Exception) {
            false
        }
    }

    private fun pbkdf2(
        password: String,
        salt: ByteArray,
        iterations: Int = PBKDF2_ITERATIONS
    ): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, HASH_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
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
