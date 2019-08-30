package io.rsbox.config.specs

import com.uchuhimo.konf.ConfigSpec

/**
 * @author Kyle Escobar
 */

object PlayerSpec : ConfigSpec("player") {
    val username by required<String>("username")
    val password by required<String>("password")
    val display_name by optional("", "display_name")
    val privilege by optional(0, "privilege")
    val uuid by required<String>("uuid")
    val banned by optional(false, "banned")
}