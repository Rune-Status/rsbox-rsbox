package io.rsbox.config.specs

import com.uchuhimo.konf.ConfigSpec

/**
 * @author Kyle Escobar
 */

object ServerSpec : ConfigSpec("server") {

    val name by optional("RSBox Server", "name")
    val revision by optional(181, "revision")

    // Network Related
    val network_address by optional("0.0.0.0", "network.address")
    val network_port by optional(43594, "network.port")
    val network_threads by optional(2, "network.threads")

}