package boon

trait CaseClassToMap[T] {
  def asMap(t: T): Map[String, String]
}

object CaseClassToMap {
   def apply[T: CaseClassToMap]: CaseClassToMap[T] = ???
}
