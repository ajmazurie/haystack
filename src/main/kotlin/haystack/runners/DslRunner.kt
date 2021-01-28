package haystack.runners

import haystack.core.Evaluation
import haystack.core.Trial
import haystack.helpers.ParetoFront
import haystack.searchers.BaseSearch

/**
 * Syntactic sugar exposing a [BaseSearch] as a Kotlin DSL
 */

fun BaseSearch.search(block: (Trial) -> Evaluation): Set<Pair<Trial, Evaluation>> =
    this.use {
        for (trial in it) {
            val evaluation = block(trial)
            it.submit(evaluation)
        }
        ParetoFront.of(it)
    }
