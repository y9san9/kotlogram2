<h1 align="center">Welcome to kotlogram2 👋</h1>
<p align="center">
    An convinient wrapper for <a href="https://github.com/y9san9/kotlogram">kotlogram</a>
    <br><br>
    <a href="https://codebeat.co/projects/github-com-y9san9-kotlogram2-master"><img alt="codebeat badge" src="https://codebeat.co/badges/5897c206-ad32-48b0-a798-8bb241c2b853" /></a>
    <a href="https://github.com/y9san9/kotlogram2/blob/master/LICENSE"><img alt="Licence" src="https://img.shields.io/github/license/y9san9/kotlogram2.svg"/></a>
    <a href="https://github.com/y9san9/kotlogram2/issues"><img alt="Issues" src="https://img.shields.io/github/issues/y9san9/kotlogram2.svg"/></a>
    <a href="https://github.com/y9san9/kotlogram2/pulls"><img alt="PRs welcome" src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg"></a>
    <img src="https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https://github.com/y9san9/kotlogram2&title=views%20daily/total" alt="Views" />
    <br><br>
    <img alt="LoC" src="https://tokei.rs/b1/github/y9san9/kotlogram2"/>
    <img alt="HoC" src="https://hitsofcode.com/github/y9san9/kotlogram2?branch=master"/>
</p>

## 👀 Example
```kotlin
client.botAuth(...)
    
val alex = client.getByUsername("y9san9") as User
val crinny = client.getByUsername("crinny") as User

client.updates {
    command("start") {
        it.reply("Welcome!")
    }
    message {
        filter {
            it.from.id in listOf(alex.id, crinny.id)
        }
        println("${it.from.fullname}: ${it.message}")
    }
}
```

## 🚩 TODO
- wrap all api with convinient functions and models
- upgrade api layer to latest


## 🚀 Installation (Gradle) [![](https://jitpack.io/v/y9san9/kotlogram2.svg)](https://jitpack.io/#y9san9/kotlogram2) 

```gradle
repositories {
    maven { url "https://jitpack.io" }  // Connecting jitpack to import github repos
}
dependencies {
    implementation 'com.github.y9san9:kotlogram2:-SNAPSHOT'
    implementation 'com.github.y9san9:kotlogram:-SNAPSHOT'  // should be implemented to use unwrapped api
}
```
## 🔥 Pro way (Kotlin Gradle DSL)
Add this 2 functions to top of your build.gradle.kts
```kotlin
/**
 * Import github repo; first add [jitpack] to repos
 * @param repo username/repo; e.g. y9san9/kotlogram2
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
```
then use pretty easy implementation code
```kotlin
repositories {
    jitpack()
}
dependencies {
    github("y9san9/kotlogram")  // should be implemented to use unwrapped api
    github("y9san9/kotlogram2")
}
```
The project is in beta, so public api can be changed any time. For stable work use releases instead of -SNAPSHOT<br>
See: [wiki](https://github.com/y9san9/kotlogram2/wiki), [examples](https://github.com/y9san9/kotlogram2/tree/master/src/main/resources/examples)
