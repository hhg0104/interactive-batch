package hhg0104.interactivebatch.step

import hhg0104.interactivebatch.constants.OperationType
import hhg0104.interactivebatch.util.ConsoleColor
import org.beryx.textio.TextIO

class OperationTypeInputStep : InteractiveStep {

    override fun proceed(textIO: TextIO, stepData: InteractiveStepData) {

        println(ConsoleColor.addColor("----------------------------------", ConsoleColor.CYAN))
        println(ConsoleColor.addColor("|   TRAVEL OPERATION TOOL LIST   |", ConsoleColor.CYAN))
        println(ConsoleColor.addColor("----------------------------------", ConsoleColor.CYAN))
        println()

        println("------------------------------------------------------------------")
        for (type in OperationType.values()) {
            println("[${type.id}] ${type.operationName}")
        }
        println("------------------------------------------------------------------")

        println()

        val operationId = textIO.newIntInputReader()
            .withItemName("operationId")
            .withValueChecker { id: Int, itemName: String ->
                if (OperationType.getTypeById(id) != null) {
                    return@withValueChecker null
                }
                return@withValueChecker listOf(ColorLog.logError("Please input the valid operation ID. [input: $id]"))
            }
            .read(ColorLog.logInput("Input the operation ID"))

        val operationType = OperationType.getTypeById(operationId)
        if (operationType != null) {
            println(ColorLog.logNormal("Selected the ${ColorLog.logResult(operationType.operationName)}."))
        }

        stepData.operationType = operationType
    }
}