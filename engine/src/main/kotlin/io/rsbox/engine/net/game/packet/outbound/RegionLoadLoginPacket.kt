package io.rsbox.engine.net.game.packet.outbound

import io.netty.buffer.Unpooled
import io.rsbox.engine.game.model.Chunk
import io.rsbox.engine.game.model.Tile
import io.rsbox.engine.net.game.Packet
import io.rsbox.engine.net.game.model.*
import io.rsbox.engine.service.xtea.XteaKeyService

/**
 * @author Kyle Escobar
 */

class RegionLoadLoginPacket(
    private val index: Int = -1,
    private val tile: Tile? = null,
    private val tiles: IntArray = intArrayOf(),
    private val xteaKeyService: XteaKeyService? = null
) : Packet(opcode = 0, type = PacketType.VARIABLE_SHORT){

    override fun encode() {
        println("Player index = $index")
        // Gpi Data
        this.encodeGpiData()

        // Z
        packet.put(type = DataType.SHORT, order = DataOrder.LITTLE, transformation = DataTransformation.ADD, value = (tile!!.z shr 3))

        // X
        packet.put(type = DataType.SHORT, transformation = DataTransformation.ADD, value = (tile.x shr 3))

        // Xteas
        this.encodeXteaData()
    }

    private fun encodeGpiData() {
        val buf = GamePacketBuilder()

        buf.switchToBitAccess()
        buf.putBits(30, tile!!.asPackedInteger)
        for(i in 1 until 2048) {
            if( i != index) {
                buf.putBits(18, tiles[i])
            }
        }
        buf.switchToByteAccess()

        val gpi = ByteArray(buf.byteBuf.readableBytes())
        buf.byteBuf.readBytes(gpi)

        packet.putBytes(gpi)
    }

    private fun encodeXteaData() {
        val chunkX = tile!!.x shr 3
        val chunkZ = tile.z shr 3

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
                if(! forceSend || z != 49 && z != 149 && z != 147 && x != 50 && (x != 49 || z != 47)) {
                    val region = z + (x shl 8)
                    val keys = xteaKeyService!!.get(region)
                    for(xteaKey in keys) {
                        buf.writeInt(xteaKey)
                    }
                    count++
                }
            }
        }
        buf.setShort(0, count)

        val xteas = ByteArray(buf.readableBytes())
        buf.readBytes(xteas)

        packet.putBytes(xteas)
    }

}