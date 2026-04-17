package com.deathdiary.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricAuthManager(private val context: Context) {

    private var promptInfo: BiometricPrompt.PromptInfo

    init {
        // 支持指纹和人脸识别
        // BIOMETRIC_STRONG 包含指纹、虹膜等强生物识别
        // BIOMETRIC_WEAK 包含人脸、简单生物识别
        // 使用 BIOMETRIC_STRONG | BIOMETRIC_WEAK 支持两种方式，优先使用强认证（指纹）
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                             BiometricManager.Authenticators.BIOMETRIC_WEAK
        
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("存证纪解锁")
            .setSubtitle("请使用指纹或面容解锁")
            .setNegativeButtonText("使用密码")
            .setAllowedAuthenticators(authenticators)
            .build()
    }

    fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(context)
        // 先检查强认证（指纹），再检查弱认证（人脸）
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                BiometricManager.BIOMETRIC_SUCCESS -> true
                else -> false
            }
        }
    }

    fun getBiometricStatus(): String {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> "指纹识别可用"
            else -> when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                BiometricManager.BIOMETRIC_SUCCESS -> "人脸识别可用"
                else -> "设备不支持生物识别"
            }
        }
    }

    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (!canAuthenticate()) {
            onFailure("设备不支持生物识别或未设置")
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        onFailure(errString.toString())
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailure("生物识别失败")
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }
}
