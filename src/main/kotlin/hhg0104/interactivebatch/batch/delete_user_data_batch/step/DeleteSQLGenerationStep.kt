package hhg0104.interactivebatch.batch.delete_user_data_batch.step

import hhg0104.interactivebatch.batch.data.LicenseData
import hhg0104.interactivebatch.batch.db.BookingDBUtil
import hhg0104.interactivebatch.batch.delete_user_data_batch.data.SqlData
import hhg0104.interactivebatch.step.ColorLog
import hhg0104.interactivebatch.step.InteractiveStep
import hhg0104.interactivebatch.step.InteractiveStepData
import hhg0104.interactivebatch.util.db.MySQLConnectUtil
import org.beryx.textio.TextIO
import org.springframework.util.StringUtils
import java.sql.Connection

class DeleteSQLGenerationStep : InteractiveStep {

    companion object {
        val licenseFileDir = "/www/license_images/"
    }

    override fun proceed(textIO: TextIO, stepData: InteractiveStepData) {

        var memberIdList = listOf<String>()
        var conn: Connection? = null

        textIO.newStringInputReader()
            .withItemName("memberIds")
            .withValueChecker { memberIds, itemName ->
                val errMsg = "Please input the valid member Ids. [input: $memberIds]"
                if (memberIds == null || memberIds.trim().isEmpty()) {
                    return@withValueChecker listOf(ColorLog.logError(errMsg))
                }

                memberIdList = memberIds.split(",").filter { it != null && it.trim().isNotEmpty() }.toList()
                if (memberIds.isEmpty()) {
                    return@withValueChecker listOf(ColorLog.logError(errMsg))
                }
                return@withValueChecker null;
            }
            .read(ColorLog.logInput("Input the member ids (separator: ',')"))

        println()

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

        println(ColorLog.logNormal("Fetching reservation ids... (member ids: $memberIdList)"))

        var reservationIdList = listOf<String>()
        var licenseDataList = listOf<LicenseData>()
        try {
            reservationIdList = fetchReservationIds(conn!!, memberIdList)
            licenseDataList = fetchLicenseList(conn!!, memberIdList)
        } finally {
            conn!!.close()
        }

        println(ColorLog.logNormal("Member Ids: $memberIdList"))
        println(ColorLog.logNormal("Reservation Ids: $reservationIdList"))

        println(ColorLog.logNormal("Creating SQLs..."))
        println()

        val sqlList = createSqls(memberIdList, reservationIdList)
        sqlList.forEach {
            println(it.title)
            it.sqls.forEach {
                println(it)
            }
            println()
        }

        printLicenseRemoveCommands(licenseDataList)
    }

    private fun fetchReservationIds(conn: Connection, memberIdList: List<String>): List<String> {
        val bookingInfoList = BookingDBUtil(conn).fetchBookingDataListByMemberIdList(memberIdList)
        return bookingInfoList.map { it.id }.toList() as List<String>
    }

    private fun fetchLicenseList(conn: Connection, memberIdList: List<String>): List<LicenseData> {
        return BookingDBUtil(conn).fetchLicenseData(memberIdList)
    }

    private fun createSqls(memberIdList: List<String>, reservationIdList: List<String>): MutableList<SqlData> {

        val allSqlList = mutableListOf<SqlData>()

        val selectSqlData = createSelectSqls(memberIdList)
        allSqlList.add(selectSqlData)

        val deleteSqlData = createDeleteLogAndHistorySqls(memberIdList)
        allSqlList.add(deleteSqlData)

        val confirmSqlData = createConfirmSqls(memberIdList)
        allSqlList.add(confirmSqlData)

        val deleteReservationHistorySql = createDeleteReservationSqls("t_reservation_history", "id", reservationIdList)
        allSqlList.add(deleteReservationHistorySql)

        val deleteReservationFeeSql =
            createDeleteReservationSqls("t_reservation_fee", "reservation_id", reservationIdList)
        allSqlList.add(deleteReservationFeeSql)

        val deleteReservationFeeHistorySql =
            createDeleteReservationSqls("t_reservation_fee_history", "reservation_id", reservationIdList)
        allSqlList.add(deleteReservationFeeHistorySql)

        val deleteCouponUseSql = createDeleteReservationSqls("t_coupon_use", "reservation_id", reservationIdList)
        allSqlList.add(deleteCouponUseSql)

        val reservationDataSql = createDeleteReservationDataSqls(memberIdList)
        allSqlList.add(reservationDataSql)

        return allSqlList
    }

