package haystack.helpers

import haystack.core.Evaluation
import haystack.core.Goal
import haystack.core.Objective
import haystack.core.Trial
import haystack.searchers.BaseSearch

object ParetoFront {
    /**
     * Return, among a set of [Evaluation], those that are Pareto efficient
     */
    fun of(evaluations: Collection<Evaluation>, objectives: Set<Objective>): Set<Evaluation> {
        if (evaluations.isEmpty()) return emptySet()

        val front = mutableSetOf<Evaluation>()
        for (i in evaluations.indices) {
            val candidate = evaluations.elementAt(i)
            var winner = true
            for (j in evaluations.indices) {
                if (i == j) continue
                val contender = evaluations.elementAt(j)
                // TODO: Ensure both candidate and contender have the same metrics
                winner = winner && objectives.all { (name, direction) ->
                    val candidateMetric = candidate.metrics[name]!!
                    val contenderMetric = contender.metrics[name]!!
                    when (direction) {
                        Goal.MAXIMIZE -> candidateMetric >= contenderMetric
                        Goal.MINIMIZE -> candidateMetric <= contenderMetric
                    }
                }
                if (!winner) break
            }
            if (winner) front.add(candidate)
        }

        return front
    }

    /**
     * Return, among the [Evaluation] submitted to a [BaseSearch] instance,
     * the ones that are Pareto efficient along with their corresponding [Trial]
     */
    fun of(search: BaseSearch): Set<Pair<Trial, Evaluation>> {
        val results = search.results()
        val best = of(results.keys, search.study.objectives)
        return results
            .filterKeys { it in best }
            .map { (evaluation, trial) -> trial to evaluation }
            .toSet()
    }
}
