package org.specs2
package text

import specification._
import matcher._

class InterpolatedSpec extends Specification with ParserMatchers with Snippets { def is = s2"""

 It is possible to retrieve, from an interpolated string, the expressions code,
 given the intermediate pieces of text

 Expressions can be collected
  normal case                                                    $e1
  when there is an starting empty text                           $e2
  when there is an ending empty text                             $e3
  when there are several variables separated by spaces           $e4

 Interpolated expressions can be parsed
  when just using an identifier                                  $e5
  when using accolades                                           $e6
  when using nested accolades                                    $e7
  when using a quoted identifier                                 $e8
  when there is non-empty text surrounding all expressions       $e9
  with a snippet                                                 $e10
  with a $$                                                      $e11
                                                                 """

  def e1 = {
    val string =
      s"""|This is an interpolated string with one variable $$e1
          |and another variable $${ new BigTrait {} }
          |Then some other text""".stripMargin

    new Interpolated(string,
      Seq("This is an interpolated string with one variable ",
          "\nand another variable ",
          "\nThen some other text")).expressions === Seq("e1", " new BigTrait {} ")
  }

  def e2 = {
    val string =
      s"""|$$e1
          |middle $$e2
          |end""".stripMargin

    new Interpolated(string,
      Seq("",
          "\nmiddle ",
          "\nend")).expressions === Seq("e1", "e2")
  }

  def e3 = {
    val string =
      s"""|start $$e1
          |middle $$e2""".stripMargin

    new Interpolated(string,
      Seq("start ",
          "\nmiddle ",
          "")).expressions === Seq("e1", "e2")
  }

  def e4 = {
    val string =
      s"""|start $$e1 $$e2 """.stripMargin

    new Interpolated(string,
      Seq("start ", " ", " ")).expressions === Seq("e1", "e2")
  }

  lazy val parsers = InterpolatedParsers

  def e5 = parsers.interpolatedVariable(s"$$hello") must beASuccess
  def e6 = parsers.interpolatedVariable(s"$${hello}") must beASuccess
  def e7 = parsers.interpolatedVariable(s"$${ exp { other } }") must beASuccess.withResult("\\Q exp { other } \\E")
  def e8 = parsers.interpolatedVariable(s"$${`hello world`}") must beASuccess.withResult("`hello world`")
  def e9 = parsers.interpolatedVariable(s"$${ exp $${ o1 } $${ o2 } end}") must beASuccess.withResult(s"\\Q exp $${ o1 } $${ o2 } end\\E")
  def e10 = {
    val snippet =
      s"""$${
block  }""".stripMargin

    parsers.interpolatedVariable(snippet) must beASuccess
  }

  def e11 = parsers.interpolatedVariable("$$") must beASuccess

}
