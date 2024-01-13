package hhg0104.interactivebatch.util.markdown.type

abstract class MarkDownContent {

    var text: String = ""
    var content: MarkDownContent? = null

    constructor(text: String) {
        this.text = text
    }

    constructor(content: MarkDownContent) {
        this.content = content
    }

    fun convert(): String {
        if (this.content != null) {
            this.text = this.content!!.convert()
        }

        return wrap().replace(System.lineSeparator(), "<br/>")
    }

    abstract protected fun wrap(): String
}