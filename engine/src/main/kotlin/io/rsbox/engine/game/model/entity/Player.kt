package io.rsbox.engine.game.model.entity

import io.rsbox.net.session.Session
import io.rsbox.util.IsaacRandom

/**
 * @author Kyle Escobar
 */

class Player : LivingEntity() {

    lateinit var session: Session

    lateinit var username: String
    lateinit var passwordHash: String
    lateinit var uuid: String
    lateinit var displayName: String

    var privilege: Int = 0

    var banned: Boolean = false

    private lateinit var encodeRandom: IsaacRandom
    private lateinit var decodeRandom: IsaacRandom


    private fun register() {

    }

    fun login(encodeRandom: IsaacRandom, decodeRandom: IsaacRandom) {
        this.encodeRandom = encodeRandom
        this.decodeRandom = decodeRandom

        this.register()
        println("fskjlfdaklfjd")
    }
}