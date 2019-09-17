package io.rsbox.engine.net

import io.netty.channel.ChannelHandlerContext
import io.rsbox.config.Conf
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.game.model.entity.Player
import io.rsbox.engine.net.game.Message
import io.rsbox.engine.net.game.RSProtocol
import io.rsbox.engine.net.game.exception.IllegalOpcodeException
import io.rsbox.engine.net.game.packet.GamePacket
import io.rsbox.engine.net.pregame.handshake.HandshakeCodec
import io.rsbox.engine.net.pregame.js5.JS5Handler
import io.rsbox.engine.net.pregame.js5.JS5Request
import io.rsbox.engine.net.pregame.login.LoginHandler
import io.rsbox.engine.net.pregame.login.LoginRequest
import io.rsbox.util.IsaacRandom
import mu.KLogging
import java.util.concurrent.ArrayBlockingQueue

/**
 * @author Kyle Escobar
 */

class Session(val ctx: ChannelHandlerContext, val networkServer: NetworkServer) {

    private val maxPacketsPerPulse = Conf.SERVER[ServerSpec.max_packets_per_tick]

    private val sendQueue = ArrayBlockingQueue<Message>(maxPacketsPerPulse)

    val sessionId = (Math.random() * Long.MAX_VALUE).toLong()
    var seed: Long = -1L

    private val js5Handler = JS5Handler()
    private val loginHandler = LoginHandler()

    lateinit var encodeRandom: IsaacRandom
    lateinit var decodeRandom: IsaacRandom

    var player: Player? = null

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
        when (msg) {
            is JS5Request -> js5Handler.handle(this, msg)
            is LoginRequest -> loginHandler.handle(this, msg)
            is GamePacket -> this.decodePacket(msg)
            else -> throw Exception("Unable to handle received message in session [$sessionId].")
        }
    }

    fun onError(cause: Throwable) {
        if(cause.stackTrace[0].methodName != "read0" && cause.javaClass.simpleName != "IllegalOpcodeException") {
            logger.error("An error occurred in session[{}]: {}", sessionId, cause.printStackTrace())
        }
    }

    fun write(message: Message) {
        sendQueue.offer(message)
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
        while(sendQueue.size > 0) {
            val msg = sendQueue.poll()
            ctx.write(RSProtocol.encodeMessage(msg))
        }
    }

    private fun decodePacket(packet: GamePacket) {
        val message = RSProtocol.decodePacket(packet)
        RSProtocol.handleMessage(this, packet.opcode, message)
    }

    companion object : KLogging()
}