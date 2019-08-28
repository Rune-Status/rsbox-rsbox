package io.rsbox.net.impl.js5

import com.google.common.primitives.Ints
import io.rsbox.engine.Engine
import io.rsbox.net.MessageHandler
import io.rsbox.net.session.Session
import mu.KLogging
import net.runelite.cache.fs.Container
import net.runelite.cache.fs.jagex.CompressionType
import net.runelite.cache.fs.jagex.DiskStorage

/**
 * @author Kyle Escobar
 */

class JS5Handler : MessageHandler<Session, JS5Request> {

    val cacheStore = Engine.CACHE

    override fun handle(session: Session, message: JS5Request) {
        if(message.archive == -1 && message.index == -1) {
            return
        }

        if(message.index == 255) {
            encodeIndexData(session, message)
        } else {
            encodeFileData(session, message)
        }
    }

    private fun encodeIndexData(session: Session, message: JS5Request) {
        val data: ByteArray

        if(message.archive == 255) {
            if(cachedIndexes == null) {
                val buf = session.ctx.alloc().heapBuffer(cacheStore.indexes.size * 8)

                cacheStore.indexes.forEach { index ->
                    buf.writeInt(index.crc)
                    buf.writeInt(index.revision)
                }

                val container = Container(CompressionType.NONE, -1)
                container.compress(buf.array().copyOf(buf.readableBytes()), null)
                cachedIndexes = container.data
                buf.release()
            }
            data = cachedIndexes!!
        } else {
            val storage = cacheStore.storage as DiskStorage
            data = storage.readIndex(message.archive)
        }

        session.writeMessage(JS5Response(message.index, message.archive, data))
    }

    private fun encodeFileData(session: Session, message: JS5Request) {
        val index = cacheStore.findIndex(message.index)!!
        val archive = index.getArchive(message.archive)!!
        var data = cacheStore.storage.loadArchive(archive)

        if(data != null) {
            val compression = data[0]
            val length = Ints.fromBytes(data[1], data[2], data[3], data[4])
            val dataLength = length + (if(compression.toInt() != CompressionType.NONE)9 else 5)
            if(dataLength != length && data.size - dataLength == 2) {
                data = data.copyOf(data.size - 2)
            }

            session.writeMessage(JS5Response(message.index, message.archive, data))
        } else {
            logger.warn("Corrupt cache archive data. index={}, archive={}.", message.index, message.archive)
        }
    }

    companion object : KLogging() {
        private var cachedIndexes: ByteArray? = null
    }
}