package scala.inlinable

private[inlinable] trait RangeLoops
extends TypeChecks
with TreeBuilders
with CommonMatchers
with InlinableRangeMatchers
{
  val universe: reflect.makro.Universe
  import universe._
  import definitions._

  def newWhileRangeLoop(
      fresh: String => String,
      start: Tree,
      end: Tree,
      step: Option[Tree],
      isInclusive: Boolean,
      param: ValDef,
      body: Tree): Tree =
  {
    val iVar = newLocalVar(fresh("i"), IntTpe, start)
    val iVal = newLocalVal(fresh("ii"), IntTpe, iVar()) // body expects a local constant
    val stepVal = newLocalVal(fresh("step"), IntTpe, step.getOrElse(newInt(1)))
    val endVal = newLocalVal(fresh("end"), IntTpe, end)

    // Type-check a fake (ordered) block, to force creation of ValDef symbols:
    typeCheck(
      Block(
        List(iVar, iVal, stepVal, endVal),
        newUnit
      )
    )

    // Replace any mention of the lambda parameter by a reference to iVal:
    val replacedBody = transform(body) {
      case tree if tree.symbol == param.symbol => iVal()
    }

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

    val outerDecls = new collection.mutable.ListBuffer[Tree]
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
        val isPositiveVal = newLocalVal(
          fresh("isStepPositive"),
          BooleanTpe,
          binOp(stepVal(), intOp(nme.GT), newInt(0))
        )
        outerDecls += isPositiveVal

        // We don't duplicate the loop and specialize in positive vs. negative case
        // for *many* reasons (code bloat is only one of them :-))

        // Note that the following expression does *not* simplify
        // (positiveCondition is not the negation of negativeCondition)
        If(
          isPositiveVal(),
          positiveCondition,
          negativeCondition
        )
    }

    outerDecls +=
      newWhileLoop(
        fresh,
        condition,
        Block(
          iVal,
          replacedBody,
          Assign(iVar(), intAdd(iVar(), stepVal()))
        )
      )

    Block(outerDecls.result, newUnit)
  }
}
