package io.rsbox.engine.game.model.interf

import io.rsbox.api.interf.DisplayMode
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap

/**
 * Based off Tomm0017's Interface system.
 *
 * @author Kyle Escobar
 */

class InterfaceSet {

    private val visible = Int2IntOpenHashMap()

    private var currentModal = -1

    var displayMode = DisplayMode.FIXED

    fun open(parent: Int, child: Int, interfaceId: Int) {
        val hash = (parent shl 16) or child
        if(visible.containsKey(hash)) {

        }
        visible[hash] = interfaceId
    }

    fun close(parent: Int): Int {
        val found = visible.filterValues { it == parent }.keys.firstOrNull()
        if(found != null) {
            closeByHash(found)
            return found
        }
        return -1
    }

    fun close(parent: Int, child: Int): Int = closeByHash((parent shl 16) or child)

    private fun closeByHash(hash: Int): Int {
        val found = visible.remove(hash)
        if(found != visible.defaultReturnValue()) {
            return found
        }
        return -1
    }

    fun openModal(parent: Int, child: Int, interfaceId: Int) {
        open(parent, child, interfaceId)
        currentModal = interfaceId
    }

    fun getModal(): Int = currentModal

    fun setModal(currentModal: Int) {
        this.currentModal = currentModal
    }

    fun isOccupied(parent: Int, child: Int): Boolean = visible.containsKey((parent shl 16) or child)

    fun isVisible(interfaceId: Int): Boolean = visible.values.contains(interfaceId)

    fun setVisible(parent: Int, child: Int, visible: Boolean) {
        val hash = (parent shl 16) or child
        if(visible) {
            this.visible[hash] = parent
        } else {
            this.visible.remove(hash)
        }
    }

    fun getInterfaceAt(parent: Int, child: Int): Int = visible.getOrDefault((parent shl 16) or child, -1)
}