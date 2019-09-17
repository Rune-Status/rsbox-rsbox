package io.rsbox.engine.net.game.impl.encoder

import io.netty.buffer.Unpooled
import io.rsbox.engine.game.model.Chunk
import io.rsbox.engine.net.game.MessageEncoder
import io.rsbox.engine.net.game.impl.message.RegionRebuildMessage
import io.rsbox.engine.net.game.packet.*

/**
 * @author Kyle Escobar
 */

class RegionRebuildEncoder : MessageEncoder<RegionRebuildMessage> {

    /**
     * For client decoding code, in runelite search the source '@Export("loadRegions")'
     */

    override fun encode(message: RegionRebuildMessage, packet: GamePacketBuilder): GamePacket {

        if(message.playerIndex > -1 && message.tiles != null) {
            val gpiBytes = this.encodeGpiData(message)
            packet.putBytes(gpiBytes)
        }

        /**
         * The tile Z value
         */
        packet.put(
            type = DataType.SHORT,
            order = DataOrder.LITTLE,
            transformation = DataTransformation.ADD,
            value = message.tile.z shr 3
        )

        /**
         * The tile X value
         */
        packet.put(
            type = DataType.SHORT,
            transformation = DataTransformation.ADD,
            value = message.tile.x shr 3
        )

        /**
         * Xtea Region decryption keys
         */
        val xteaKeyBytes = this.encodeXteaKeys(message)
        packet.putBytes(xteaKeyBytes)

        return packet.toGamePacket()
    }

    private fun encodeGpiData(message: RegionRebuildMessage): ByteArray {
        val buf = GamePacketBuilder()

        buf.switchToBitAccess()

        buf.putBits(30, message.tile.asPackedInteger)

        for(i in 1 until 2048) {
            if(i != message.playerIndex) {
                buf.putBits(18, message.tiles!![i])
            }
        }

        buf.switchToByteAccess()

        val gpi = ByteArray(buf.byteBuf.readableBytes())
        buf.byteBuf.readBytes(gpi)

        return gpi
    }

    /**
     * Encodes the 4 XTEA encryption keys for a given region that are read from
     * the xteas.json data file in data/xteas/.
     */
    private fun encodeXteaKeys(message: RegionRebuildMessage): ByteArray {
        val chunkX = message.tile.x shr 3
        val chunkZ = message.tile.x shr 3

        val lx = (chunkX - (Chunk.MAX_VIEWPORT shr 4)) shr 3
        val rx = (chunkX + (Chunk.MAX_VIEWPORT shr 4)) shr 3
        val lz = (chunkZ - (Chunk.MAX_VIEWPORT shr 4)) shr 3
        val rz = (chunkZ + (Chunk.MAX_VIEWPORT shr 4)) shr 3

        val buf = Unpooled.buffer(Short.SIZE_BYTES + (Int.SIZE_BYTES * 10))
        var forceSend = false

        if((chunkX / 8 == 48 || chunkX / 8 == 49) && chunkZ / 8 == 48) {
            forceSend = true
        }

        if(chunkX / 8 == 48 && chunkZ / 8 == 148) {
            forceSend = true
        }

        var count = 0

        buf.writeShort(count)

        for(x in lx..rx) {
            for(z in lz..rz) {
                if(!forceSend || z != 49 && z != 149 && z != 147 && x != 50 && (x != 49 || z != 47)) {
                    val region = z + (x shl 8)
                    val keys = message.xteaKeyService.get(region)
                    for(key in keys) {
                        buf.writeInt(key)
                    }
                    count++
                }
            }
        }

        buf.setShort(0, count)

        val xteas = ByteArray(buf.readableBytes())
        buf.readBytes(xteas)
        return xteas
    }
}