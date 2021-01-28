package haystack.helpers

import haystack.core.Evaluation
import haystack.core.Trial
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*


/**
 * Helper class to manage [Trial] and
 * corresponding [Evaluation] objects
 */
class TrialManager(
    private val timeout: Duration = Duration.ofMinutes(10),
    private val expectedMetrics: Set<String>,
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    init {
        val rejected = expectedMetrics.filter { it.startsWith('@') }
        if (rejected.isNotEmpty()) {
            throw IllegalArgumentException("Invalid metrics: ${rejected.joinToString(", ")}")
        }
    }

    private enum class Status { PENDING, COMPLETED, ABANDONNED, REJECTED }

    private val status = mutableMapOf<String, Status>()
    private val trials = mutableMapOf<String, Trial>()
    private val evaluations = mutableSetOf<Evaluation>()

    fun create(values: Map<String, Any>): Trial = synchronized(status) {
        val trialId = generateSequence { uuid() }.first { it !in status }
        val trial = Trial.of(trialId, values)
        status[trialId] = Status.PENDING
        trials[trialId] = trial
        return trial
    }

    fun complete(evaluation: Evaluation) = synchronized(status) {
        val trialId = evaluation.trialId
        if (trialId !in status) {
            logger.warn("Ignoring result for trial $trialId (unknown identifier)")
            return
        }
        if (status[trialId] == Status.COMPLETED) {
            logger.warn("Ignoring result for trial $trialId (already completed)")
            return
        }
        val missingMetrics = expectedMetrics - evaluation.metrics.keys
        if (missingMetrics.isEmpty()) {
            logger.info("Received result for trial $trialId")
            evaluations.add(evaluation.copy(completed = Date()))
            status[trialId] = Status.COMPLETED
        } else {
            val metricsList = missingMetrics.joinToString(", ") { "'$it'" }
            logger.warn("Ignoring result for trial $trialId (missing metrics: $metricsList)")
            status[trialId] = Status.REJECTED
        }
    }

    fun results(): Map<Evaluation, Trial> =
        evaluations.associateWith { trials.getValue(it.trialId) }
}

fun uuid(): String = UUID.randomUUID().toString()