    private fun createSelectSqls(reservationIdList: List<String>): SqlData {

        val selectSqlList = mutableListOf<String>()

        reservationIdList.forEach {
            val sql = "SELECT * FROM t_contact_log WHERE MEMBERSHIP_ID = '$it';"
            selectSqlList.add(sql)
        }
        selectSqlList.add("SELECT count(*) FROM t_contact_log;" + System.lineSeparator())

        reservationIdList.forEach {
            val sql = "SELECT * FROM t_contact_log_history WHERE MEMBERSHIP_ID = '$it';"
            selectSqlList.add(sql)
        }
        selectSqlList.add("SELECT count(*) FROM t_contact_log_history;" + System.lineSeparator())

        reservationIdList.forEach {
            val sql = "SELECT * FROM t_membership_history WHERE ID = '$it';"
            selectSqlList.add(sql)
        }
        selectSqlList.add("SELECT count(*) FROM t_membership_history;" + System.lineSeparator())

        reservationIdList.forEach {
            val sql = "SELECT * FROM m_membership WHERE ID = '$it';"
            selectSqlList.add(sql)
        }
        selectSqlList.add("SELECT count(*) FROM m_membership;")

        return SqlData("# Confirm before remove", selectSqlList)
    }

    private fun createDeleteLogAndHistorySqls(reservationIdList: List<String>): SqlData {

        val deleteSqlList = mutableListOf<String>()

        reservationIdList.forEach {
            deleteSqlList.add("DELETE FROM t_contact_log WHERE MEMBERSHIP_ID = '$it';")
            deleteSqlList.add("DELETE FROM t_contact_log_history WHERE MEMBERSHIP_ID = '$it';")
            deleteSqlList.add("DELETE FROM t_membership_history WHERE ID = '$it';")
            deleteSqlList.add("DELETE FROM t_member_remarks WHERE membership_id= '$it';")
            deleteSqlList.add("DELETE FROM m_membership WHERE ID = '$it';")
        }

        return SqlData("# Remove user data", deleteSqlList)
    }

    private fun createConfirmSqls(reservationIdList: List<String>): SqlData {

        val confirmSqlList = mutableListOf<String>()

        reservationIdList.forEach {
            confirmSqlList.add("SELECT * FROM t_contact_log WHERE MEMBERSHIP_ID = '$it';")
        }
        confirmSqlList.add("SELECT count(*) FROM t_contact_log;")

        reservationIdList.forEach {
            confirmSqlList.add("SELECT * FROM t_contact_log_history WHERE MEMBERSHIP_ID = '$it';")
        }
        confirmSqlList.add("SELECT count(*) FROM t_contact_log_history;")

        reservationIdList.forEach {
            confirmSqlList.add("SELECT * FROM t_membership_history WHERE ID = '$it';")
        }
        confirmSqlList.add("SELECT count(*) FROM t_membership_history;")

        reservationIdList.forEach {
            confirmSqlList.add("SELECT * FROM m_membership WHERE ID = '$it';")
        }
        confirmSqlList.add("SELECT count(*) FROM m_membership;")

        return SqlData("# Confirm after remove", confirmSqlList)
    }

    private fun createDeleteReservationSqls(
        tableName: String,
        primaryId: String,
        reservationIdList: List<String>
    ): SqlData {

        val confirmSqlList = mutableListOf<String>()

        val idStr = reservationIdList.joinToString(prefix = "(", postfix = ")", separator = ",") { "'$it'" }

        val selectSql = "select COUNT(*) from $tableName where $primaryId in $idStr"
        val deleteSql = "delete from $tableName where $primaryId in $idStr"

        confirmSqlList.add(selectSql)
        confirmSqlList.add(deleteSql)
        confirmSqlList.add(selectSql)

        return SqlData("# remove $tableName data", confirmSqlList)
    }

    private fun createDeleteReservationDataSqls(
        memberIdList: List<String>
    ): SqlData {

        val deleteSqlList = mutableListOf<String>()

        memberIdList.forEach {
            deleteSqlList.add("select id from t_reservation where membership_id = '$it';")
            deleteSqlList.add("delete from t_reservation where membership_id = '$it';")
            deleteSqlList.add("select COUNT(*) from t_reservation where membership_id = '$it';")
        }

        return SqlData("# remove t_reservation data", deleteSqlList)
    }

    private fun printLicenseRemoveCommands(licenseDataList: List<LicenseData>) {

        println("# Remove license images")
        println("ls -lart ${licenseFileDir}")
        println()

        licenseDataList.forEach {
            println("rm ${licenseFileDir + it.licenseFrontImageUrl}")
            println("rm ${licenseFileDir + it.licenseBackImageUrl}")
        }

        println()
        println("ls -lart $licenseFileDir")
    }
}