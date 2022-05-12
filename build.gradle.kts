import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import io.github.cdimascio.dotenv.dotenv

// Dependencies
val dotenv_kotlin_version: String by project
val exposed_version: String by project
val grpc_kotlin_version: String by project
val grpc_version: String by project
val h2_version: String by project
val jvm_version: String by project
val kotest_version: String by project
val kotlin_version: String by project
val kotlinx_version: String by project
val liquibase_groovy_dsl_version: String by project
val liquibase_version: String by project
val logback_version: String by project
val picocli_version: String by project
val postgresql_version: String by project
val protobuf_version: String by project

// Configuration
val env = dotenv { ignoreIfMissing = true }
val jdbc_database_url: String = env["JDBC_DATABASE_URL"] ?: ""

plugins {
    application
    kotlin("jvm") version "1.6.21"

    id("com.adarshr.test-logger") version "3.2.0"
    id("com.diffplug.spotless") version "6.4.2"
    id("com.google.protobuf") version "0.8.18"
    id("org.liquibase.gradle") version "2.1.1"
    
    // See https://badass-runtime-plugin.beryx.org/releases/latest/ for doc & details
    id("org.beryx.runtime") version "1.12.7"
}

group = "com.example"
version = "0.0.1"
application {
    mainClass.set("com.example.demo.ApplicationKt")
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

buildscript {
    configurations.classpath {
        resolutionStrategy.activateDependencyLocking()
    }
    dependencies {
        classpath("io.github.cdimascio:dotenv-kotlin:6.2.2")
    }
}

dependencyLocking {
    lockAllConfigurations()
    lockMode.set(LockMode.STRICT)
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of("$jvm_version"))
    }
}

liquibase {
    activities.register("main") {
        arguments = mapOf(
            "changeLogfile" to "src/main/db/changelog.groovy",
            "url" to jdbc_database_url
        )
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobuf_version"
    }

    plugins {
        id("grpc-java") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpc_version"
        }
        id("grpc-kotlin") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpc_kotlin_version:jdk7@jar"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc-java")
                id("grpc-kotlin")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

spotless {
    kotlin {
        ktlint()
    }
}

repositories {
    mavenCentral()
}

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
}

dependencies {
    implementation("com.google.protobuf:protobuf-kotlin:$protobuf_version")
    implementation("io.github.cdimascio:dotenv-kotlin:$dotenv_kotlin_version")
    implementation("io.grpc:grpc-kotlin-stub:$grpc_kotlin_version")
    implementation("io.grpc:grpc-protobuf:$grpc_version")
    implementation("io.grpc:grpc-services:$grpc_version")
    implementation("io.grpc:grpc-stub:$grpc_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.postgresql:postgresql:$postgresql_version")

    liquibaseRuntime("org.liquibase:liquibase-core:$liquibase_version")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:$liquibase_groovy_dsl_version")
    liquibaseRuntime("org.postgresql:postgresql:$postgresql_version")
    liquibaseRuntime("info.picocli:picocli:$picocli_version")

    runtimeOnly("io.grpc:grpc-netty-shaded:$grpc_version")

    testImplementation("com.h2database:h2:$h2_version")
    testImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-core:$kotest_version")
    testImplementation("io.kotest:kotest-property:$kotest_version")
    testImplementation("io.grpc:grpc-testing:$grpc_version")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_version")
    testImplementation("io.mockk:mockk:1.12.3")
}
