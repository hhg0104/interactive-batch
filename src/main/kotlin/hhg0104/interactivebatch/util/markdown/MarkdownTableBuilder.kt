package hhg0104.interactivebatch.util.markdown

import hhg0104.interactivebatch.step.ColorLog
import hhg0104.interactivebatch.util.markdown.type.Blockquotes
import hhg0104.interactivebatch.util.markdown.type.MarkDownContent
import org.apache.commons.lang3.StringUtils

class MarkdownTableBuilder {

    private var title: String = ""

    private val headers: MutableList<String> = mutableListOf()

    private val rows: MutableList<MutableList<MarkDownContent>> = mutableListOf()

    private constructor()

    companion object {
        fun builder(): MarkdownTableBuilder {
            return MarkdownTableBuilder()
        }
    }

    fun title(title: String): MarkdownTableBuilder {
        this.title = title
        return this
    }

    fun headers(titles: MutableList<String>): MarkdownTableBuilder {
        this.headers.clear()
        this.headers.addAll(titles)
        return this
    }

    fun row(row: MutableList<MarkDownContent>): MarkdownTableBuilder {
        this.rows.add(row)
        return this
    }

    fun rows(rows: MutableList<MutableList<MarkDownContent>>): MarkdownTableBuilder {
        this.rows.addAll(rows)
        return this
    }

    fun build(): String {

        if (!check()) return ""

        val tableContents = mutableListOf<String>()
        val maxColumnLengths: MutableList<Int> = calculateMaxColumnLengths()

        if (this.title.isNotEmpty()) {
            val formattedTitle = Blockquotes(this.title).convert()
            tableContents.add(formattedTitle + System.lineSeparator())
        }

        val headerLine = createHeaderLine(maxColumnLengths)
        tableContents.add(headerLine)

        val headerBottomLine = createHeaderBottomLine(maxColumnLengths)
        tableContents.add(headerBottomLine)

        val rowLines = createRowLines(maxColumnLengths)
        tableContents.addAll(rowLines)

        return tableContents.joinToString(separator = System.lineSeparator())
    }

    private fun createHeaderBottomLine(maxColumnLengths: MutableList<Int>): String {
        return maxColumnLengths
            .map { StringUtils.repeat("-", it) }
            .toList()
            .joinToString(prefix = "|", postfix = "|", separator = "|")
    }

    private fun createHeaderLine(maxColumnLengths: MutableList<Int>): String {
        var columnIdx = 0
        return this.headers
            .joinToString(prefix = "|", postfix = "|", separator = "|") {
                addSpaces(it, maxColumnLengths[columnIdx++])
            }
    }

    private fun createRowLines(maxColumnLengths: MutableList<Int>): List<String> {
        return this.rows.map {
            var columnIdx = 0
            it.joinToString(prefix = "|", postfix = "|", separator = "|") { column ->
                addSpaces(column.convert(), maxColumnLengths[columnIdx++])
            }
        }.toList()
    }

    private fun calculateMaxColumnLengths(): MutableList<Int> {
        val columnSize = this.headers.size
        val maxColumnLengths = mutableListOf<Int>()
        for (i in 0 until columnSize) {
            val maxLength: Int = this.rows.map {
                it[i].convert().length
            }.toList().max()
            maxColumnLengths.add(maxLength)
        }

        return maxColumnLengths
    }

    private fun check(): Boolean {
        if (this.headers == null || this.headers.isEmpty()) {
            println(ColorLog.logError("Headers should not be null and empty"))
            return false
        }

        val sizeUnmatchedRows = this.rows.filter { this.headers.size != it.size }.toList()
        if (sizeUnmatchedRows.isNotEmpty()) {
            println(
                ColorLog.logError("There is unmatched size rows."))
            return false
        }

        return true
    }

    private fun addSpaces(text: String, targetLength: Int): String {
        if (text.length >= targetLength) {
            return text
        }

        var textWithSpaces = text
        var addedToFront = false

        while (textWithSpaces.length < targetLength) {
            if (addedToFront) {
                textWithSpaces = "$textWithSpaces "
            } else {
                textWithSpaces = " $textWithSpaces"
            }

            addedToFront = !addedToFront
        }

        return textWithSpaces
    }
}