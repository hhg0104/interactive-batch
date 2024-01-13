package hhg0104.interactivebatch.batch.data_update_batch.data

class SqlGenerationData{

    var id: String? = null

    var bookingTableSql: String = ""

    var bookingHistoryTableSql: String = ""

    var couponUseTableSql: String = ""

    var diffDataList: MutableList<CompareData> = mutableListOf()
}