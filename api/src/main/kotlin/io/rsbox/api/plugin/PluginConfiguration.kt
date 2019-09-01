package io.rsbox.api.plugin

/**
 * @author Kyle Escobar
 */

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PluginConfiguration(
    val name: String,
    val version: String,
    val revision: Int,
    val author: String = "",
    val authors: Array<String> = []
)