package boon

import syntax._

object ExceptionSuite extends SuiteLike("ExceptionSuite") {

  private class Flakey() {
    def blow: Int = throw new RuntimeException("boom!")
    def blowNested: String = throw new RuntimeException("nested boom!", new IllegalArgumentException("This is illegal"))
    def safe: Boolean = true
  }

  private val t1 = test("Exception Assertions") {
    ->>(
      "abcd".toInt =!=[NumberFormatException](_ =?= """For input string: "abcd"""" |
        "number format error"),

      List.empty[String].head =!=[NoSuchElementException](_ =?= "head of empty list" |
        "head on empty List"),

      new Flakey().blow  =!=[RuntimeException](_ =?=  "boom!" | "throw RuntimeException"),

      new Flakey().blowNested =!=[RuntimeException](_.contains("boom!") | "nested.exception.message")
    )
  }

  override val tests = NonEmptySeq.nes(t1)
}