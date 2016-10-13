package org.specs2
package matcher

import mutable.Specification
import execute._

class MatchersImplicitsSpec extends Specification with ResultMatchers {
  "A sequence of match results can be implictly transformed to a single result" >> {
    ((List.empty[MatchResult[Any]]): Result) must beSuccessful
    ResultExecution.execute(Seq(1 === 1, 2 === 3): Result) must beFailing
  }

  "A matcher can be built from a function returning a MatchResult" >> {
    val beZero: Matcher[Int] = (i: Int) => { i must_== 0 }
    1 must not(beZero)
  }

  "2 nested foralls must throw an Error if the inner one throws an Error" >> {
    def nested =
      forall(Seq(1)) { i =>
        forall(Seq(2)) { j =>
          sys.error("boom"); true
        }
      }
    nested must throwAn[ErrorException]("boom")
  }
}
