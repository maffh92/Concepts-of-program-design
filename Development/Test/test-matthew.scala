:paste
import scala.language.higherKinds
import scala.language.postfixOps

/*
	Generic
*/

sealed class Unit
object Unit extends Unit

// Use tuple?
case class Product[A,B](a: A, b : B)

// Use Either?
class Plus[A,B]
case class Inl[A,B](a : A) extends Plus[A,B]
case class Inr[A,B](b : B) extends Plus[A,B]

trait Iso[A,B]{
	def from : A => B
	def to   : B => A
} 

type ListS[A] = Plus[Unit,Product[A,List[A]]]

def fromList[A] : (List[A] => ListS[A]) = l =>
	l match {
		case Nil 	   => Inl(Unit)
		case (x :: xs) => Inr(Product(x,xs))
	}

def toList[A] : (ListS[A] => List[A]) = r =>	
	r match {
		case Inl(_) => Nil
		case Inr(Product(x,xs)) => x :: xs		
	}

def listIso[A]  = new Iso[List[A],ListS[A]] {
	def from = fromList
	def to = toList
}

def frepList[A,G[_]](g : G[A])(implicit gg : Generic[G]): G[List[A]] = {
	gg.view(listIso[A],() => gg.plus(gg.unit,gg.product(g,frepList[A,G](g))))
}


def frep2List[A,B,G[_,_]](g : G[A,B])(implicit gg : Generic2[G]): G[List[A],List[B]] = {
	gg.view(listIso[A],listIso[B],() => gg.plus(gg.unit,gg.product(g,frep2List[A,B,G](g))))
}


// Not sure if correct
abstract class GenericList[G[_]](implicit gg: Generic[G]) {
	def list[A]: G[A] => G[List[A]] = {
		x => frepList(x)
	}
}

type Arity = Int
type Name = String



/*
help functions
*/

trait Number[A] {
  // an identity element
  def empty: A
  // an associative operation
  def plus(x: A)(y: A): A
  def multiple(x: A)(y: A): A
}

implicit val IntMonoid = new Number[Int] {
    def empty = 0
    def plus(x: Int)(y: Int) = x + y
    def multiple(x: Int)(y: Int) = x * y
}



/*
	Rep
*/

trait Rep[A]{
	def rep[G[_]](implicit gg : Generic[G]) : G[A] 
}

trait Rep2[A,B]{
	def rep[G[_,_]](implicit gg : Generic2[G]) : G[A,B] 
}

implicit val grepUnit = {
	new Rep[Unit]{
		def rep[G[_]](implicit gg : Generic[G]) : G[Unit] = gg.unit 
	}
}

implicit val grep2Unit = {
	new Rep2[Unit,Unit]{
		def rep[G[_,_]](implicit gg : Generic2[G]) : G[Unit,Unit] = gg.unit 
	}
}



implicit val grepChar : Rep[Char] = {
	new Rep[Char]{
		def rep[G[_]](implicit gg : Generic[G]) : G[Char] = gg.char 
	}
} 

implicit val grep2Char : Rep2[Char,Char] = {
	new Rep2[Char,Char]{
		def rep[G[_,_]](implicit gg : Generic2[G]) : G[Char,Char] = gg.char 
	}
} 

implicit def grepProduct[A,B] (implicit a : Rep[A],b : Rep[B]) : Rep[Product[A,B]] = {
	new Rep[Product[A,B]]{
		def rep[G[_]](implicit gg : Generic[G]) : G[Product[A,B]] = {
			gg.product[A,B](a.rep,b.rep)
		}
	}
}

// Map[Product[A1,B1],Product[A2,B2]]


implicit def grep2Product[A1,A2,B1,B2] (implicit a : Rep2[A1,A2],b : Rep2[B1,B2]) : Rep2[Product[A1,B1],Product[A2,B2]] = {
	new Rep2[Product[A1,B1],Product[A2,B2]]{
		def rep[G[_,_]](implicit gg : Generic2[G]) : G[Product[A1,B1],Product[A2,B2]] = {
			gg.product(a.rep,b.rep)
		}
	}
}


implicit def grepPlus[A,B] (implicit a : Rep[A],b : Rep[B]) : Rep[Plus[A,B]] = {
	new Rep[Plus[A,B]]{
		def rep[G[_]](implicit gg : Generic[G]) : G[Plus[A,B]] = {
			gg.plus[A,B](a.rep,b.rep)
		}
	}
}


implicit def grep2Plus[A1,A2,B1,B2] (implicit a : Rep2[A1,A2],b : Rep2[B1,B2]) : Rep2[Plus[A1,B1],Plus[A2,B2]] = {
	new Rep2[Plus[A1,B1],Plus[A2,B2]]{
		def rep[G[_,_]](implicit gg : Generic2[G]) : G[Plus[A1,B1],Plus[A2,B2]] = {
			gg.plus(a.rep,b.rep)
		}
	}
}


