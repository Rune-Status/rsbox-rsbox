package io.rsbox.engine.net.game.impl.handler

import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.MessageHandler
import io.rsbox.engine.net.game.impl.message.IgnoredMessage

/**
 * @author Kyle Escobar
 */

class IgnoredHandler : MessageHandler<IgnoredMessage> {

    override fun handle(session: Session, message: IgnoredMessage) {}

}