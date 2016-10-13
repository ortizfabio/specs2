package org.specs2
package guide

object Execution extends UserGuidePage { def is = s2"""

### Parallel by default

$specs2 examples are executed concurrently by default:

 - this makes the execution faster

 - it encourages to write independent examples when the result of a given example should not be influenced by others

Starting from this default you can progressively add constraints to get more control over the execution.

### Steps

A `Step` is an action which can be executed anywhere in a specification. When you declare a `Step` like this:${snippet{
class StepSpec extends Specification { def is = s2"""
  this is example 1 $ok
  this is example 2 $ok
  ${step("stop here for a second")}

  this is example 3 $ok
  this is example 4 $ok
"""
}
}}

Then the specification will:

  1. execute examples 1 and 2 in parallel
  2. execute the step
  3. execute examples 3 and 4 in parallel

There is no "result" for a step but if it throws an Exception an `Error` will be reported. This will not however stop the execution of the rest of the specification.

### Stop the execution

You can still control if the rest of the specification must be executed by adding some constraints on the step. For example:${snippet{
class StepWithStopOnErrorSpec extends Specification { def is = s2"""
  this is example 1 $ok
  this is example 2 $ok
  ${step { sys.error("sorry!"); "stop here for a second" }.stopOnError}

  this is example 3 $ok
  this is example 4 $ok
"""
}
}}

When this specification is executed examples 3 and 4 will be skipped because the step returns an `Error`. An `Error` is likely to be a fatal condition but you can use other methods to stop the execution:

 - `stopOnFail` stop if there is a failure in the previous examples or in the step
 - `stopOnSkipped` stop if there is a skipped result in the previous examples or in the step
 - `stopWhen(Result => Boolean)` stop if the `and`-ed result of the previous examples and the step verifies a given condition

### Sequential

If your specification is a list of well-ordered examples you can use the `sequential` argument to make sure that they are executed in order:${snippet{
class SequentialSpec extends Specification { def is = sequential ^ s2"""
  this is example 1 $ok
  this is example 2 $ok
  this is example 3 $ok
  this is example 4 $ok
"""
}
}}

Thanks to the `sequential` argument the 4 examples above will execute one after the other.

### Action

Finally if you want to execute "silent" actions, like steps, but with no impact on the sequencing of the specification, you can use an `Action`:${snippet{
class ActionSpec extends Specification { def is = s2"""
  this is example 1 $ok
  this is example 2 $ok

  // this will only be reported if there is a failure
  ${action("do something here")}

  this is example 3 $ok
  this is example 4 $ok
"""
}
}}

$NowLearnTo

 - use ${"arguments and tags" ~/ Selection} to select the examples to execute
 - run each example in ${"its own instance of the specification" ~/ Isolation}

$vid

$AndIfYouWantToKnowMore

 - add ${"random sequencing" ~/ RandomExecution} to your specification
 - consult the ${"arguments reference guide" ~/ ArgumentsReference} for a list of all arguments

$vid
"""

}
