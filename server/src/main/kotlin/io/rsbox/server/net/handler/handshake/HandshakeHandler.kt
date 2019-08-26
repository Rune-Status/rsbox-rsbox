package io.rsbox.server.net.handler.handshake

import io.rsbox.net.MessageHandler
import io.rsbox.server.Server
import io.rsbox.server.net.ServerResponseType
import io.rsbox.server.net.message.handshake.HandshakeRequest
import io.rsbox.server.net.message.handshake.HandshakeResponse
import io.rsbox.server.net.session.GameSession

/**
 * @author Kyle Escobar
 */

class HandshakeHandler : MessageHandler<GameSession, HandshakeRequest> {

    override fun handle(session: GameSession, message: HandshakeRequest) {
        if(Server.REVISION != message.revision) {
            session.send(HandshakeResponse(ServerResponseType.REVISION_MISMATCH))
        } else {
            session.send(HandshakeResponse(ServerResponseType.ACCEPTABLE))
        }
    }

}