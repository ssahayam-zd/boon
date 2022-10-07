package boon
package internal

import model._
import result._

//All implicits for internal boon types - mainly used for testing boon.
object instances {
  implicit val assertionCombinatorBoonType: BoonType[AssertionCombinator]     = BoonType.defaults[AssertionCombinator]

  implicit val assertionBoonType: BoonType[Assertion]               = BoonType.defaults[Assertion]

  implicit val assertionDataBoonType: BoonType[AssertionData]           = BoonType.defaults[AssertionData]
  
  implicit val assertionResultBoonType: BoonType[AssertionResult]         = BoonType.defaults[AssertionResult]
  
  implicit val assertionOutputBoonType: BoonType[AssertionOutput]         = BoonType.defaults[AssertionOutput]

  implicit val assertionStateBoonType: BoonType[AssertionState]          = BoonType.defaults[AssertionState]

  implicit val testStateBoonType: BoonType[TestState]               = BoonType.defaults[TestState]

  implicit val sequentialPassBoonType: BoonType[SequentialPass]          = BoonType.defaults[SequentialPass]
  
  implicit val sequentialNotRunBoonType: BoonType[SequentialNotRun]        = BoonType.defaults[SequentialNotRun]

  implicit val suiteStateBoonType: BoonType[SuiteState]              = BoonType.defaults[SuiteState]

  implicit val equalityTypeBoonType: BoonType[EqualityType]            = BoonType.defaults[EqualityType]

  implicit val assertionFailureDoubleeBoonType: BoonType[AssertionFailureDouble] = BoonType.defaults[AssertionFailureDouble]
}

