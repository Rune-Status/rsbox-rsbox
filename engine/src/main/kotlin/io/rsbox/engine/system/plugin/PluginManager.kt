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

    override val loadedPlugins: Array<RSPlugin> get() = plugins.toTypedArray()

    internal fun loadPlugins() {
        this.scanPackagedPlugins()
        this.scanJarPlugins()

        logger.info("Finished loading {} plugins.", this.loadedPlugins.size)
    }

    private fun scanPackagedPlugins() {
        ClassGraph().enableAllInfo().scan().use { result ->
            val pluginClassList = result.getSubclasses(RSPlugin::class.java.name).directOnly()
            pluginClassList.forEach { classInfo ->
                val scriptClass = classInfo.loadClass(RSPlugin::class.java)
                val scriptConstructor = scriptClass.getConstructor()

                val instance = scriptConstructor.newInstance()

                plugins.add(instance)
                logger.info { "Loaded plugin ${scriptClass.simpleName}." }
            }
        }
    }

    private fun scanJarPlugins() {

    }

    companion object : KLogging()
}