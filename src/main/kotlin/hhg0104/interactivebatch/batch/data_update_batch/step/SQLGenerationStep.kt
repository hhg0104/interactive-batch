package hhg0104.interactivebatch.batch.data_update_batch.step

import com.github.vertical_blank.sqlformatter.SqlFormatter
import hhg0104.interactivebatch.batch.data.BookingHistoryData
import hhg0104.interactivebatch.batch.data.ExcelAndDBBookingData
import hhg0104.interactivebatch.batch.db.BookingDBUtil
import hhg0104.interactivebatch.batch.data_update_batch.data.CompareData
import hhg0104.interactivebatch.batch.data_update_batch.data.SQLFiles
import hhg0104.interactivebatch.batch.data_update_batch.data.SqlGenerationData
import hhg0104.interactivebatch.batch.data_update_batch.data.UpdateBookingSqlData
import hhg0104.interactivebatch.step.ColorLog
import hhg0104.interactivebatch.step.InteractiveStep
import hhg0104.interactivebatch.step.InteractiveStepData
import hhg0104.interactivebatch.util.LocalDateUtil
import hhg0104.interactivebatch.util.ReflectionUtils
import hhg0104.interactivebatch.util.db.MySQLConnectUtil
import hhg0104.interactivebatch.util.markdown.MarkdownTableBuilder
import hhg0104.interactivebatch.util.markdown.type.Bold
import hhg0104.interactivebatch.util.markdown.type.Italic
import hhg0104.interactivebatch.util.markdown.type.Plain
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.io.FileUtils
import org.beryx.textio.TextIO
import org.springframework.util.StringUtils
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.sql.Connection
import java.time.LocalDateTime
import java.util.*
import kotlin.io.path.absolutePathString

class SQLGenerationStep : InteractiveStep {

    companion object {
        const val DATETIME_FORMAT_MYSQL = "%Y/%m/%d %H:%i:%s"
        const val DIFF_FILE_NAME = "target_data.md"
    }

    override fun proceed(textIO: TextIO, stepData: InteractiveStepData) {

        if (CollectionUtils.isEmpty(stepData.excelDataList)) {
            println(
                ColorLog.logError("There is no booking data in excel file. Can't compare with the DB data.")
            )
            return
        }

        var conn: Connection? = null

        while (conn == null) {

            val url: String = textIO.newStringInputReader()
                .withItemName("url")
                .withValueChecker { dbHost, itemName ->
                    if (!StringUtils.hasText(dbHost)) {
                        return@withValueChecker listOf(ColorLog.logError("Please input the valid URL. [input: $dbHost]"))
                    }
                    return@withValueChecker null;
                }
                .read(ColorLog.logInput("Input DB server host ({host}:{port}/{db})"))

            val dbUser: String = textIO.newStringInputReader()
                .withItemName("dbUser")
                .withValueChecker { dbUser, itemName ->
                    if (!StringUtils.hasText(dbUser)) {
                        return@withValueChecker listOf(ColorLog.logError("Please input the valid DB user. [input: $dbUser]"))
                    }
                    return@withValueChecker null;
                }
                .read(ColorLog.logInput("Input DB user"))

            textIO.newStringInputReader()
                .withItemName("dbPassword")
                .withInputMasking(true)
                .withValueChecker { dbPassword, itemName ->
                    val errMsg = ColorLog.logError("Please input the valid DB password.")
                    if (!StringUtils.hasText(dbPassword)) {
                        return@withValueChecker listOf(errMsg)
                    }

                    try {
                        println("[-] Connecting DB...")
                        conn = MySQLConnectUtil.createConnection(url, dbUser, dbPassword)
                        println("[-] DB connected successfully.")
                        return@withValueChecker null

                    } catch (e: Exception) {
                        println(ColorLog.logError("Error from DB connection [${e.message}]"))
                    }

                    return@withValueChecker null;
                }
                .read(ColorLog.logInput("Input DB password"))
        }

        try {
            val bookingIds: List<String> =
                stepData.excelDataList?.filter { it.id != null }?.map { it.id }?.toList() as List<String>
            println(ColorLog.logNormal("Fetching Booking DB data... ${bookingIds}"))

            if (bookingIds == null || bookingIds.isEmpty()) {
                println(ColorLog.logError("There is no target booking ids to fetch DB data."))
                return
            }

            var bookingDBUtil = BookingDBUtil(conn)
            var sqlDataList = createOperationSQLs(stepData, bookingDBUtil, bookingIds)

            if (CollectionUtils.isEmpty(sqlDataList)) {
                println(ColorLog.logError("There is no booking data in DB. [$bookingIds]"))
                return
            }

            val fileData: SQLFiles = writeSQLsToFile(stepData.rtrvTicketName as String, sqlDataList)

            if (fileData.sqlFile != null) {
                println(ColorLog.logNormal("SQL File Path -> ${ColorLog.logResult(fileData.sqlFile!!.absolutePath)}"))
            }
            if (fileData.diffFile != null) {
                println(ColorLog.logNormal("Diff Data File Path -> ${ColorLog.logResult(fileData.diffFile!!.absolutePath)}"))
            }

        } finally {
            conn!!.close()
        }
    }

