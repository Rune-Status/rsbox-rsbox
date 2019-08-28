package io.rsbox.net

import io.netty.buffer.ByteBuf

/**
 * @author Kyle Escobar
 */

interface Codec<I : Message, O : Message> {

    fun encode(buf: ByteBuf, message: O): ByteBuf

    fun decode(buf: ByteBuf): I

    data class CodecRegistration(val opcode: Int, private val codec: Codec<*, *>) {

        @Suppress("UNCHECKED_CAST")
        fun <M : Message> getCodec(): Codec<*, M> {
            return codec as Codec<*, M>
        }

    }

}