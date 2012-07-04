package scala.inlinable

import language.experimental.macros
import reflect.makro.Context

private[inlinable] object InlinableRangeMacros
{
  private[this] val errorMessageFormat = "Failed to optimize loop (%s)"
  private[this] val successMessage = "Rewrote this for-comprehension into a while loop"
  
  def rangeForeachImpl(c: Context)(f: c.Expr[Int => Unit]): c.Expr[Unit] = {
    c.Expr[Unit](
      new Contextualized(c) with RangeLoops {
        import universe._
        import definitions._

        val fTree = f.tree.asInstanceOf[Tree]
        val prefixTree = c.prefix.tree.asInstanceOf[Tree]
        
        lazy val defaultReplacement = {
          Apply(
            Select(
              Select(prefixTree, "toRange"),
              "foreach"),
            List(fTree))
        }

        val result = try {
          typeCheck(fTree) match {
            case Function(List(param @ ValDef(mods, name, tpt, rhs)), body) =>
              prefixTree match {
                case InlinableRangeTree(start, end, step, isInclusive) =>
                  val optimized = 
                    newWhileRangeLoop(c.fresh(_), start, end, step, isInclusive, param, body)
                  c.info(c.enclosingPosition, successMessage, force = false)
                  optimized
                case _ =>
                  c.warning(c.prefix.tree.pos, errorMessageFormat.format("unsupported range: " + c.prefix.tree))
                  defaultReplacement
              }
            case _ =>
              c.warning(f.tree.pos.asInstanceOf[c.Position], errorMessageFormat.format("unsupported body function: " + f.tree))
              defaultReplacement
          }
        } catch { case ex =>
          ex.printStackTrace
          c.warning(c.enclosingPosition, errorMessageFormat.format("internal loop optimization error: " + ex))
          defaultReplacement  
        }
      }.result.asInstanceOf[c.Tree]
    )
  }
}

