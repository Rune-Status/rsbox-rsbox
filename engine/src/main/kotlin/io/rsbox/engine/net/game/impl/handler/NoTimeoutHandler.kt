package io.rsbox.engine.net.game.impl.handler

import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.MessageHandler
import io.rsbox.engine.net.game.impl.message.NoTimeoutMessage

/**
 * @author Graviton #1697
 */

class NoTimeoutHandler : MessageHandler<NoTimeoutMessage> {

    override fun handle(session: Session, message: NoTimeoutMessage) {}

}