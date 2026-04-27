package com.memoamber.desktop.data

import java.io.File
import java.security.SecureRandom
import java.security.Security
import java.util.Base64
import java.util.prefs.Preferences
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters

/**
 * 桌面端安全管理器
 * AES-256-GCM 加密 + Argon2id 密钥派生
 */
class DesktopSecurityManager {

    companion object {
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 128
        private const val SALT_LENGTH = 32
        private const val KEY_SIZE = 32
    }

    private val prefs = Preferences.userNodeForPackage(DesktopSecurityManager::class.java)
    private val dataDir = File(System.getProperty("user.home"), ".memoamber")
    private val keyDir = File(dataDir, "keys")

    init {
        Security.addProvider(BouncyCastleProvider())
        dataDir.mkdirs()
        keyDir.mkdirs()
    }

    fun hasMasterPassword(): Boolean =
        prefs.get("master_password_hash", null) != null

    fun setMasterPassword(password: String) {
        val salt = ByteArray(SALT_LENGTH).also { SecureRandom().nextBytes(it) }
        val hash = deriveKey(password, salt)
        prefs.put("master_password_hash", Base64.getEncoder().encodeToString(hash))
        prefs.put("password_salt", Base64.getEncoder().encodeToString(salt))

        val dek = ByteArray(KEY_SIZE).also { SecureRandom().nextBytes(it) }
        val encryptedDek = encryptWithDerivedKey(dek, password)
        File(keyDir, "dek.enc").writeBytes(encryptedDek)
    }

    fun verifyMasterPassword(password: String): Boolean {
        val storedHash = prefs.get("master_password_hash", null) ?: return false
        val salt = Base64.getDecoder().decode(prefs.get("password_salt", ""))
        return Base64.getDecoder().decode(storedHash).contentEquals(deriveKey(password, salt))
    }

    fun getDataEncryptionKey(password: String): ByteArray {
        val encryptedDek = File(keyDir, "dek.enc").readBytes()
        return decryptWithDerivedKey(encryptedDek, password)
    }

    fun encrypt(plaintext: ByteArray, key: ByteArray): ByteArray {
        val iv = ByteArray(GCM_IV_LENGTH).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return iv + cipher.doFinal(plaintext)
    }

    fun decrypt(ciphertext: ByteArray, key: ByteArray): ByteArray {
        val iv = ciphertext.copyOfRange(0, GCM_IV_LENGTH)
        val encrypted = ciphertext.copyOfRange(GCM_IV_LENGTH, ciphertext.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return cipher.doFinal(encrypted)
    }

    fun encryptString(plaintext: String, key: ByteArray): String =
        Base64.getEncoder().encodeToString(encrypt(plaintext.toByteArray(Charsets.UTF_8), key))

    fun decryptString(ciphertext: String, key: ByteArray): String =
        String(decrypt(Base64.getDecoder().decode(ciphertext), key), Charsets.UTF_8)

    private fun deriveKey(password: String, salt: ByteArray): ByteArray {
        val gen = Argon2BytesGenerator()
        gen.init(Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withIterations(3).withMemoryAsKB(65536).withParallelism(4).withSalt(salt).build())
        val key = ByteArray(KEY_SIZE)
        gen.generateBytes(password.toCharArray(), key)
        return key
    }

    private fun encryptWithDerivedKey(data: ByteArray, password: String): ByteArray {
        val salt = ByteArray(SALT_LENGTH).also { SecureRandom().nextBytes(it) }
        val key = deriveKey(password, salt)
        val iv = ByteArray(GCM_IV_LENGTH).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return salt + iv + cipher.doFinal(data)
    }

    private fun decryptWithDerivedKey(data: ByteArray, password: String): ByteArray {
        val salt = data.copyOfRange(0, SALT_LENGTH)
        val iv = data.copyOfRange(SALT_LENGTH, SALT_LENGTH + GCM_IV_LENGTH)
        val encrypted = data.copyOfRange(SALT_LENGTH + GCM_IV_LENGTH, data.size)
        val key = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return cipher.doFinal(encrypted)
    }
}
