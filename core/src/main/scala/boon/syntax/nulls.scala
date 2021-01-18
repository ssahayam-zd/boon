package boon
package syntax

import boon.model.StringRep
import boon.model.Null
import boon.model.AssertionData

object nulls {
  def null_![A: StringRep](value: => A)(f : A => AssertionData)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(invalid(errorTemplate(plain("not null"), Null)).| (
      "expected not null", input(Null)))(v => f(v).context(inputM(v)))

  def isNotNull[A: StringRep](value: => A)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(
      invalid(errorTemplate(plain("not null"), Null)).| ("is not null", input(Null)))(_ =>
        pass.| ("is not null", input(value)))

  def null_?[A: StringRep](value: => A)(f : => AssertionData)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(f.context(inputM(Null)))(v =>
      invalid(errorTemplate(plain("null"), v)).| ("expected null", input(v)))

  def isNull[A: StringRep](value: => A)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(
      pass.| ("is null", input(Null)))(
      _ => invalid(errorTemplate(plain("null"), value)).| ("is null", input(value)))

  private def fold[A, B](value: => A)(n: => B)(s: A => B): B = Option(value).fold(n)(s)
}