# Kotlin Failure class

A Kotlin multiplatform, serializable, [Failure interface](src/commonMain/kotlin/net/orandja/failure/Failure.kt).

In almost every project, I use some form of error int or class to represent my code failures. 
To not have to repeat myself every time I have created this lib.

## Import from maven

### Multiplatform

```kotlin
repositories {
    mavenCentral()
}
val commonMain by getting {
    dependencies {
        implementation("net.orandja.kt:failure:1.0.0")
    }
}
```

### Jvm

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("net.orandja.kt:failure:1.0.0")
}
```

## Usage

[dokka documentation here](https://l-briand.github.io/failure/failure/net.orandja.failure/index.html)

```kotlin
val TOO_LOW = failure("TOO_LOW", description = "Value is less than 0")
val TOO_HIGH by namedFailure(description = "Value is greater than 100")

fun assert(value: Int): Failure? {
    if (value < 0) return TOO_LOW.copy(information = "value is $value")
    if (value > 100) return TOO_HIGH.copy(information = "value is $value")
    return null
}

assert(102)?.throws() // Exception in thread "main" FailureException: TOO_HIGH [Value is greater than 100] value is 102
```