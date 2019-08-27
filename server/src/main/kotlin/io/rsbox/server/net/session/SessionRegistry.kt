package io.rsbox.server.net.session

/**
 * @author Kyle Escobar
 */

class SessionRegistry {

    private val sessions = hashMapOf<GameSession, Boolean>()

    fun add(session: GameSession) {
        sessions[session] = true
    }

    fun remove(session: GameSession) {
        sessions.remove(session)
    }

}