    fun writeSQLsToFile(rtrvTicketName: String, sqlDataList: List<SqlGenerationData>): SQLFiles {

        println(ColorLog.logNormal("Creating SQL file..."))

        val currentDir = Paths.get("").absolutePathString()
        val sqlFile = File("$currentDir/${rtrvTicketName}.sql")
        val diffFile = File("$currentDir/${rtrvTicketName}_$DIFF_FILE_NAME")

        if (sqlFile.exists()) sqlFile.delete()
        if (diffFile.exists()) diffFile.delete()

        createSqlFile(sqlFile, sqlDataList)
        createDiffDataFile(diffFile, sqlDataList)

        return SQLFiles(sqlFile, diffFile)
    }

    private fun createDiffDataFile(
        diffFile: File,
        sqlDataList: List<SqlGenerationData>
    ) {
        diffFile.createNewFile()

        sqlDataList
            .filter { it.diffDataList.isNotEmpty() }
            .forEach {
                val id = it.id
                val mdTableBuilder = MarkdownTableBuilder
                    .builder()
                    .title("-- Reservation ID: ${Bold(id.toString()).convert()}")
                    .headers(mutableListOf("Column", "DB", "Excel"))

                it.diffDataList.forEach {
                    val diffData = if(it.isDiff) Italic(Bold(it.excelData.toString())) else Plain(it.excelData.toString())

                    mdTableBuilder.row(
                        mutableListOf(
                            Plain(it.columName),
                            Plain(it.dbData.toString()),
                            diffData
                        )
                    )
                }

                val diffTableContents: String = mdTableBuilder.build()

                FileUtils.write(
                    diffFile,
                    "${diffTableContents + System.lineSeparator() + System.lineSeparator()}",
                    StandardCharsets.UTF_8.name(),
                    true
                )
            }
    }

