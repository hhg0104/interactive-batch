package hhg0104.interactivebatch.util.excel

import hhg0104.interactivebatch.step.ColorLog
import org.apache.poi.hssf.usermodel.HSSFFont
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFFont
import org.springframework.util.CollectionUtils
import java.sql.Timestamp
import java.util.Date


class ExcelParser<T> {

    var parserType: Class<T>? = null

    constructor(parserType: Class<T>) {
        this.parserType = parserType
    }

    fun parse(workbook: Workbook): MutableList<T> {

        val totalDataList: MutableList<T> = ArrayList()

        println(ColorLog.logNormal("This excel file has ${workbook.numberOfSheets} sheet(s)."))

        for (sheetIdx in 0 until workbook.numberOfSheets) {
            val sheet = workbook.getSheetAt(sheetIdx)
            val dataListInSheet = parseSheet(sheet)

            if (!CollectionUtils.isEmpty(dataListInSheet)) {
                totalDataList.addAll(dataListInSheet)
            }
        }

        return totalDataList
    }

    private fun parseSheet(
        sheet: Sheet
    ): MutableList<T> {

        println(ColorLog.logNormal("Parsing the '${sheet.sheetName}' sheet..."))

        val dataList: MutableList<T> = ArrayList()
        val columnRow = sheet.getRow(0)

        val columnNameAndIndexMap = mutableMapOf<String, Int>()
        for (cell: Cell in columnRow) {
            if (sheet.isColumnHidden(cell.columnIndex)) continue
            columnNameAndIndexMap[cell.stringCellValue] = cell.columnIndex
        }

        sheet.forEachIndexed loop@{ idx: Int, row: Row ->
            if (idx === 0 || row.zeroHeight) return@loop

            val data = this.parserType?.newInstance()
            this.parserType?.declaredFields?.forEach { field ->
                if (!field.isAnnotationPresent(ExcelColumn::class.java)) return@forEach

                val annotation = field.getAnnotation(ExcelColumn::class.java)
                val columnIndex: Int? = columnNameAndIndexMap[annotation.name]
                if (columnIndex === null || columnIndex === -1) {
                    println(
                        ColorLog.logNormal("'${annotation.name}' column does not exist in the excel file")
                    )
                    return@forEach
                }

                val cell = row.getCell(columnIndex) ?: return@forEach

                var columnValue: Any? = when (annotation.resultType) {
                    ExcelDataType.STRING -> cell.stringCellValue
                    ExcelDataType.NUMERIC -> {
                        val value = cell.numericCellValue.toInt()
                        if (field.type.isAssignableFrom(String::class.java)) {
                            value?.toString()
                        } else {
                            value
                        }
                    }

                    ExcelDataType.DATE -> {
                        val dateValue: Date = cell.dateCellValue
                        Timestamp(dateValue.time).toLocalDateTime()
                    }
                }

                field.isAccessible = true
                field.set(data, columnValue)
            }
            if (data != null) {
                dataList.add(data)
            }
        }

        println(ColorLog.logNormal("Parsed the ${dataList.size} data from the '${sheet.sheetName}' sheet."))

        return dataList
    }

    private fun getFontColorHex(cell: Cell): String {

        val workbook = cell.sheet.workbook
        val font = workbook.getFontAt(cell.cellStyle.fontIndex)

        var color: Color? = null
        if (font is XSSFFont) {
            color = font.xssfColor
        } else if (font is HSSFFont) {
            color = font.getHSSFColor(workbook as HSSFWorkbook)
        }

        var colorHex: String = ""
        if (color is XSSFColor) {
            val rgb: ByteArray = (color as XSSFColor).getRGB()
            colorHex = toHexString(rgb)
        } else if (color is HSSFColor) {
            val triplet: ShortArray = (color as HSSFColor).getTriplet()
            colorHex = toHexString(triplet)
        }

        return colorHex
    }

    private fun toHexString(arrayOfNumbers: Any): String {
        var hex = ""
        if (arrayOfNumbers is ByteArray) {
            for (b in arrayOfNumbers) {
                hex += String.format("%02X", b)
            }
        } else if (arrayOfNumbers is ShortArray) {
            for (s in arrayOfNumbers) {
                hex += String.format("%02X", s)
            }
        }
        return hex
    }
}