# robs-rules
A ruleset for detecting coroutine cancellation bugs with Detekt. Ensures that coroutine
cancellation is handled correctly in catch blocks, `runCatching`, `mapCatching` and
`recoverCatching` inside suspend functions.

[The Silent Killer that's Crashing Your Coroutines](https://betterprogramming.pub/the-silent-killer-thats-crashing-your-coroutines-9171d1e8f79b)

[CancellationException can be thrown not only to indicate cancellation](https://github.com/Kotlin/kotlinx.coroutines/issues/3658)

## Usage
In the `dependencies` block of your `build.gradle` file, add the following:
```
detektPlugins("dev.robch.rules:robs-rules:1.0.0")
```

or if you're using a `libs.version.toml` file, add this there:
```
robs-rules = { module = "dev.robch.rules:robs-rules", version = "1.0.0" }
```

and this in your `build.gradle` file:
```
detektPlugins(libs.robs.rules)
```

You can find the latest version [here](https://central.sonatype.com/artifact/dev.robch.rules/robs-rules).

## Configuration
Add this to your detekt config.
```yaml
robs-rules:
  CatchingCoroutineCancellation:
    active: true
  CatchingCoroutineCancellationLambda:
    active: true
```

## Type Resolution
These rules require [type resolution](https://detekt.dev/docs/gettingstarted/type-resolution/) to be enabled.

## Example code fails
```kotlin
suspend fun catchThrowable() {
    try {
        delay(1_000)
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
    }
}
```

## Example code that passes
```kotlin
suspend fun handlesCancellation() {
    try {
        delay(1_000)
    } catch (throwable: Throwable) {
        currentCoroutineContext().ensureActive()
        throwable.printStackTrace()
    }
}
```

## Example code fails
```kotlin
suspend fun runCatchingInSuspend() {
    runCatching {
        delay(1_000)
    }
}
```

## Example code passes
```kotlin
suspend fun handlesCancellation() {
    runCatching {
        delay(1_000)
    }.onFailure {
        currentCoroutineContext().ensureActive()
    }
}
```