    private fun createSqlFile(
        sqlFile: File,
        sqlDataList: List<SqlGenerationData>
    ) {
        val updateBookingSQLs =
            sqlDataList.filter { StringUtils.hasText(it.bookingTableSql) }.map { it.bookingTableSql }.toList()
        val insertHistorySQLs =
            sqlDataList.filter { StringUtils.hasText(it.bookingHistoryTableSql) }.map { it.bookingHistoryTableSql }
                .toList()
        val updateCouponUseSQLs =
            sqlDataList.filter { StringUtils.hasText(it.couponUseTableSql) }.map { it.couponUseTableSql }.toList()
        val bookingIdList = sqlDataList.filter { StringUtils.hasText(it.id) }.map { it.id.toString() }.toList()

        sqlFile.createNewFile()

        val checkingCountSqls = createCheckingCountSqls(bookingIdList)
        if (CollectionUtils.isNotEmpty(checkingCountSqls)) {
            val titleLine = "-- Check the booking info count for each tables"
            writeSQLsToFile(
                sqlFile,
                checkingCountSqls,
                titleLine,
                addEmptyLineBetweenQueries = false,
                addEmptyLineAfterLastSql = true
            )
        }
        if (CollectionUtils.isNotEmpty(updateBookingSQLs)) {
            val titleLine = "-- Update t_reservation"
            writeSQLsToFile(sqlFile, updateBookingSQLs, titleLine, addEmptyLineAfterLastSql = true)
        }
        if (CollectionUtils.isNotEmpty(insertHistorySQLs)) {
            val titleLine = "-- Insert into t_reservation_history"
            writeSQLsToFile(sqlFile, insertHistorySQLs, titleLine, addEmptyLineAfterLastSql = true)
        }
        if (CollectionUtils.isNotEmpty(updateCouponUseSQLs)) {
            val titleLine = "-- Update t_coupon_use"
            writeSQLsToFile(sqlFile, updateCouponUseSQLs, titleLine, addEmptyLineAfterLastSql = true)
        }
        if (CollectionUtils.isNotEmpty(checkingCountSqls)) {
            val titleLine = "-- Confirm the booking info count for each tables"
            writeSQLsToFile(sqlFile, checkingCountSqls, titleLine, addEmptyLineBetweenQueries = false)
        }
    }

    private fun writeSQLsToFile(
        sqlFile: File,
        sqls: List<String>,
        titleLine: String = "",
        addEmptyLineBetweenQueries: Boolean = true,
        addEmptyLineAfterLastSql: Boolean = false
    ) {
        if (StringUtils.hasText(titleLine)) {
            FileUtils.write(
                sqlFile,
                titleLine + System.lineSeparator(),
                StandardCharsets.UTF_8.name(),
                true
            )
        }

        sqls.forEachIndexed { idx, sql ->
            var lineBreak = System.lineSeparator()
            if (idx < sqls.size - 1) {
                if (addEmptyLineBetweenQueries) {
                    lineBreak += System.lineSeparator()
                }
            } else {
                if (addEmptyLineAfterLastSql) {
                    lineBreak += System.lineSeparator()
                }
            }

            FileUtils.write(
                sqlFile,
                sql + lineBreak,
                StandardCharsets.UTF_8.name(),
                true
            )
        }
    }

    private fun createCheckingCountSqls(bookingIdList: List<String>): List<String> {
        if (bookingIdList.isEmpty()) {
            return mutableListOf()
        }

        val bookingIdsForQuery: String = BookingDBUtil.convertIdsToWhereInSql(bookingIdList)

        val sqls = mutableListOf<String>()
        sqls.add("SELECT COUNT(*) FROM t_reservation WHERE ID IN $bookingIdsForQuery")
        sqls.add("SELECT COUNT(*) FROM t_reservation_history WHERE ID IN $bookingIdsForQuery")
        sqls.add("SELECT COUNT(*) FROM t_coupon_use WHERE RESERVATION_ID IN $bookingIdsForQuery")

        return sqls
    }

    fun createOperationSQLs(
        stepData: InteractiveStepData,
        bookingDBUtil: BookingDBUtil,
        bookingIds: List<String>
    ): List<SqlGenerationData> {

        val bookingDataListInDB: List<ExcelAndDBBookingData> =
            bookingDBUtil.fetchBookingDataListByBookingIdList(bookingIds)
        if (CollectionUtils.isEmpty(bookingDataListInDB)) {
            return mutableListOf();
        }

        val bookingHistoryListInDB: List<BookingHistoryData> = bookingDBUtil.fetchBookingHistoryList(bookingIds)
        val bookingCouponUseListInDB: List<hhg0104.interactivebatch.batch.data.BookingCouponUseData> = bookingDBUtil.fetchCouponUseDataList(bookingIds)

        val sqlDataList = mutableListOf<SqlGenerationData>()

        for (bookingDataInDB in bookingDataListInDB) {
            val bookingId = bookingDataInDB.id

            val sortedHistoryListById =
                bookingHistoryListInDB.filter { bookingId.equals(it.id) }.sortedByDescending { it.no }.toList()
            val sortedCouponUseListById =
                bookingCouponUseListInDB.filter { bookingId.equals(it.reservationId) }.sortedByDescending { it.no }
                    .toList()

            val sqlData =
                createSqlAndDiffData(
                    stepData,
                    bookingDataInDB,
                    sortedHistoryListById,
                    sortedCouponUseListById,
                    bookingId
                )
            sqlDataList.add(sqlData)
        }

        return sqlDataList
    }

