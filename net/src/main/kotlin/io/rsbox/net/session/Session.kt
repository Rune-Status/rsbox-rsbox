package io.rsbox.net.session

import io.netty.channel.ChannelHandlerContext
import io.rsbox.engine.login.GameLoginRequest
import io.rsbox.net.AsyncMessage
import io.rsbox.net.Message
import io.rsbox.net.NetworkServer
import io.rsbox.net.pipeline.NormalCodecHandler
import io.rsbox.net.protocol.RSProtocol
import mu.KLogging
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author Kyle Escobar
 */

open class Session(val server: NetworkServer, val ctx: ChannelHandlerContext) {

    private var currentProtocol: RSProtocol = NetworkServer.PROTOCOLS.handshake

    private val sendQueue = ConcurrentLinkedQueue<Message>()

    val protocol: RSProtocol get() = currentProtocol

    var reconnecting: Boolean = false
    var seed: Long = -1L
    var lastLoginRequest: GameLoginRequest? = null

    fun onReady() {
        val p = ctx.pipeline()
        p.addBefore("handler","codec_handler", NormalCodecHandler(this))

        this.currentProtocol.onSet(this)
    }

    fun onDisconnect() {
        invalidate()
    }

    fun onError(cause: Throwable) {
        if(cause.stackTrace[0].methodName != "read0") {
            logger.warn("Session threw an exception: {}", cause)
        }
    }

    fun onMessageReceive(message: Message) {
        val handler = protocol.getMessageHandler(message.javaClass)
        handler.handle(this, message)
    }

    fun invalidate() {
        server.invalidateSession(this)
    }

    fun changeProtocol(newProtocol: RSProtocol) {
        this.currentProtocol.onUnset(this)
        this.currentProtocol = newProtocol
        this.currentProtocol.onSet(this)
    }

    fun writeMessage(message: Message) {
        if(message is AsyncMessage) {
            sendQueue.offer(message)
        } else {
            ctx.channel().writeAndFlush(message)
        }
    }

    private fun pulse() {

    }

    companion object : KLogging()
}