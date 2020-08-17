[![](https://jitpack.io/v/y9san9/kotlogram-wrapper.svg)](https://jitpack.io/#y9san9/kotlogram-wrapper) [![Hits-of-Code](https://hitsofcode.com/github/y9san9/kotlogram-wrapper?branch=master)](https://hitsofcode.com/view/github/y9san9/kotlogram-wrapper?branch=master)
# kotlogram-wrapper
Kotlogram wrapper. Now in developing, write to https://t.me/y9san9 to implement something
## Installation (Gradle)
```gradle
repositories {
    maven { url "https://jitpack.io" }  // Connecting jitpack to import github repos
}
dependencies {
    implementation 'com.github.y9san9:kotlogram-wrapper:-SNAPSHOT'
    implementation 'com.github.y9san9:kotlogram:-SNAPSHOT'
}
```
## Pro way (Kotlin Gradle DSL)
Add this 2 functions to top of your build.gradle.kts
```kotlin
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
```
then use pretty easy implementation code
```kotlin
repositories {
    jitpack()
}
dependencies {
    github("y9san9/kotlogram")
    github("y9san9/kotlogram-wrapper")
}
```
The project is in beta, so public api can be changed any time. For stable work use releases instead of -SNAPSHOT<br>
See: [examples](https://github.com/y9san9/kotlogram-wrapper/tree/master/src/main/resources/examples)
