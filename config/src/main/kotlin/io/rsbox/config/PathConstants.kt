package io.rsbox.config

/**
 * @author Kyle Escobar
 */

object PathConstants {

    const val CONFIG_SERVER_PATH = "rsbox/config/server.yml"
    const val CONFIG_MONGO_PATH = "rsbox/config/mongo.yml"
    const val CACHE_PATH = "rsbox/data/cache/"
    const val XTEAS_FILE_PATH = "rsbox/data/xteas/xteas.json"
    const val RSA_PRIVATE_PATH = "rsbox/data/rsa/private.pem"
    const val RSA_PUBLIC_PATH = "rsbox/data/rsa/public.pem"
    const val RSA_MODULUS_PATH = "rsbox/data/rsa/modulus.txt"
    const val PLAYER_SAVES_PATH = "rsbox/data/saves/"
    const val CONFIG_PATH = "api/src/main/kotlin/io/rsbox/api/consts/"

    /**
     * Repos
     */
    const val ASSETS_REPO = "https://api.github.com/repos/rsbox/cache/branches"
    const val CACHE_REPO = "https://github.com/rsbox/cache/raw/rev_<>/cache.zip"
    const val XTEAS_REPO = "https://github.com/rsbox/cache/raw/rev_<>/xteas.json"
}