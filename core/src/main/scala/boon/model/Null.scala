package boon
package model

object Null {
  implicit val nullStringRep: StringRep[Null.type] = StringRep.from[Null.type](_ => "null")
}

final case class Plain(value: String)

object Plain {
  implicit val plainStringRep: StringRep[Plain] = StringRep.from[Plain](_.value)
}