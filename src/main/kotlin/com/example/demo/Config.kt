package com.example.demo

import io.github.cdimascio.dotenv.dotenv

object Config {
    private val dotenv = dotenv { ignoreIfMissing = true }
    // dotenv checks a `.env` file first, and if it does not exist it will check for
    // system environment variables.
    // If we know it won't be set in the `.env` file ever we can just check System
    val flyAppName: String? = dotenv["FLY_APP_NAME"]
    val jdbcDatabaseUrl: String = dotenv["DATABASE_URL"]
        ?.replace("postgres://(\\w*):(\\w*)@(.*)".toRegex(), "jdbc:postgresql://\\3?user=\\1&password=\\2")
        ?: dotenv["JDBC_DATABASE_URL"]
        ?: "jdbc:postgresql://localhost:5432/postgres?user=${dotenv["USER"]}"
    val port: Int = dotenv["PORT"]?.toInt() ?: 8080
}
