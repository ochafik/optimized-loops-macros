package scala.inlinable

import language.experimental.macros
import reflect.makro.Context

import collection._

trait TypeChecks
{
  val universe: reflect.makro.Universe
  import universe._
  
  import universe._
  import definitions._

  def resetAllAttrs[T <: Tree](tree: T): T
  def typeCheck[T <: Tree](tree: T): T
}
  
