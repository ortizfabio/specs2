package org.specs2
package guide
package matchers

object OutsideSpecs2 extends UserGuidePage { def is = s2"""

The $specs2 matchers are a well-delimited piece of functionality that you should be able to reuse in your own test framework. You can reuse the following traits:

 * `${fullName[matcher.MustMatchers]}` (or `${fullName[matcher.ShouldMatchers]}`) to write anything like `1 must be_==(1)` and
   get a `Result` back

 * **Important**: the `MustMatchers` *trait* will fill-in stacktraces on `MatchResults` to mark the location of a result while the `MustMatchers` object will not. This has some important consequences in terms of performances because creating stack traces is expensive

 * You can also use the side-effecting version of that trait called `${fullName[matcher.MustThrownMatchers]}` (or `${fullName[matcher.ShouldThrownMatchers]}`).
   It throws a `FailureException` as soon as an expectation is failing

 * Finally, in a JUnit-like library you can use the `org.specs2.matcher.JUnitMustMatchers` trait which throws `AssertionFailureError`s

#### Without any dependency on specs2

It is possible to add testing features to your library without depending on a specific testing library, like $specs2 or ScalaTest. You will let clients of your library decide which one they want with the following trait:${snippet{
trait TestInterface {
  def fail(msg: String): Nothing
  def skip(msg: String): Nothing
}
// and use the trait in your library
trait TestKit extends TestInterface {
  def runTest(call: =>Unit) = {
    // run the code and if there is an error
    fail("error!")
  }
}
}}

When there is a failure or an error the library will call the `TestKit` methods. Then the library client can use both the library and $specs2 by mixing in the `${fullName[matcher.ThrownMessages]}` trait:
```
trait ThrownMessages { this: ThrownExpectations =>
  def fail(m: String): Nothing = failure(m)
  def skip(m: String): Nothing = skipped(m)
}

class MySpec extends Specification with TestKit with ThrownMessages { def is = s2$triple
  An example using the TestKit $$e1
$triple
  def e1 = {
    // do something with the library
    runTest(...)
  }
}
```
"""

}
