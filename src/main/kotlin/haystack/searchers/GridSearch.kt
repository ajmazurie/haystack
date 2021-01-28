package haystack.searchers

import haystack.core.Evaluation
import haystack.core.Study
import haystack.core.Trial
import haystack.helpers.TrialManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Exhaustive (or grid) search for optimum parameter values,
 * going through all possible combinations of input parameters
 */
class GridSearch(study: Study): BaseSearch(study, Options()) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.info("Starting grid search across ${study.parameters.size} parameter(s)")
    }

    // domain size for each of the parameters
    private val sizes = study.parameters.map { it.domain.size }.toLongArray()

    // overall search space size
    private val size: Long = sizes.fold(1) { acc, n -> acc * n }

    private var index: Long = 0
    override fun hasNext(): Boolean = index < size

    // iterators for each of the parameters
    private val iterators = mutableMapOf<String, Iterator<Any>>().apply {
        putAll(study.parameters.map { p -> p.name to p.domain.iterator() })
    }

    // current set of cursors and values
    private lateinit var cursors: List<Long>
    private lateinit var values: Map<String, Any>

    private val trials = TrialManager(expectedMetrics = study.objectiveNames)

    override fun next(): Trial {
        if (!hasNext()) {
            logger.error("No more trials available")
            throw NoSuchElementException()
        }

        // look at the next set of cursors
        val nextCursors = if (index == 0L) {
            cursors = MutableList(study.parameters.size + 1) { 0 }
            values = iterators.mapValues { (_, iterator) -> iterator.next() }
            cursors
        } else {
            divmod(index, *sizes)
        }

        // check which iterators need to advance or be reset
        val diffs = cursors.zip(nextCursors) { c, n -> (n - c).toInt() }

        // build the next set of values
        val nextValues = mutableMapOf<String, Any>()
        for ((name, diff) in study.parameterNames.zip(diffs)) {
            when {
                diff == 0 -> {
                    // no cursor movement since last iteration: reuse the last value
                    nextValues[name] = values.getOrElse(name) { iterators[name]!!.next() }
                }
                diff < 0 -> {
                    // cursor decreased since last iteration: reset the iterator
                    iterators[name] = study.parametersMap[name]!!.domain.iterator()
                    nextValues[name] = iterators[name]!!.next()
                }
                diff > 0 -> {
                    // cursor increased since last iteration: get next value
                    nextValues[name] = iterators[name]!!.next()
                }
            }
        }

        // advanced our internal states
        index += 1
        cursors = nextCursors
        values = nextValues

        // create a new trial out of the fresh set of values
        return trials.create(values).also {
            logger.info("Created trial ${it.trialId} ($index/$size)")
        }
    }

    override fun submit(evaluation: Evaluation) {
        trials.complete(evaluation)
    }

    override fun results(): Map<Evaluation, Trial> {
        return trials.results()
    }

    override fun close() {
        logger.info("Stopping grid search")
    }
}


/**
 * Recursively divide a [dividend] by one or more [divisors],
 * returning a list of corresponding remainders and last quotient
 * Adapted from https://www.python.org/dev/peps/pep-0303
 */
internal fun divmod(dividend: Long, vararg divisors: Long): List<Long> {
    val quotients = mutableListOf<Long>()
    var quotient = dividend
    for (divisor in divisors) {
        val q = quotient / divisor
        val r = quotient % divisor
        quotients.add(r)
        quotient = q
    }
    return quotients.plus(quotient)
}
