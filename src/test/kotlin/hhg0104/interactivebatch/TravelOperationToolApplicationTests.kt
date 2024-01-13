package hhg0104.interactivebatch

import org.junit.jupiter.api.Test

class TravelOperationToolApplicationTests {

    @Test
    fun testSqlFormatter() {
        val urlStr = "https://jira.com/jira/browse/ISSUE-25234"
        val ticketName = urlStr.split("/").filter { it.startsWith("ISSUE-") }.firstOrNull()
        println(ticketName)
    }
}
