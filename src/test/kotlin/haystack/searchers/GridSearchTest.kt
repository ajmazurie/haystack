package haystack.searchers

import haystack.core.Evaluation
import haystack.core.Goal
import haystack.core.Parameter
import haystack.core.Study
import haystack.core.asDomain
import haystack.core.asParameter
import haystack.runners.search
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GridSearchTest {

    @Test
    fun `test of grid search results`() {
        // Let's create a study with two parameters and one objective to maximize
        val study = Study(
            (1..5).asParameter("a"),
            (1..4).asParameter("b"),
            objective = Goal.MAXIMIZE to "r")

        GridSearch(study).search { trial ->
            // We should be offered values for our two parameters
            val a = trial.get<Long>("a")
            val b = trial.get<Long>("b")

            // Our metric is just their product
            val r = (a * b).toFloat()
            trial.evaluation("r" to r)
        }.let { bests ->
            // Only one best set of parameters should be found
            assertEquals(1, bests.size)

            // That set of parameters should correspond
            // to the highest value of each parameters
            val (trial, evaluation) = bests.first()
            assertEquals(5L, trial.values["a"])
            assertEquals(4L, trial.values["b"])
            assertEquals(20f, evaluation.metrics["r"])
        }
    }

    @Test
    fun `test of grid search iterations`() {
        val d1 = (1..5).asDomain()
        val d2 = setOf("a", "b", "c").asDomain()

        val search = GridSearch(Study(
            Parameter("p1", d1),
            Parameter("p2", d2),
            objective = Goal.MAXIMIZE to "r"))

        val trials = search.asSequence().map { it.values }.toSet()

        assertEquals(d1.size * d2.size, trials.size.toLong())
        for (v1 in d1) {
            for (v2 in d2) {
                assertTrue { mapOf("p1" to v1, "p2" to v2) in trials }
            }
        }
    }

    @Test
    fun `test of the divmod helper function`() {
        assertEquals(listOf<Long>(123), divmod(123))

        // 1,000 seconds breaks down to 40 seconds, 16 minutes
        divmod(1_000, 60).let {
            assertEquals(2, it.size)
            val (s, m) = it

            assertEquals(40, s)
            assertEquals(16, m)

            assertEquals(1_000, s + 60 * m)
        }

        // 10,000 seconds breaks down to 40 seconds, 46 minutes, 2 hours
        divmod(10_000, 60, 60).let {
            assertEquals(3, it.size)
            val (s, m, h) = it

            assertEquals(40, s)
            assertEquals(46, m)
            assertEquals(2, h)

            assertEquals(10_000, s + 60 * (m + 60 * h))
        }

        // 100,000 seconds breaks down to 40 seconds, 46 minutes, 3 hours, 1 day
        divmod(100_000, 60, 60, 24).let {
            assertEquals(4, it.size)
            val (s, m, h, d) = it

            assertEquals(40, s)
            assertEquals(46, m)
            assertEquals(3, h)
            assertEquals(1, d)

            assertEquals(100_000, s + 60 * (m + 60 * (h + 24 * d)))
        }

        // 1,000,000 seconds breaks down to 40 seconds, 46 minutes, 13 hours, 4 days and 1 week
        divmod(1_000_000, 60, 60, 24, 7).let {
            assertEquals(5, it.size)
            val (s, m, h, d, w) = it

            assertEquals(40, s)
            assertEquals(46, m)
            assertEquals(13, h)
            assertEquals(4, d)
            assertEquals(1, w)

            assertEquals(1_000_000, s + 60 * (m + 60 * (h + 24 * (d + (w * 7)))))
        }
    }
}
