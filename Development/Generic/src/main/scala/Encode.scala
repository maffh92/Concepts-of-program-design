import GenericObject._

object EncodeObject {
  abstract class Encode[A] {
    def encode_ :  A => List[Bit]
  }

  sealed class Bit
  case object One extends Bit
  case object Zero extends Bit



  implicit object Encode extends Generic[Encode] {
    def unit : Encode[Unit] = new Encode[Unit] {def encode_ = const(Nil)}
    def plus[A,B](a : Encode[A], b : Encode[B]) : Encode[Plus[A,B]] = {
      new Encode[Plus[A,B]]
      {
        def encode_ = x => x match {
          case Inl(l) => Zero :: a.encode_(l)
          case Inr(r) => One :: b.encode_(r)
        }
      }
    }
    def product[A,B](a : Encode[A], b : Encode[B]) : Encode[Product[A,B]] = {
      new Encode[Product[A,B]]
      {
        def encode_ = x => x match {
          case  Product(l,r) => (a.encode_(l)) ++ (b.encode_(r))
        }

      }
    }
    def char : Encode[Char] = new Encode[Char] {def encode_ = encodeChar}
    def int = new Encode[Int]{def encode_ = encodeInt}
    def view[A,B](iso : Iso[B,A],  a : () => Encode[A]) : Encode[B] = {
      new Encode[B]
      {
        def encode_ = x => a().encode_(iso.from(x))
      }
    }
  }


  // Extend to fit all characters
  def encodeChar(x : Char) : List[Bit] = {
    x match{
      case '0' => List(Zero)
      case '1' => List(One)
    }
  }

  // Extend to fit all integers
  def encodeInt(x : Int) : List[Bit] = {
    x match{
      case 0 => List(Zero)
      case 1 => List(One)
    }
  }
}
