package hhg0104.interactivebatch.step

import hhg0104.interactivebatch.constants.OperationType
import hhg0104.interactivebatch.batch.data.ExcelAndDBBookingData

class InteractiveStepData {

    var operationType: OperationType? = null

    var rtrvTicketName: String? = null

    var excelDataList: MutableList<ExcelAndDBBookingData>? = mutableListOf()
}
