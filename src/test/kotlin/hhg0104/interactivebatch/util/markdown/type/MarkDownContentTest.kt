package hhg0104.interactivebatch.util.markdown.type

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MarkDownContentTest {

    @Test
    fun testConvert() {

        val testStr = "test"

        var converted = Blockquotes(testStr).convert()
        assertEquals(">$testStr", converted)

        converted = Bold(testStr).convert()
        assertEquals("**$testStr**", converted)

        converted = Italic(testStr).convert()
        assertEquals("_${testStr}_", converted)

        val testHref = "https://abc.net"
        converted = Link(testStr, testHref).convert()
        assertEquals("[$testStr]($testHref)", converted)

        converted = Plain(testStr).convert()
        assertEquals("$testStr", converted)

        converted = UnderLine(testStr).convert()
        assertEquals("<u>$testStr</u>", converted)
    }

    @Test
    fun testConvertMultiple() {

        val testStr = "test"

        var converted = Blockquotes(Bold(testStr)).convert()
        assertEquals(">**$testStr**", converted)

        converted = Italic(Bold(testStr)).convert()
        assertEquals("_**$testStr**_", converted)

        converted = Italic(Bold(UnderLine(testStr))).convert()
        assertEquals("_**<u>$testStr</u>**_", converted)
    }
}