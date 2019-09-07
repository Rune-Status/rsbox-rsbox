package io.rsbox.engine.net.game.impl.handler

import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.MessageHandler
import io.rsbox.engine.net.game.impl.message.MapBuildCompleteMessage

/**
 * @author Graviton #1697
 */

class MapBuildCompleteHandler : MessageHandler<MapBuildCompleteMessage> {

    override fun handle(session: Session, message: MapBuildCompleteMessage) {}

}