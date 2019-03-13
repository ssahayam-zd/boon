package boon
package syntax

import boon.model.Assertion
import boon.model.Defer
import boon.model.Equality
import boon.model.EqualityType
import boon.model.IsEqual
import boon.model.IsNotEqual
import boon.model.StringRep
import boon.model.Difference
import boon.model.TestData
import boon.model.Sequential

import Boon.defineAssertion
import Boon.defineAssertionWithContext

import scala.util.Try
import scala.reflect.ClassTag

/*
 * Operator Precedence: https://docs.scala-lang.org/tour/operators.html
 *
 * (characters not shown below)
 * * / %
 * + -
 * :
 * = !
 * < >
 * &
 * ^
 * |
 * (all letters)
 */

final class EqSyntax[A](value1: => A) {
  def =?=(value2: => A): DescSyntax[A] = new DescSyntax[A]((defer(value1), defer(value2)), IsEqual)

  def =/=(value2: => A): DescSyntax[A] = new DescSyntax[A]((defer(value1), defer(value2)), IsNotEqual)

  def =!=[T <: Throwable](assertMessage: String => ContinueSyntax)(
    implicit classTag: ClassTag[T], SR: StringRep[A]): ContinueSyntax = {
    val expectedClass = classTag.runtimeClass
    val expectedClassName = expectedClass.getName
    Try(value1).fold[ContinueSyntax](
      e => expectedClass.isAssignableFrom(e.getClass) |# (s"exception class ${expectedClassName}",
                                                           "expected class" -> expectedClassName,
                                                           "got class"      -> e.getClass.getName) and
           assertMessage(e.getMessage),
      s => fail(s"expected ${expectedClassName} but got class:${s.getClass.getName} value:${SR.strRep(s)}") | s"exception class ${expectedClassName}"
    )
  }
}

final class DescSyntax[A](pair: (Defer[A], Defer[A]), equalityType: EqualityType) {
  def |(name: => String)(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): ContinueSyntax =
    new ContinueSyntax(NonEmptySeq.nes(defineAssertion[A](name, (pair), equalityType)))

  def |#(name: => String, ctx: (String, String)*)(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): ContinueSyntax =
    new ContinueSyntax(NonEmptySeq.nes(defineAssertionWithContext[A](name, (pair), equalityType, Map(ctx:_*))))
}

final case class ContinueSyntax(assertions: NonEmptySeq[Assertion]) {
    def &(other: ContinueSyntax): ContinueSyntax = ContinueSyntax(assertions.concat(other.assertions))

    def and(other: ContinueSyntax): ContinueSyntax = ContinueSyntax(assertions.concat(other.assertions))

    def sequentially: TestData = TestData(assertions, Sequential)
}

