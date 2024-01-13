package hhg0104.interactivebatch.util.markdown.type

class Link : MarkDownContent {

    var linkUrl: String? = null

    constructor(text: String, linkUrl: String) : super(text) {
        this.linkUrl = linkUrl
    }

    override fun wrap(): String {
        return "[$text]($linkUrl)"
    }
}