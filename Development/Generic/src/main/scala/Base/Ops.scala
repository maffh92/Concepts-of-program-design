package Base

object Ops {

  def const[A,B](a : A)(b : B) : A = a
  def id[A](a : A) : A = a

}