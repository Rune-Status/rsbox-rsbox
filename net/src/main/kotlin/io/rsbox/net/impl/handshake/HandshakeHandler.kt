package io.rsbox.net.impl.handshake

import io.rsbox.net.LoginState
import io.rsbox.net.MessageHandler
import io.rsbox.net.NetworkServer
import io.rsbox.net.impl.login.LoginType
import io.rsbox.net.session.Session
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class HandshakeHandler : MessageHandler<Session, HandshakeRequest> {

    override fun handle(session: Session, message: HandshakeRequest) {
        val handshake = HandshakeType.values().firstOrNull { it.opcode == message.type }
        when(handshake) {
            HandshakeType.JS5 -> handleJS5Handshake(session, message)
            HandshakeType.LOGIN -> handleLoginHandshake(session, message)
        }
    }

    private fun handleJS5Handshake(session: Session, message: HandshakeRequest) {
        if(message.revision != NetworkServer.revision) {
            logger.info("Session {} rejected due to client revision mismatch.",session.ctx.channel())
            session.writeMessage(HandshakeResponse(LoginState.REVISION_MISMATCH))
        } else {
            session.writeMessage(HandshakeResponse(LoginState.ACCEPTABLE))
            session.changeProtocol(NetworkServer.PROTOCOLS.js5)
        }
    }

    private fun handleLoginHandshake(session: Session, message: HandshakeRequest) {
        if(message.type == LoginType.RECONNECTING.opcode) {
            session.reconnecting = true
        }

        val seed = (Math.random() * Long.MAX_VALUE).toLong()
        session.seed = seed

        session.writeMessage(HandshakeResponse(LoginState.ACCEPTABLE))
        session.writeMessage(HandshakeSeedResponse(seed))

        session.changeProtocol(NetworkServer.PROTOCOLS.login)
    }

    companion object : KLogging()
}