package io.rsbox.engine.game.model.entity

import io.rsbox.util.IsaacRandom

/**
 * @author Kyle Escobar
 */

class Player : LivingEntity() {

    /**
     * Player Data
     */
    lateinit var username: String
    lateinit var passwordHash: String
    lateinit var uuid: String
    lateinit var displayName: String
    var privilege: Int = 0
    var banned: Boolean = false


    private fun register() {

    }

    fun login(encodeRandom: IsaacRandom, decodeRandom: IsaacRandom) {
        this.register()
        println("fskjlfdaklfjd")
    }
}