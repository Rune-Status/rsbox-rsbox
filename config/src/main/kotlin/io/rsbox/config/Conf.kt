package io.rsbox.config

import io.rsbox.config.specs.ServerSpec
import java.io.File

/**
 * @author Kyle Escobar
 */

object Conf {

    val SERVER = Serializer(File(PathConstants.CONFIG_SERVER_PATH), ConfigDataType.YAML, ServerSpec).load()

}