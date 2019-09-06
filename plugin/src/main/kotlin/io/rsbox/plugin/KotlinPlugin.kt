package io.rsbox.plugin

import kotlin.script.experimental.annotations.KotlinScript

/**
 * @author Kyle Escobar
 */

@KotlinScript("Kotlin Plugin")
abstract class KotlinPlugin {

    fun helloWorld() = println("Hello World")

}