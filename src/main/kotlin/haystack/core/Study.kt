package haystack.core

/**
 * A [Study] is a set of [Parameter] to scan
 * that optimize one or more [Objective]
 */
data class Study  constructor(
    val parameters: Set<Parameter<out Any>>,
    val objectives: Set<Objective>
) {
    /**
     * Common use case when only one [Objective] must be optimized
     */
    constructor(vararg parameters: Parameter<out Any>, objective: Pair<Goal, String>): this(
        parameters = setOf(*parameters), objectives = setOf(Objective(objective.second, objective.first)))

    init {
        if (parameters.isEmpty()) throw IllegalArgumentException("At least one parameter is required")
        duplicates(parameters.map { it.name }).let { duplicates ->
            if (duplicates.isNotEmpty()) throw IllegalArgumentException(
                "Duplicate parameters: ${duplicates.joinToString(", ")}")
        }

        if (objectives.isEmpty()) throw IllegalArgumentException("At least one objective is required")
        duplicates(objectives.map { it.name }).let { duplicates ->
            if (duplicates.isNotEmpty()) throw IllegalArgumentException(
                "Duplicate objectives: ${duplicates.joinToString(", ")}")
        }
    }

    val parameterNames: Set<String> by lazy {
        parameters.map { it.name }.toSet() }

    val parametersMap: Map<String, Parameter<out Any>> by lazy {
        parameters.map { it.name to it }.toMap() }

    val objectiveNames: Set<String> by lazy {
        objectives.map { it.name }.toSet() }

    val objectivesMap: Map<String, Objective> by lazy {
        objectives.map { it.name to it }.toMap() }
}

private fun <T> duplicates(values: List<T>): Set<T> =
    values.groupingBy { it }.eachCount().filterValues { it > 1 }.keys
