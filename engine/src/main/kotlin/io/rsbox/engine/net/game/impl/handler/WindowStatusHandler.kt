package io.rsbox.engine.net.game.impl.handler

import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.MessageHandler
import io.rsbox.engine.net.game.impl.message.NoTimeoutMessage
import io.rsbox.engine.net.game.impl.message.WindowStatusMessage

/**
 * @author Graviton #1697
 */

class WindowStatusHandler : MessageHandler<WindowStatusMessage> {

    override fun handle(session: Session, message: WindowStatusMessage) {}

}