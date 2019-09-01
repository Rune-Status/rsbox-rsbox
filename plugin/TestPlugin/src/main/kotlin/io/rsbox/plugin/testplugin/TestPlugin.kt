package io.rsbox.plugin.testplugin

import io.rsbox.api.plugin.PluginConfiguration
import io.rsbox.api.plugin.RSPlugin

/**
 * @author Kyle Escobar
 */

@PluginConfiguration(
    name = "Test Plugin",
    version = "1.0",
    revision = 182,
    author = "Kyle Escobar"
)
class TestPlugin : RSPlugin() {

    override fun onStart() {

    }

    override fun onStop() {

    }

}