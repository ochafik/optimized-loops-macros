package scala.inlinable

private[inlinable] trait TypeChecks
{
  val universe: reflect.makro.Universe
  import universe._

  def resetAllAttrs[T <: Tree](tree: T): T
  def typeCheck[T <: Tree](tree: T): T
}

