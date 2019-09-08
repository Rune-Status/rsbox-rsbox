package io.rsbox.api.plugin

import kotlin.script.experimental.annotations.KotlinScript

/**
 * @author Kyle Escobar
 */

@KotlinScript(
    displayName = "RSBox Plugin Script",
    fileExtension = "rs.kts"
)
abstract class RSPlugin {

    fun helloWorld() = println("This is a test.")

}