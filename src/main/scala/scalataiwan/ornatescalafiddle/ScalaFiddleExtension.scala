package scalataiwan.ornatescalafiddle

import com.novocode.ornate.Extension
import com.novocode.ornate.commonmark.NodeExtensionMethods.nodeToNodeExtensionMethods
import com.novocode.ornate.commonmark.SimpleHtmlNodeRenderer
import com.typesafe.config.Config
import org.commonmark.node._
import org.commonmark.parser.{Parser, PostProcessor}
import org.commonmark.renderer.html.{HtmlNodeRendererContext, HtmlRenderer}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.language.postfixOps
import scala.util.Try
import scala.util.control.NonFatal
import scala.util.parsing.combinator.{JavaTokenParsers, RegexParsers}
import scalaj.http.Http

class ScalaFiddleExtension extends Extension {
  val spec = "scalaFiddle"
  case class ScalaFiddleBlock(fcb: FencedCodeBlock) extends CustomBlock
  case class ApiResult(id: String, version: Int)
  override def parserExtensions(config: Config): Seq[Parser.ParserExtension] = Seq(
    new Parser.ParserExtension {
      override def extend(builder: Parser.Builder): Unit = {
        builder.postProcessor(new PostProcessor {
          def process(node: Node): Node = {
            node.accept(new AbstractVisitor {
              override def visit(fcb: FencedCodeBlock): Unit = {
                if (fcb.getInfo.startsWith(spec)) {
                  fcb.replaceWith(ScalaFiddleBlock(fcb))
                }
              }
            })
            node
          }
        })
      }
    }
  )
  override val htmlRendererExtensions: Seq[HtmlRenderer.HtmlRendererExtension] = Seq(
    new HtmlRenderer.HtmlRendererExtension {
      override def extend(builder: HtmlRenderer.Builder): Unit = {
        builder.nodeRendererFactory(SimpleHtmlNodeRenderer { (n: ScalaFiddleBlock, c: HtmlNodeRendererContext) =>
          val w = c.getWriter
          val as = parseAttributes(n.fcb.getInfo.drop(spec.length).trim)
          val url = getEmbedScalaFiddleUrl(
            as.get("name").getOrElse(""),
            as.get("description").getOrElse(""),
            n.fcb.getLiteral)
          w.raw(s"""<iframe height="300" frameborder="0" style="width: 100%; overflow: hidden;" src="$url"></iframe>""")
        })
      }
    }
  )
  def parseAttributes(info: String): Map[String, String] = {
    def unquote(s: String): String =
      if (s.isEmpty) s else s.charAt(0) match {
        case '"' => unquote(s.tail)
        case '\\' => s.charAt(1) match {
          case 'b' => '\b' + unquote(s.drop(2))
          case 'f' => '\f' + unquote(s.drop(2))
          case 'n' => '\n' + unquote(s.drop(2))
          case 'r' => '\r' + unquote(s.drop(2))
          case 't' => '\t' + unquote(s.drop(2))
          case '"' => '"'  + unquote(s.drop(2))
          case 'u' => Integer.parseInt(s.drop(2).take(4), 16).toChar + unquote(s.drop(6))
          case c => c +: unquote(s.drop(2))
        }
        case c => c + unquote(s.tail)
      }
    object AttributeParser extends JavaTokenParsers {
      override val skipWhitespace = true
      def key = ident
      def value = stringLiteral ^^ unquote
      def attribute = (key ~ '=' ~ value) ^^ { case k ~ _ ~ v => (k, v) }
      def attributes = rep(attribute)
    }
    import AttributeParser._
    Map(parse(attributes, info).get: _*)
  }
  def getEmbedScalaFiddleUrl(name: String, description: String, code: String): String = {
    val c =
      s"""
        |import fiddle.Fiddle, Fiddle.println
        |import scalajs.js
        |
        |@js.annotation.JSExport
        |object ScalaFiddle {
        |  // $$FiddleStart
        |  $code
        |  // $$FiddleEnd
        |}
      """.stripMargin
    val json =
      ("fiddle" ->
        ("name" -> name) ~
        ("description" -> description) ~
        ("sourceCode" -> c) ~
        ("libraries" -> List[String]()) ~
        ("forced" -> List[String]()) ~
        ("available" -> List[String]()) ~
        ("author" -> List[String]()))
    Try {
      val result = Http("https://scalafiddle.io/api/scalafiddle/shared/Api/save")
        .timeout(10000, 30000)
        .postData(compact(render(json)))
        .header("content-type", "text/plain;charset=UTF-8")
        .asString
        .body
      implicit val formats = DefaultFormats
      val ApiResult(id, version) = parse(result)(1).extract[ApiResult]
      s"https://embed.scalafiddle.io/embed?sfid=$id/$version"
    } recover {
      case NonFatal(e) => e.getMessage
    } get
  }
}