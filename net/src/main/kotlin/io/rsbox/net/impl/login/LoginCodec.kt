package io.rsbox.net.impl.login

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.rsbox.engine.Engine
import io.rsbox.net.Codec
import io.rsbox.net.LoginState
import mu.KLogging
import java.math.BigInteger
import io.rsbox.util.BufferUtils.readString
import io.rsbox.util.BufferUtils.readJagexString
import io.rsbox.util.Xtea

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
        val payloadLength: Int

        if(buf.readableBytes() >= size) {
            val revision = buf.readInt()
            buf.skipBytes(Int.SIZE_BYTES)
            buf.skipBytes(Byte.SIZE_BYTES)
            if(revision == Engine.REVISION) {
                payloadLength = size - (Int.SIZE_BYTES + Int.SIZE_BYTES + Byte.SIZE_BYTES)
                return decodeLoginRequest(buf, payloadLength, opcode, revision)
            }
        } else {
            buf.resetReaderIndex()
        }

        return LoginRequest(error = true, errorResponse = LoginState.REVISION_MISMATCH)
    }

    private fun decodeLoginRequest(buf: ByteBuf, payloadLength: Int, opcode: Int, revision: Int): LoginRequest {
        if(buf.readableBytes() >= payloadLength) {
            buf.markReaderIndex()

            val secureBuf: ByteBuf = run {
                val length = buf.readUnsignedShort()
                val secureBuf = buf.readBytes(length)
                val rsa = BigInteger(secureBuf.toByteArraySafe()).modPow(Engine.RSA.exponent, Engine.RSA.modulus)
                Unpooled.wrappedBuffer(rsa.toByteArray())
            }

            // Test if encryption was successful.
            val success = secureBuf.readUnsignedByte().toInt() == 1
            if(!success) {
                buf.resetReaderIndex()
                buf.skipBytes(payloadLength)
                logger.info("Login request rejected due to invalid RSA encryption key.")
                return LoginRequest(error = true, errorResponse = LoginState.MALFORMED_PACKET)
            }

            /**
             * Lets generate the Xtea encrypted buffer.
             */
            val xteaKeys = IntArray(4) { secureBuf.readInt() }
            val seed = secureBuf.readLong()

            val authCode: Int
            val password: String?
            val previousXteaKeys = IntArray(4)

            val connectionType = LoginType.values().first { it.opcode == opcode }
            when(connectionType) {
                LoginType.RECONNECTING -> {
                    for(i in 0 until previousXteaKeys.size) {
                        previousXteaKeys[i] = secureBuf.readInt()
                    }

                    authCode = -1
                    password = null
                }

                LoginType.NORMAL -> {
                    val authType = secureBuf.readByte().toInt()
                    when(authType) {
                        0, 2 -> {
                            authCode = secureBuf.readUnsignedMedium()
                            secureBuf.skipBytes(Byte.SIZE_BYTES)
                        }

                        else -> {
                            authCode = secureBuf.readInt()
                        }
                    }

                    secureBuf.skipBytes(Byte.SIZE_BYTES)
                    password = secureBuf.readString()
                }
            }

            val xteaBuf = buf.decipherXteaBuf(xteaKeys)

            val username = xteaBuf.readString()

            val viewportSettings = xteaBuf.readByte().toInt()
            val viewportResizable = (viewportSettings shr 1) == 1
            val viewportWidth = xteaBuf.readUnsignedShort()
            val viewportHeight = xteaBuf.readUnsignedShort()

            xteaBuf.skipBytes(24)
            xteaBuf.readString()

            xteaBuf.skipBytes(Int.SIZE_BYTES)

            xteaBuf.skipBytes(Byte.SIZE_BYTES * 10)
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            xteaBuf.skipBytes(Byte.SIZE_BYTES * 4)
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            for(i in 0 until 4) { xteaBuf.readJagexString() }
            xteaBuf.skipBytes(Byte.SIZE_BYTES)
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            for(i in 0 until 2) { xteaBuf.readJagexString() }
            xteaBuf.skipBytes(Byte.SIZE_BYTES * 2)
            xteaBuf.skipBytes(Int.SIZE_BYTES * 4)
            xteaBuf.readJagexString()

            xteaBuf.skipBytes(Int.SIZE_BYTES * 3)

            val cacheCrcs = Engine.CACHE.indexes.map { it.crc }.toIntArray()
            val crcs = IntArray(cacheCrcs.size) { xteaBuf.readInt() }

            for(i in 0 until crcs.size) {
                if(i == 16) continue

                if(crcs[i] != cacheCrcs[i]) {
                    buf.resetReaderIndex()
                    buf.skipBytes(payloadLength)
                    logger.info("Login request for {} rejected due to cache crc mismatch.", username)
                    return LoginRequest(error = true, errorResponse = LoginState.REVISION_MISMATCH)
                }
            }

            return LoginRequest(
                username = username,
                password = password ?: "",
                revision = revision,
                xteaKeys = xteaKeys,
                reconnecting = connectionType == LoginType.RECONNECTING,
                viewportResizable = viewportResizable,
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
                authCode = authCode,
                uuid = "".toUpperCase(),
                seed = seed
            )
        }
        /**
         * If there are any errors, respond with Invalid credentials.
         */
        return LoginRequest(error = true, errorResponse = LoginState.INVALID_CREDENTIALS)
    }

    private fun ByteBuf.toByteArraySafe(): ByteArray {
        if(this.hasArray()) {
            return this.array()
        }

        val bytes = ByteArray(this.readableBytes())
        this.getBytes(this.readerIndex(), bytes)

        return bytes
    }

    private fun ByteBuf.decipherXteaBuf(xteaKeys: IntArray): ByteBuf {
        val data = ByteArray(readableBytes())
        readBytes(data)
        return Unpooled.wrappedBuffer(Xtea.decipher(xteaKeys, data, 0, data.size))
    }

    companion object : KLogging()
}