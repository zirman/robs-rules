# robs-rules
An opinionated ruleset for detecting coroutine cancellation bugs with Detekt.

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
