package org.specs2
package matcher

import execute._
import org.specs2.concurrent._
import FuturezAttempt._
import scala.concurrent.duration._
import scalaz.concurrent.{Future}

/**
 * This trait is for transforming matchers of values to matchers of scalaz.concurrent.Future
 */
trait FuturezMatchers extends FuturezBaseMatchers { outer =>
  /**
   * add an `attempt` method to any matcher `Matcher[T]` so that it can be transformed into a `Matcher[scalaz.concurrent.Future[T]]`
   */
  implicit class FuturezMatchable[T](m: Matcher[T])(implicit ee: ExecutionEnv) {
    def attempt: Matcher[Future[T]]                                        = attemptMatcher(m)(retries = 0, timeout = 1.second)(ee)
    def attempt(retries: Int, timeout: FiniteDuration): Matcher[Future[T]] = attemptMatcher(m)(retries, timeout)(ee)
    def retryAttempt(retries: Int): Matcher[Future[T]]                     = attemptMatcher(m)(retries, timeout = 1.second)(ee)
    def attemptFor(timeout: FiniteDuration): Matcher[Future[T]]            = attemptMatcher(m)(retries = 0, timeout)(ee)
  }

  /**
   * when a Future contains a result, it can be attempted to return this result
   */
  implicit class futureAsResult[T](f: Future[T])(implicit ee: ExecutionEnv, asResult: AsResult[T]) extends FuturezAsResult[T](f)
}

private[specs2]
trait FuturezBaseMatchers extends ExpectationsCreation {

  def attempt[T](m: Matcher[T])(implicit ee: ExecutionEnv): Matcher[Future[T]] = attemptMatcher(m)(retries = 0, timeout = 1.second)
  def attempt[T](m: Matcher[T])(retries: Int, timeout: FiniteDuration)(implicit ee: ExecutionEnv): Matcher[Future[T]] = attemptMatcher(m)(retries, timeout)
  def attempt[T](m: Matcher[T])(retries: Int)(implicit ee: ExecutionEnv): Matcher[Future[T]] = attemptMatcher(m)(retries, timeout = 1.second)
  def attempt[T](m: Matcher[T])(timeout: FiniteDuration)(implicit ee: ExecutionEnv): Matcher[Future[T]] = attemptMatcher(m)(0, timeout)

  private[specs2]
  class FuturezAsResult[T](f: Future[T])(implicit ee: ExecutionEnv, asResult: AsResult[T]) {
    def attempt: Result =
      attempt(retries = 0, timeout = 1.second)

    def attemptFor(timeout: FiniteDuration): Result =
      attempt(retries = 0, timeout)

    def attempt(retries: Int, timeout: FiniteDuration): Result =
      AttemptFuture(f).attempt(retries, timeout).fold(
        timedout => Failure(s"Timeout after ${timedout.totalDuration + timedout.appliedTimeout} (retries = $retries, timeout = $timeout, timeFactor = ${timedout.timeFactor})"),
        t => asResult.asResult(t)
      )

  }

  private[specs2] def attemptMatcher[T](m: Matcher[T])(retries: Int, timeout: FiniteDuration)(implicit ee: ExecutionEnv): Matcher[Future[T]] = new Matcher[Future[T]] {
    def apply[S <: Future[T]](a: Expectable[S]) = {
      try {
        val r = new FuturezAsResult(a.value.map(v => createExpectable(v).applyMatcher(m).toResult)).attempt(retries, timeout)
        result(r.isSuccess, r.message, r.message, a)
      } catch {
        // if attempting on the future throws an exception because it was a failed future
        // there try to match again because the matcher can be a `throwA` matcher
        case t: Throwable =>
          val r = createExpectable(throw t).applyMatcher(m).toResult
          result(r.isSuccess, r.message, r.message, a)
      }
    }
  }
}

object FuturezMatchers extends FuturezMatchers
