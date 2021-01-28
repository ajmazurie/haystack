package haystack.core

import java.util.*

/**
 * Set of parameter values to be evaluated
 */
data class Trial(
    val trialId: String,
    val created: Date = Date(),
    val values: Map<String, Any>
) {
    companion object {
        fun of(id: String, values: Map<String, Any>): Trial =
            Trial(id, values = values)

        fun of(id: String, vararg values: Pair<String, Any>): Trial =
            Trial(id, values = values.toMap())
    }

    fun <T> get(name: String): T = values.getValue(name) as T

    fun evaluation(metrics: Map<String, Float>): Evaluation =
        Evaluation(trialId, metrics = metrics)

    fun evaluation(vararg metrics: Pair<String, Float>): Evaluation =
        Evaluation(trialId, metrics = metrics.toMap())
}

/**
 * Set of metrics resulting from the evaluation of a [Trial]
 */
data class Evaluation(
    val trialId: String,
    val completed: Date = Date(),
    val metrics: Map<String, Float>
) {
    companion object {
        fun of(trial: Trial, metrics: Map<String, Float>): Evaluation =
            Evaluation(trial.trialId, metrics = metrics)

        fun of(trial: Trial, vararg metrics: Pair<String, Float>): Evaluation =
            Evaluation(trial.trialId, metrics = metrics.toMap())
    }
}


