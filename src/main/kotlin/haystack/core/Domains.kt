package haystack.core

import kotlin.random.Random

/**
 * A domain is a set of discrete values, either comparable
 * (see [NumericalDomain]) or not (see [CategoricalDomain])
 */
sealed class Domain<T: Any>: Iterable<T> {
    /**
     * Return the size of this domain space
     */
    abstract val size: Long

    /**
     * Pick a random value (with replacement) from this domain
     */
    abstract fun random(seed: Long?): T

    /**
     * Check if a given value exists in this domain
     */
    abstract operator fun contains(element: T): Boolean
}

/**
 * A domain defined by a range of numeric values
 */
data class NumericalDomain(val min: Long, val max: Long): Domain<Long>() {
    init {
        if (min > max) throw IllegalArgumentException(
            "Invalid bounds ($min is greater than $max)")
    }

    override val size: Long = (max - min + 1)

    override fun random(seed: Long?): Long =
        randomGenerator(seed).nextLong(min, max + 1)

    override operator fun contains(element: Long): Boolean = (element >= min) && (element <= max)

    override fun iterator(): Iterator<Long> = LongRange(min, max).iterator()
}

/**
 * A domain defined by one or more unique literals
 */
data class CategoricalDomain<T: Any>(val values: Set<T>): Domain<T>() {
    init {
        if (values.isEmpty()) throw IllegalArgumentException(
            "Invalid value set (must not be empty)")
    }

    override val size: Long = values.size.toLong()

    override fun random(seed: Long?): T =
        values.random(randomGenerator(seed))

    override operator fun contains(element: T): Boolean = element in values

    override fun iterator(): Iterator<T> = values.iterator()
}

private fun randomGenerator(seed: Long?): Random =
    if (seed == null) Random else Random(seed)


fun Number.asDomain(): NumericalDomain =
    NumericalDomain(this.toLong(), this.toLong())

fun String.asDomain(): CategoricalDomain<String> =
    CategoricalDomain(values = setOf(this))

fun Boolean.asDomain(): CategoricalDomain<Boolean> =
    CategoricalDomain(values = setOf(this))

fun IntRange.asDomain(): NumericalDomain =
    NumericalDomain(this.start.toLong(), this.endInclusive.toLong())

fun LongRange.asDomain(): NumericalDomain =
    NumericalDomain(this.start, this.endInclusive)

fun CharRange.asDomain(): CategoricalDomain<Char> =
    CategoricalDomain(values = this.toSet())

fun <T: Any> Collection<T>.asDomain(): CategoricalDomain<T> =
    CategoricalDomain(values = this.toSet())
