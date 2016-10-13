package org.specs2
package html

import TableOfContents._
import io._
import specification._
import core.SpecStructure
import scala.xml.NodeSeq
import matcher.XmlMatchers

class TableOfContentsSpec extends script.Specification with HtmlDocuments with Grouped with XmlMatchers { def is = s2"""

 The table of contents is created from the specifications and the generated html files


 Creating a table of content for a html document
    creates an unordered list from the html headers                                             
      + as nested <li/> lists corresponding to the hierarchy of the document headers
      each <li/> element has
        + the header text as text
        + an url+anchor referencing the header name
        + an id attribute with the spec id. the id attribute is expected by jstree
                                                                                                  """

  "toc" - new g1 {
    e1 := addToc(aBodyWithHeaders) must \\("li") \\ ("ul") \ ("li")

    //    <li><a href="http://specs2.org/#title_123456">title</a>
    //      <ul><li><a href="http://specs2.org/#a+header_123456">a header</a></li>
    //      </ul>
    //    </li>
    e2 := addToc(aBodyWithHeaders) must \\ ("li") \ ("a") \> "Table of conten..."
    e3 := addToc(aBodyWithHeaders) must \\ ("li") \ ("a", "href" -> "UserGuide.html")
    e4 := addToc(aBodyWithHeaders) must \\ ("li", "id")

  }

  def addToc(body: NodeSeq) = {
    val page = SpecHtmlPage(SpecStructure.empty(getClass), outDir | "UserGuide.html", outDir, body.toString)
    createToc(List(page), outDir, entryMaxSize = 18)(page)
  }

  val outDir = DirectoryPath.unsafe("guide")
}