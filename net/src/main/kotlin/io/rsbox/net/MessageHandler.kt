package io.rsbox.net

import io.rsbox.net.session.Session

/**
 * @author Kyle Escobar
 */

interface MessageHandler<S: Session, M: Message> {

    fun handle(session: S, message: M)

}