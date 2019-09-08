package io.rsbox.api.plugin

/**
 * @author Kyle Escobar
 */

interface PluginManager {

    fun register(plugin: RSPlugin)

    fun unregister(plugin: RSPlugin)

    val loadedPlugins: Array<RSPlugin>

}