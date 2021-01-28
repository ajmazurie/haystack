import haystack.core.Evaluation
import haystack.core.Goal
import haystack.core.Objective
import haystack.core.Study
import haystack.core.asParameter
import haystack.runners.search
import haystack.searchers.GridSearch


fun main() {
    val study = Study(
        parameters = setOf(
            (1..5).asParameter("a"),
            (1..5).asParameter("b")),
        objectives = setOf(
            Objective("result", Goal.MAXIMIZE))
    )

    GridSearch(study).search { trial ->
        println(">>> trial: $trial")

        val a = (trial.values["a"] as Number).toLong()
        val b = (trial.values["b"] as Number).toLong()
        val r = (a * b).toFloat()

        Evaluation.of(trial, "result" to r)
    }
}


