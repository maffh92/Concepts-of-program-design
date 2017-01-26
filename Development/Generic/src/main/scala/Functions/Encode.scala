package Functions

import Base._
import Base.Ops._

/*
  Trait Encode represent a encoding of a value of type A
  into a list of Bit.
 */
trait Encode[A] {
  def encode_ :  A => List[Bit]
}

sealed trait Bit
case object One extends Bit
case object Zero extends Bit

object Encode {

  /* Instance for Generic[Encode] */
  implicit object EncodeC extends Generic[Encode] {
    def unit : Encode[Unit] = new Encode[Unit] {def encode_ = const(Nil)}
    def plus[A,B](a : Encode[A], b : Encode[B]) : Encode[Plus[A,B]] = new Encode[Plus[A,B]] {
      def encode_ = x => x match {
        case Inl(l) => Zero :: a.encode_(l)
        case Inr(r) => One :: b.encode_(r)
      }
    }
    def product[A,B](a : Encode[A], b : Encode[B]) : Encode[Product[A,B]] = new Encode[Product[A,B]]{
      def encode_ = x => x match {
        case  Product(l,r) => (a.encode_(l)) ++ (b.encode_(r))
      }
    }
    def char : Encode[Char] = new Encode[Char]{
      def encode_ = encodeChar
    }
    def int = new Encode[Int]{
      def encode_ = encodeInt
    }
    def string = new Encode[String]{
      def encode_ = encodeString
    }
    def view[A,B](iso : Iso[B,A],  a : () => Encode[A]) : Encode[B] = new Encode[B]{
        def encode_ = x => a().encode_(iso.from(x))
    }
  }

  // Extend to fit all characters
  def encodeChar(x : Char) : List[Bit] = {
    x match{
      case '0' => List(Zero)
      case '1' => List(One)
    }
  }

  // Extend to encode a String
  def encodeString(x : String) : List[Bit] = {
    x.flatMap(encodeChar).toList
  }

  // Extend to fit all integers
  def encodeInt(x : Int) : List[Bit] = {
    x match{
      case 0 => List(Zero)
      case 1 => List(One)
    }
  }

  // The method encode is the actual interface that we desire to use.
  def encode[A](a : A)(implicit grep: GRep[Encode,A]) : List[Bit] = {
    grep.grep.encode_(a)
  }
}
