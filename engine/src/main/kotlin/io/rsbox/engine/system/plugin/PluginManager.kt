package io.rsbox.engine.system.plugin

import io.github.classgraph.ClassGraph
import io.rsbox.api.plugin.RSPlugin
import io.rsbox.engine.Engine
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class PluginManager(private val engine: Engine) : io.rsbox.api.plugin.PluginManager {

    private val plugins = mutableListOf<RSPlugin>()

    override fun register(plugin: RSPlugin) {
        this.plugins.add(plugin)
        logger.info("Registered plugin {}.", plugin.javaClass.simpleName)
    }

    override fun unregister(plugin: RSPlugin) {
        this.plugins.remove(plugin)
    }

    override val loadedPlugins: Array<RSPlugin> = plugins.toTypedArray()

    internal fun loadPlugins() {
        this.scanPackagedPlugins()
        this.scanJarPlugins()

        logger.info("Finished loading {} plugins.", this.loadedPlugins.size)
    }

    private fun scanPackagedPlugins() {
        ClassGraph().enableAllInfo().whitelistModules().scan().use { result ->
            val pluginClassList = result
                .getSubclasses(RSPlugin::class.java.name)
                .directOnly()

            pluginClassList.forEach {
                val inst = it.loadClass(RSPlugin::class.java).getDeclaredConstructor().newInstance()
                this.register(inst)
            }
        }
    }

    private fun scanJarPlugins() {

    }

    companion object : KLogging()
}