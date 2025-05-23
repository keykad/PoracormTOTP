package com.example.authenticator

class Base32 {
    private val base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray()
    private val indexTable = IntArray(128)

    init {
        for (i in base32Chars.indices) {
            indexTable[base32Chars[i].code] = i
        }
    }
    fun decode(input: String): ByteArray {
        val cleanedInput = input.replace("=", "") // Убираем возможные символы "="
        val byteArray = ByteArray((cleanedInput.length * 5 + 7) / 8) // Ручной расчет длины массива байтов
        var byteIndex = 0
        var buffer = 0
        var bitsInBuffer = 0

        for (char in cleanedInput) {
            val charValue = indexTable[char.code]
            if (charValue < 0) {
                throw IllegalArgumentException("Неправильный символ: $char")
            }
            buffer = (buffer shl 5) or charValue
            bitsInBuffer += 5

            while (bitsInBuffer >= 8) {
                bitsInBuffer -= 8
                byteArray[byteIndex++] = (buffer shr bitsInBuffer).toByte()
            }
        }
        return byteArray.copyOf(byteIndex) // Возвращаем только нужное количество байтов
    }
}