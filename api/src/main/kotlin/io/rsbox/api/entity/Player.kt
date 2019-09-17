package io.rsbox.api.entity

import io.rsbox.api.interf.InterfaceManager

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

    val interfaces: InterfaceManager get() = _interfaces()
    fun _interfaces(): InterfaceManager
}