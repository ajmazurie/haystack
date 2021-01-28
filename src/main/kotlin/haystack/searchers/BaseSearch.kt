package haystack.searchers

import haystack.core.Evaluation
import haystack.core.Study
import haystack.core.Trial

/**
 * Base implementation of parameter search algorithms
 */
abstract class BaseSearch(
    val study: Study,
    val options: Options
): Iterator<Trial>, AutoCloseable {

    /**
     * Submit the metrics resulting from the evaluation of a
     * given [Trial] as identified by its unique identifier
     */
    abstract fun submit(evaluation: Evaluation)

    /**
     * Return all [Evaluation] obtained so
     * far and their corresponding [Trial]
     */
    abstract fun results(): Map<Evaluation, Trial>
}

data class Options(val values: Map<String, Any?> = emptyMap()) {
//    fun getString(name: String, default: String? = null): String? = values.getOrDefault(name, default)
//    fun getInt(name: String, default: Int? = null): Int? = values.get

    companion object {
        fun from(vararg options: Pair<String, Any?>) = Options(options.toMap())
    }
}
