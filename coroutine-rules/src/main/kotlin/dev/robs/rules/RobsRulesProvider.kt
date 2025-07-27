package dev.robs.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

internal class RobsRulesProvider : RuleSetProvider {
    override val ruleSetId: String = "robs-rules"
    override fun instance(config: Config): RuleSet = RuleSet(
        id = ruleSetId,
        rules = listOf(
            CatchingCoroutineCancellation(config),
            RunCatchingCoroutineCancellation(config),
        ),
    )
}
