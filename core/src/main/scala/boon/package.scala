package object boon {

import boon.model._
import syntax.toStrRep

import scala.util.Try

  def createSuite(name: => String)(tests: NonEmptySeq[Test]): DeferredSuite =
    DeferredSuite(SuiteName(name), tests)

  def test(name: => String)(data: => TestData)(implicit testLocation: SourceLocation): Test =
    Try(data).fold(ex => {
      UnsuccessfulTest(ThrownTest(TestName(name), ex, testLocation))
    } , td => {
      SuccessfulTest(DeferredTest(TestName(name), td.assertions, td.combinator))
    })

  def table[T: StringRep, U: Equality : Difference: StringRep](name: => String, values: NonEmptyMap[T, (U, SourceLocation)])(f: T => U): Test = {
    SuccessfulTest(
      DeferredTest(
        TestName(name),
        //TODO: we may need to Try over these values
        values.map {
          case (t, (u, loc)) =>
            implicit val sl: SourceLocation = loc
            Boon.defineAssertion[U](s"with ${t.strRep} is ${u.strRep}", (Defer(() => f(t)), Defer(() => u)), IsEqual, noContext)
        },
        Independent
      )
    )
  }


  def xtest(name: => String)(data: => TestData): Test = {
    IgnoredTest(TestName(name))
  }

  def defer[A](value: => A): Defer[A] = Defer[A](() => value)

  def tval[U](value: U)(implicit SL: SourceLocation): (U, SourceLocation) = (value, SL)

  type NonEmptyMap[K, V] = NonEmptySeq[(K, V)]

  def noContext: Map[String, String] = Map.empty[String, String]

  def noErrorOverrides: Option[NonEmptySeq[String]] = None

  def oneOrMore[A](head: A, tail: A*): NonEmptySeq[A] = NonEmptySeq[A](head, tail.toSeq)

  def one[A](head: A): NonEmptySeq[A] = NonEmptySeq.nes[A](head)
}