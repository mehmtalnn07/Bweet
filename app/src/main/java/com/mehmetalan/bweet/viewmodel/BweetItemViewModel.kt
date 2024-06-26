package com.mehmetalan.bweet.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class BweetItemViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()

    fun epochToFormattedTiem(epochString: String): String {
        val epochLong = epochString.toLongOrNull()
        if (epochLong == null) {
            return "Invalid timestamp"
        }

        val instant = Instant.ofEpochMilli(epochLong)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        val now = LocalDateTime.now()
        val difference = ChronoUnit.SECONDS.between(localDateTime, now)

        return when {
            difference < 60 -> "$difference saniye önce"
            difference < 3600 -> "${difference / 60} dakika önce"
            difference < 86400 -> "${difference / 3600} saat önce"
            difference < 604800 -> "${difference / 86400} gün önce"
            else -> localDateTime.toString()
        }
    }

}