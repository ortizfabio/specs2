package org.specs2
package text

import java.io.StringWriter
import java.util.regex.Pattern
import util.matching.Regex
import util.matching.Regex.Match

/**
 * Utility methods for trimming text
 */
private[specs2]
trait Trim {

  /** add trimming methods to a String */
  implicit def trimmed(s: String): Trimmed = new Trimmed(s)
  /** utility conversion for StringBuffers */
  implicit def stringBufferToString(sb: java.lang.StringBuffer): Trimmed = Trimmed(sb.toString)
  /** utility conversion for StringBuffers */
  implicit def stringWriterToString(sb: StringWriter): Trimmed = Trimmed(sb.toString)

  case class Trimmed(s: String) {

    def trimStart(start: String) =
      if (s.trim.startsWith(start)) s.trim.drop(start.size) else s.trim

    def trimEnd(end: String) =
      if (s.trim.endsWith(end)) s.trim.dropRight(end.size)  else s.trim

    def trimEndSpace =
      s.takeWhile(_ == ' ') + s.trim

    def trimEnclosing(start: String): String = trimEnclosing(start, start)

    def trimEnclosing(start: String, end: String): String = if (s.trim.startsWith(start) && s.trim.endsWith(end)) {
      trimStart(start).trimEnd(end).trim
    } else s

    def trimEnclosingXmlTag(t: String) = trimFirst("<"+t+".*?>").trimEnd("</"+t+">")

    def removeStart(start: String) =
      if (s.startsWith(start)) s.drop(start.size) else s

    def removeEnd(end: String) =
      if (s.endsWith(end)) s.dropRight(end.size)  else s

    def removeEnclosing(toRemove: String):String = removeEnclosing(toRemove, toRemove)

    def removeEnclosing(start: String, end: String):String =
      if (isEnclosing(start, end)) removeStart(start).removeEnd(end)
      else                                 s

    def removeEnclosingXmlTag(t: String) =
      if (isEnclosing("<"+t, "</"+t+">")) removeFirst("<"+t+".*?>").trimEnd("</"+t+">")
      else                                s

    def isEnclosing(start: String, end: String) = s.startsWith(start) && s.endsWith(end)

    def trimNewLines = Seq("\r", "\n").foldLeft(s) { (res, cur) =>
      res.trimStart(cur).trimEnd(cur)
    }

    def removeNewLines = Seq("\r", "\n").foldLeft(s) { (res, cur) =>
      res.replaceAll(cur, "")
    }

    def trimFirst(exp: String) = new Regex(exp).replaceFirstIn(s.trim, "")

    def removeFirst(exp: String) = new Regex(exp).replaceFirstIn(s, "")

    def removeLast(exp: String) = {
      val matches = exp.r.findAllIn(s).matchData.toSeq
      if (matches.isEmpty) s
      else {
        val last = matches.last
        s.substring(0, last.start) + s.substring(last.end, s.size)
      }
    }

    /** trim the string of everything that is before the start substring if there is one */
    def startFrom(start: String) = if (s.startsWith(start) || !s.contains(start)) s else new String(s.substring(s.indexOf(start)))

    def trimReplace(pairs: (String, String)*) = pairs.foldLeft(s.trim) { (res, cur) =>
      res.replace(cur._1, cur._2)
    }

    def trimReplaceAll(pairs: (String, String)*) = pairs.foldLeft(s.trim) { (res, cur) =>
      res.replaceAll(cur._1, cur._2)
    }

    def trimStart = s.dropWhile(Seq(' ', '\n').contains)

    def trimEnd = s.reverse.dropWhile(Seq(' ', '\n').contains).reverse

    def trimSpaceStart = s.dropWhile(Seq(' ').contains)

    def trimSpaceEnd = s.reverse.dropWhile(Seq(' ').contains).reverse

    def replaceAll(pairs: (String, String)*) = pairs.foldLeft(s) { (res, cur) =>
      res.replaceAll(cur._1, cur._2)
    }

    def replaceInsideTag(tag: String, p: (String, String)*) = {
      replaceAll(tagPattern(tag), (s: String) => java.util.regex.Matcher.quoteReplacement(s.replaceAll(p:_*)))
    }

    def replaceInsideTags(tags: String*)(p: (String, String)*) = {
      tags.foldLeft(s) { (res, tag) =>
        res.replaceAll(tagPattern(tag), (s: String) => java.util.regex.Matcher.quoteReplacement(s.replaceAll(p:_*)))
      }
    }

    private def tagPattern(tag: String) = "<"+tag+">(.(.|\n)*?)</"+tag+">"

    /** replace each group with something else */
    def replaceAll(exp: String, f: String => String) = {
      new Regex(exp).replaceAllIn(s, (m: Match) => f(m.group(0).replace("\\", "\\\\")))
    }

    /** @return a sequence of lines by splitting on newlines */
    def lines: Seq[String] = s.removeAll("\r").split("\n")
    /** remove empty lines in a block of lines */
    def removeEmptyLines: String = nonEmptyLines.mkString("\n")
    /** @return split on newlines and remove empty ones */
    def nonEmptyLines: Seq[String] = Trimmed(s).lines.filterNot(_.isTrimEmpty)
    /** @return only the last block of lines when there's separated by a newline */
    def lastBlock = s.split("\n").reverse.dropWhile(_.isTrimEmpty).span(!_.isTrimEmpty)._1.reverse.mkString("\n")
    /** @return true if empty after trimming */
    def isTrimEmpty = s.trim.isEmpty

    def remove(toRemove: String*) = toRemove.foldLeft(s) { (res, cur) => res.replace(cur, "") }
    def removeAll(remove: String) = s.replaceAll(Pattern.quote(remove), "")

    /** split and trim each, removing empty strings */
    def splitTrim(separator: String): Seq[String] = s.split(separator).collect { case t if !t.trim.isEmpty => t.trim}.toSeq

    /** @return the string or empty if the condition is true */
    def unless(condition: Boolean) = if (condition) "" else s

    /** truncate a string to a given number of characters and ellide the missing characters with ... */
    def truncate(length: Int): String =
      if (s.size > length) s.take(length - 3)+"..."
      else s
  }

  implicit class offSettable(s: String) {
    def offset(n: Int) =
      if (n == 0) s
      else        s.split("\n", -1).map(l => offsetLine(l, n)).mkString("\n")

    private def offsetLine(l: String, n: Int) =
      if (n > 0 ) " " * n + l
      else        l.takeWhile(_ == ' ').drop(-n).mkString + l.dropWhile(_ == ' ').mkString
  }
}

private[specs2]
object Trim extends Trim
