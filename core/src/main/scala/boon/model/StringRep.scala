package boon
package model

import scala.util.Try
import boon.data.NonEmptySeq

trait StringRep[A] {
  def strRep(a: A): String

  trait StringRepLaws {

    /* Stability
     * strRep(a) == strRep(a), over many invocations
     */
    def stability(value: A, equality: Equality[String]): Boolean =
      equality.eql(strRep(value), strRep(value)) &&
      equality.eql(strRep(value), strRep(value)) &&
      equality.eql(strRep(value), strRep(value))

    /* Equal values should have the same String representation
     * x == y, then strRep(x) == strRep(y)
     */
    def equalValuesHaveSameStringRep(value1: A, value2: A, equalityA: Equality[A], equalityStr: Equality[String]): Boolean =
      !equalityA.eql(value1, value2) || equalityStr.eql(strRep(value1), strRep(value2))

    /* Equal Strings should have equal values
     * strRep(x) == strRep(y) then x == y
     */
    def sameStringRepHasEqualValues(value1: A, value2: A, equalityA: Equality[A], equalityStr: Equality[String]): Boolean =
      !equalityStr.eql(strRep(value1), strRep(value2)) || equalityA.eql(value1, value2)
  }

  val stringReplaws = new StringRepLaws {}
}

object StringRep {

  def apply[A: StringRep]: StringRep[A] = implicitly[StringRep[A]]

  def from[A](str: A => String): StringRep[A] = new StringRep[A] {
    override def strRep(value: A): String = str(value)
  }

  def genericStringRep[A]: StringRep[A] = from[A](_.toString)

  implicit val intStringRep: StringRep[Int]     = genericStringRep[Int]
  implicit val longStringRep: StringRep[Long]    = genericStringRep[Long]
  implicit val booleanStringRep: StringRep[Boolean] = genericStringRep[Boolean]
  implicit val floatStringRep: StringRep[Float]   = genericStringRep[Float]
  implicit val doubleStringRep: StringRep[Double]  = genericStringRep[Double]

  implicit val stringStringRep: StringRep[String] = from[String](str => s""""$str"""")

  implicit val charStringRep: StringRep[Char] = from[Char](c => s"'$c'")

  private def colStringRep[A: StringRep, F[_]](toIt: F[A] => Iterable[A])(prefix: String, open: String, close: String): StringRep[F[A]] =
    from[F[A]](fa => toIt(fa).map(StringRep[A].strRep).mkString(s"${prefix}${open}", ", ", s"${close}"))

  implicit def arrayStringRep[A](implicit S: StringRep[A]): StringRep[Array[A]] =
    colStringRep[A, Array](_.toSeq)("Array", "[", "]")

  implicit def listStringRep[A: StringRep]: StringRep[List[A]] = colStringRep[A, List](_.toSeq)("List", "(", ")")

  implicit def vectorStringRep[A: StringRep]: StringRep[Vector[A]] = colStringRep[A, Vector](_.toSeq)("Vector", "(", ")")

  implicit def setStringRep[A: StringRep]: StringRep[Set[A]] = colStringRep[A, Set](_.toSeq)("Set", "(", ")")

  implicit def seqStringRep[A: StringRep]: StringRep[Seq[A]] = colStringRep[A, Seq](_.toSeq)("Seq", "(", ")")

  implicit def nonEmptySeqStringRep[A: StringRep]: StringRep[NonEmptySeq[A]] = from[NonEmptySeq[A]](_.map(StringRep[A].strRep).mkString("NES(", ",", ")"))

  implicit def eitherStringRep[A: StringRep, B: StringRep]: StringRep[Either[A,B]] =
    from[Either[A, B]](_.fold(l => s"Left(${StringRep[A].strRep(l)})", r => s"Right(${StringRep[B].strRep(r)})"))

  implicit def throwableStringRep: StringRep[Throwable] = from[Throwable](t => s"${t.getClass.getName}(${t.getMessage})")

  implicit def tryStringRep[A: StringRep]: StringRep[Try[A]] =
    from[Try[A]](_.fold(t => s"Failure(${StringRep[Throwable].strRep(t)})", success => s"Success(${StringRep[A].strRep(success)})"))

  implicit def optionStringRep[A: StringRep]: StringRep[Option[A]] = from[Option[A]](_.fold("None")(v => s"Some(${StringRep[A].strRep(v)})"))

  implicit def pairStringRep[A: StringRep, B: StringRep]: StringRep[(A, B)] =
    from[(A, B)](pair => s"(${StringRep[A].strRep(pair._1)}, ${StringRep[B].strRep(pair._2)})")

  implicit def tripleStringRep[A: StringRep, B: StringRep, C: StringRep]: StringRep[(A, B, C)] =
    from[(A, B, C)](triple => s"(${StringRep[A].strRep(triple._1)}, ${StringRep[B].strRep(triple._2)}, ${StringRep[C].strRep(triple._3)})")

  implicit def tuple4StringRep[A: StringRep, B: StringRep, C: StringRep, D: StringRep]: StringRep[(A, B, C, D)] =
    from[(A, B, C, D)]{ tuple =>
      "(" +
        s"${StringRep[A].strRep(tuple._1)}, "  +
        s"${StringRep[B].strRep(tuple._2)}, "  +
        s"${StringRep[C].strRep(tuple._3)}, "  +
        s"${StringRep[D].strRep(tuple._4)}"    +
      ")"
    }

  implicit def mapStringRep[A: StringRep, B: StringRep]: StringRep[Map[A,B]] =
    from[Map[A, B]](_.map { case (k, v) =>  s"${StringRep[A].strRep(k)} -> ${StringRep[B].strRep(v)}" }.mkString("Map(", ",", ")"))
}