    private fun createSqlAndDiffData(
        stepData: InteractiveStepData,
        bookingDataInDB: ExcelAndDBBookingData,
        sortedHistoryListById: List<BookingHistoryData>,
        sortedCouponUseListById: List<hhg0104.interactivebatch.batch.data.BookingCouponUseData>,
        bookingId: String?
    ): SqlGenerationData {

        val excelBookingData: ExcelAndDBBookingData? =
            stepData.excelDataList?.first { it.id == bookingDataInDB.id }

        var updateBookingData: UpdateBookingSqlData? = null
        if (excelBookingData != null) {
            updateBookingData = createUpdateBookingDataQuery(bookingDataInDB, excelBookingData)
        }

        var insertBookingHistoryQuery: String? = null
        if (excelBookingData != null && sortedHistoryListById.isNotEmpty()) {
            insertBookingHistoryQuery = createInsertBookingHistoryQuery(excelBookingData, sortedHistoryListById.get(0))
        }

        var updateCouponUseQuery: String? = null
        if (sortedCouponUseListById.isNotEmpty()) {
            updateCouponUseQuery = createUpdateCouponUseQuery(bookingId)
        }

        val sqlData = SqlGenerationData()
        if (updateBookingData != null) {
            sqlData.id = updateBookingData.id
            sqlData.bookingTableSql = updateBookingData.sql.toString()
            sqlData.diffDataList = updateBookingData.compareDataList
        }
        if (insertBookingHistoryQuery != null) sqlData.bookingHistoryTableSql = insertBookingHistoryQuery
        if (updateCouponUseQuery != null) sqlData.couponUseTableSql = updateCouponUseQuery

        return sqlData
    }

    private fun printAllTargetBookingData(
        bookingDataListInExcel: List<ExcelAndDBBookingData>,
        bookingId: String?,
        bookingDataInDB: ExcelAndDBBookingData,
        sortedHistoryListById: List<BookingHistoryData>,
        sortedCouponUseListById: List<hhg0104.interactivebatch.batch.data.BookingCouponUseData>
    ) {

        val bookingDataInExcel = bookingDataListInExcel.filter { bookingId.equals(it.id) }[0]
        println(ColorLog.logNormal("[$bookingId][Booking Data Excel] ${ReflectionUtils.toString(bookingDataInExcel)}"))
        println(ColorLog.logNormal("[$bookingId][Booking Data DB] ${ReflectionUtils.toString(bookingDataInDB)}"))

        if (sortedHistoryListById.isEmpty()) {
            println(ColorLog.logNormal("[$bookingId][History Data] No History Data"))
        } else {
            val lastHistoryData = sortedHistoryListById[0]
            println(ColorLog.logNormal("[$bookingId][History Data] ${ReflectionUtils.toString(lastHistoryData)}"))
        }


        if (sortedCouponUseListById.isEmpty()) {
            println(ColorLog.logNormal("[$bookingId][Coupon Use Data] No Coupon Use Data"))
        } else {
            for (couponData in sortedCouponUseListById) {
                println(ColorLog.logNormal("[$bookingId][Coupon Use Data] ${ReflectionUtils.toString(couponData)}"))
            }
        }
    }

