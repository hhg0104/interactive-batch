package hhg0104.interactivebatch.util.markdown

import hhg0104.interactivebatch.util.markdown.type.Bold
import hhg0104.interactivebatch.util.markdown.type.Plain
import hhg0104.interactivebatch.util.markdown.type.UnderLine
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

internal class MarkdownBuilderTest {

    @Test
    fun testMDMarkTableBuild() {

        val tableContents = MarkdownTableBuilder
            .builder()
            .title("-- Reservation ID: 12345678")
            .headers(mutableListOf("Column", "DB", "Excel"))
            .row(mutableListOf(Plain("GRANT_POINT"), Plain("800"), Bold("1300")))
            .row(mutableListOf(Plain("ACTUAL_BASIC_AMOUNT"), Plain("4500"), Bold("4000")))
            .row(mutableListOf(Plain("ACTUAL_DISTANCE_AMOUNT"), Plain("254"), UnderLine(Bold("1465"))))
            .row(mutableListOf(Plain("ACTUAL_DISCOUNT_AMOUNT"), Plain("1500"), Bold("2000")))
            .row(mutableListOf(Plain("DISTANCE_KM"), Plain("180"), Bold("213")))
            .row(
                mutableListOf(
                    Plain("ACTUAL_DEPARTURE_TIME"),
                    Plain("2023/09/23 11:30:00"),
                    Bold("2023/09/23 12:00:00")
                )
            )
            .row(mutableListOf(Plain("ACTUAL_RETURN_TIME"), Plain(""), UnderLine(Bold("2023/09/24 17:10:00"))))
            .row(
                mutableListOf(
                    Plain("PARTNER_REMARKS"),
                    Plain(""),
                    Bold("クーポン利用額：1000\nクーポンID：6OAN-EVAO-GEG9-0IBZ\nポイント利用：3280")
                )
            )
            .build()

        val expectedContents = """
            >-- Reservation ID: 12345678

            |        Column        |         DB        |                             Excel                             |
            |----------------------|-------------------|---------------------------------------------------------------|
            |      GRANT_POINT     |        800        |                            **1300**                           |
            |  ACTUAL_BASIC_AMOUNT |        4500       |                            **4000**                           |
            |ACTUAL_DISTANCE_AMOUNT|        254        |                        <u>**1465**</u>                        |
            |ACTUAL_DISCOUNT_AMOUNT|        1500       |                            **2000**                           |
            |      DISTANCE_KM     |        180        |                            **213**                            |
            | ACTUAL_DEPARTURE_TIME|2023/09/23 11:30:00|                    **2023/09/23 12:00:00**                    |
            |  ACTUAL_RETURN_TIME  |                   |                 <u>**2023/09/24 17:10:00**</u>                |
            |    PARTNER_REMARKS   |                   |**クーポン利用額：1000<br/>クーポンID：6OAN-EVAO-GEG9-0IBZ<br/>ポイント利用：3280**|
        """.trimIndent()

        assertEquals(expectedContents, tableContents)
    }
}