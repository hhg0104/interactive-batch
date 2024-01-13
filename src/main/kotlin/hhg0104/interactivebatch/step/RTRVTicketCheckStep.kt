package hhg0104.interactivebatch.step

import org.apache.commons.validator.routines.UrlValidator
import org.beryx.textio.TextIO
import java.net.URL

class RTRVTicketCheckStep : InteractiveStep {

    override fun proceed(textIO: TextIO, stepData: InteractiveStepData) {

        textIO.newStringInputReader()
            .withItemName("ticketUrl")
            .withValueChecker { urlPath: String, itemName: String ->
                val errMsg = ColorLog.logError("Please input the valid RTRV ticket URL. [input: $urlPath]")

                if (UrlValidator().isValid(urlPath)) {
                    val url = URL(urlPath)
                    val ticketName = url.path.split("/").firstOrNull { it.startsWith("RTRV-") } ?: return@withValueChecker listOf(errMsg)

                    stepData.rtrvTicketName = ticketName
                    println(ColorLog.logNormal("Target RTRV ticket: ${ColorLog.logResult(ticketName)}."))

                    return@withValueChecker null
                }
                return@withValueChecker listOf(errMsg)
            }
            .read(ColorLog.logInput("Input RTRV ticket link"))
    }
}