    fun createUpdateBookingDataQuery(
        dbBookingData: ExcelAndDBBookingData,
        excelBookingData: ExcelAndDBBookingData
    ): UpdateBookingSqlData {

        val updateBookingSqlData = UpdateBookingSqlData()
        val strJoiner = StringJoiner(", ")

        updateBookingSqlData.id = dbBookingData.id.toString()

        // Compare data
        if (excelBookingData.totalAmount != null) {
            val grantPointFromExcel = calculateGrantedPoint(excelBookingData.totalAmount!!)
            val grantPointCompareData = addSqlAndCompareData("GRANT_POINT", dbBookingData.grantPoint, grantPointFromExcel,
                CompareData.Type.NUMBER, true, strJoiner)
            updateBookingSqlData.compareDataList.add(grantPointCompareData)
        }

        val usagePointCompareData = addSqlAndCompareData("USAGE_POINT", dbBookingData.usagePoint, excelBookingData.usagePoint,
            CompareData.Type.NUMBER, false, strJoiner)
        if ((excelBookingData.usagePoint != null)) {
            // when only the excel data exists, it will be included.
            updateBookingSqlData.compareDataList.add(usagePointCompareData)
        }

        val reservationDateCompareData = addSqlAndCompareData("RESERVATION_DATE", dbBookingData.reservationDate, excelBookingData.reservationDate,
            CompareData.Type.DATE, true, strJoiner)
        updateBookingSqlData.compareDataList.add(reservationDateCompareData)

        val carModelNameCompareData = addSqlAndCompareData("CAR_MODEL_NAME", dbBookingData.carModelName, excelBookingData.carModelName,
            CompareData.Type.STRING, true, strJoiner)
        updateBookingSqlData.compareDataList.add(carModelNameCompareData)

        val actualBasicAmountCompareData = addSqlAndCompareData("ACTUAL_BASIC_AMOUNT", dbBookingData.actualBasicAmount, excelBookingData.actualBasicAmount,
            CompareData.Type.NUMBER, false, strJoiner)
        updateBookingSqlData.compareDataList.add(actualBasicAmountCompareData)

        val actualDistanceAmountCompareData = addSqlAndCompareData("ACTUAL_DISTANCE_AMOUNT", dbBookingData.actualDistanceAmount, excelBookingData.actualDistanceAmount,
            CompareData.Type.NUMBER, false, strJoiner)
        updateBookingSqlData.compareDataList.add(actualDistanceAmountCompareData)

        val actualTaxableExcessAmountCompareData = addSqlAndCompareData("ACTUAL_TAXABLE_EXCESS_AMOUNT", dbBookingData.actualTaxableExcessAmount, excelBookingData.actualTaxableExcessAmount,
            CompareData.Type.NUMBER, false, strJoiner)
        updateBookingSqlData.compareDataList.add(actualTaxableExcessAmountCompareData)

        val actualNonTaxableExcessAmountCompareData = addSqlAndCompareData("ACTUAL_NON_TAXABLE_EXCESS_AMOUNT", dbBookingData.actualNonTaxableExcessAmount, excelBookingData.actualNonTaxableExcessAmount,
            CompareData.Type.NUMBER, false, strJoiner)
        updateBookingSqlData.compareDataList.add(actualNonTaxableExcessAmountCompareData)

        val actualDiscountAmountCompareData = addSqlAndCompareData("ACTUAL_DISCOUNT_AMOUNT", dbBookingData.actualDiscountAmount, excelBookingData.actualDiscountAmount,
            CompareData.Type.NUMBER, false, strJoiner)
        updateBookingSqlData.compareDataList.add(actualDiscountAmountCompareData)

        val actualDistanceKiloCompareData = addSqlAndCompareData("DISTANCE_KM", dbBookingData.distanceKm, excelBookingData.distanceKm,
            CompareData.Type.NUMBER, false, strJoiner)
        updateBookingSqlData.compareDataList.add(actualDistanceKiloCompareData)

        val departureTimeCompareData = addSqlAndCompareData("DEPARTURE_TIME", dbBookingData.departureTime, excelBookingData.departureTime,
            CompareData.Type.DATE, true, strJoiner)
        updateBookingSqlData.compareDataList.add(departureTimeCompareData)

        val returnTimeCompareData = addSqlAndCompareData("RETURN_TIME", dbBookingData.returnTime, excelBookingData.returnTime,
            CompareData.Type.DATE, true, strJoiner)
        updateBookingSqlData.compareDataList.add(returnTimeCompareData)

        val actualDepartureTimeCompareData = addSqlAndCompareData("ACTUAL_DEPARTURE_TIME", dbBookingData.actualDepartureTime, excelBookingData.actualDepartureTime,
            CompareData.Type.DATE, false, strJoiner)
        updateBookingSqlData.compareDataList.add(actualDepartureTimeCompareData)

        val actualReturnTimeCompareData = addSqlAndCompareData("ACTUAL_RETURN_TIME", dbBookingData.actualReturnTime, excelBookingData.actualReturnTime,
            CompareData.Type.DATE, false, strJoiner)
        updateBookingSqlData.compareDataList.add(actualReturnTimeCompareData)

        val remarksCompareData = addSqlAndCompareData("PARTNER_REMARKS", dbBookingData.partnerRemarks, excelBookingData.partnerRemarks,
            CompareData.Type.STRING, false, strJoiner)
        updateBookingSqlData.compareDataList.add(remarksCompareData)

        // Fixed data
        strJoiner.add("CANCEL_AMOUNT = NULL")
        strJoiner.add("FIXED_AMOUNT_FLG = 1")
        strJoiner.add("PAYMENT_SETTLED_FLG = 1")
        strJoiner.add("ACTUAL_CANCEL_AMOUNT = 0")
        strJoiner.add("STATUS = 2")
        strJoiner.add("PARTNER_STATUS = '返却済'")
        strJoiner.add("AUTO_CANCEL_ALERT_DATE = NULL")
        strJoiner.add("RETURN_CAR_NOTIFICATION_DATE = CURRENT_TIMESTAMP")
        strJoiner.add("UPDATE_DATE = CURRENT_TIMESTAMP")
        strJoiner.add("CANCEL_DATE = NULL")
        strJoiner.add("GRANT_POINT_COOPERATION_DATE = CURRENT_TIMESTAMP")
        strJoiner.add("RAS_COMPLETE_DATE = CURRENT_TIMESTAMP")
        strJoiner.add("BILLING_STATUS = 1")

        val sql = """
            UPDATE 
                t_reservation
            SET
                ${strJoiner}
            WHERE
                ID = '${dbBookingData.id}';
        """.trimIndent()

        updateBookingSqlData.sql = SqlFormatter.format(sql)

        return updateBookingSqlData
    }

