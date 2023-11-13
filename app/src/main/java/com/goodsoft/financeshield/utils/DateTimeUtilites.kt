package com.goodsoft.financeshield.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun extractDateFromSQLite(dateParam: String): String {
    val sqPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localDateTime = LocalDateTime.parse(dateParam, sqPattern)
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return localDateTime.format(dateFormatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun extractTimeFromSQLite(timeParam: String): String {
    val sqPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localDateTime = LocalDateTime.parse(timeParam, sqPattern)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    return localDateTime.format(timeFormatter)
}

fun extractDateTimeFromSQLite(dateParam: String): Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT)).parse(dateParam)!!

fun dateToDateStr(dateParam: Date): String = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT)).format(dateParam.time)

fun dateToTimeStr(dateParam: Date): String = SimpleDateFormat("HH:mm", Locale.getDefault(Locale.Category.FORMAT)).format(dateParam.time)

fun dateToSQLite(dateParam: Date): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT)).format(dateParam)