implicit def grepList[A](implicit a : Rep[A]) : Rep[List[A]] = {
	new Rep[List[A]]{
		def rep[G[_]](implicit gg : Generic[G]) : G[List[A]] = {
			frepList(a.rep)
		}
	}
}


implicit def grep2List[A,B](implicit a : Rep2[A,B]) : Rep2[List[A],List[B]] = {
	new Rep2[List[A],List[B]]{
		def rep[G[_,_]](implicit gg : Generic2[G]) : G[List[A],List[B]] = {
			frep2List(a.rep)
		}
	}
}






/*
 FREP
*/

trait FRep[G[_],F[_]]{
	def frep[A](g1 : G[A]) : G[F[A]]
}

trait FRep2[G[_,_],F[_]]{
	def frep2[A,B](g1 : G[A,B]) : G[F[A],F[B]]
}


/*
	Auxiliary functions
*/

def const[A,B](a : A)(b : B) : A = a
def id[A](a : A) : A = a



/*
  Map functions
*/

trait Map[A,B]{ 
	def selMap : A => B
}


trait Generic[G[_]] {
	def unit : G[Unit]
	def plus[A,B](a : G[A], b : G[B]) : G[Plus[A,B]]
	def product[A,B](a : G[A], b : G[B]) : G[Product[A,B]]
	def constr[A](n : Name, ar : Arity, a : G[A]) : G[A] = a
	def char : G[Char]
	def int  : G[Int]
	def view[A,B](iso : Iso[B,A], a: () => G[A]) : G[B]	
}


trait Generic2[G[_,_]] {
	def unit : G[Unit,Unit]
	def plus[A1,A2,B1,B2](a : G[A1,A2], b : G[B1,B2]) : G[Plus[A1,B1],Plus[A2,B2]]
	def product[A1,A2,B1,B2](a : G[A1,A2], b : G[B1,B2]) : G[Product[A1,B1],Product[A2,B2]]
	def constr[A,Z](n : Name, ar : Arity, a : G[A,Z]) : G[A,Z] = a
	def char : G[Char,Char]
	def int  : G[Int,Int]
	def view[A1,A2,B1,B2](iso1 : Iso[A2,A1],iso2 : Iso[B2,B1], a: () => G[A1,B1]) : G[A2,B2]	
}


implicit object MapC extends Generic2[Map]{

	def idMap[A] : Map[A,A] = new Map[A,A]{def selMap = id}

	def unit = idMap
	def char = idMap
	def int  = idMap

	def product[A1,A2,B1,B2](ra : Map[A1,A2], rb : Map[B1,B2]) = {
		new Map[Product[A1,B1],Product[A2,B2]]{
			def selMap = rprodMap(ra)(rb)
		}
	}

	def plus[A1,A2,B1,B2](ra : Map[A1,A2], rb : Map[B1,B2]) = {
		new Map[Plus[A1,B1],Plus[A2,B2]]{
			def selMap = rsumMap(ra)(rb)
		}
	}

	def view[A1,A2,B1,B2](iso1 : Iso[A2,A1],iso2 : Iso[B2,B1],a: () => Map[A1,B1]) : Map[A2,B2] =
	{
		new Map[A2,B2]{
			def selMap = rTypeMap(iso1)(iso2)(a())
		}
	}	


	def rprodMap[A1,A2,B1,B2](ra : Map[A1,A2])
		(rb : Map[B1,B2])(product : Product[A1,B1]) : Product[A2,B2] = {
			product match{
				case Product(a,b) => Product(ra.selMap(a),rb.selMap(b))
			}
		}


	def rsumMap[A1,A2,B1,B2]
			(ra : Map[A1,A2])(rb : Map[B1,B2])(plus : Plus[A1,B1]) : Plus[A2,B2] = {
				plus match{
					case Inl(x) => Inl(ra.selMap(x))
					case Inr(x) => Inr(rb.selMap(x))
				}
			}



	def rTypeMap[B,R,D,A](iso1 : Iso[B,R])(iso2 : Iso[D,A])(ra : Map[R,A])(b : B) :  D = {
		iso2.to(ra.selMap(iso1.from(b)))
	}
}

def map[A,B,F[_]](f : A => B)(functor : F[A])(implicit rep : FRep2[Map, F]) : F[B] = {
		val fMap = new Map[A,B]{
			def selMap = f
		}
		return(rep frep2(fMap) selMap(functor))
}

// def mapRep[A,B,F[_]](f : A => B)(functor : List[A])(implicit rep : Rep2[A,B], g : Generic2[Map]) : scala.Unit = {
// 		val fMap = new Map[A,B]{
// 			def selMap = f
// 		}
// 		val x = rep.rep(g) //.selMap(functor)
// }





/* 
Representations

*/


implicit def Rep2List[G[_,_]](implicit g : Generic2[G]) : FRep2[G,List] = new FRep2[G,List] {
	def frep2[A,B](g1 : G[A,B]) : G[List[A], List[B]] = {
		frep2List(g1)
	}
}



