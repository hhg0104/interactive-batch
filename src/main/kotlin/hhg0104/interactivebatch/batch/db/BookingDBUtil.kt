package hhg0104.interactivebatch.batch.db

import hhg0104.interactivebatch.batch.data.BookingHistoryData
import hhg0104.interactivebatch.batch.data.ExcelAndDBBookingData
import hhg0104.interactivebatch.batch.data.LicenseData
import hhg0104.interactivebatch.step.ColorLog
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.dbutils.BasicRowProcessor
import org.apache.commons.dbutils.BeanProcessor
import org.apache.commons.dbutils.QueryRunner
import org.apache.commons.dbutils.RowProcessor
import org.apache.commons.dbutils.handlers.BeanListHandler
import java.sql.Connection


class BookingDBUtil {

    var conn: Connection? = null

    constructor(conn: Connection?) {
        this.conn = conn
    }

    companion object {
        fun convertIdsToWhereInSql(bookingIds: List<String>): String {
            return bookingIds.joinToString(prefix = "(", postfix = ")", separator = ", ") { "'$it'" }
        }
    }

    fun fetchBookingDataListByBookingIdList(
        bookingIds: List<String>
    ): List<ExcelAndDBBookingData> {

        if (CollectionUtils.isEmpty(bookingIds)) {
            println(ColorLog.logError("The booking id list is null or empty."))
            return mutableListOf()
        }

        val bookingIdsForQuery: String = convertIdsToWhereInSql(bookingIds)

        val sql = "SELECT * FROM t_reservation WHERE ID IN $bookingIdsForQuery"
        println(ColorLog.logNormal("Booking data fetch SQL: ${sql}"))

        val beanProcessor = BeanProcessor(ExcelAndDBBookingData.mapColumnsToProperties)
        val rowProcessor: RowProcessor = BasicRowProcessor(beanProcessor)

        return QueryRunner().query(
            this.conn,
            sql,
            BeanListHandler(ExcelAndDBBookingData::class.java, rowProcessor)
        )
    }

    fun fetchBookingDataListByMemberIdList(
        memberIds: List<String>
    ): List<ExcelAndDBBookingData> {

        if (CollectionUtils.isEmpty(memberIds)) {
            println(ColorLog.logError("The member id list is null or empty."))
            return mutableListOf()
        }

        val memberIdsForQuery: String = convertIdsToWhereInSql(memberIds)

        val sql = "SELECT * FROM t_reservation WHERE MEMBERSHIP_ID IN $memberIdsForQuery"
        println(ColorLog.logNormal("Booking data fetch SQL: ${sql}"))

        val beanProcessor = BeanProcessor(ExcelAndDBBookingData.mapColumnsToProperties)
        val rowProcessor: RowProcessor = BasicRowProcessor(beanProcessor)

        return QueryRunner().query(
            this.conn,
            sql,
            BeanListHandler(ExcelAndDBBookingData::class.java, rowProcessor)
        )
    }

    fun fetchBookingHistoryList(bookingIds: List<String>): List<BookingHistoryData> {

        if (CollectionUtils.isEmpty(bookingIds)) {
            println(ColorLog.logError("The booking id list is null or empty."))
            return mutableListOf()
        }

        val bookingIdsForQuery: String = convertIdsToWhereInSql(bookingIds)

        val sql = "SELECT * FROM t_reservation_history WHERE ID IN $bookingIdsForQuery"
        println(ColorLog.logNormal("Booking history data fetch SQL: ${sql}"))

        val beanProcessor = BeanProcessor(BookingHistoryData.mapColumnsToProperties)
        val rowProcessor: RowProcessor = BasicRowProcessor(beanProcessor)

        return QueryRunner().query(
            this.conn,
            sql,
            BeanListHandler(BookingHistoryData::class.java, rowProcessor)
        )
    }

    fun fetchCouponUseDataList(bookingIds: List<String>): List<hhg0104.interactivebatch.batch.data.BookingCouponUseData> {

        if (CollectionUtils.isEmpty(bookingIds)) {
            println(ColorLog.logError("The booking id list is null or empty."))
            return mutableListOf()
        }

        val bookingIdsForQuery: String = convertIdsToWhereInSql(bookingIds)

        val sql = "SELECT * FROM t_coupon_use WHERE RESERVATION_ID IN $bookingIdsForQuery"
        println(ColorLog.logNormal("Booking coupon use data fetch SQL: ${sql}"))

        val beanProcessor = BeanProcessor(hhg0104.interactivebatch.batch.data.BookingCouponUseData.mapColumnsToProperties)
        val rowProcessor: RowProcessor = BasicRowProcessor(beanProcessor)

        return QueryRunner().query(
            this.conn,
            sql,
            BeanListHandler(hhg0104.interactivebatch.batch.data.BookingCouponUseData::class.java, rowProcessor)
        )
    }

    fun fetchLicenseData(memberIds: List<String>): List<LicenseData> {

        if (CollectionUtils.isEmpty(memberIds)) {
            println(ColorLog.logError("The member id list is null or empty."))
            return mutableListOf()
        }

        val memberIdsForQuery: String = convertIdsToWhereInSql(memberIds)

        val sql = "SELECT ID, LICENSE_FRONT_IMAGE_URL, LICENSE_BACK_IMAGE_URL FROM m_membership WHERE ID IN $memberIdsForQuery"
        println(ColorLog.logNormal("License data fetch SQL: ${sql}"))

        val beanProcessor = BeanProcessor(LicenseData.mapColumnsToProperties)
        val rowProcessor: RowProcessor = BasicRowProcessor(beanProcessor)

        return QueryRunner().query(
            this.conn,
            sql,
            BeanListHandler(LicenseData::class.java, rowProcessor)
        )
    }
}