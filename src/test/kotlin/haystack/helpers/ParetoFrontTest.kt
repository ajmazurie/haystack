package haystack.helpers

import haystack.core.Evaluation
import haystack.core.Goal
import haystack.core.Objective
import kotlin.test.Test
import kotlin.test.assertEquals

class ParetoFrontTest {

    @Test
    fun `test Pareto frontier determination`() {
        //   A(0,2) ----- B(2,2)
        //   |            |
        //   |    C(1,1)  |
        //   |            |
        //   D(0,0) ----- E(2,0)

        val evaluations = setOf(
            evaluation("A", "x" to 0f, "y" to 2f),
            evaluation("B", "x" to 2f, "y" to 2f),
            evaluation("C", "x" to 1f, "y" to 1f),
            evaluation("D", "x" to 0f, "y" to 0f),
            evaluation("E", "x" to 2f, "y" to 0f))

        val maxmax = setOf(Objective("x", Goal.MAXIMIZE), Objective("y", Goal.MAXIMIZE))
        assertEquals(setOf("B"), ParetoFront.of(evaluations, maxmax).map { it.trialId }.toSet())

        val minmin = setOf(Objective("x", Goal.MINIMIZE), Objective("y", Goal.MINIMIZE))
        assertEquals(setOf("D"), ParetoFront.of(evaluations, minmin).map { it.trialId }.toSet())

        val maxmin = setOf(Objective("x", Goal.MAXIMIZE), Objective("y", Goal.MINIMIZE))
        assertEquals(setOf("E"), ParetoFront.of(evaluations, maxmin).map { it.trialId }.toSet())

        val minmax = setOf(Objective("x", Goal.MINIMIZE), Objective("y", Goal.MAXIMIZE))
        assertEquals(setOf("A"), ParetoFront.of(evaluations, minmax).map { it.trialId }.toSet())
    }

    private fun evaluation(name: String, vararg metrics: Pair<String, Float>) =
        Evaluation(name, metrics = metrics.toMap())
}
