import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Import github repo; first add [jitpack] to repos
 * @param repo username/repo; e.g. y9san9/kotlogram-wrapper
 */
fun DependencyHandlerScope.github(repo: String, tag: String = "-SNAPSHOT") = implementation(
        repo.split("/").let { (username, repo) ->
            "com.github.${username}:${repo}:${tag}"
        }
)

/**
 * Jitpack maven
 */
fun RepositoryHandler.jitpack() = maven("https://jitpack.io")

plugins {
    kotlin("jvm") version "1.4.0"
    maven
}

group = "com.y9san9.kotlogram"
version = "beta-2-1"

repositories {
    mavenCentral()
    jitpack()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    github("y9san9/kotlogram", "v3-2")
    github("y9san9/kotlin-data-storage", "stable-7")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

val fatJar = task("fatJar", type = Jar::class) {
    @Suppress("UnstableApiUsage")
    manifest {
        attributes["Implementation-Title"] = "kotlogram2"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.4"
}