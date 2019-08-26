package io.rsbox.server.config.spec

import com.uchuhimo.konf.ConfigSpec

/**
 * @author Kyle Escobar
 */

object ServerSpec : ConfigSpec("server") {
    // Server Settings
    val name by optional("RSBox Server", "name")
    val revision by optional(181, "revision")
    val auto_create_account by optional(true, "auto_create_account")

    // Network Settings
    val network_address by optional("0.0.0.0", "network.address")
    val network_port by optional(43594, "network.port")
    val network_threads by optional(2, "network.threads")
    val network_idle_timeout by optional(30, "network.idle_timeout")

    // Storage settings
    val storage_mode by optional("yaml", "storage.mode")
    // Storage MySQL
    val storage_mysql_host by optional("127.0.0.1", "storage.mysql.host")
    val storage_mysql_port by optional(3306, "storage.mysql.port")
    val storage_mysql_username by optional("sqluser", "storage.mysql.username")
    val storage_mysql_password by optional("sqlpassword123", "storage.mysql.password")
    val storage_mysql_database by optional("rsbox", "storage.mysql.database")
}