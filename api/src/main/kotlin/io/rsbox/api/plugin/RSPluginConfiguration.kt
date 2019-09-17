package io.rsbox.api.plugin

import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports

/**
 * @author Kyle Escobar
 */

object RSPluginConfiguration : ScriptCompilationConfiguration({
    defaultImports(
        "io.rsbox.api.*"
    )
})