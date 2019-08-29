package io.rsbox.net.impl.login

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.rsbox.engine.Engine
import io.rsbox.net.Codec
import io.rsbox.net.LoginState
import mu.KLogging
import java.math.BigInteger

/**
 * @author Kyle Escobar
 */

class LoginCodec : Codec<LoginRequest, LoginResponse> {

    override fun encode(buf: ByteBuf, message: LoginResponse): ByteBuf {
        buf.writeByte(2)
        buf.writeByte(13)
        buf.writeByte(0)
        buf.writeInt(0)
        buf.writeByte(message.privilege)
        buf.writeBoolean(true)
        buf.writeShort(message.index)
        buf.writeBoolean(true)
        return buf
    }


    override fun decode(buf: ByteBuf): LoginRequest {
        buf.resetReaderIndex()
        buf.markReaderIndex()
        val opcode = buf.readByte().toInt()
        val size = buf.readUnsignedShort()
        var payloadLength = -1

        if(buf.readableBytes() >= size) {
            val revision = buf.readInt()
            buf.skipBytes(Int.SIZE_BYTES)
            buf.skipBytes(Byte.SIZE_BYTES)
            if(revision == Engine.REVISION) {
                payloadLength = size - (Int.SIZE_BYTES + Int.SIZE_BYTES + Byte.SIZE_BYTES)
                return decodePayload(buf, payloadLength)
            }
        } else {
            buf.resetReaderIndex()
        }

        return LoginRequest(error = true, errorResponse = LoginState.REVISION_MISMATCH)
    }

    private fun decodePayload(buf: ByteBuf, payloadLength: Int): LoginRequest {
        if(buf.readableBytes() >= payloadLength) {
            buf.markReaderIndex()

            val secureBuf = decryptSecureBuffer(buf)

            // Test if encryption was successful.
            val success = secureBuf.readUnsignedByte().toInt() == 1
            if(!success) {
                buf.resetReaderIndex()
                buf.skipBytes(payloadLength)
                logger.info("Login request rejected due to invalid RSA encryption key.")
                return LoginRequest(error = true, errorResponse = LoginState.MALFORMED_PACKET)
            }
        }

        return LoginRequest(error = true, errorResponse = LoginState.INVALID_CREDENTIALS)
    }

    private fun decryptSecureBuffer(buf: ByteBuf): ByteBuf {
        val secureBufLength = buf.readUnsignedShort()
        val secureBufData = buf.readBytes(secureBufLength)
        val rsaValue = BigInteger(secureBufData.array()).modPow(Engine.RSA.exponent, Engine.RSA.modulus)
        return Unpooled.wrappedBuffer(rsaValue.toByteArray())
    }

    companion object : KLogging()
}