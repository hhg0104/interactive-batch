package hhg0104.interactivebatch.batch.data_update_batch.step

import hhg0104.interactivebatch.batch.data.BookingHistoryData
import hhg0104.interactivebatch.batch.data.ExcelAndDBBookingData
import hhg0104.interactivebatch.batch.data_update_batch.data.SqlGenerationData
import hhg0104.interactivebatch.batch.data_update_batch.data.UpdateBookingSqlData
import hhg0104.interactivebatch.util.LocalDateUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SQLGenerationStepTest {

    private val sut = SQLGenerationStep()

    private val testBookingId = "12345678"

    @Test
    fun createUpdateBookingDataQuery() {

        val dbBookingData = getTestDbBookingDdata()
        val excelBookingData = getTestExcelBookingData()

        val updateBookingData: UpdateBookingSqlData = sut.createUpdateBookingDataQuery(dbBookingData, excelBookingData)
        val expectedSql = """
            UPDATE
              t_reservation
            SET
              GRANT_POINT = 18,
              USAGE_POINT = 160,
              ACTUAL_BASIC_AMOUNT = 4000,
              ACTUAL_DISTANCE_AMOUNT = 1465,
              ACTUAL_DISCOUNT_AMOUNT = 2000,
              DISTANCE_KM = 213,
              ACTUAL_DEPARTURE_TIME = STR_TO_DATE('2023/09/23 12:00:00', '%Y/%m/%d %H:%i:%s'),
              ACTUAL_RETURN_TIME = STR_TO_DATE('2023/09/24 17:10:00', '%Y/%m/%d %H:%i:%s'),
              PARTNER_REMARKS = '【車両変更後20221104】通常料金',
              CANCEL_AMOUNT = NULL,
              FIXED_AMOUNT_FLG = 1,
              PAYMENT_SETTLED_FLG = 1,
              ACTUAL_CANCEL_AMOUNT = 0,
              STATUS = 2,
              PARTNER_STATUS = '返却済',
              AUTO_CANCEL_ALERT_DATE = NULL,
              RETURN_CAR_NOTIFICATION_DATE = CURRENT_TIMESTAMP,
              UPDATE_DATE = CURRENT_TIMESTAMP,
              CANCEL_DATE = NULL,
              GRANT_POINT_COOPERATION_DATE = CURRENT_TIMESTAMP,
              RAS_COMPLETE_DATE = CURRENT_TIMESTAMP,
              BILLING_STATUS = 1
            WHERE
              ID = '$testBookingId';
        """.trimIndent()

        assertEquals(testBookingId, updateBookingData.id)
        assertEquals(expectedSql, updateBookingData.sql)

        assertEquals(15, updateBookingData.compareDataList.size)

        var idx = 0
        assertEquals("GRANT_POINT", updateBookingData.compareDataList[idx].columName)
        assertEquals("800", updateBookingData.compareDataList[idx].dbData)
        assertEquals("18", updateBookingData.compareDataList[idx].excelData)
        assertTrue(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("USAGE_POINT", updateBookingData.compareDataList[idx].columName)
        assertEquals("null", updateBookingData.compareDataList[idx].dbData)
        assertEquals("160", updateBookingData.compareDataList[idx].excelData)
        assertTrue(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("RESERVATION_DATE", updateBookingData.compareDataList[idx].columName)
        assertEquals("2023/09/22 10:00:00", updateBookingData.compareDataList[idx].dbData)
        assertEquals("2023/09/22 10:00:00", updateBookingData.compareDataList[idx].excelData)
        assertFalse(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("CAR_MODEL_NAME", updateBookingData.compareDataList[idx].columName)
        assertEquals("testCar111", updateBookingData.compareDataList[idx].dbData)
        assertEquals("testCar111", updateBookingData.compareDataList[idx].excelData)
        assertFalse(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("ACTUAL_BASIC_AMOUNT", updateBookingData.compareDataList[idx].columName)
        assertEquals("4500", updateBookingData.compareDataList[idx].dbData)
        assertEquals("4000", updateBookingData.compareDataList[idx].excelData)
        assertTrue(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("ACTUAL_DISTANCE_AMOUNT", updateBookingData.compareDataList[idx].columName)
        assertEquals("254", updateBookingData.compareDataList[idx].dbData)
        assertEquals("1465", updateBookingData.compareDataList[idx].excelData)
        assertTrue(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("ACTUAL_TAXABLE_EXCESS_AMOUNT", updateBookingData.compareDataList[idx].columName)
        assertEquals("0", updateBookingData.compareDataList[idx].dbData)
        assertEquals("0", updateBookingData.compareDataList[idx].excelData)
        assertFalse(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("ACTUAL_NON_TAXABLE_EXCESS_AMOUNT", updateBookingData.compareDataList[idx].columName)
        assertEquals("0", updateBookingData.compareDataList[idx].dbData)
        assertEquals("0", updateBookingData.compareDataList[idx].excelData)
        assertFalse(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("ACTUAL_DISCOUNT_AMOUNT", updateBookingData.compareDataList[idx].columName)
        assertEquals("1500", updateBookingData.compareDataList[idx].dbData)
        assertEquals("2000", updateBookingData.compareDataList[idx].excelData)
        assertTrue(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("DISTANCE_KM", updateBookingData.compareDataList[idx].columName)
        assertEquals("180", updateBookingData.compareDataList[idx].dbData)
        assertEquals("213", updateBookingData.compareDataList[idx].excelData)
        assertTrue(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("DEPARTURE_TIME", updateBookingData.compareDataList[idx].columName)
        assertEquals("2023/09/23 10:00:00", updateBookingData.compareDataList[idx].dbData)
        assertEquals("2023/09/23 10:00:00", updateBookingData.compareDataList[idx].excelData)
        assertFalse(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("RETURN_TIME", updateBookingData.compareDataList[idx].columName)
        assertEquals("2023/09/24 17:30:00", updateBookingData.compareDataList[idx].dbData)
        assertEquals("2023/09/24 17:30:00", updateBookingData.compareDataList[idx].excelData)
        assertFalse(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("ACTUAL_DEPARTURE_TIME", updateBookingData.compareDataList[idx].columName)
        assertEquals("2023/09/23 11:30:00", updateBookingData.compareDataList[idx].dbData)
        assertEquals("2023/09/23 12:00:00", updateBookingData.compareDataList[idx].excelData)
        assertTrue(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("ACTUAL_RETURN_TIME", updateBookingData.compareDataList[idx].columName)
        assertEquals("null", updateBookingData.compareDataList[idx].dbData)
        assertEquals("2023/09/24 17:10:00", updateBookingData.compareDataList[idx].excelData)
        assertTrue(updateBookingData.compareDataList[idx].isDiff)
        idx++

        assertEquals("PARTNER_REMARKS", updateBookingData.compareDataList[idx].columName)
        assertEquals("null", updateBookingData.compareDataList[idx].dbData)
        assertEquals("【車両変更後20221104】通常料金", updateBookingData.compareDataList[idx].excelData)
        assertTrue(updateBookingData.compareDataList[idx].isDiff)
    }

    @Test
    fun createInsertBookingHistoryQuery() {

        val bookingDataForHistory = getTestBookingDataForHistory()
        val lastHistoryData = getTestHistoryData()

        val sql = sut.createInsertBookingHistoryQuery(bookingDataForHistory, lastHistoryData)
        val expected = """
            INSERT INTO
              t_reservation_history (
                ID,
                NO,
                CAR_TYPE_ID,
                DEPARTURE_TIME,
                RETURN_TIME,
                RAKUTEN_TOTAL_AMOUNT,
                PARTNER_TOTAL_AMOUNT,
                EVENT,
                EXE_USER,
                CREATE_TIMESTAMP
              )
            VALUES
              (
                '$testBookingId',
                5,
                188,
                STR_TO_DATE('2023/09/13 12:15:00', '%Y/%m/%d %H:%i:%s'),
                STR_TO_DATE('2023/09/14 15:00:00', '%Y/%m/%d %H:%i:%s'),
                5800,
                5800,
                9,
                'BAT01-01',
                CURRENT_TIMESTAMP
              );
        """.trimIndent()

        assertEquals(expected, sql)
    }

    @Test
    fun createInsertBookingHistoryQueryWithNullableValues() {

        val bookingData = ExcelAndDBBookingData()

        val lastHistoryData = BookingHistoryData()
        lastHistoryData.id = testBookingId
        lastHistoryData.no = 4
        lastHistoryData.event = 1
        lastHistoryData.exeUser = "BAT01-01"

        val sql = sut.createInsertBookingHistoryQuery(bookingData, lastHistoryData)
        val expected = """
            INSERT INTO
              t_reservation_history (
                ID,
                NO,
                CAR_TYPE_ID,
                DEPARTURE_TIME,
                RETURN_TIME,
                RAKUTEN_TOTAL_AMOUNT,
                PARTNER_TOTAL_AMOUNT,
                EVENT,
                EXE_USER,
                CREATE_TIMESTAMP
              )
            VALUES
              (
                '$testBookingId',
                5,
                null,
                null,
                null,
                null,
                null,
                9,
                'BAT01-01',
                CURRENT_TIMESTAMP
              );
        """.trimIndent()

        assertEquals(expected, sql)
    }

    @Test
    fun createUpdateCouponUseQuery() {
        val sql = this.sut.createUpdateCouponUseQuery(testBookingId)
        val expected = """
            UPDATE
              t_coupon_use
            SET
              UPDATE_TIMESTAMP = CURRENT_TIMESTAMP,
              DELETE_FLG = 0
            WHERE
              RESERVATION_ID = '$testBookingId';
        """.trimIndent()

        assertEquals(expected, sql)
    }

    @Test
    fun createUpdateCouponUseQueryWithNullOrEmpty() {

        var sql = this.sut.createUpdateCouponUseQuery("")
        assertEquals(null, sql)

        sql = this.sut.createUpdateCouponUseQuery(null)
        assertEquals(null, sql)
    }

    private fun getTestExcelBookingData(): ExcelAndDBBookingData {
        val excelBookingData = ExcelAndDBBookingData()
        excelBookingData.id = testBookingId
        excelBookingData.grantPoint = 1300
        excelBookingData.usagePoint = 160
        excelBookingData.membershipId = "RC019281232"
        excelBookingData.reservationDate = LocalDateUtil.parse("2023/09/22 10:00:00", LocalDateUtil.DATETIME_FORMAT)
        excelBookingData.carModelName = "testCar111"
        excelBookingData.totalAmount = 2000
        excelBookingData.actualBasicAmount = 4000
        excelBookingData.actualDistanceAmount = 1465
        excelBookingData.actualTaxableExcessAmount = 0
        excelBookingData.actualNonTaxableExcessAmount = 0
        excelBookingData.actualDiscountAmount = 2000
        excelBookingData.distanceKm = 213
        excelBookingData.departureTime = LocalDateUtil.parse("2023/09/23 10:00:00", LocalDateUtil.DATETIME_FORMAT)
        excelBookingData.returnTime = LocalDateUtil.parse("2023/09/24 17:30:00", LocalDateUtil.DATETIME_FORMAT)
        excelBookingData.actualDepartureTime = LocalDateUtil.parse("2023/09/23 12:00:00", LocalDateUtil.DATETIME_FORMAT)
        excelBookingData.actualReturnTime = LocalDateUtil.parse("2023/09/24 17:10:00", LocalDateUtil.DATETIME_FORMAT)
        excelBookingData.partnerRemarks = "【車両変更後20221104】通常料金"
        return excelBookingData
    }

    private fun getTestDbBookingDdata(): ExcelAndDBBookingData {
        val dbBookingData = ExcelAndDBBookingData()
        dbBookingData.id = testBookingId
        dbBookingData.grantPoint = 800
        dbBookingData.paymentId = "182737"
        dbBookingData.membershipId = "RC019281232"
        dbBookingData.reservationDate = LocalDateUtil.parse("2023/09/22 10:00:00", LocalDateUtil.DATETIME_FORMAT)
        dbBookingData.carModelName = "testCar111"
        dbBookingData.actualBasicAmount = 4500
        dbBookingData.actualDistanceAmount = 254
        dbBookingData.actualTaxableExcessAmount = 0
        dbBookingData.actualNonTaxableExcessAmount = 0
        dbBookingData.actualDiscountAmount = 1500
        dbBookingData.distanceKm = 180
        dbBookingData.departureTime = LocalDateUtil.parse("2023/09/23 10:00:00", LocalDateUtil.DATETIME_FORMAT)
        dbBookingData.returnTime = LocalDateUtil.parse("2023/09/24 17:30:00", LocalDateUtil.DATETIME_FORMAT)
        dbBookingData.actualDepartureTime = LocalDateUtil.parse("2023/09/23 11:30:00", LocalDateUtil.DATETIME_FORMAT)
        dbBookingData.actualReturnTime = null
        dbBookingData.partnerRemarks = null
        return dbBookingData
    }

    private fun getTestHistoryData(): BookingHistoryData {
        val lastHistoryData = BookingHistoryData()
        lastHistoryData.id = testBookingId
        lastHistoryData.no = 4
        lastHistoryData.carTypeId = "188"
        lastHistoryData.usagePoint = 350
        lastHistoryData.event = 1
        lastHistoryData.exeUser = "BAT01-01"
        return lastHistoryData
    }

    private fun getTestBookingDataForHistory(): ExcelAndDBBookingData {
        val bookingDataForHistory = ExcelAndDBBookingData()

        bookingDataForHistory.departureTime = LocalDateUtil.parse("2023/09/13 12:15:00", LocalDateUtil.DATETIME_FORMAT)
        bookingDataForHistory.returnTime = LocalDateUtil.parse("2023/09/14 15:00:00", LocalDateUtil.DATETIME_FORMAT)
        bookingDataForHistory.actualBasicAmount = 5800

        return bookingDataForHistory
    }

//    @Test
    fun testCreateSqlFiles() {

        val dbBookingData = getTestDbBookingDdata()
        val excelBookingData = getTestExcelBookingData()
        val bookingDataForHistory = getTestBookingDataForHistory()
        val testHistoryData = getTestHistoryData()

        val updateBookingData1 = sut.createUpdateBookingDataQuery(dbBookingData, excelBookingData)
        updateBookingData1.id = "R123456"
        val historySql1 = sut.createInsertBookingHistoryQuery(bookingDataForHistory, testHistoryData)
        val couponUseSql1 = this.sut.createUpdateCouponUseQuery(dbBookingData.id)

        // Just add the same data to print multiple sqls
        val updateBookingData2 = sut.createUpdateBookingDataQuery(dbBookingData, excelBookingData)
        updateBookingData2.id = "R7890928"
        val historySql2 = sut.createInsertBookingHistoryQuery(bookingDataForHistory, testHistoryData)
        val couponUseSql2 = this.sut.createUpdateCouponUseQuery(dbBookingData.id)

        val sqlGenerationData1 = SqlGenerationData()
        sqlGenerationData1.id = updateBookingData1.id
        sqlGenerationData1.bookingTableSql = updateBookingData1.sql.toString()
        sqlGenerationData1.bookingHistoryTableSql = historySql1
        sqlGenerationData1.couponUseTableSql = couponUseSql1.toString()
        sqlGenerationData1.diffDataList = updateBookingData1.compareDataList

        val sqlGenerationData2 = SqlGenerationData()
        sqlGenerationData2.id = updateBookingData2.id
        sqlGenerationData2.bookingTableSql = updateBookingData2.sql.toString()
        sqlGenerationData2.bookingHistoryTableSql = historySql2
        sqlGenerationData2.couponUseTableSql = couponUseSql2.toString()
        sqlGenerationData2.diffDataList = updateBookingData2.compareDataList

        sut.writeSQLsToFile("RTRV-1234", listOf(sqlGenerationData1, sqlGenerationData2))
    }
}