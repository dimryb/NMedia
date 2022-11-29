package ru.netology.nmedia.util

import android.os.Build
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

object TimeUtils {
    fun dateTimeFromEpochSecond(seconds: String): LocalDateTime =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.ofEpochSecond(
                seconds.toLong(),
                0,
                ZoneOffset.UTC
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }

    fun dataFormatted(dateTime: LocalDateTime): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("d MMM yyyy HH:mm", Locale.US)
            return dateTime.format(formatter)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    private fun currentSeconds(): Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            OffsetDateTime.now().toEpochSecond()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

    fun timePeriod(seconds: Long): TimePeriod =
        when (currentSeconds() - seconds) {
            in 0..SEC_PER_HOUR -> TimePeriod.THIS_HOUR
            in SEC_PER_HOUR..SEC_PER_TWO_HOUR -> TimePeriod.HOURS_AGO
            in SEC_PER_TWO_HOUR..SEC_PER_DAY -> TimePeriod.TODAY
            in SEC_PER_DAY..SEC_PER_TWO_DAY -> TimePeriod.YESTERDAY
            else -> TimePeriod.LAST_WEAK
        }


    private const val SEC_PER_MIN = 60L
    private const val SEC_PER_HOUR = SEC_PER_MIN * 60L
    private const val SEC_PER_TWO_HOUR = SEC_PER_HOUR * 2L
    private const val SEC_PER_DAY = SEC_PER_HOUR * 24L
    private const val SEC_PER_TWO_DAY = SEC_PER_DAY * 2L

    enum class TimePeriod {
        THIS_HOUR,
        HOURS_AGO,
        TODAY,
        YESTERDAY,
        LAST_WEAK,
    }
}