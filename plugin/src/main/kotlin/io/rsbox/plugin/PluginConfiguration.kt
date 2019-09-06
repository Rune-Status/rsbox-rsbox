package io.rsbox.plugin

import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports

/**
 * @author Kyle Escobar
 */

object PluginConfiguration : ScriptCompilationConfiguration({
    defaultImports(
        "io.rsbox.plugin.*",
        "io.rsbox.game.*",
        "io.rsbox.api.*"
    )
})