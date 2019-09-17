package io.rsbox.api.plugin

import io.rsbox.api.Engine
import io.rsbox.api.RSBox
import io.rsbox.api.World
import io.rsbox.api.event.Event
import io.rsbox.api.event.EventManager
import kotlin.script.experimental.annotations.KotlinScript

/**
 * @author Kyle Escobar
 */

@KotlinScript(
    displayName = "RSBox Plugin Script",
    fileExtension = "rs.kts",
    compilationConfiguration = RSPluginConfiguration::class
)
abstract class RSPlugin {
    val engine: Engine get() = RSBox.engine
    val world: World get() = RSBox.world

    inline fun <reified E : Event> on_event(noinline logic: (E) -> Unit) {
        EventManager.registerListener(E::class, logic)
    }
}