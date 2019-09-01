package io.rsbox.api.entity

/**
 * @author Kyle Escobar
 */

interface Player : LivingEntity {

    var username: String

    var passwordHash: String

    var uuid: String

    var displayName: String

    var privilege: Int

    var banned: Boolean

    var initiated: Boolean

}