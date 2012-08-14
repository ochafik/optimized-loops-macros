package scala.inlinable

private[inlinable] trait TreeBuilders
extends TypeChecks
with InlinableNames
{
  val universe: reflect.makro.Universe
  import universe._
  import definitions._

  implicit def valDef2Ident(d: ValDef) = new {
    def apply(): Ident = {
      val i = Ident(d.name)
      if (d.symbol != null && d.symbol != NoSymbol) {
        i.symbol = d.symbol
        typeCheck(i)
      }
      i
    }
  }

  def transform(tree: Tree)(pf: PartialFunction[Tree, Tree]) = {
    new Transformer {
      override def transform(tree: Tree) = {
        if (pf.isDefinedAt(tree))
          pf(tree)
        else
          super.transform(tree)
      }
    }.transform(tree)
  }

  def newBool(v: Boolean) = Literal(Constant(v))
  def newInt(v: Int) =      Literal(Constant(v))
  def newLong(v: Long) =    Literal(Constant(v))
  def newNull(tpe: Type) =  Literal(Constant(null))
  def newUnit =             Literal(Constant({}))

  def binOp(a: Tree, op: Symbol, b: Tree) =
    Apply(Select(a, op), List(b))

  def intOp(name: TermName) =
    IntTpe.member(name)

  def boolOp(name: TermName) =
    BooleanTpe.member(name)

  def boolAnd(a: Tree, b: Tree) = {
    if (a == null)
      b
    else if (b == null)
      a
    else
      binOp(a, boolOp(N("&&")), b)
  }
  def boolOr(a: Tree, b: Tree) = {
    if (a == null)
      b
    else if (b == null)
      a
    else
      binOp(a, boolOp(N("||")), b)
  }
  def boolNot(a: => Tree) = {
    Select(a, "!")
  }

  def intAdd(a: => Tree, b: => Tree) =
    binOp(a, IntTpe.member(N("+")), b)

  import Flag._

  def newLocalVar(name: String, tpe: Type, defaultValue: Tree = EmptyTree) =
    ValDef(Modifiers(union(MUTABLE, PRIVATE)), N(name), TypeTree(tpe), defaultValue)

  def newLocalVal(name: String, tpe: Type, defaultValue: Tree = EmptyTree) =
    ValDef(Modifiers(PRIVATE), N(name), TypeTree(tpe), defaultValue)

  def newWhileLoop(fresh: String => String, cond: Tree, body: Tree): Tree = {
    val labelName = N(fresh("while$"))
    LabelDef(
      labelName,
      Nil,
      If(
        cond,
        Block(
          if (body == null)
            Nil
          else
            List(body),
          Apply(
            Ident(labelName),
            Nil
          )
        ),
        newUnit
      )
    )
  }
}

