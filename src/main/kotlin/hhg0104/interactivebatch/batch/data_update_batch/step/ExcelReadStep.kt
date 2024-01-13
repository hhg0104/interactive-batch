package hhg0104.interactivebatch.batch.data_update_batch.step

import hhg0104.interactivebatch.batch.data.ExcelAndDBBookingData
import hhg0104.interactivebatch.step.ColorLog
import hhg0104.interactivebatch.step.InteractiveStep
import hhg0104.interactivebatch.step.InteractiveStepData
import hhg0104.interactivebatch.util.excel.ExcelParser
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.beryx.textio.TextIO
import java.io.File


class ExcelReadStep : InteractiveStep {

    override fun proceed(textIO: TextIO, stepData: InteractiveStepData) {

        val excelPaths = mutableListOf<String>()
        var needInputMore = true

        while (needInputMore) {
            textIO.newStringInputReader()
                .withItemName("excelPath")
                .withValueChecker { excelPath, itemName ->
                    if (!File(excelPath).isFile) {
                        return@withValueChecker listOf(
                            ColorLog.logError("This file does not exist [input: $excelPath]")
                        )
                    }

                    if (excelPaths.contains(excelPath)) {
                        return@withValueChecker listOf(
                            ColorLog.logError("This excel file is already included. [input: $excelPath]")
                        )
                    }

                    excelPaths.add(excelPath)
                    ColorLog.logNormal("New excel file is inputted [${ColorLog.logResult(excelPath)}}")

                    return@withValueChecker null;
                }
                .read(ColorLog.logInput("Input the excel path [${excelPaths.size}]"))

            needInputMore = textIO.newBooleanInputReader()
                .read(ColorLog.logInput("Do you want to input more file?"))
        }

        val excelFilesStr = excelPaths.joinToString(separator = ",")
        println(ColorLog.logNormal("Total ${excelPaths.size} files has been inputted. [${ColorLog.logResult(excelFilesStr)}]"))
        println()

        excelPaths.forEach {
            ColorLog.logNormal(ColorLog.logResult(it))
        }

        excelPaths.forEach {
            try {
                println(ColorLog.logNormal("Extracting the excel data... [${ColorLog.logResult(it)}]"))
                XSSFWorkbook(File(it)).use { workbook ->
                    val dataList = ExcelParser(ExcelAndDBBookingData::class.java).parse(workbook)
                    println(ColorLog.logNormal("Extracted ${dataList?.size} booking data from the excel file. [$it]"))

                    if (dataList.isNotEmpty()) {
                        val reservationIds = dataList.joinToString(separator = ", ") { "${it.id}" }
                        println(ColorLog.logNormal("Booking Id List: $reservationIds"))

                        stepData.excelDataList!!.addAll(dataList)
                    }
                }
            } catch (e: Exception) {
                println(ColorLog.logError("Cannot read the excel file [$it]: ${e.message}"))
            }
        }

        println(ColorLog.logNormal("Extracted total ${ColorLog.logResult(stepData.excelDataList!!.size.toString())} booking data from all excel files."))

        if (stepData.excelDataList!!.isNotEmpty()) {
            val reservationIds = stepData.excelDataList!!.joinToString(separator = ", ") { "${it.id}" }
            println(ColorLog.logNormal("Total Booking Id List: ${ColorLog.logResult(reservationIds)}"))
        }
    }
}