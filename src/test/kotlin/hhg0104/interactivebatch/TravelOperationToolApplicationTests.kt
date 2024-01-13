package hhg0104.interactivebatch

import org.junit.jupiter.api.Test

class TravelOperationToolApplicationTests {

    @Test
    fun testSqlFormatter() {
        val urlStr = "https://jira.rakuten-it.com/jira/browse/AF-25234"
        val ticketName = urlStr.split("/").filter { it.startsWith("TAF-") }.firstOrNull()
        println(ticketName)
    }
}
