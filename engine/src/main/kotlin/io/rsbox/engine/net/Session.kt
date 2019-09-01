package io.rsbox.engine.net

import io.netty.channel.ChannelHandlerContext
import io.rsbox.engine.net.pregame.handshake.HandshakeCodec
import io.rsbox.engine.net.pregame.js5.JS5Handler
import io.rsbox.engine.net.pregame.js5.JS5Request
import io.rsbox.engine.net.pregame.login.LoginHandler
import io.rsbox.engine.net.pregame.login.LoginRequest
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class Session(val ctx: ChannelHandlerContext, val networkServer: NetworkServer) {

    val sessionId = (Math.random() * Long.MAX_VALUE).toLong()
    var seed: Long = -1L

    private val js5Handler = JS5Handler()
    private val loginHandler = LoginHandler()

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
        ctx.channel().write(msg)
    }

    fun flush() {
        if(ctx.channel().isActive) {
            ctx.channel().flush()
        }
    }

    fun close() {
        ctx.channel().close()
    }

    companion object : KLogging()
}