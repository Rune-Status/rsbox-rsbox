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

    val x by optional(3221, "location.x")
    val z by optional(3218, "location.z")
    val height by optional(0, "location.height")
}