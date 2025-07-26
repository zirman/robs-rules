@file:Suppress("OPT_IN_USAGE")

package buildsrc.convention

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.the

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
}
val libs = the<LibrariesForLibs>()
dependencies {
    compileOnly(libs.detektApi)
    compileOnly(libs.detektTooling)
    detektPlugins(libs.detektRulesRuleauthors)
    testImplementation(libs.assertjCore)
    testImplementation(libs.detektTest)
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformEngine)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
tasks.named<Test>("test") {
    useJUnitPlatform()
}
