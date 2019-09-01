package io.rsbox.config.specs

import com.uchuhimo.konf.ConfigSpec

/**
 * @author Kyle Escobar
 */

object ServerSpec : ConfigSpec("server") {

    val name by optional("RSBox Server", "name")
    val revision by optional(182, "revision")
    val encryption_key by optional("YOUR-ENCRYPTION-KEY-HERE", "encryption_key")

    // Network Related
    val network_address by optional("0.0.0.0", "network.address")
    val network_port by optional(43594, "network.port")
    val network_threads by optional(2, "network.threads")

    // Game Related
    val game_login_threads by optional(3, "game.login_threads")
    val auto_create_accounts by optional(true, "game.auto_create_accounts")
}