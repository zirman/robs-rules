package dev.robs.rules

import org.jetbrains.kotlin.name.FqName

val RUN_CATCHING_FQ_NAME = FqName("kotlin.runCatching")
val CATCHING_FQ_NAMES = listOf(
    RUN_CATCHING_FQ_NAME,
    FqName("kotlin.recoverCatching"),
    FqName("kotlin.mapCatching"),
)
