package hhg0104.interactivebatch.batch.data_update_batch.data

class CompareData(var columName: String, var dbData: String?, var excelData: String?, var isDiff: Boolean = false) {
    enum class Type {
        STRING, NUMBER, DATE
    }
}