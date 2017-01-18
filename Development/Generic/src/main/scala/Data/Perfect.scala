package Data
import Base.GenericObject._

sealed trait Perfect[A]

case class Zero[A](v : A) extends Perfect[A]
case class Succ[A](v : Perfect[Fork[A]]) extends Perfect[A]

case class Fork[A](a : A,x : A)

object PerfectTree {
  type PerfectRep[A] = Plus[A,Perfect[Fork[A]]]
  type ForkRep[A] = Product[A,A]

  def isoPerfectTree[A] = new Iso[Perfect[A],PerfectRep[A]]{
    override def from = fromPerfectTree
    override def to = toPerfectTree
  }
  def fromPerfectTree[A](s : Perfect[A]) : PerfectRep[A] = {
    s match{
      case Zero(x) => Inl(x)
      case Succ(p) => Inr(p)
    }
  }
  def toPerfectTree[A](s : PerfectRep[A]) : Perfect[A] = {
    s match{
      case Inl(x) => Zero(x)
      case Inr(p) => Succ(p)
    }
  }

  def isoForkTree[A] = new Iso[Fork[A],ForkRep[A]]{
    override def from = fromForkTree
    override def to = toForkTree
  }
  def fromForkTree[A,X](s : Fork[A]) : ForkRep[A] = {
    s match{
      case Fork(x,y) => Product(x,y)
    }
  }
  def toForkTree[A](s : ForkRep[A]) : Fork[A] = {
    s match{
      case Product(x,y) => Fork(x,y)
    }
  }

  def fork[A,G[_]](g : G[A])(implicit gg : Generic[G]): G[Fork[A]] = {
      gg.view(isoForkTree[A],() => gg.product(g,g))
  }

  def perfecttree[A,G[_]](g : G[A])(implicit gg : Generic[G]): G[Perfect[A]] = {
    gg.view(isoPerfectTree[A],() => gg.plus(g,perfecttree(fork(g))))
  }

  implicit def frepPerfect[G[_]](implicit g : Generic[G]) : FRep[G,Perfect] = {
      new FRep[G,Perfect] {
        override def frep[A](g1 : G[A]) : G[Perfect[A]] = {
          perfecttree(g1)
        }
      }
    }

  implicit def frepFork[G[_]](implicit g : Generic[G]) : FRep[G,Fork] = {
    new FRep[G,Fork] {
      override def frep[A](g1 : G[A]) : G[Fork[A]] = {
        fork(g1)
      }
    }
  }

  def gPerfect[A, G[_]](implicit gg: Generic[G], a: GRep[G, A]) = new GRep[G, Perfect[A]] {
    override def grep: G[Perfect[A]] = perfecttree(a.grep)
  }

  def gFork[A, G[_]](implicit gg: Generic[G], a: GRep[G, A]) = new GRep[G, Fork[A]] {
    override def grep: G[Fork[A]] = fork(a.grep)
  }

}