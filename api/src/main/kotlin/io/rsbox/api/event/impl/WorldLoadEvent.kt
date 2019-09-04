package io.rsbox.api.event.impl

import io.rsbox.api.World
import io.rsbox.api.event.Event

/**
 * @author Kyle Escobar
 */

class WorldLoadEvent(val world: World) : Event()