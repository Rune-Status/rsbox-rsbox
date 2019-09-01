package io.rsbox.engine.net

import io.netty.channel.ChannelHandlerContext
import io.rsbox.config.Conf
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.net.pregame.handshake.HandshakeCodec
import io.rsbox.engine.net.pregame.js5.JS5Handler
import io.rsbox.engine.net.pregame.js5.JS5Request
import io.rsbox.engine.net.pregame.login.LoginHandler
import io.rsbox.engine.net.pregame.login.LoginRequest
import io.rsbox.util.IsaacRandom
import mu.KLogging
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author Kyle Escobar
 */

class Session(val ctx: ChannelHandlerContext, val networkServer: NetworkServer) {

    private val sendQueue = ConcurrentLinkedQueue<Message>()
    private val maxPacketsPerPulse = Conf.SERVER[ServerSpec.max_packets_per_tick]

    val sessionId = (Math.random() * Long.MAX_VALUE).toLong()
    var seed: Long = -1L

    private val js5Handler = JS5Handler()
    private val loginHandler = LoginHandler()

    lateinit var encodeRandom: IsaacRandom
    lateinit var decodeRandom: IsaacRandom

    /**
     * Setup the initial pipelines.
     */
    fun onConnect() {
        val p = ctx.pipeline()

        p.addBefore("handler", "handshake_codec", HandshakeCodec(this))
    }

    fun onDisconnect() {
        close()
    }

    fun onMessageReceived(msg: Any) {
        if(msg is JS5Request) js5Handler.handle(this, msg)
        else if(msg is LoginRequest) loginHandler.handle(this, msg)
    }

    fun onError(cause: Throwable) {
        if(cause.stackTrace[0].methodName != "read0") {
            logger.error("An error occurred in session[{}]: {}", sessionId, cause.printStackTrace())
        }
    }

    fun write(msg: Message) {
        sendQueue.offer(msg)
    }

    fun flush() {
        if(ctx.channel().isActive) {
            ctx.channel().flush()
        }
    }

    fun close() {
        ctx.channel().close()
    }

    fun pulse() {
        sendQueuedMessages()
        flush()
    }

    private fun sendQueuedMessages() {
        var sent = 0
        while(sendQueue.size > 0 && sent < maxPacketsPerPulse) {
            val msg = sendQueue.poll()
            ctx.channel().write(msg)
            sent++
        }
    }

    companion object : KLogging()
}