package ru.netology.nmedia.presentation

class Model {

    private var likesCount = ""

    private fun counterCompressionFraction(counter: Long, divider: Int, suffix: String): String {
        val thousands = counter / divider
        val hundreds = (counter - thousands * divider) / (divider / 10)
        return if (hundreds == 0L) {
            "${thousands}${suffix}"
        } else {
            "${thousands}.${hundreds}${suffix}"
        }
    }

    fun counterCompression(counter: Long): String {
        return when {
            counter < 1000 -> counter.toString()
            counter in 1000 until 10_000 -> counterCompressionFraction(
                counter,
                COUNTER_DIVIDER_K,
                SUFFIX_K
            )
            counter in 10_000 until 1_000_000 -> "${counter / COUNTER_DIVIDER_K}${SUFFIX_K}"
            counter in 1_000_000 until 10_000_000 -> counterCompressionFraction(
                counter,
                COUNTER_DIVIDER_M,
                SUFFIX_M
            )
            else -> "${counter / COUNTER_DIVIDER_M}${SUFFIX_M}"
        }
    }

    fun calcLickedCounter(counter: Long, isLiked: Boolean): String {
        val newCount = counter + if (isLiked) 1 else 0
        return counterCompression(newCount)
    }

    companion object {
        const val COUNTER_DIVIDER_K = 1000
        const val COUNTER_DIVIDER_M = 1_000_000
        const val SUFFIX_K = "K"
        const val SUFFIX_M = "M"
    }
}