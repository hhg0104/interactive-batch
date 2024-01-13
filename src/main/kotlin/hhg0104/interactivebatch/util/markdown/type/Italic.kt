package hhg0104.interactivebatch.util.markdown.type

class Italic : MarkDownContent {

    constructor(text: String) : super(text)

    constructor(content: MarkDownContent) : super(content)

    override fun wrap(): String {
        return "_${text}_"
    }
}