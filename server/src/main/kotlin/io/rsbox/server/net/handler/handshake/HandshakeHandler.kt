package io.rsbox.server.net.handler.handshake

import io.rsbox.server.net.MessageHandler
import io.rsbox.server.Server
import io.rsbox.server.net.ServerResponseType
import io.rsbox.server.net.message.handshake.HandshakeRequest
import io.rsbox.server.net.message.handshake.HandshakeResponse
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

}