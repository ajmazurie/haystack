package haystack.core

data class Objective(
    val name: String,
    val goal: Goal)

enum class Goal { MAXIMIZE, MINIMIZE }
