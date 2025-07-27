package dev.robs.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.backend.common.descriptors.isSuspend
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

// Handling coroutine cancellation to avoid bugs
// https://betterprogramming.pub/the-silent-killer-thats-crashing-your-coroutines-9171d1e8f79b
// https://github.com/Kotlin/kotlinx.coroutines/issues/3658

// Based on SleepInsteadOfDelay code from Detekt project:
// https://github.com/detekt/detekt/blob/main/detekt-rules-coroutines/src/main/kotlin/io/gitlab/arturbosch/detekt/rules/coroutines/SleepInsteadOfDelay.kt

@Suppress("TooManyFunctions")
@RequiresTypeResolution
class CatchingCoroutineCancellation(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Defect,
        description = "This rule reports not handling coroutine cancellation exceptions correctly.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitCatchSection(catchClause: KtCatchClause) {
        super.visitCatchSection(catchClause)
        if (catchClause.shouldReport()) {
            catchClause.report()
        }
    }

    private fun KtCatchClause.report() {
        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(this),
                message = "Check for coroutine cancellation with `currentCoroutineContext().ensureActive()` to " +
                    "prevent [coroutine cancellation bugs]" +
                    "(https://betterprogramming.pub/the-silent-killer-thats-crashing-your-coroutines-9171d1e8f79b).",
            )
        )
    }

    @Suppress("ForbiddenComment")
    private fun KtCatchClause.shouldReport(): Boolean = catchesCoroutineCancellation() &&
        parent.safeAs<KtTryExpression>()?.tryBlock?.hasSuspendCalls() == true &&
        // TODO: check if there is a catch block up the chain that has already handled coroutine
        //       cancellation
        isEnsureActiveCalled().not()

    private fun KtBlockExpression.hasSuspendCalls(): Boolean = statements.any {
        it.safeAs<KtCallExpression>()
            ?.getResolvedCall(bindingContext)
            ?.resultingDescriptor?.isSuspend == true
    }

    private fun KtCatchClause.catchesCoroutineCancellation(): Boolean {
        return catchParameter?.typeReference?.let { bindingContext[BindingContext.TYPE, it] }
            ?.constructor?.declarationDescriptor?.fqNameOrNull()
            ?.let { POSSIBLE_COROUTINE_CANCELLATION_FQ_NAMES.contains(it) } == true
    }

    private fun KtCatchClause.isEnsureActiveCalled(): Boolean =
        catchBody?.children?.firstOrNull()
            ?.safeAs<KtExpression>()
            ?.isEnsureActiveExpression() == true

    private fun KtExpression.isEnsureActiveExpression(): Boolean = when {
        this !is KtCallableReferenceExpression -> getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameOrNull() == ENSURE_ACTIVE_FQ_NAME

        parent is KtValueArgument -> callableReference.isEnsureActiveExpression()
        else -> false
    }

    companion object {
        private val ENSURE_ACTIVE_FQ_NAME = FqName("kotlinx.coroutines.ensureActive")
        private val POSSIBLE_COROUTINE_CANCELLATION_FQ_NAMES = listOf(
            FqName("kotlin.Throwable"),
            FqName("java.lang.Exception"),
            FqName("java.util.concurrent.CancellationException"),
        )
    }
}
