package hhg0104.interactivebatch.batch.data

import java.io.Serializable
import java.sql.Timestamp

class BookingCouponUseData : Serializable {

    companion object {
        val mapColumnsToProperties = mapOf(
            "RESERVATION_ID" to "reservationId",
            "RESERVATION_HISTORY_NO" to "reservationHistoryNo",
            "NO" to "no",
            "CODE" to "code",
            "NAME" to "name",
            "DISCOUNT_AMOUNT" to "discountAmount",
            "ACTUAL_DISCOUNT_AMOUNT" to "actualDiscountAmount",
            "CREATE_TIMESTAMP" to "createTimestamp",
            "UPDATE_TIMESTAMP" to "updateTimestamp",
            "DELETE_FLG" to "deleteFlg",
        )
    }

    var reservationId: String? = null

    var reservationHistoryNo: Int? = null

    var no: Int? = null

    var code: String? = null

    var name: String? = null

    var discountAmount: Int? = null

    var actualDiscountAmount: Int? = null

    var createTimestamp: Timestamp? = null

    var updateTimestamp: Timestamp? = null

    var deleteFlg: Int? = null
}