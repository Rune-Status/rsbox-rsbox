package io.rsbox.engine.net.pregame.login

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import io.rsbox.engine.net.LoginState
import io.rsbox.engine.net.Session
import mu.KLogging
import java.math.BigInteger
import io.rsbox.util.BufferUtils.readString
import io.rsbox.util.BufferUtils.readJagexString
import io.rsbox.util.Xtea

/**
 * @author Kyle Escobar
 */

class LoginCodec(private val session: Session) : MessageToMessageCodec<ByteBuf, LoginResponse>() {

    override fun encode(ctx: ChannelHandlerContext?, msg: LoginResponse?, out: MutableList<Any>?) {

    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {

        var length = -1
        var reconnecting = false

        if(!msg.isReadable) {
            ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(LoginState.BAD_SESSION_ID.id))
                .addListener(ChannelFutureListener.CLOSE)
            return
        }

        val opcode = msg.readByte().toInt()
        val loginType = LoginRequestType.values().firstOrNull { it.opcode == opcode }

        if(loginType == LoginRequestType.RECONNECT) {
            reconnecting = true
        }
        else if(loginType == null) {
            ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(LoginState.BAD_SESSION_ID.id))
                .addListener(ChannelFutureListener.CLOSE)
            return
        }

        val size = msg.readUnsignedShort()

        val revision = msg.readInt()
        msg.skipBytes(Int.SIZE_BYTES)
        msg.skipBytes(Byte.SIZE_BYTES)

        if(revision == session.networkServer.engine.revision) {
            length = size - (Int.SIZE_BYTES + Int.SIZE_BYTES + Byte.SIZE_BYTES)
        } else {
            ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(LoginState.REVISION_MISMATCH.id))
                .addListener(ChannelFutureListener.CLOSE)
            return
        }

        this.decodeSecureBuf(ctx, msg, out, length, revision, reconnecting)
    }

    private fun decodeSecureBuf(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>, length: Int, revision: Int, reconnecting: Boolean) {
        if(buf.readableBytes() >= length) {
            buf.markReaderIndex()

            val secureBuf: ByteBuf = run {
                val secureBufLength = buf.readUnsignedShort()
                val secureBuf = buf.readBytes(secureBufLength)
                val rsaData = BigInteger(secureBuf.arrayTypeSafe()).modPow(
                    session.networkServer.engine.rsa.exponent,
                    session.networkServer.engine.rsa.modulus
                )
                Unpooled.wrappedBuffer(rsaData.toByteArray())
            }

            val success = secureBuf.readUnsignedByte().toInt() == 1
            if(!success) {
                buf.resetReaderIndex()
                buf.skipBytes(length)
                logger.info("Session [{}] login request rejected due to RSA key mismatch.", session.sessionId)
                ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(LoginState.COULD_NOT_COMPLETE_LOGIN.id))
                    .addListener(ChannelFutureListener.CLOSE)
                return
            }

            val xteaKeys = IntArray(4) { secureBuf.readInt() }
            val clientSeed = secureBuf.readLong()
            val serverSeed = session.seed

            val authCode: Int
            val password: String?
            val xteaBufferKeys = IntArray(4)

            if(reconnecting) {
                xteaBufferKeys.forEach { key ->
                    xteaBufferKeys[key] = secureBuf.readInt()
                }

                authCode = -1
                password = null
            } else {
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

            val xteaBuf = buf.decipher(xteaKeys)
            val username = xteaBuf.readString()

            if(clientSeed != serverSeed) {
                secureBuf.resetReaderIndex()
                secureBuf.skipBytes(length)
                logger.info("Username {} login request rejected due to seed mismatch.", username)
                ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(LoginState.COULD_NOT_COMPLETE_LOGIN.id))
                    .addListener(ChannelFutureListener.CLOSE)
                return
            }

            val clientSettings = xteaBuf.readByte().toInt()
            val clientResizable = (clientSettings shr 1) == 1
            val clientWidth = xteaBuf.readUnsignedShort()
            val clientHeight = xteaBuf.readUnsignedShort()

            /**
             * Tomm0017's code
             * Skips the unused machine info data.
             * Defined in the PlatformInfo class in runelite client.
             */
            xteaBuf.skipBytes(24) // random.dat data
            xteaBuf.readString()
            xteaBuf.skipBytes(Int.SIZE_BYTES)

            xteaBuf.skipBytes(Byte.SIZE_BYTES * 10)
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            xteaBuf.skipBytes(Byte.SIZE_BYTES)
            xteaBuf.skipBytes(Byte.SIZE_BYTES * 3)
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            xteaBuf.readJagexString()
            xteaBuf.readJagexString()
            xteaBuf.readJagexString()
            xteaBuf.readJagexString()
            xteaBuf.skipBytes(Byte.SIZE_BYTES)
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            xteaBuf.readJagexString()
            xteaBuf.readJagexString()
            xteaBuf.skipBytes(Byte.SIZE_BYTES * 2)
            xteaBuf.skipBytes(Int.SIZE_BYTES * 3)
            xteaBuf.skipBytes(Int.SIZE_BYTES)
            xteaBuf.readJagexString()

            xteaBuf.skipBytes(Int.SIZE_BYTES * 3)

            val cacheCrcs = session.networkServer.engine.cacheStore.indexes.map { it.crc }.toIntArray()
            val crcs = IntArray(cacheCrcs.size) { xteaBuf.readInt() }

            for(i in 0 until crcs.size) {
                if(i == 16) continue

                if(crcs[i] != cacheCrcs[i]) {
                    buf.resetReaderIndex()
                    buf.skipBytes(length)
                    logger.info("Username {} login request rejected due to cache checksum mismatch.", username)
                    ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(LoginState.REVISION_MISMATCH.id))
                        .addListener(ChannelFutureListener.CLOSE)
                    return
                }
            }

            val request = LoginRequest(
                username = username,
                password = password ?: "",
                reconnecting = reconnecting,
                revision = revision,
                xteaKeys = xteaKeys,
                clientResizable = clientResizable,
                clientWidth = clientWidth,
                clientHeight = clientHeight,
                authCode = authCode,
                session = session
            )

            out.add(request)
            return
        }

        ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(LoginState.COULD_NOT_COMPLETE_LOGIN.id))
            .addListener(ChannelFutureListener.CLOSE)
        return
    }

    private fun ByteBuf.arrayTypeSafe(): ByteArray {
        if(this.hasArray()) {
            return this.array()
        }

        val bytes = ByteArray(this.readableBytes())
        this.getBytes(this.readerIndex(), bytes)
        return bytes
    }

    private fun ByteBuf.decipher(xteaKeys: IntArray): ByteBuf {
        val data = ByteArray(readableBytes())
        readBytes(data)
        return Unpooled.wrappedBuffer(Xtea.decipher(xteaKeys, data, 0, data.size))
    }

    companion object : KLogging()
}