package hhg0104.interactivebatch.batch.data

import java.io.Serializable
import java.sql.Timestamp

class BookingHistoryData : Serializable {

    companion object {
        val mapColumnsToProperties = mapOf(
            "ID" to "id",
            "NO" to "no",
            "CAR_TYPE_ID" to "carTypeId",
            "DEPARTURE_TIME" to "departureTime",
            "RETURN_TIME" to "returnTime",
            "RAKUTEN_TOTAL_AMOUNT" to "rakutenTotalAmount",
            "PARTNER_TOTAL_AMOUNT" to "partnerTotalAmount",
            "USAGE_POINT" to "usagePoint",
            "EVENT" to "event",
            "EXE_USER" to "exeUser",
            "CREATE_TIMESTAMP" to "createTimestamp"
        )
    }

    var id: String? = null

    var no: Int? = null

    var carTypeId: String? = null

    var rakutenTotalAmount: Int? = null

    var partnerTotalAmount: Int? = null

    var usagePoint: Int? = null

    var event: Int? = null

    var exeUser: String? = null

    var createTimestamp: Timestamp? = null
}