    fun addSqlAndCompareData(
        columnName: String,
        dbData: Any?,
        excelData: Any?,
        dataType: CompareData.Type,
        isNotNullColumn: Boolean,
        queryJoiner: StringJoiner
    ): CompareData {

        val compareData = CompareData(
            columnName,
            convertDiffValueByType(dbData),
            convertDiffValueByType(excelData)
        )

        var isDiff = if (dataType == CompareData.Type.DATE) {
            !LocalDateUtil.equalDates(dbData as? LocalDateTime, excelData as? LocalDateTime)
        } else {
            (dbData !== excelData)
        }

        if (isDiff) {
            val partialSql = when(dataType) {
                CompareData.Type.NUMBER -> createNumberSQL(columnName, excelData as? Int, isNotNullColumn)
                CompareData.Type.DATE -> createDateSQL(columnName, excelData as? LocalDateTime, isNotNullColumn)
                else -> {createStringSQL(columnName, excelData as? String, isNotNullColumn)}
            }

            if (partialSql != null) {
                queryJoiner.add(partialSql)
            }

            compareData.isDiff = true
        }

        return compareData
    }

    fun createInsertBookingHistoryQuery(
        excelBookingData: ExcelAndDBBookingData,
        lastHistoryData: BookingHistoryData
    ): String {

        val newHistoryNumber = lastHistoryData.no!! + 1
        val departureDateStr = LocalDateUtil.format(excelBookingData.departureTime, LocalDateUtil.DATETIME_FORMAT)
        val returnDateStr = LocalDateUtil.format(excelBookingData.returnTime, LocalDateUtil.DATETIME_FORMAT)

        val departureDateSql =
            if (excelBookingData.departureTime != null) "STR_TO_DATE('$departureDateStr', '$DATETIME_FORMAT_MYSQL')" else null
        val returnDateSql =
            if (excelBookingData.returnTime != null) "STR_TO_DATE('$returnDateStr', '$DATETIME_FORMAT_MYSQL')" else null

        val sql = """
            INSERT INTO 
                t_reservation_history
                (ID, NO, CAR_TYPE_ID, DEPARTURE_TIME, RETURN_TIME, RAKUTEN_TOTAL_AMOUNT, PARTNER_TOTAL_AMOUNT, 
                ${
            if (excelBookingData.usagePoint != null) {
                "USAGE_POINT,"
            } else ""
        } 
                EVENT, EXE_USER, CREATE_TIMESTAMP)
            VALUES
                ('${lastHistoryData.id}', $newHistoryNumber, ${lastHistoryData.carTypeId}, 
                $departureDateSql, $returnDateSql,
                ${excelBookingData.actualBasicAmount}, ${excelBookingData.actualBasicAmount},
                ${
            if (excelBookingData.usagePoint != null) {
                "${excelBookingData.usagePoint},"
            } else ""
        }
                9, 'BAT01-01', CURRENT_TIMESTAMP);
        """.trimIndent()

        return SqlFormatter.format(sql)
    }

