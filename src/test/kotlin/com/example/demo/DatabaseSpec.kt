package com.example.demo

import io.kotest.core.spec.style.AnnotationSpec
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

abstract class DatabaseSpec : AnnotationSpec() {
    private val H2_CONNECTION = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;"
    private val H2_DRIVER = "org.h2.Driver"

    fun dbSetup(vararg tables: Table) {
        Database.connect(
            H2_CONNECTION,
            driver = H2_DRIVER
        )

        transaction { SchemaUtils.create(*tables) }
    }

    fun dbCleanup(vararg tables: Table) = transaction { SchemaUtils.drop(*tables) }
}
