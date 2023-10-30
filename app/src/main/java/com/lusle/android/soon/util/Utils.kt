package com.lusle.android.soon.util

import java.util.Calendar

object Utils {
    @JvmStatic
    fun calDDay(calendar: Calendar?): Int {
        if(calendar==null) return -1
        return try {
            val today = Calendar.getInstance()
            val day = calendar.timeInMillis / 86400000
            val tday = today.timeInMillis / 86400000
            val count = day - tday
            count.toInt() + 1
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }
}
