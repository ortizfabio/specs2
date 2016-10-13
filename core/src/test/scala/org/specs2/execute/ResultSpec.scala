package org.specs2
package execute

import matcher.{ResultMatchers, DataTables}
import matcher.MatchersImplicits._

class ResultSpec extends Spec with DataTables with ResultMatchers { def is = s2"""

Results are the outcome of some execution. There are several kinds of Results, all having a message describing them
more precisely:

 * Success: everything is ok
 * Failure: an expectation is not met
 * Error: something completely unexpected happened
 * Skipped: the user decided to skip the execution for some reason
 * Pending: the user decided that the execution was not yet implemented


 Results can be combined with and
 ${ (success1 and success2) must_== Success("s1 and s2") }
 ${ (success1 and success1) must_== Success("s1") }
 ${ (success1 and failure1) must_== failure1 }
 ${ (success1 and error1)   must_== error1 }
 ${ (success1 and skipped1) must_== success1 }
 ${ (skipped1 and success1) must_== success1 }
 ${ (failure1 and success1) must_== failure1 }
 ${ (thrownFailure1 and success1) must throwA[FailureException] }
 ${ (failure1 and failure2) must_== failure1 }
 ${ (failure1 and error1)   must_== failure1 }
 ${ (error1   and failure1) must_== error1 }
 ${ (error1   and failure1) must_== error1 }
 ${ (error1   and failure1) must_== error1 }
 ${ (error1   and failure1) must_== error1 }
   the expectationsNb must be ok
   ${ (success1 and success2).expectationsNb must_== 2 }
   ${ (success1 and failure1).expectationsNb must_== 2 }
   ${ (success1 and error1)  .expectationsNb must_== 2 }
   ${ (success1 and skipped1).expectationsNb must_== 2 }
   ${ (failure1 and success1).expectationsNb must_== 2 }
   ${ (failure1 and failure2).expectationsNb must_== 2 }
   ${ (failure1 and error1)  .expectationsNb must_== 2 }
   ${ (error1   and success1).expectationsNb must_== 2 }

   the expected message must be ok
   ${ (success1_1 and success2_1).expected must_== "exp1; exp2" }
   ${ (success1_1 and failure1_1).expected must_== "exp1; exp1" }
   ${ (success1_1 and error1)    .expected must_== "" }
   ${ (success1_1 and skipped1_1).expected must_== "exp1; exp1" }
   ${ (failure1_1 and success1_1).expected must_== "exp1" }
   ${ (failure1_1 and failure2_1).expected must_== "exp1" }
   ${ (failure1_1 and error1)    .expected must_== "exp1" }
   ${ (error1 and success1_1)    .expected must_== "" }

 Results can be combined with or
 ${ (success1 or success2) must_== Success("s1") }
 ${ (success1 or failure1) must_== success1 }
 ${ (success1 or skipped1) must_== success1 }
 ${ (skipped1 or success1) must_== success1 }
 ${ (failure1 or success1) must_== Success("f1 and s1") }
 ${ (success1 or failure1) must_== Success("s1") }
 ${ (failure1 or failure2) must_== Failure("f1 and f2") }
 ${ (failure1 or error1)   must_== failure1 }
 ${ (skipped1 or failure1) must_== failure1 }
   the expectationsNb must be ok
  ${ (success1 or success2).expectationsNb must_== 2 }
  ${ (success1 or failure1).expectationsNb must_== 2 }
  ${ (success1 or skipped1).expectationsNb must_== 2 }
  ${ (failure1 or success1).expectationsNb must_== 2 }
  ${ (success1 or failure1).expectationsNb must_== 2 }
  ${ (skipped1 or success1).expectationsNb must_== 1 }
  ${ (skipped1 or failure1).expectationsNb must_== 1 }
  ${ (failure1 or failure2).expectationsNb must_== 2 }
  ${ (failure1 or error1)  .expectationsNb must_== 2 }
 results have methods to know their status: isSuccess, isPending, ... $statuses

 The result monoid must only evaluate values once
  with the result monoid  $monoidAppendOnce
  with the failure monoid $failureMonoidAppendOnce

 A result message can be updated or mapped
 ${ success1.updateMessage("ok").message must_== "ok" }
 ${ success1.mapMessage(_.capitalize).message must_== "S1" }

 A result expected can be updated or mapped
 ${ success1.updateExpected("ok").expected must_== "ok" }
 ${ Success("s1", "s1").mapExpected(_.capitalize).expected must_== "S1" }

 Boolean values can also be combined as if they were results
 ${ (true: Result) }
 ${ true and true }
 ${ (true and false) must beFailing }

 A match result can be evaluated only when a boolean condition is satisfied
 ${ (1 must_== 2: Result).when(false) }

 A match result can be evaluated only unless a boolean condition is satisfied
 ${ (1 must_== 2: Result).unless(true) }

 A match result can be evaluated if and only if a boolean condition is satisfied
 ${ (1 must_== 2: Result).iff(false) }
 ${ (1 must_== 1: Result).iff(true) }

 the Result.unit method must re-throw FailureExceptions
 ${ Result.unit(Seq(1).foreach(i => throw new FailureException(failure))) must throwA[FailureException] }

"""

  def statuses =
    "result" | "isSuccess" | "isFailure" | "isError" | "isSkipped" | "isPending" |>
    success1 ! true        ! false       ! false     ! false       ! false       |
    failure1 ! false       ! true        ! false     ! false       ! false       |
    error1   ! false       ! false       ! true      ! false       ! false       |
    skipped1 ! false       ! false       ! false     ! true        ! false       |
    pending1 ! false       ! false       ! false     ! false       ! true        | { (r, s, f, e, sk, p) =>
      (r.isSuccess, r.isFailure, r.isError, r.isSkipped, r.isPending) must_== ((s, f, e, sk, p))
    }

  def monoidAppendOnce = {
    var count = 0
    def a: Result = { count += 1; Success() }
    def b: Result = { count += 1; Success() }
    Result.ResultMonoid.append(a, b)
    count must be_==(2)
  }

  def failureMonoidAppendOnce = {
    var count = 0
    def a: Result = { count += 1; Success() }
    def b: Result = { count += 1; Success() }
    Result.ResultFailureMonoid.append(a, b)
    count must be_==(2)
  }

  val success1: Result = Success("s1")
  val success2 = Success("s2")                                                                                          
  val failure1 = Failure("f1")
  def thrownFailure1: Result = { throw new FailureException(failure1); success }

  val failure2 = Failure("f2")
  val error1   = Error("e1")
  val skipped1 = Skipped("sk1")
  val pending1 = Pending("p1")

  val success1_1: Result = Success("s1", "exp1")
  val success2_1 = Success("s2", "exp2")
  val failure1_1 = Failure("f1", "exp1")
  val failure2_1 = Failure("f2", "exp2")
  val skipped1_1 = Skipped("sk1", "exp1")
}