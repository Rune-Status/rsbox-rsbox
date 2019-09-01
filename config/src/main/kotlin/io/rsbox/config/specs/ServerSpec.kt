package io.rsbox.config.specs

import com.uchuhimo.konf.ConfigSpec

/**
 * @author Kyle Escobar
 */

object ServerSpec : ConfigSpec("server") {

    val name by optional("RSBox Server", "name")
    val revision by optional(182, "revision")

    // Network Related
    val network_address by optional("0.0.0.0", "network.address")
    val network_port by optional(43594, "network.port")
    val network_threads by optional(2, "network.threads")
    val max_packets_per_tick by optional(50, "network.max_packets_per_tick")

    // Game Related
    val game_login_threads by optional(3, "game.login_threads")
    val auto_create_accounts by optional(true, "game.auto_create_accounts")
    val game_pulse_interval by optional(600, "game.pulse_interval")
}