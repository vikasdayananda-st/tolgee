package io.tolgee.formats.android.out

import io.tolgee.formats.MobileStringEscaper
import io.tolgee.formats.android.AndroidParsingConstants
import io.tolgee.formats.android.AndroidStringValue
import io.tolgee.formats.paramConvertors.`in`.JavaToIcuPlaceholderConvertor
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class TextToAndroidXmlConvertor(
  private val document: Document,
  private val value: AndroidStringValue,
) {
  val string = value.string

  fun convert(): ContentToAppend {
    try {
      if (value.isWrappedCdata || containsXmlAndPlaceholders) {
        return contentWrappedInCdata
      }

      wrapUnsupportedTagsWithCdata(analysisResult, parsed)
      escapeTextNodes()

      return ContentToAppend(
        children = parsed.childNodes.item(0).childNodes.asSequence().toList(),
      )
    } catch (ex: java.lang.Exception) {
      return ContentToAppend(text = string)
    }
  }

  private val parsed by lazy {
    parseString(string)
  }

  private val analysisResult by lazy {
    parsed.analyze()
  }

  private val containsXmlAndPlaceholders
    get() = analysisResult.containsXml && analysisResult.containsPlaceholders

  private val contentWrappedInCdata: ContentToAppend
    get() {
      val cdata =
        document.createCDATASection(
          string.escape(
            escapeApos = true,
            keepPercentSignEscaped = true,
            quoteMoreWhitespaces = false,
            escapeNewLines = true,
          ),
        )
      return ContentToAppend(children = listOf(cdata))
    }

  private fun escapeTextNodes() {
    analysisResult.textNodes.forEach { node ->
      node.escapeText(keepPercentSignEscaped = analysisResult.containsPlaceholders, quoteMoreWhitespaces = true)
    }
  }

  private fun wrapUnsupportedTagsWithCdata(
    analysisResult: AnalysisResult,
    doc: Document,
  ) {
    analysisResult.unsupportedTagNodes.forEach { node ->
      node.parentNode.replaceChild(
        doc.createCDATASection(
          node.writeToString().escape(
            escapeApos = true,
            keepPercentSignEscaped = analysisResult.containsPlaceholders,
            quoteMoreWhitespaces = false,
            escapeNewLines = true,
          ),
        ),
        node,
      )
    }
  }

  private fun parseString(contentNotNull: String): Document =
    documentBuilder.parse(InputSource(StringReader("<root>$contentNotNull</root>")))

  private fun Node.writeToString(): String {
    val source = DOMSource(this)
    val writer = StringWriter()
    val result = StreamResult(writer)
    xmlTransformer.transform(source, result)
    return writer.buffer.toString()
  }

  private fun NodeList.forEach(action: (Node) -> Unit) {
    this.asSequence().forEach(action)
  }

  private fun NodeList.asSequence(): Sequence<Node> {
    return (0 until this.length).asSequence().map { this.item(it) }
  }

  private fun Document.analyze(): AnalysisResult {
    var containsTags = false
    var containsPlaceholders = false
    val unsupportedTagNodes = mutableListOf<Node>()
    val textNodes = mutableListOf<Node>()
    forEachNodeDeep { node ->
      if (node.nodeType == Node.TEXT_NODE) {
        val matches = JavaToIcuPlaceholderConvertor.JAVA_PLACEHOLDER_REGEX.findAll(node.textContent)
        if (matches.any { it.value != "%%" }) {
          containsPlaceholders = true
        }
        textNodes.add(node)
      }
      if (node.nodeType == Node.ELEMENT_NODE) {
        if (node.nodeName.lowercase() !in AndroidParsingConstants.supportedTags) {
          unsupportedTagNodes.add(node)
        } else {
          containsTags = true
        }
      }
    }
    return AnalysisResult(
      containsXml = containsTags,
      containsPlaceholders = containsPlaceholders,
      unsupportedTagNodes = unsupportedTagNodes,
      textNodes = textNodes,
    )
  }

  private fun Document.forEachNodeDeep(action: (Node) -> Unit) {
    val childNodes = this.documentElement.childNodes
    for (i in 0 until childNodes.length) {
      val node = childNodes.item(i)
      action(node)
      if (node.hasChildNodes()) {
        node.childNodes.forEach {
          action(it)
        }
      }
    }
  }

  private fun Node.escapeText(
    keepPercentSignEscaped: Boolean,
    quoteMoreWhitespaces: Boolean,
  ) {
    this.textContent = this.getEscapedText(keepPercentSignEscaped, quoteMoreWhitespaces)
  }

  private fun Node.getEscapedText(
    keepPercentSignEscaped: Boolean,
    quoteMoreWhitespaces: Boolean,
  ): String {
    return this.textContent.escape(
      escapeApos = isParentRoot(),
      keepPercentSignEscaped = keepPercentSignEscaped,
      quoteMoreWhitespaces = quoteMoreWhitespaces,
      escapeNewLines = !analysisResult.containsXml,
    )
  }

  private fun Node.isParentRoot(): Boolean {
    return this.parentNode.nodeName == "root" && this.parentNode.parentNode === this.ownerDocument
  }

  companion object {
    private val documentBuilder: DocumentBuilder by lazy { DocumentBuilderFactory.newInstance().newDocumentBuilder() }

    private val xmlTransformer by lazy {
      val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
      transformer
    }
  }

  private fun String.escape(
    escapeApos: Boolean,
    keepPercentSignEscaped: Boolean,
    /**
     * We only support this non XML strings
     */
    quoteMoreWhitespaces: Boolean,
    escapeNewLines: Boolean,
  ): String {
    return MobileStringEscaper(
      string = this,
      escapeApos = escapeApos,
      keepPercentSignEscaped = keepPercentSignEscaped,
      quoteMoreWhitespaces = quoteMoreWhitespaces,
      escapeNewLines = escapeNewLines,
      utfSymbolCharacter = 'u',
      escapeQuotes = true,
    ).escape()
  }

  data class ContentToAppend(val text: String? = null, val children: Collection<Node>? = null)
}

private data class AnalysisResult(
  val containsXml: Boolean,
  val containsPlaceholders: Boolean,
  val unsupportedTagNodes: List<Node>,
  val textNodes: MutableList<Node>,
)
