:paste
import scala.language.higherKinds

sealed class Bit
case class One() extends Bit
case class Zero() extends Bit
object One extends Bit
object Zero extends Bit

case class Unit()

class Plus[A,B]
case class Lnl[A,B](lnl: A) extends Plus[A,B]
case class Lnr[A,B](lnr: B) extends Plus[A,B]
case class Product[A,B](lnl : A,lnr : B)  // A x B

//Function is used as an isomorphism for to and from...
case class Iso[A,B](f : A => B,t : B => A){
	def from(a : A) : B = f(a)
	def to(b : B) : A = t(b)
} 

//Help types
type Arity = Int
type Name = String

trait Generic[A]{
	type G[_]
	def unit : G[Unit]
	def product[A,B](ga : G[A])(gb : G[B]) : G[Product[A,B]]
	def plus[A,B](ga : G[A])( gb : G[B]) : G[Plus[A,B]]
	def constr[A](name : Name)(arity : Arity)(ga : G[A]) : G[A] = ga
	def char : G[Char]
	def int : G[Int] 
	def view[A,B](iso : Iso[B,A])(ga : G[A]) : G[B]

	def id[Z](z : Z) : Z = z 
}


abstract class EncodeInstance extends Generic[Encode[Any]]{
	type G[Any] = Encode[Any]
	def unit = new Encode[Unit]{def encode = const(List())}
	def product[A,B](a : G[A], b : G[B]) : Encode[Product[A,B]] = {
		new Encode[Product[A,B]]{
			def encode = z => z match{
				case Product(x,y) => a.encode(x) ++ b.encode(y)
			}
		}
	}
	def plus[A,B](a : G[A], b : G[B]) : Encode[Plus[A,B]] = {
		new Encode[Plus[A,B]]{def encode = {
				x => x match{
					case Lnl(l) => Zero :: a.encode(l)
					case Lnr(r) => One :: b.encode(r)
				}
			}}
	}
 	def char = new Encode[Char]{def encode = encodeChar}
	def int = new Encode[Int]{def encode = encodeInt}
	def view[A,B](iso : Iso[B,A], a : G[A]) : G[B] = {
		new Encode[B]{
			def encode = x => {
				a.encode(iso.from(x))
			}
		}
	}


	def const[A,B](a : A)(b : B) : A = a
	
	def encodeChar(x : Char) : List[Bit] = {
		x match{
			case '0' => List(Zero)
			case '1' => List(One)
		}
	}

	def encodeInt(x : Int) : List[Bit] = {
		x match{
			case 0 => List(Zero)
			case 1 => List(One)
		}
	}

}

// case class Unit
abstract class Encode[A] {
	def encode : A => List[Bit]
}



class encodeList[A,B]{
	def fromList(a : List[A]) : Plus[Unit,Product[A,List[A]]] = {
		a match {
			case x :: xs => Lnr(new Product(x,xs))
			case Nil     => Lnl(new Unit())
		}
	}
	def toList(a : Plus[Unit,Product[A,List[A]]]) : List[A] =  {
		a match{
			case Lnr(Product(x,xs)) => x :: xs
			case Lnl(Unit()) => List()
		}
	}

	def isoList : Iso[List[A],Plus[Unit,Product[A,List[A]]]] = Iso(fromList,toList)

	// def rList(g : Generic{type G = A}) : Generic{type G = List[A]} = {
	// 	g.view(isoList, g.plus(g.unit, g.product(g,rList(g))))
	// 	// view isoList (unit `plus` (a `prod` rList a))
	// }
}



object IntUtils {
   implicit class Fishies(val x: Int) {
     def fishes = "Fish" * x
   }

}

implicit val values : List[Int] = List(2,5,6,7,9,10)

object Mapp{ 
	def mapFunctie(values : List[Int]) : List[Int] = {
		values match{
			case (x :: xs) => ((x+1) :: mapFunctie(xs))
			case Nil 	 => Nil
		}
	}

	def mapFunctieHigherOrder[A,B](f : A => B)(values : List[A]) : List[B] = {
		values match{
			case (x :: xs) => f(x) :: mapFunctieHigherOrder(f)(xs)
			case Nil 	 => Nil
		}
	}
}







// object TestOp{
// 	val test : Encode[Unit] = new Encode[Unit] {def encode(a:Unit) = List('h')}
// }

// abstract class encodeList[A,B]{
// 	def fromList(a : List[A]) : Plus[Unit,Product[A,List[A]]] = {
// 		a match {
// 			case x :: xs => Lnr(new Product(x,xs))
// 			case Nil     => Lnl(Unit)
// 		}
// 	}
// 	def toList(a : Plus[Unit,Product[A,List[A]]]) : List[A] =  {
// 		a match{
// 			case Lnr(Product(x,xs)) => x :: xs
// 			case Lnl(Unit) => List()
// 		}
// 	}

// 	// def isoList : Iso[List[A],Plus[Unit,Product[A,List[A]]]] = new Iso(fromList,toList)

// isoList = Iso fromList toList

// }

// g.view()
// 



// implicit object encode[A] extends Generic[List[A]]{

// }