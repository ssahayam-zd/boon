package boon
package model

import NonEmptySeq.nes

//This may not need to be a typeclass.
trait Difference[A] {
  def diff(a1: A, a2: A): NonEmptySeq[String]
}

object Difference {

  def apply[A: Difference]: Difference[A] = implicitly[Difference[A]]

  def genericDifference[A](implicit rep: StringRep[A]): Difference[A] = new Difference[A] {
    override def diff(a1: A, a2: A): NonEmptySeq[String] = one(s"${rep.strRep(a1)} != ${rep.strRep(a2)}")
  }

  implicit object IntDifference extends Difference[Int] {
    override def diff(a1: Int, a2: Int): NonEmptySeq[String] = genericDifference[Int].diff(a1, a2)
  }

  implicit object LongDifference extends Difference[Long] {
    override def diff(a1: Long, a2: Long): NonEmptySeq[String] = genericDifference[Long].diff(a1, a2)
  }

  implicit object FloatDifference extends Difference[Float] {
    override def diff(a1: Float, a2: Float): NonEmptySeq[String] = genericDifference[Float].diff(a1, a2)
  }

  implicit object DoubleDifference extends Difference[Double] {
    override def diff(a1: Double, a2: Double): NonEmptySeq[String] = genericDifference[Double].diff(a1, a2)
  }

  implicit object BooleanDifference extends Difference[Boolean] {
    val rep = StringRep[Boolean]
    override def diff(a1: Boolean, a2: Boolean): NonEmptySeq[String] = one(s"${rep.strRep(a1)} is not ${rep.strRep(a2)}")
  }

  implicit object StringDifference extends Difference[String] {
    val rep = StringRep[String]
    override def diff(a1: String, a2: String): NonEmptySeq[String] = one(s"${rep.strRep(a1)} != ${rep.strRep(a2)}")
  }

  implicit object CharDifference extends Difference[Char] {
    val rep = StringRep[Char]
    override def diff(a1: Char, a2: Char): NonEmptySeq[String] = one(s"${rep.strRep(a1)} != ${rep.strRep(a2)}")
  }

  implicit def listDifference[A: StringRep]: Difference[List[A]] = new Difference[List[A]] {
    val rep = StringRep[List[A]]
    override def diff(xs: List[A], ys: List[A]): NonEmptySeq[String] = {
      val summary = s"${rep.strRep(xs)} != ${rep.strRep(ys)}"
      val both    = xs.filter(ys.contains(_)).mkString(",")
      val left    = xs.filter(!ys.contains(_)).mkString(",")
      val right   = ys.filter(!xs.contains(_)).mkString(",")

      nes(
        s"${summary}",
        s"both: [${both}]",
        s"only on left: [${left}]",
        s"only on right: [${right}]"
      )
    }
  }

  implicit def nonEmptySeqDifference[A: StringRep]: Difference[NonEmptySeq[A]] = new Difference[NonEmptySeq[A]] {
    val rep = StringRep[NonEmptySeq[A]]
    override def diff(xs: NonEmptySeq[A], ys: NonEmptySeq[A]): NonEmptySeq[String] = {
      val summary = s"${rep.strRep(xs)} != ${rep.strRep(ys)}"
      val both    = xs.filter(ys.contains(_)).mkString(",")
      val left    = xs.filter(!ys.contains(_)).mkString(",")
      val right   = ys.filter(!xs.contains(_)).mkString(",")

      nes(
        s"${summary}",
        s"both: [${both}]",
        s"only on left: [${left}]",
        s"only on right: [${right}]"
      )
    }
  }

  implicit def optionDifference[A: StringRep]: Difference[Option[A]] = new Difference[Option[A]] {
    val rep = StringRep[Option[A]]
    override def diff(xs: Option[A], ys: Option[A]): NonEmptySeq[String] = one(s"${rep.strRep(xs)} != ${rep.strRep(ys)}")
  }

  implicit def eitherDifference[A: StringRep, B: StringRep]: Difference[Either[A, B]] = new Difference[Either[A, B]] {
    val rep = StringRep[Either[A, B]]
    override def diff(xs: Either[A, B], ys: Either[A, B]): NonEmptySeq[String] = one(s"${rep.strRep(xs)} != ${rep.strRep(ys)}")
  }

  implicit def pairDifference[A: StringRep, B: StringRep]: Difference[Tuple2[A, B]] = new Difference[Tuple2[A, B]] {
    val rep = StringRep[Tuple2[A, B]]
    override def diff(pair1: Tuple2[A, B], pair2: Tuple2[A, B]): NonEmptySeq[String] = one(s"${rep.strRep(pair1)} != ${rep.strRep(pair2)}")
  }

  implicit def mapDifference[A: StringRep, B: StringRep]: Difference[Map[A, B]] = new Difference[Map[A, B]] {
    val rep = StringRep[Map[A, B]]
    override def diff(map1: Map[A, B], map2: Map[A, B]): NonEmptySeq[String] = one(s"${rep.strRep(map1)} != ${rep.strRep(map2)}")
  }
}