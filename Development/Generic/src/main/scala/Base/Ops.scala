package Base

object Ops {

  trait Monoid[M] {
    def mempty : M
    def mappend(x : M, y : M) : M
  }

  object IntPlusMonoid extends Monoid[Int] {
    def mempty : Int = 0
    def mappend(x : Int, y : Int) : Int = x + y
  }

  object IntProdMonoid extends Monoid[Int] {
    def mempty : Int = 1
    def mappend(x : Int, y : Int) : Int = x * y
  }


  trait Number[M] {
    def emptyPlus : M
    def emptyProduct : M
    def plus(x : M)(y : M) : M
    def product(x : M)(y : M) : M
  }

  implicit  object NumberInt extends Number[Int] {
    override def emptyPlus: Int = 0
    override def emptyProduct: Int = 1
    override def plus(x: Int)(y: Int): Int = x + y
    override def product(x: Int)(y: Int): Int = x * y
  }


  def const[A,B](a : A)(b : B) : A = a
  def id[A](a : A) : A = a

}