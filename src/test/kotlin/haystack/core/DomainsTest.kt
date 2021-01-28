package haystack.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DomainsTest {

    @Test
    fun `test basic domain properties`() {
        123.asDomain().let {
            assertTrue { it is NumericalDomain }
            assertEquals(1, it.size)
            assertEquals(123, it.random(null))
        }

        (1..5).asDomain().let {
            assertTrue { it is NumericalDomain }
            assertEquals(5, it.size)
            assertTrue { it.random(null) in 1..5 }
            assertTrue { 1 in it }
            assertTrue { 6 !in it }
        }

        setOf("A", "B", "C").asDomain().let {
            assertTrue { it is CategoricalDomain<String> }
            assertEquals(3, it.size)
            assertTrue { it.random(null) in setOf("A", "B", "C") }
            assertTrue { "A" in it }
            assertTrue { "D" !in it }
        }
    }
}
