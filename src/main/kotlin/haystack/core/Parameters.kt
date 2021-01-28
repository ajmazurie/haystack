package haystack.core

/**
 * A parameter is a value from a named [Domain]
 */
data class Parameter<T: Any>(
    val name: String,
    val domain: Domain<T>,
    val value: T = domain.random(null),
) {
    init {
        if (domain.size == 0L) throw IllegalArgumentException("Empty domain")
        if (value !in domain) throw IllegalArgumentException("Value '$value' is out of bounds")
    }
}


fun Number.asParameter(name: String): Parameter<Long> =
    Parameter(name, this.asDomain(), this.toLong())

fun String.asParameter(name: String): Parameter<String> =
    Parameter(name, this.asDomain(), this)

fun Boolean.asParameter(name: String): Parameter<Boolean> =
    Parameter(name, this.asDomain(), this)

fun IntRange.asParameter(name: String, value: Int = this.first): Parameter<Long> =
    Parameter(name, this.asDomain(), value.toLong())

fun LongRange.asParameter(name: String, value: Long = this.first): Parameter<Long> =
    Parameter(name, this.asDomain(), value)

fun CharRange.asParameter(name: String, value: Char = this.first): Parameter<Char> =
    Parameter(name, this.asDomain(), value)

fun <T: Any> Collection<T>.asParameter(name: String, value: T = this.first()) =
    Parameter(name, this.asDomain(), value)


