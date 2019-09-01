package io.rsbox.engine.game.model.entity

/**
 * @author Kyle Escobar
 */

class LivingEntityList<T : LivingEntity>(private val entities: Array<T?>) {

    val capacity = entities.size

    private var count = 0

    operator fun get(index: Int): T? = entities[index]

    fun contains(entity: T): Boolean = entities[entity.index] == entity

    fun count(): Int = count

    fun count(predicate: (T) -> Boolean): Int {
        var count = 0
        for(element in entities) {
            if(element != null && predicate(element)) {
                count++
            }
        }
        return count
    }

    fun add(entity: T): Boolean {
        for(i in 1 until entities.size) {
            if(entities[i] == null) {
                entities[i] = entity
                entity.index = i
                count++
                return true
            }
        }
        return false
    }

    fun remove(entity: T): Boolean {
        if(entities[entity.index] == entity) {
            entities[entity.index] = null
            entity.index = -1
            count--
            return true
        }
        return false
    }

    fun forEach(action: (T) -> Unit) {
        for(element in entities) {
            if(element != null) {
                action(element)
            }
        }
    }
}