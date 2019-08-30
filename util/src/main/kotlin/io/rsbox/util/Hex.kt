package io.rsbox.util

import java.lang.StringBuilder

/**
 * @author Kyle Escobar
 */

object Hex {

    fun toHexString(ba: ByteArray): String {
        val str = StringBuilder()
        for (i in ba.indices)
            str.append(String.format("%x", ba[i]))
        return str.toString()
    }

    fun fromHexString(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

}