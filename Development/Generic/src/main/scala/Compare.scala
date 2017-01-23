import scala.language.{higherKinds, postfixOps}
import Base.Generic._
import scalaz.Alternative

/**
  * Created by maffh on 8-1-17.
  */
class Ordering
case object LT extends Ordering
case object EQ extends Ordering
case object GT extends Ordering

trait Compare[A]{
  def selCompare : A => A => Ordering
}

object Compare {

  def rsumCompare[A, B](ra: Compare[A])(rb: Compare[B])(plus1: Plus[A, B])(plus2: Plus[A, B]): Ordering = {
    plus1 match {
      case Inl(a1) => {
        plus2 match {
          case Inl(a2) => ra.selCompare(a1)(a2)
          case Inr(_) => LT
        }
      }
      case Inr(b1) => {
        plus2 match {
          case Inl(_) => GT
          case Inr(b2) => rb.selCompare(b1)(b2)
        }
      }
    }
  }


  def rprodCompare[A, B](ra: Compare[A])(rb: Compare[B])(product1: Product[A, B])(product2: Product[A, B]): Ordering = {
    product1 match {
      case Product(a1, b1) => product2 match {
        case Product(a2, b2) => ra.selCompare(a1)(a2) match {
          case EQ => rb.selCompare(b1)(b2)
          case other => other
        }
      }
    }
  }


  def rtypeCompare[A, B](ep: Iso[A, B])(rb: Compare[B])(a1: A)(a2: A): Ordering = {
    rb.selCompare(ep.from(a1))(ep.from(a2))
  }

  trait CompareC extends Generic[Compare] {
    // def compareFunction[A] = new Compare[A]{
    // 	def selCompare = compare
    // }
    // def unit : Compare[Unit] = compareFunction
    // def char : Compare[Char] = compareFunction
    // def int  : Compare[Int] = compareFunction


    def plus[A, B](ra: Compare[A], rb: Compare[B]): Compare[Plus[A, B]] = {
      new Compare[Plus[A, B]] {
        def selCompare = rsumCompare(ra)(rb)
      }
    }

    def product[A, B](ra: Compare[A], rb: Compare[B]): Compare[Product[A, B]] = {
      new Compare[Product[A, B]] {
        def selCompare = rprodCompare(ra)(rb)
      }
    }

    def view[A, B](iso1: Iso[B, A], ra: () => Compare[A]): Compare[B] = {
      new Compare[B] {
        def selCompare = rtypeCompare(iso1)(ra())
      }
    }
  }

}