    fun createUpdateCouponUseQuery(bookingId: String?): String? {

        if (!StringUtils.hasText(bookingId)) {
            return null
        }

        val sql = """
            UPDATE 
                t_coupon_use
            SET
                UPDATE_TIMESTAMP = CURRENT_TIMESTAMP,
                DELETE_FLG = 0
            WHERE
                RESERVATION_ID = '$bookingId';
        """.trimIndent()

        return SqlFormatter.format(sql)
    }

    private fun createStringSQL(columnName: String, value: String?, isNotNullColumn: Boolean): String? {
        if (isNotNullColumn && value == null) {
            printlnNotNullColumn(columnName)
            return null
        }
        return if (value == null) "$columnName = $value" else "$columnName = '$value'"
    }

    private fun createNumberSQL(columnName: String, value: Int?, isNotNullColumn: Boolean): String? {
        if (isNotNullColumn && value == null) {
            printlnNotNullColumn(columnName)
            return null
        }
        return if (value == null) "$columnName = NULL" else "$columnName = $value"
    }

    private fun createDateSQL(columnName: String, dateTime: LocalDateTime?, isNotNullColumn: Boolean): String? {
        if (isNotNullColumn && dateTime == null) {
            printlnNotNullColumn(columnName)
            return null
        }
        return if (dateTime == null) "$columnName = NULL" else "$columnName = STR_TO_DATE('${
            LocalDateUtil.format(
                dateTime,
                LocalDateUtil.DATETIME_FORMAT
            )
        }', '$DATETIME_FORMAT_MYSQL')"
    }

    private fun printlnNotNullColumn(columnName: String) {
        println(ColorLog.logError("$columnName column is 'NOT NULL' column."))
    }

    fun calculateGrantedPoint(totalPrice: Int): Int {
        println(ColorLog.logNormal("Point calculation formula: ${totalPrice}(料金合計) / 1.1 * 0.01 ROUND DOWN"))
        return BigDecimal(totalPrice)
            .divide(BigDecimal(1.1), 5, RoundingMode.HALF_UP)
            .multiply(BigDecimal(0.01))
            .toInt()
    }

    private fun convertDiffValueByType(value: Any?): String {
        if (value is LocalDateTime) {
            return LocalDateUtil.format(value, LocalDateUtil.DATETIME_FORMAT).toString()
        }
        return value.toString()
    }
}
