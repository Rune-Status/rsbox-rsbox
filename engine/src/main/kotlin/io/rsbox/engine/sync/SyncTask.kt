package io.rsbox.engine.sync

import io.rsbox.engine.game.model.entity.LivingEntity

/**
 * @author Kyle Escobar
 */

interface SyncTask<T : LivingEntity> {

    fun execute(livingEntity: T)

}