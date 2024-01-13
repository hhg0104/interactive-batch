package hhg0104.interactivebatch.util.excel

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExcelColumn(val name: String = "", val resultType: ExcelDataType)