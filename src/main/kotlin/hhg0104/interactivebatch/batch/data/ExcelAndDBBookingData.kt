package hhg0104.interactivebatch.batch.data

import hhg0104.interactivebatch.util.excel.ExcelColumn
import hhg0104.interactivebatch.util.excel.ExcelDataType
import java.io.Serializable
import java.time.LocalDateTime

class ExcelAndDBBookingData : Serializable {

    companion object {
        val mapColumnsToProperties = mapOf(
            "ID" to "id",
            "USAGE_POINT" to "usagePoint",
            "GRANT_POINT" to "grantPoint",
            "PAYMENT_ID" to "paymentId",
            "MEMBERSHIP_ID" to "membershipId",
            "RESERVATION_DATE" to "reservationDate",
            "CAR_MODEL_NAME" to "carModelName",
            "ACTUAL_BASIC_AMOUNT" to "actualBasicAmount",
            "ACTUAL_DISTANCE_AMOUNT" to "actualDistanceAmount",
            "ACTUAL_TAXABLE_EXCESS_AMOUNT" to "actualTaxableExcessAmount",
            "ACTUAL_NON_TAXABLE_EXCESS_AMOUNT" to "actualNonTaxableExcessAmount",
            "ACTUAL_DISCOUNT_AMOUNT" to "actualDiscountAmount",
            "DISTANCE_KM" to "distanceKm",
            "DEPARTURE_TIME" to "departureTime",
            "RETURN_TIME" to "returnTime",
            "ACTUAL_DEPARTURE_TIME" to "actualDepartureTime",
            "ACTUAL_RETURN_TIME" to "actualReturnTime",
            "PARTNER_REMARKS" to "partnerRemarks",
        )
    }

    @ExcelColumn("予約番号", ExcelDataType.STRING)
    var id: String? = null

    @ExcelColumn("ポイント利用", ExcelDataType.NUMERIC)
    var usagePoint: Int? = null

    var grantPoint: Int? = null

    var paymentId: String? = null

    var membershipId: String? = null

    @ExcelColumn("予約受付日時", ExcelDataType.DATE)
    var reservationDate: LocalDateTime? = null

    @ExcelColumn("予約車種", ExcelDataType.STRING)
    var carModelName: String? = null

    /**
     * Excel Data Only
     */
    @ExcelColumn("利用者表示料金(円)", ExcelDataType.NUMERIC)
    var displayPrice: Int? = null

    /**
     * Excel Data Only
     */
    @ExcelColumn("料金合計(円)", ExcelDataType.NUMERIC)
    var totalAmount: Int? = null

    @ExcelColumn("通常利用料金(円)", ExcelDataType.NUMERIC)
    var actualBasicAmount: Int? = null

    @ExcelColumn("距離料金(円)", ExcelDataType.NUMERIC)
    var actualDistanceAmount: Int? = null

    @ExcelColumn("課税追加(円)", ExcelDataType.NUMERIC)
    var actualTaxableExcessAmount: Int? = null

    @ExcelColumn("非課税追加料金(円)", ExcelDataType.NUMERIC)
    var actualNonTaxableExcessAmount: Int? = null

    @ExcelColumn("割引料金(円)", ExcelDataType.NUMERIC)
    var actualDiscountAmount: Int? = null

    /**
     * Excel Data Only
     */
    @ExcelColumn("実利用時間(分)", ExcelDataType.NUMERIC)
    var totalMinutesForUse: Int? = null

    /**
     * Excel Data Only
     */
    @ExcelColumn("課金対象時間(分)", ExcelDataType.NUMERIC)
    var totalMinutesForOverUse: Int? = null

    @ExcelColumn("利用距離(km)", ExcelDataType.NUMERIC)
    var distanceKm: Int? = null

    @ExcelColumn("予定日時(乗車)", ExcelDataType.DATE)
    var departureTime: LocalDateTime? = null

    @ExcelColumn("予定日時(返却)", ExcelDataType.DATE)
    var returnTime: LocalDateTime? = null

    @ExcelColumn("利用日時(乗車)", ExcelDataType.DATE)
    var actualDepartureTime: LocalDateTime? = null

    @ExcelColumn("利用日時(返却)", ExcelDataType.DATE)
    var actualReturnTime: LocalDateTime? = null

    /**
     * Excel Data Only
     */
    @ExcelColumn("駐車場(出発)", ExcelDataType.STRING)
    var parkinglotName: String? = null

    /**
     * Excel Data Only
     */
    @ExcelColumn("入力者区分", ExcelDataType.STRING)
    var registerType: String? = null

    @ExcelColumn("備考", ExcelDataType.STRING)
    var partnerRemarks: String? = null

    /**
     * Excel Data Only
     */
    @ExcelColumn("料金請求方式", ExcelDataType.STRING)
    var billingType: String? = null
}