package org.specs2
package form

import control.Property
import execute._
import DecoratedProperties._
import text.NotNullStrings._
import StandardResults._

/**
 * A Field is a property which is used only to display input values or output values.
 * 
 * The apply method can be used to retrieve the Field value:
 *   `Field(label, 1).apply() must_== 1`
 * 
 * The value is stored in a Property object so it will not be evaluated until explicitly queried
 *
 */
case class Field[T](label: String, value: Property[T], decorator: Decorator = Decorator().bkGreyLabel) extends Executable with DecoratedProperty[Field[T]] {
  /** executing a field execute the value and returns success unless there is an Error */
  override def execute = {
    valueOrResult match {
      case Left(e)  => e
      case Right(v) => skipped
    }
  }

  lazy val valueOrResult: Either[Result, T] = ResultExecution.executeProperty(value)

  /** set a new value on the field. */
  def apply(v: =>T) = new Field(label, value(v), decorator)

  /** @return the field value as an Option */
  def toOption = value.toOption
  /** @return the field value as an Option */
  def optionalValue = value.optionalValue

  override def toString = {
    val valueString = valueOrResult match {
      case Left(Success(_,_)) => "_"
      case Left(result)       => result.toString
      case Right(v)           => v.notNull
    }
    (if (label.nonEmpty) label + ": " else "") + valueString
  }
  /** transforms this typed Field as a Field containing the toString value of the Fields value*/
  def toStringField = new Field(label, value.map(_.toString), decorator)
  /** set a new Decorator */
  def decoratorIs(d: Decorator) = copy(decorator = d)

  /** use this Field as a header in a table */
  def header = this.center.bold.bkGrey

  override def equals(a: Any) = a match {
    case Field(l, v, _) => label == l && value == v
    case other          => false
  }
  override def hashCode = label.hashCode + value.hashCode
}
/**
 * Factory methods for creating Fields. Fields values can also be concatenated to produce
 * "summary" fields.
 * 
 * val f1 = Field(label, "hello")
 * val f2 = Field(label, "world")
 * val concatenatedFields = Field(label, f1, f2)
 * concatenatedFields.toString == label: hello/world
 * 
 * val concatenatedFields2 = Field(label, ", ", f1, f2)
 * concatenatedFields2.toString == label: hello, world
 */
case object Field {
  /** create a Field with no label */
  def apply[T](value: =>T): Field[T] = new Field("", Property(value))

  /** create a Field with a label and a value */
  def apply[T](label: String, value: =>T): Field[T] = new Field(label, Property(value))

  /** create a Field with a label and other fields values, concatenated as strings */
  def apply(label: String, value1: Field[_], values: Field[_]*): Field[String] = Field(label, "/", value1, values:_*)

  /** create a Field with a label and other fields values, concatenated as strings */
  def apply(label: String, separator: String, value1: Field[_], values: Field[_]*): Field[String] =
    Field(label, if (values.isEmpty) value1.toString else (value1 :: values.toList).map(_.value).mkString(separator))
}

