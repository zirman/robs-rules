@file:Suppress("OPT_IN_USAGE")

package buildsrc.convention

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    id("com.vanniktech.maven.publish")
}
val libs = the<LibrariesForLibs>()
detekt {
    buildUponDefaultConfig = true // preconfigure defaults.
    allRules = false // activate all available (even unstable) rules.
    autoCorrect = false // To enable or disable auto formatting.
    // To enable or disable parallel execution of detekt on multiple submodules.
    parallel = true
    // point to your custom config defining rules to run, overwriting default behavior.
    config.setFrom("${rootProject.projectDir}/detekt.yml")
}
dependencies {
    compileOnly(libs.detektApi)
    compileOnly(libs.detektTooling)
    detektPlugins(libs.detektFormatting)
    detektPlugins(libs.detektRulesRuleauthors)
    testImplementation(libs.assertjCore)
    testImplementation(libs.detektTest)
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformEngine)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
tasks.withType<Detekt> {
    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    reports {
        // observe findings in your browser with structure and code snippets
        html {
            required = true
            outputLocation = file("build/reports/mydetekt.html")
        }
        // similar to the console output, contains issue signature to manually edit baseline files
        txt {
            required = true
            outputLocation = file("build/reports/mydetekt.txt")
        }
        // simple Markdown format
        md {
            required = true
            outputLocation = file("build/reports/mydetekt.md")
        }
    }
    jvmTarget = libs.versions.jvmTarget.get()
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    jvmTarget = libs.versions.jvmTarget.get()
}
tasks.named<Test>("test") {
    useJUnitPlatform()
}
