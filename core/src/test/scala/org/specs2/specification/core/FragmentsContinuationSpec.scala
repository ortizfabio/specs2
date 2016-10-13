package org.specs2
package specification
package core

import process.DefaultExecutor
import execute._

class FragmentsContinuationSpec(env: Env) extends Specification { def is = s2"""

 A fragment continuation must
   return other fragments if the previous result is a success                $continuationAfterSuccess
   not return other fragments if the previous result is not a success        $continuationAfterFailure
   return an error fragment if the continuation fragments fail to be created $continuationError


"""

  def continuationAfterSuccess =
    runContinuation(ok, ko) must haveSize(2)

  def continuationAfterFailure =
    runContinuation(ko, ko) must haveSize(1)

  def continuationError = {
    val fs = runContinuation(ok, {sys.error("boom"); ko})
    (fs must haveSize(2)) and
    (fs(1).description.show must beMatching("Could not create fragments after the previous successful result"))
  }

  def runContinuation[R : AsResult](r: =>R, fs: =>Fragments): IndexedSeq[Fragment] =
    Fragments(DefaultExecutor.execute1(env)(Fragments("test" ! FragmentsContinuation.continueWith(r, fs)).contents)).fragments
}
