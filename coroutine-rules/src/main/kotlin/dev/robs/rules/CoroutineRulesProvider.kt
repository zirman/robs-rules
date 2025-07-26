package dev.robs.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

internal class CoroutineRulesProvider : RuleSetProvider {
    override val ruleSetId: String = "coroutine"
    override fun instance(config: Config): RuleSet = RuleSet(
        id = ruleSetId,
        rules = listOf(
            CatchingCoroutineCancellation(config),
            RunCatchingCoroutineCancellation(config),
        ),
    )
}
