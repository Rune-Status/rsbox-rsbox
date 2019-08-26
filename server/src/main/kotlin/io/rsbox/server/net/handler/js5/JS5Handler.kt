package io.rsbox.server.net.handler.js5

import com.google.common.primitives.Ints
import io.rsbox.net.MessageHandler
import io.rsbox.server.Server
import io.rsbox.server.net.message.js5.JS5Request
import io.rsbox.server.net.message.js5.JS5Response
import io.rsbox.server.net.session.GameSession
import mu.KLogging
import net.runelite.cache.fs.Container
import net.runelite.cache.fs.jagex.CompressionType
import net.runelite.cache.fs.jagex.DiskStorage

/**
 * @author Kyle Escobar
 */

class JS5Handler : MessageHandler<GameSession, JS5Request> {

    private val cacheStore = Server.cacheStore

    override fun handle(session: GameSession, message: JS5Request) {

        if(message.ignore) return

        if(message.index == 255) {
            encodeIndexData(session, message)
        } else {
            encodeFileData(session, message)
        }
    }

    private fun encodeIndexData(session: GameSession, message: JS5Request) {
        val data: ByteArray

        if(message.archive == 255) {
            if(cachedIndexData == null) {
                val buf = session.channel.alloc().heapBuffer(cacheStore.indexes.size * 8)

                cacheStore.indexes.forEach { index ->
                    buf.writeInt(index.crc)
                    buf.writeInt(index.revision)
                }

                val container = Container(CompressionType.NONE, -1)
                container.compress(buf.array().copyOf(buf.readableBytes()), null)
                cachedIndexData = container.data
                buf.release()
            }
            data = cachedIndexData!!
        } else {
            val storage = cacheStore.storage as DiskStorage
            data = storage.readIndex(message.archive)
        }

        val response = JS5Response(index = message.index, archive = message.archive, data = data)
        session.send(response)
    }

    private fun encodeFileData(session: GameSession, message: JS5Request) {
        val index = cacheStore.findIndex(message.index)!!
        val archive = index.getArchive(message.archive)!!
        var data = cacheStore.storage.loadArchive(archive)

        if(data != null) {
            val compression = data[0]
            val length = Ints.fromBytes(data[1], data[2], data[3], data[4])
            val expectedLength = length + (if(compression.toInt() != CompressionType.NONE) 9 else 5)
            if(expectedLength != length && data.size - expectedLength == 2) {
                data = data.copyOf(data.size - 2)
            }

            val response = JS5Response(index = message.index, archive = message.archive, data = data)
            session.send(response)
        } else {
            logger.warn("Missing data from cache archive. [${message.index},${message.archive}]")
        }
    }

    companion object : KLogging() {
        private var cachedIndexData: ByteArray? = null
    }
}