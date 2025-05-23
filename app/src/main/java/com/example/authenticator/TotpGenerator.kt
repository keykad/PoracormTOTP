package com.example.authenticator

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.*

class TotpGenerator {
    companion object {
        const val TIME_STEP = 30 // Интервал в секундах

        fun generateCode(secret: String): String {
            val key = Base32().decode(secret)
            val time = Date().time / 1000 / TIME_STEP
            val data = ByteArray(8)
            for (i in 7 downTo 0) {
                data[i] = (time shr (8 * (7 - i))).toByte()
            }
            val mac = Mac.getInstance("HmacSHA1")
            mac.init(SecretKeySpec(key, "HmacSHA1"))
            val hash = mac.doFinal(data)

            val offset = hash.last().toInt() and 0xF
            val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                    ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                    ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                    (hash[offset + 3].toInt() and 0xFF)
            return String.format("%06d", binary % 1000000)
        }
        fun getRemainingSeconds(): Int {
            val currentTime = System.currentTimeMillis() / 1000
            return TIME_STEP - (currentTime % TIME_STEP).toInt()
        }
    }
}