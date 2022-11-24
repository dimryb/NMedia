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

    fun currentTime(): LocalDateTime =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

    fun currentSeconds(): Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            OffsetDateTime.now().toEpochSecond()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

    fun secondsToHours(seconds: Long): Long =
        seconds / SEC_PER_MIN / MIN_PER_HOUR

    fun today(seconds: Long): Boolean = secondsToHours(currentSeconds() - seconds) < HOURS_PER_DAY

    fun yesterday(seconds: Long): Boolean {
        val hoursDif = secondsToHours(currentSeconds() - seconds)
        return (hoursDif >= HOURS_PER_DAY)&&(hoursDif < 2 * HOURS_PER_DAY)
    }


    private const val SEC_PER_MIN = 60L
    private const val MIN_PER_HOUR = 60L
    private const val HOURS_PER_DAY = 24L
}