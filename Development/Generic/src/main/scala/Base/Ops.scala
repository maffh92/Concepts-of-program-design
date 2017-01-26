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

  def const[A,B](a : A)(b : B) : A = a
  def id[A](a : A) : A = a

}