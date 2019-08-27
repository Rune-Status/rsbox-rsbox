package io.rsbox.server.net.handler.handshake

import io.rsbox.server.net.MessageHandler
import io.rsbox.server.Server
import io.rsbox.server.net.ServerResponseType
import io.rsbox.server.net.message.handshake.HandshakeRequest
import io.rsbox.server.net.message.handshake.HandshakeResponse
import io.rsbox.server.net.message.handshake.HandshakeSeedResponse
import io.rsbox.server.net.session.GameSession

/**
 * @author Kyle Escobar
 */

/**
 * Processes the handshake inbound message.
 * Changes the session's current protocol to the required one
 * after successfully sending the response through the previous protocol.
 */
class HandshakeHandler : MessageHandler<GameSession, HandshakeRequest> {

    override fun handle(session: GameSession, message: HandshakeRequest) {
        when(message.type) {
            15 -> handleJS5Handshake(session, message)
            14 -> handleLoginHandshake(session, message)
        }
    }

    /**
     * Handles the JS5 handshake.
     * The client sent its revision along with this packet so we compare it to
     * the server revision specified in server.yml.
     *
     * @param session The session this handshake came from.
     * @param message The message containing the handshake data.
     */
    private fun handleJS5Handshake(session: GameSession, message: HandshakeRequest) {
        if(Server.REVISION != message.revision) {
            session.send(HandshakeResponse(ServerResponseType.REVISION_MISMATCH))
        } else {
            session.send(HandshakeResponse(ServerResponseType.ACCEPTABLE))!!.addListener { future ->
                if(future.isSuccess) {
                    session.protocol = session.protocolProvider.js5
                }
            }
        }
    }

    /**
     * Handles the login handshake.
     * The server generates a random number to be used as a seed to create the secure XTEA buffer.
     * This is sent after sending the client a Server response acceptable (byte value of 0)
     * This is to prevent MITM attacks / prevent sniffing passwords from login requests.
     *
     * Sets the protocol to login after sending this.
     *
     * @param session The session this handshake came from.
     * @param message The message containing the data.
     */
    private fun handleLoginHandshake(session: GameSession, message: HandshakeRequest) {
        val seed = (Math.random() * Long.MAX_VALUE).toLong()

        session.send(HandshakeResponse(ServerResponseType.ACCEPTABLE))
        session.send(HandshakeSeedResponse(seed))!!.addListener { future ->
            if(future.isSuccess) {
                session.protocol = session.protocolProvider.login
            }
        }
    }

}