package scala.inlinable

import language.experimental.macros
import reflect.makro.Context

import collection._

trait RangeLoops
extends TreeBuilders
with CommonMatchers
with InlinableRangeMatchers
{
  val universe: reflect.makro.Universe
  import universe._
  import definitions._

  def resetAllAttrs(tree: Tree): Tree
  
  def newWhileRangeLoop(
      fresh: String => String,
      start: Tree, 
      end: Tree, 
      step: Option[Tree], 
      isInclusive: Boolean, 
      param: ValDef, 
      body: Tree): Tree = 
  {
    val iVar = newVar(fresh("i"), IntTpe, start)
    val iVal = newVar(fresh("ii"), IntTpe, iVar())
    val stepVal = newVar(fresh("step"), IntTpe, step.getOrElse(newInt(1)))
    val endVal = newVal(fresh("end"), IntTpe, end)

    val transformedBody = resetAllAttrs(transform(body) {
      case tree if tree.symbol == param.symbol => iVal()
    })

    def positiveCondition =
      binOp(
        iVar(), 
        intOp(if (isInclusive) nme.LE else nme.LT), 
        endVal()
      )
      
    def negativeCondition =
      binOp(
        iVar(),
        intOp(if (isInclusive) nme.GE else nme.GT), 
        endVal()
      )
    
    val outerDecls = new mutable.ListBuffer[Tree]
    outerDecls += iVar
    outerDecls += endVal
    outerDecls += stepVal
    
    val condition = step match {
      case None | Some(PositiveIntConstant(_)) =>
        positiveCondition
      case Some(NegativeIntConstant(_)) =>
        negativeCondition
      case _ =>
        // we don't know if the step is positive or negative: cool!
        val isPositiveVal = newVal(
          fresh("isStepPositive"), 
          BooleanTpe,
          binOp(stepVal(), intOp(nme.GT), newInt(0))
        )
        outerDecls += isPositiveVal
        
        // We don't duplicate the loop and specialize in positive vs. negative case
        // for *many* reasons (code bloat is only one of them :-))
        
        // Note that the following expression does *not* simplify
        // (positiveCondition is not the negation of negativeCondition)
        boolOr(
          boolAnd(isPositiveVal(), positiveCondition),
          boolAnd(boolNot(isPositiveVal()), negativeCondition)
        )
    }
    
    outerDecls += 
      newWhileLoop(
        fresh,
        condition,
        Block(
          iVal,
          transformedBody,
          Assign(iVar(), intAdd(iVar(), stepVal()))
        )
      )
      
    Block(outerDecls.result, newUnit)
  }
}
