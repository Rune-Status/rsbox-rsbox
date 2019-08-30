package io.rsbox.net.session

/**
 * @author Kyle Escobar
 */

class SessionRegistry {

    val sessions = hashMapOf<Session, Boolean>()

    fun add(session: Session) {
        sessions[session] = true
    }

    fun remove(session: Session) {
        sessions.remove(session)
    }

    fun count(): Int = sessions.size
}