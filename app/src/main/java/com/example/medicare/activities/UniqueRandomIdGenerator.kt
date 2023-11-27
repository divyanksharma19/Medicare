package com.example.medicare.activities
import kotlin.random.Random
class   UniqueRandomIdGenerator {
    private val usedIds = mutableSetOf<String>()

    fun generateUniqueRandomId(min: Int, max: Int): String {
        while (true) {
            val randomId = Random.nextInt(min, max + 1).toString()
            if (!usedIds.contains(randomId)) {
                usedIds.add(randomId)
                return randomId
            }
        }
    }
}

