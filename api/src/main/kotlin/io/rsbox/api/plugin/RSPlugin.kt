package io.rsbox.api.plugin

/**
 * @author Kyle Escobar
 */

abstract class RSPlugin {

    private var loaded = false

    var name: String = ""

    var revision: Int = -1

    var authors = mutableListOf<String>()

    var version: String = ""

    var dependencyPlugins = hashMapOf<String, String>()

    abstract fun onStart()

    abstract fun onStop()

}