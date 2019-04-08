package boon
package model

import syntax._
import option._

object PredicateSuite extends SuiteLike("Predicate Suite") {

  private val t1 = test("create AssertionData") {
    val deferInt1 = Defer(() => 10)
    val deferInt2 = Defer(() => 20)
    val intPair = (deferInt1, deferInt2)
    val intPredicate = new Predicate[Int](intPair, IsEqual, None)
    val assertionData = intPredicate | "Int predicate"

    %@(assertionData.assertions) { assertions =>
      assertions.length =?= 1 | "no of assertions" and
      %@(assertions.head){ a1 =>
        a1.name.value =?= "Int predicate" | "assertion name" and
        a1.context =?= noContext | "assertion context" and %@(a1.testable.run) { testable =>
          val value1 = testable.value1.run
          val value2 = testable.value2.run

          value1.asInstanceOf[Int] =?= 10                              | "assertion.testable.value1"     and
          value2.asInstanceOf[Int] =?= 20                              | "assertion.testable.value2"     and
          testable.equality.eql(value1, value2) =?= false              | "assertion.testable.equality"   and
          testable.difference.diff(value1, value2) =?= one("10 != 20") | "assertion.testable.difference" and
          testable.equalityType =?= IsEqual                            | "assertion.testable.equalityType"
        } and %@(a1.location) { loc =>
          loc.line =?= 14 | "assertion.loc line" and
          some_?(loc.fileName)(_ =?= "PredicateSuite.scala" | "assertion.loc fileName") and
          some_?(loc.filePath)(_.endsWith("PredicateSuite.scala") | "assertion.loc filePath")
        }
      }
    } and none_?(intPredicate.hints)( pass | "no hints")
  }

  private val t2 = test("Override error messages") {
    val deferString = Defer(() => "some String")
    val stringPair = (deferString, deferString)
    val stringPredicate = new Predicate[String](stringPair, IsEqual, None)

    none_?(stringPredicate.hints)(pass | "start without hints") and
    %@(stringPredicate >> oneOrMore("error1", "error2")) { pred1 =>
      some_?(pred1.hints)(_ =?= oneOrMore("error1", "error2") | "override with hints")
    }
  }

  private val t3 = test("create Predicate with hints") {
    val deferChar1 = Defer(() => 'c')
    val deferChar2 = Defer(() => 'd')
    val charPair = (deferChar1, deferChar2)
    val charPredicate = new Predicate[Char](charPair, IsNotEqual, oneOrMore("err1", "err2", "err3").some)

    some_?(charPredicate.hints)(_ =?= oneOrMore("err1", "err2", "err3") | "create with hints")
  }

  override val tests = oneOrMore(t1, t2, t3)
}