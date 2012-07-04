package scala.inlinable

import reflect.makro.Context

class Contextualized(val c: Context)
extends TypeChecks
{
  override val universe = c.universe
  import universe._

  def resetAllAttrs[T <: Tree](tree: T): T =
    c.resetAllAttrs(tree.asInstanceOf[c.Tree]).asInstanceOf[T]
  
  def typeCheck[T <: Tree](tree: T): T =
    c.typeCheck(tree.asInstanceOf[c.Tree]).asInstanceOf[T]
}
  
