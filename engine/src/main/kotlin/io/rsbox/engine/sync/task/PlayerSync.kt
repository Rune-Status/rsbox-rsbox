package io.rsbox.engine.sync.task

import io.rsbox.engine.game.model.entity.Player
import io.rsbox.engine.sync.SyncTask

/**
 * @author Kyle Escobar
 */

object PlayerSync : SyncTask<Player> {

    override fun execute(livingEntity: Player) {
        val player = livingEntity
    }

}