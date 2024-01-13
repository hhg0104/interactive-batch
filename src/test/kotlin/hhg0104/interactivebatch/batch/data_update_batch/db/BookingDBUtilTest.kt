package hhg0104.interactivebatch.batch.data_update_batch.db

import hhg0104.interactivebatch.batch.db.BookingDBUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class BookingDBUtilTest {

    @Test
    fun testConvertBookingIdsToWhereInSql() {

        val testIds = mutableListOf("RC2981827", "RC123456789", "RC9981828")
        val actualWhereInQuery = BookingDBUtil.convertIdsToWhereInSql(testIds)

        val expected = "('RC2981827', 'RC123456789', 'RC9981828')"
        assertEquals(expected, actualWhereInQuery)
    }
}