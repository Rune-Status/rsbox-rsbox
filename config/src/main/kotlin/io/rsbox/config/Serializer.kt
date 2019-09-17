package io.rsbox.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.Item
import com.uchuhimo.konf.source.json.toJson
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.source.yaml.toYaml
import java.io.File
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

/**
 * @author Kyle Escobar
 */

class Serializer(private val file: File, private val dataType: ConfigDataType = ConfigDataType.YAML, vararg defaultSpecs: ConfigSpec) {

    var config: Config = Config()

    private var isDirty: Boolean by Delegates.observable(true) { _, _, _ ->
        onDirty?.invoke()
    }

    private val specs = mutableListOf(*defaultSpecs)

    fun load(): Serializer {
        specs.forEach { spec ->
            config.addSpec(spec)
        }

        config = when(dataType) {
            ConfigDataType.YAML -> config.from.yaml.file(file)
            ConfigDataType.JSON -> config.from.json.file(file)
        }

        isDirty = false

        return this
    }

    fun save() {
        when(dataType) {
            ConfigDataType.YAML -> config.toYaml.toFile(file)
            ConfigDataType.JSON -> config.toJson.toFile(file)
        }
    }

    operator fun <T> get(item: Item<T>): T {
        return config[item]
    }

    operator fun <T> get(name: String): T {
        return config[name]
    }

    fun <T> getOrNull(item: Item<T>): T? {
        return config.getOrNull(item)
    }

    fun <T> getOrNull(name: String): T? {
        return config.getOrNull(name)
    }

    fun <T> delegate(item: Item<T>): ReadWriteProperty<Any?, T> {
        return config.property(item)
    }

    fun <T> delegate(name: String): ReadWriteProperty<Any?, T> {
        return config.property(name)
    }

    private var onDirty: (() -> Unit)? = null
}