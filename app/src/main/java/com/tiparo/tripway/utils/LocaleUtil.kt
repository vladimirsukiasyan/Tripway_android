package com.tiparo.tripway.utils

import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.text.format.DateUtils
import androidx.core.os.ConfigurationCompat
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object LocaleUtil {
    fun getLanguage(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault().toLanguageTags()
        } else {
            Locale.getDefault().language
        }
    }
}

fun Timestamp.convertTimestampToRelativeDateTime(context: Context): CharSequence? {
    val simpleDateFormat = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss",
        ConfigurationCompat.getLocales(context.resources.configuration)[0]
    )
    return DateUtils.getRelativeDateTimeString(
        context,
        simpleDateFormat.parse(toString())?.time ?: System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.WEEK_IN_MILLIS*2,
        DateUtils.FORMAT_ABBREV_RELATIVE

    )
}