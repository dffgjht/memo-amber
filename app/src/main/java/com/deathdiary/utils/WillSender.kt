package com.deathdiary.utils

import android.content.Context
import android.content.Intent
import com.deathdiary.data.entities.Will
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object WillSender {

    data class EmailConfig(
        val smtpHost: String,
        val smtpPort: Int,
        val username: String,
        val password: String,
        val senderEmail: String,
        val useSsl: Boolean = true
    )

    data class SmsConfig(
        val apiUrl: String = "",
        val apiKey: String = "",
        val senderName: String = ""
    )

    suspend fun checkAndSendWills(
        context: Context,
        wills: List<Will>,
        emailConfig: EmailConfig?,
        smsConfig: SmsConfig?
    ): List<Pair<Will, Boolean>> {
        val results = mutableListOf<Pair<Will, Boolean>>()
        val currentTime = System.currentTimeMillis()
        for (will in wills) {
            if (will.isReleased) continue
            val shouldRelease = when (will.releaseCondition) {
                "date" -> will.releaseDate != null && currentTime >= will.releaseDate
                "manual" -> false
                else -> false
            }
            if (shouldRelease) {
                val success = when (will.contactType) {
                    "email" -> sendEmail(context, will, emailConfig)
                    "sms" -> sendSms(context, will, smsConfig)
                    else -> false
                }
                results.add(will to success)
            }
        }
        return results
    }

    private suspend fun sendEmail(context: Context, will: Will, config: EmailConfig?): Boolean {
        if (config == null) return sendEmailViaIntent(context, will)
        return try {
            withContext(Dispatchers.IO) {
                val props = Properties().apply {
                    put("mail.smtp.host", config.smtpHost)
                    put("mail.smtp.port", config.smtpPort.toString())
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.ssl.enable", config.useSsl.toString())
                    put("mail.smtp.starttls.enable", "true")
                }
                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(config.username, config.password)
                    }
                })
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(config.senderEmail))
                    setRecipient(Message.RecipientType.TO, InternetAddress(will.recipientContact))
                    subject = "[death-diary] " + will.title
                    setText(buildEmailBody(will))
                }
                Transport.send(message)
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            sendEmailViaIntent(context, will)
        }
    }

    private suspend fun sendEmailViaIntent(context: Context, will: Will): Boolean {
        return withContext(Dispatchers.Main) {
            try {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(will.recipientContact))
                    putExtra(Intent.EXTRA_SUBJECT, "[death-diary] " + will.title)
                    putExtra(Intent.EXTRA_TEXT, buildEmailBody(will))
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private suspend fun sendSms(context: Context, will: Will, config: SmsConfig?): Boolean {
        if (config == null || config.apiUrl.isBlank()) return sendSmsViaIntent(context, will)
        return try {
            withContext(Dispatchers.IO) {
                val body = buildSmsBody(will)
                val encodedBody = java.net.URLEncoder.encode(body, "UTF-8")
                val urlStr = config.apiUrl + "?apikey=" + config.apiKey + "&to=" + will.recipientContact + "&message=" + encodedBody
                val client = java.net.URL(urlStr)
                val conn = client.openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 10000
                conn.readTimeout = 10000
                val responseCode = conn.responseCode
                conn.disconnect()
                responseCode in 200..299
            }
        } catch (e: Exception) {
            e.printStackTrace()
            sendSmsViaIntent(context, will)
        }
    }

    private suspend fun sendSmsViaIntent(context: Context, will: Will): Boolean {
        return withContext(Dispatchers.Main) {
            try {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = android.net.Uri.parse("smsto:" + will.recipientContact)
                    putExtra("sms_body", buildSmsBody(will))
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun buildEmailBody(will: Will): String {
        val dateStr = will.releaseDate?.let {
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(it))
        } ?: "N/A"
        return "[death-diary]\n" + will.title + "\n\n" + will.content + "\n\n---\nRecipient: " + will.recipientName + "\nScheduled: " + dateStr
    }

    private fun buildSmsBody(will: Will): String {
        return "[" + will.title + "] " + will.content
    }
}
