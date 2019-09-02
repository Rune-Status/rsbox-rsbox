package io.rsbox.engine.service.xtea

import com.google.gson.Gson
import io.rsbox.config.PathConstants
import io.rsbox.engine.Engine
import io.rsbox.engine.service.Service
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import mu.KLogging
import net.runelite.cache.IndexType
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Kyle Escobar
 */

class XteaKeyService(private val engine: Engine) : Service {

    private val keys = Int2ObjectOpenHashMap<IntArray>()

    val validRegions: IntArray get() = keys.keys.toIntArray()

    override fun start() {
        val path = Paths.get(PathConstants.XTEAS_FILE_PATH)
        if(!Files.exists(path)) {
            throw FileNotFoundException("Xteas json file could not be found.")
        }

        this.loadXteasFile(path)

        this.loadKeys()
    }

    override fun stop() {

    }

    fun get(region: Int): IntArray {
        if(keys[region] == null) {
            //logger.warn("No XTEA keys found for region $region.")
            keys[region] = EMPTY_KEYS
        }
        return keys[region]!!
    }

    private fun loadKeys() {
        val maxRegions = Short.MAX_VALUE
        var totalRegions = 0
        val missingKeys = mutableListOf<Int>()

        val regionIndex = engine.cacheStore.getIndex(IndexType.MAPS)
        for(regionId in 0 until maxRegions) {
            val x = regionId shr 8
            val z = regionId and 0xFF

            regionIndex.findArchiveByName("m${x}_$z") ?: continue
            regionIndex.findArchiveByName("l${x}_$z") ?: continue

            totalRegions++

            if(get(regionId).contentEquals(EMPTY_KEYS)) {
                missingKeys.add(regionId)
            }
        }

        engine.xteaKeyService = this

        val validKeys = totalRegions - missingKeys.size
        logger.info("Loaded {} / {} ({}%) XTEA region keys.", validKeys, totalRegions,
            String.format("%.2f", (validKeys.toDouble() * 100.0) / totalRegions.toDouble()))
    }

    private fun loadXteasFile(path: Path) {
        val reader = Files.newBufferedReader(path)
        val xteas = Gson().fromJson(reader, Array<RegionXtea>::class.java)
        reader.close()
        xteas?.forEach { xtea ->
            keys[xtea.region] = xtea.keys
        }
    }

    private data class RegionXtea(val region: Int, val keys: IntArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as RegionXtea

            if (region != other.region) return false
            if (!keys.contentEquals(other.keys)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = region
            result = 31 * result + keys.contentHashCode()
            return result
        }
    }

    companion object : KLogging() {
        val EMPTY_KEYS = intArrayOf(0, 0, 0, 0)
    }
}