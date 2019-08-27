package io.rsbox.server.net

import io.rsbox.server.net.session.Session


/**
 * @author Kyle Escobar
 */

/**
 * Defines the logic to be executed after a message is decoded by a codec.
 * Handlers are only used for inbound messages as you do not need to process outbound
 * messages.
 */
interface MessageHandler<S : Session, M : Message> {

    /**
     * Executed to process the message.
     *
     * @param session The session this message arrived through.
     * @param message The message to be processed containing the data.
     */
    fun handle(session: S, message: M)

}