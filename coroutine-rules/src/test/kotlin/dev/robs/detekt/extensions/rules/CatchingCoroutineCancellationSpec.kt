@file:Suppress("MaxLineLength")

package dev.robs.detekt.extensions.rules

import dev.robs.rules.CatchingCoroutineCancellation
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CatchingCoroutineCancellationSpec(private val env: KotlinCoreEnvironment) {

    @Test
    fun `Detect caught CancellationException when try block has suspend calls`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun main() {
                try {
                    delay(1_000)
                    println("Hello, World!")
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `Pass caught CancellationException when try block has suspend calls and catch block calls ensureActive()`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun main() {
                try {
                    delay(1_000)
                    println("Hello, World!")
                } catch (throwable: Throwable) {
                    currentCoroutineContext().ensureActive()
                    throwable.printStackTrace()
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Pass caught CancellationException when try block has no suspend calls`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun main() {
                try {
                    println("Hello, World!")
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Detect caught CancellationException in nested function when try block has suspend calls`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun main() {
                suspend fun foo() {
                    try {
                        delay(1_000)
                        print("Hello, ")
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }
                foo()
                println("World!")
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `Pass caught CancellationException in nested function when try block has suspend calls and catch block calls ensureActive()`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun main() {
                suspend fun foo() {
                    try {
                        delay(1_000)
                        print("Hello, ")
                    } catch (throwable: Throwable) {
                        currentCoroutineContext().ensureActive()
                        throwable.printStackTrace()
                    }
                }
                foo()
                println("World!")
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Pass caught CancellationException in nested function when try block has no suspend calls`() {
        val code = """
            suspend fun main() {
                suspend fun foo() {
                    try {
                        print("Hello, ")
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }
                foo()
                println("World!")
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Detect caught CancellationException when a lambda try block has suspend calls`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun main() {
                run {
                    try {
                        delay(1_000)
                        println("Hello, World")
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `Detect caught CancellationException when a lambda try block has suspend calls and catch block calls ensureActive()`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun main() {
                run {
                    try {
                        delay(1_000)
                        println("Hello, World")
                    } catch (throwable: Throwable) {
                        currentCoroutineContext().ensureActive()
                        throwable.printStackTrace()
                    }
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Pass caught CancellationException when a lambda try block has no suspend calls`() {
        val code = """
            suspend fun main() {
                run {
                    try {
                        println("Hello, World")
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Detekt caught Throwable in suspend lambda when try block has suspend calls`() {
        val code = """
            import kotlinx.coroutines.*
            fun main() {
                GlobalScope.launch {
                    try {
                        delay(1_000)
                        println("Hello, World!")
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }                
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `Pass caught Throwable in suspend lambda when try block has suspend calls and catch block calls ensureActive()`() {
        val code = """
            import kotlinx.coroutines.*
            fun main() {
                GlobalScope.launch {
                    try {
                        delay(1_000)
                        println("Hello, World!")
                    } catch (throwable: Throwable) {
                        currentCoroutineContext.ensureActive()
                        throwable.printStackTrace()
                    }
                }                
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Pass caught Throwable in suspend lambda when try block has no suspend calls`() {
        val code = """
            import kotlinx.coroutines.*
            fun main() {
                GlobalScope.launch {
                    try {
                        println("Hello, World!")
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }                
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Pass caught IllegalStateException when a lambda try block has suspend calls`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun main() {
                try {
                    delay(1_000)
                    println("Hello, World")
                } catch (illegalStateException: IllegalStateException) {
                    illegalStateException.printStackTrace()
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
