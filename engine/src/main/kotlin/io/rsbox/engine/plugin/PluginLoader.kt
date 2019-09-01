package io.rsbox.engine.plugin

import io.rsbox.api.plugin.PluginConfiguration
import io.rsbox.api.plugin.RSPlugin
import mu.KLogging
import org.reflections.Reflections

/**
 * @author Kyle Escobar
 */

object PluginLoader : KLogging() {

    private val plugins = mutableListOf<RSPlugin>()

    private val reflections = Reflections("io.rsbox.plugin")

    fun init() {
        logger.info("Scanning for plugins...")
        this.loadPluginModules()

        logger.info("Finished loading {} server plugins...", plugins.size)
    }

    private fun loadPluginModules() {
        logger.info("Loading plugin modules.")
        val classes = reflections.getSubTypesOf(RSPlugin::class.java)

        if(classes.size == 0) {
            logger.info("No plugin modules were found.")
            return
        }

        classes.forEach { clazz ->
            this.loadPlugin(clazz)
        }
    }

    private fun <T : RSPlugin> loadPlugin(clazz: Class<T>) {
        val annotation = clazz.getDeclaredAnnotation(PluginConfiguration::class.java)
        val inst = clazz.newInstance() as T

        inst.name = annotation.name
        inst.version = annotation.version
        inst.revision = annotation.revision

        if(annotation.authors.isEmpty()) {
            inst.authors.add(annotation.author)
        } else {
            inst.authors.addAll(annotation.authors)
        }

        inst.onStart()
        logger.info("Loaded plugin ${inst.name}.")

        plugins.add(inst)
    }
}