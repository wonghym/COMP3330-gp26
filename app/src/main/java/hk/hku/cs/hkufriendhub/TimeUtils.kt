package hk.hku.cs.hkufriendhub

import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object TimeUtils {
    fun calculateTime(timestamp: String): String {
        val postTime = Instant.parse(timestamp)
        val now = Instant.now()
        val duration = Duration.between(postTime, now)

        val hours = duration.seconds/ 3600
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30

        return when {
            months > 0 -> "Long time ago"
            weeks > 0 -> "${weeks}w ago"
            days > 0 -> "${days}d ago"
            hours > 1 -> "${hours}h ago"
            else -> "Just now"
        }
    }

    fun getFormattedDate(timestamp: String): String {
        try {
            val offset = OffsetDateTime.parse(timestamp)

            return offset.format(DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            if (timestamp.length >= 10) {
                return timestamp.substring(0, 10)
            }
            return timestamp
        }
    }
}