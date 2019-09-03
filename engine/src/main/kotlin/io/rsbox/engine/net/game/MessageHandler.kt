package io.rsbox.engine.net.game

import io.rsbox.engine.net.Session

/**
 * @author Kyle Escobar
 */

interface MessageHandler<T : Message> {

    fun handle(session: Session, message: T)

}