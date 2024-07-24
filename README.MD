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
        implementation("net.orandja.kt:failure:2.0.0")
    }
}
```

### Jvm

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("net.orandja.kt:failure:2.0.0")
}
```

## Usage

[dokka documentation here](https://l-briand.github.io/failure/failure/net.orandja.failure/index.html)

Usage example:

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

### Exceptions

The default `Failure` is an interface.
You can implement it whenever it seems OK to do so.
For example, a failure that throws is also a failure.

```kotlin
val failure: Failure = failure("MY_FAILURE", code = 200)
try {
    failure.throws()
} catch (e: FailureException) {
    assert(e.id == failure.id)
    assert(e.code == failure.code)
}
```

### Equality

To keep things simple and not implement your own failure class, you can use the `GenericFailure` data class.
This class is used when you create failures through `failure()` or `namedFailure()`

Keep in mind that the failure implementation can differ.
A `FailureException` is not equals to `GenericFailure`.
Either check for failure `id` equality or transforms them both to `GenericFailure` with the `defaults()` function.

```kotlin
val failure1: Failure = failure("FAILURE")
val failure2: FailureException = runCatching { failure1.throws() }.exceptionOrNull() !! as FailureException

// Proper checks
assert(failure1.id == failure2.id)
assert(failure1 == failure2.defaults()) // failure1 is a GenericFailure

// This fails
assert(failure1 == failure2)
```

### Serialization

Any class that implements the `Failure` interface can use the `FailureSerializer` to serialize the failure.
For example, the `FailureException` is a `Failure`, it can be serialized but cannot be deserialized.

```kotlin
var failure: Throwable = FailureException(failure("FAILURE"))
val json = Json.encodeToString<Failure>(failure)
println(json) //  {"id":"FAILURE"}
```

Any serialized `Failure` can be decoded to a `GenericFailure`.

```kotlin
val json = """{"id":"FAILURE"}"""
val failure = Json.decodFromString<Failure>(json)
assert(failure.id == "FAILURE")
```