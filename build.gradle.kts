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
    kotlin("jvm") version "1.3.72"
    maven
}

group = "com.y9san9.kotlogram"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jitpack()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    github("badoualy/kotlogram", "1.0.0-RC3")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}