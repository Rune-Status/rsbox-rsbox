package io.rsbox.engine.net.pregame.js5

import com.google.common.primitives.Ints
import io.rsbox.engine.net.Session
import mu.KLogging
import net.runelite.cache.fs.Container
import net.runelite.cache.fs.Store
import net.runelite.cache.fs.jagex.CompressionType
import net.runelite.cache.fs.jagex.DiskStorage

/**
 * @author Kyle Escobar
 */

class JS5Handler {

    private var cacheStore: Store? = null

    fun handle(session: Session, msg: JS5Request) {
        if(cacheStore == null) {
            cacheStore = session.networkServer.engine.cacheStore
        }

        if(msg.index == 255) {
            encodeIndexData(session, msg)
        } else {
            encodeFileData(session, msg)
        }
    }

    private fun encodeIndexData(session: Session, msg: JS5Request) {
        val data:ByteArray

        if(msg.archive == 255) {
            if(cacheIndexes == null) {
                val buf = session.ctx.alloc().heapBuffer(cacheStore!!.indexes.size * 8)

                cacheStore!!.indexes.forEach { index ->
                    buf.writeInt(index.crc)
                    buf.writeInt(index.revision)
                }

                val compressedData = Container(CompressionType.NONE, -1)
                compressedData.compress(buf.array().copyOf(buf.readableBytes()), null)
                cacheIndexes = compressedData.data
                buf.release()
            }

            data = cacheIndexes!!
        } else {
            val storage = cacheStore!!.storage as DiskStorage
            data = storage.readIndex(msg.archive)
        }

        val response = JS5Response(
            index = msg.index,
            archive = msg.archive,
            data = data
        )

        session.ctx.writeAndFlush(response)
    }

    private fun encodeFileData(session: Session, msg: JS5Request) {
        val index = cacheStore!!.findIndex(msg.index)!!
        val archive = index.getArchive(msg.archive)!!
        var data = cacheStore!!.storage.loadArchive(archive)

        if(data != null) {
            val compression = data[0]
            val length = Ints.fromBytes(data[1], data[2], data[3], data[4])
            val expectedLength = length + (if(compression.toInt() != CompressionType.NONE) 9 else 5)

            if(expectedLength != length && data.size - expectedLength == 2) {
                data = data.copyOf(data.size - 2)
            }

            val response = JS5Response(
                index = msg.index,
                archive = msg.archive,
                data = data
            )

            session.ctx.writeAndFlush(response)
        } else {
            logger.warn("Corrupted cache archive was unable to be read. index={}, archive={}.", msg.index, msg.archive)
        }
    }

    companion object : KLogging() {
        private var cacheIndexes: ByteArray? = null
    }
}