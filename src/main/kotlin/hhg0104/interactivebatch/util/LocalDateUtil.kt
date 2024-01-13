package hhg0104.interactivebatch.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateUtil {

    companion object {
        const val DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss"

        private const val DATETIME_FORMAT_DEFAULT = "YYYYMMddHHmmss"

        fun format(dateTime: LocalDateTime?, dateFormat: String): String? {
            if (dateTime == null) return null

            val formatter = DateTimeFormatter.ofPattern(dateFormat)
            return dateTime.format(formatter)
        }

        fun equalDates(dateTime1: LocalDateTime?, dateTime2: LocalDateTime?): Boolean {
            if (dateTime1 == null && dateTime2 == null) {
                return true
            }
            if (dateTime1 != null && dateTime2 != null) {
                return dateTime1.compareTo(dateTime2) == 0
            }
            return false
        }

        fun parse(dateStr: String, dateFormat: String): LocalDateTime {
            val formatter = DateTimeFormatter.ofPattern(dateFormat)
            return LocalDateTime.parse(dateStr, formatter)
        }

        fun getDateStringYYYYMMddHHmmss(): String? {
            val formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_DEFAULT)
            return LocalDateTime.now().format(formatter)
        }
    }
}