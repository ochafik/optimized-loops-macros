package scala.inlinable

import language.experimental.macros
import reflect.makro.Context

import collection._

class Contextualized(val c: Context)
extends TypeChecks
with TreeBuilders
{
  override val universe = c.universe
  
  import universe._
  import definitions._

  def resetAllAttrs[T <: Tree](tree: T): T =
    c.resetAllAttrs(tree.asInstanceOf[c.Tree]).asInstanceOf[T]
  
  def typeCheck[T <: Tree](tree: T): T =
    c.typeCheck(tree.asInstanceOf[c.Tree]).asInstanceOf[T]
}
  
