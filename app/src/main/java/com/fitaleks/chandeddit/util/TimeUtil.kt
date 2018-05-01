package com.fitaleks.chandeddit.util

/**
 * Created by alex206512252 on 1/11/18.
 */
private const val millisInSecond = 1000
private const val millisInMinute = millisInSecond * 60
private const val millisInHour = millisInMinute * 60
private const val millisInDay = millisInHour * 24

fun timeDiffToString(timeDiff: Long) : String {
    val days = timeDiff / millisInDay
    if (days > 0) {
        return "$days days ago"
    }
    val hours = timeDiff/ millisInHour
    if (hours > 0) {
        return "$hours hours ago"
    }
    val minutes = timeDiff / millisInMinute
    if (minutes > 0) {
        return "$minutes minutes ago"
    }
    return "just now"
}

fun timeDiffToStringShort(timeDiff: Long) : String {
    val days = timeDiff / millisInDay
    if (days > 0) {
        return "${days}d"
    }
    val hours = timeDiff/ millisInHour
    if (hours > 0) {
        return "${hours}h"
    }
    val minutes = timeDiff / millisInMinute
    if (minutes > 0) {
        return "${minutes}m"
    }
    return "just now"
}