implicit def RepList[G[_]](implicit g : Generic[G]) : FRep[G,List] = {
	new FRep[G,List] {
		def frep[A](g1 : G[A]) : G[List[A]] = {
			frepList(g1)
		}
	}
} 





// def hoi[F[_],B] (implicit rep : FRep[({type AB[X] = Crush[B,X]})#AB,F])  = rep
// val hoi1 = hoi



val lijst = List(1,2,3)


def callMattthew(implicit m : Matthew) : String = m.shout
object Matthew{
	implicit def matthew : Matthew = new Matthew
}
class Matthew{
	def shout : String  = "Matthew"
}




sealed trait BinTree[T]

case class Leaf[T](leaf : T) extends BinTree[T]
case class Bin[T](left : BinTree[T], right : BinTree[T]) extends BinTree[T]


type BinTreeRep[T] = Plus[T,Product[BinTree[T],BinTree[T]]]
  def fromBinTree[T](tree : BinTree[T]) : BinTreeRep[T] = {
    tree match{
      case Leaf(x) => Inl(x)
      case Bin(l,r) => Inr(Product(l,r))
    }
  }

  def toBinTree[T](plus : BinTreeRep[T]) : BinTree[T] = {
    plus match{
      case Inl(x) => Leaf(x)
      case Inr(Product(l,r)) => Bin(l,r)
    }
  }

  def binTreeIso[T] = new Iso[BinTree[T],BinTreeRep[T]]{
    override def from = fromBinTree
    override def to = toBinTree
  }


  def binTree1[A,G[_]](g : G[A])(implicit gg : Generic[G]): G[BinTree[A]] = {
    gg.view(binTreeIso[A],() => gg.plus(g,gg.product(binTree1[A,G](g),binTree1[A,G](g))))
  }

  def binTree2[A,B,G[_,_]](g : G[A,B])(implicit gg : Generic2[G]): G[BinTree[A],BinTree[B]] = {
    gg.view(binTreeIso[A],binTreeIso[B],() => gg.plus(g,gg.product(binTree2[A,B,G](g),binTree2[A,B,G](g))))
  }

  implicit def frepTree1[G[_]](implicit g : Generic[G]) : FRep[G,BinTree] = {
    new FRep[G,BinTree] {
      override def frep[A](g1 : G[A]) : G[BinTree[A]] = {
        binTree1(g1)
      }
    }
  }

  implicit def frepTree2(implicit g : Generic2[Map]) : FRep2[Map,BinTree] = {
    new FRep2[Map,BinTree] {
      override def frep2[A, B](g1: Map[A, B]): Map[BinTree[A], BinTree[B]] = binTree2(g1)
    }
  }

  def gTree[A, G[_]](implicit gg: Generic[G], a: GRep[G, A]) = new GRep[G, BinTree[A]] {
    override def grep: G[BinTree[A]] = binTree1(a.grep)
  }



//Function to test:
def increase(x : Int) = x + 1
val listInt : List[Int] = List(1,3,6,9)
val tree : BinTree[Int] = Bin(Leaf(2),Leaf(10))


abstract class Encode[A] {
  def encode_ :  A => List[Bit]
}

sealed class Bit
case object One extends Bit
case object Zero extends Bit

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

  // Extend to fit all integers
  def encodeInt(x : Int) : List[Bit] = {
    x match{
      case 0 => List(Zero)
      case 1 => List(One)
    }
  }

def encodeTest(implicit g : Generic[Encode]) : List[Bit] = {
	g.product(g.char,g.char).encode_(Product('0','0'))
}

def encodeTestRep(implicit rep : Rep[Char]) : List[Bit] = {
	rep.rep.encode_('0')
}

def encodeTestGRep(implicit rep : GRep[Encode,Char]) : List[Bit] = {
	rep.grep.encode_('0')
}


trait GRep[G[_], A] {
def grep: G[A]
}

// implicit def mkGUnit[G[_]] = new GUnit[Encode]
implicit def GUnit[G[_]](implicit gg: Generic[G]) = {
	new GRep[G, Unit] {
		def grep: G[Unit] = gg.unit
	}
} 

implicit def GInt[G[_]](implicit gg: Generic[G]) = new GRep[G, Int] {
    def grep: G[Int] = gg.int
  }


implicit def GChar[G[_]](implicit gg: Generic[G]) = {
	new GRep[G, Char] {
		def grep: G[Char] = gg.char
	}	
}	 


implicit def GPlus[A, B, G[_]](implicit gg: Generic[G], a: GRep[G, A], b: GRep[G, B]) = {
	new GRep[G, Plus[A, B]] {
		def grep: G[Plus[A, B]] = gg.plus[A, B](a.grep, b.grep)
	}
}


implicit def Gproduct[A, B, G[_]](implicit gg: Generic[G], a: GRep[G, A], b: GRep[G, B]) = {
	new GRep[G, Product[A, B]] {
	    def grep: G[Product[A, B]] = gg.product[A, B](a.grep, b.grep)
	}
}  

