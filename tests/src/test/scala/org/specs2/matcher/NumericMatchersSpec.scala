package org.specs2
package matcher

class NumericMatchersSpec extends Spec with NumericMatchers with MustExpectations with MatchersImplicits {  def is = s2"""
                                                                                                                        
The NumericMatchers trait provides matchers to do comparisons with Numeric
types and more generally with Ordered types.
                                                                                                                        
  beLessThanOrEqualTo compares any Ordered type with <=                                                               
  ${ 1 must be_<=(2) }
  ${ 2 must be <=(2) }
  ${ 1 must beLessThanOrEqualTo(2) }
  ${ 2 must not beLessThanOrEqualTo(1) }
  ${ 2 must not be <=(1) }
  ${ 2 must not be_<=(1) }
  ${ 1 must be lessThanOrEqualTo(2) }
  and return a failure if the comparison fails                                                                    $e1
  and return a failure if the comparison fails - with aka                                                         $e1_1

  beLessThan compares any Ordered type with <
  ${ 1 must be_<(2) }
  ${ 1 must be <(2) }
  ${ 2 must not be <(1) }
  ${ 2 must not be_<(1) }
  ${ 2 must not beLessThan(1) }
  ${ 1 must beLessThan(2) }
  ${ 1 must be lessThan(2) }
  and return a failure if the comparison fails                                                                     $e2
  and return a failure if the comparison fails - with aka                                                          $e2_1

  beGreaterThanOrEqualTo compares any Ordered type with >=
  ${ 2 must be_>=(1) }
  ${ 2 must be >=(1) }
  ${ 2 must not be_>=(3) }
  ${ 2 must not be >=(3) }
  ${ 2 must not beGreaterThanOrEqualTo(3) }
  ${ 2 must beGreaterThanOrEqualTo(1) }
  ${ 2 must be greaterThanOrEqualTo(1) }
   and return a failure if the comparison fails                                                                    $e3
   and return a failure if the comparison fails - with aka                                                         $e3_1

  beGreaterThan compares any Ordered type with >
  ${ 2 must be_>(1) }
  ${ 2 must be >(1) }
  ${ 2 must not be >(3) }
  ${ 2 must not be_>(3) }
  ${ 2 must not beGreaterThan(3) }
  ${ 2 must beGreaterThan(1) }
  ${ 2 must be greaterThan(1) }
  and return a failure if the comparison fails                                                                     $e4
  and return a failure if the comparison fails - with aka                                                          $e4_1

  the comparison matchers also work with doubles
  ${ 2.0 must be_>(1.0) }
  ${ 2.0 must be >=(1.0) }

  beCloseTo tests if 2 Numerics are close to each other
  ${ 1.0 must beCloseTo(1, 0.5) }
  ${ 4 must be ~(5 +/- 2) }
  ${ 2 must not be closeTo(4 +/- 1) }
  ${ 2 must not beCloseTo(4 +/- 1) }
  and return a failure if the comparison fails                                                                     $e5
  and return a failure if the comparison fails - with aka                                                          $e5_1

  beCloseTo tests if 2 Numerics are close to each other, within some order of magnitude
  ${ 1001.1232455 must beCloseTo(1003.12, 2.significantFigures) }
  ${ 5.1 must beCloseTo(5.0 within 1.significantFigure) }
  ${ 4.994 must beCloseTo(5.0 within 2.significantFigures) }
  ${ 4.994 must not beCloseTo(5.0 within 3.significantFigures) }
  ${ 4.995 must beCloseTo(5.0 within 3.significantFigures) }
  ${ 4.995 must not beCloseTo(4.99 within 3.significantFigures) }
  ${ 0.00123 must not beCloseTo(0.00124 within 3.significantFigures) }
  ${ 0.00123 must beCloseTo(0.00124 within 2.significantFigures) }
  ${ 900 must not be closeTo(1000 within 2.significantFigures) }
  ${ 900 must be closeTo(1000 within 1.significantFigures) }
  ${ 0 must be closeTo(0 within 1.significantFigure) }
  ${ 0.0 must not be closeTo(0.1 within 1.significantFigure) }

  beBetween tests if one value is between 2 other values                                                              
  ${ 5 must beBetween(3, 6) }
  ${ 5 must beBetween(3, 5) }
  ${ 5 must beBetween(3, 6).excludingEnd }
  ${ 5 must beBetween(4, 6).excludingStart }
  ${ 5 must beBetween(4, 6).excludingBounds }
  ${ 5 must not be between(2, 3) }
  ${ 5 must not be between(7, 9) }
  ${ 5 must not be between(3, 5).excludingEnd }
  ${ 5 must not be between(5, 7).excludingStart }
  ${ 5 must not be between(5, 5).excludingBounds }
  ${ 5 must (`be[`(4, 7)`]`) }
                                                                                                                        """

  def e1   = (2 must be_<=(1)) returns "2 is greater than 1"
  def e1_1 = (2 aka "two" must be_<=(1)) returns "two '2' is greater than 1"

  def e2   = (2 must be_<(1))  returns "2 is not less than 1"
  def e2_1 = (2 aka "two" must be_<(1))  returns "two '2' is not less than 1"

  def e3   = (1 must be_>=(2)) returns "1 is less than 2"
  def e3_1 = (1 aka "one" must be_>=(2)) returns "one '1' is less than 2"

  def e4   = (1 must be_>(2))  returns "1 is less than 2"
  def e4_1 = (1 aka "one" must be_>(2))  returns "one '1' is less than 2"

  def e5   = (1.0 must beCloseTo(3, 0.5)) returns "1.0 is not close to 3.0 +/- 0.5"
  def e5_1 = (1.0 aka "one" must beCloseTo(3, 0.5)) returns "one '1.0' is not close to 3.0 +/- 0.5"
}
