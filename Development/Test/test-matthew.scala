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
	Rep
*/

trait Rep[A]{
	def rep[G[_]](implicit gg : Generic[G]) : G[A] 
}

implicit object RepUnit extends Rep[Unit]{
	def rep[G[_]](implicit gg : Generic[G]) : G[Unit] = gg unit 
}

implicit object RepChar extends Rep[Char]{
	def rep[G[_]](implicit gg : Generic[G]) : G[Char] = gg char 
}

class RepProduct[A,B] (implicit a : Rep[A],b : Rep[B]) extends Rep[Product[A,B]]{
	def rep[G[_]](implicit gg : Generic[G]) : G[Product[A,B]] = {
		gg product[A,B](a.rep,b.rep)
	}
}


class RepList[A] (implicit a : Rep[A]) extends Rep[List[A]]{
	def rep[G[_]](implicit gg : Generic[G]) : G[List[A]] = {
		frepList(a.rep)
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



/*
  Map functions
*/

class Assoc
case object AssocLeft extends Assoc
case object AssocRight extends Assoc

trait Crush[B,A]{ 
	def selCrush : Assoc => A => B => B
}

def rsumCrush[A,B,D](ra : Crush[D,A])(rb : Crush[D,B])(asc : Assoc)(plus : Plus[A,B])(d : D) : D = {
	plus match{
		case Inl(a) => ra.selCrush(asc)(a)(d)
		case Inr(b) => rb.selCrush(asc)(b)(d)
	}
}

def rprodCrush[A,B,D](ra : Crush[D,A])(rb : Crush[D,B])(asc : Assoc)(product : Product[A,B])(d : D) : D = {
	product match{
		case Product(a,b) => {
				asc match{
					case AssocLeft => rb.selCrush(asc)(b)(ra.selCrush(asc)(a)(d))
					case AssocRight => ra.selCrush(asc)(a)(rb.selCrush(asc)(b)(d))
			}
		}
	}
	
}


def rtypeCrush[A,B,D](iso1 : Iso[B,A])(ra : Crush[D,A])(asc : Assoc)(b : B)(d : D) : D  = {
	ra.selCrush(asc)(iso1.from(b))(d)
}


object crushC{
	implicit def mkCrush[Z] = new crushC[Z] 
}

class crushC[B] extends Generic[({type AB[A] = Crush[B,A]})#AB]{

	def idCrush[A] = 
	{
		new Crush[B,A]{
			override def selCrush = _ => _ => id
		}
	}

 

	def unit = idCrush
	def char = idCrush
	def int  = idCrush
	
// 

	def plus[X,Y](ra : Crush[B,X], rb : Crush[B,Y]) = 
	{
		new Crush[B,Plus[X,Y]]{
			def selCrush = rsumCrush[X,Y,B](ra)(rb)
		}
	}

	def product[X,Y](ra : Crush[B,X], rb : Crush[B,Y]) = 
	{
		new Crush[B,Product[X,Y]]{
			def selCrush = rprodCrush[X,Y,B](ra)(rb)
		}
	}
	

	def view[X,Y](ra : Crush[B,X], rb : Crush[B,Y]) = 
	{
		new Crush[B,Product[X,Y]]{
			def selCrush = rprodCrush[X,Y,B](ra)(rb)
		}
	}
	// def product[A,B](a : G[A], b : G[B]) : G[Product[A,B]]
	def view[X,Y](iso1 : Iso[Y,X], a: () => Crush[B,X]) : Crush[B,Y] = {
		new Crush[B,Y]{
			def selCrush = rtypeCrush[X,Y,B](iso1)(a())
		}
	}

}


/* 
Representations

*/


 def frepList2[A,G[_]](g : Crush[Int, A])(implicit gg : Generic[({type AB[A] = Crush[Int,A]})#AB]): Crush[Int, List[A]] = {
    gg.view(listIso[A],() => gg.plus(gg.unit,gg.product(g,frepList2[A,G](g))))
  }

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


implicit def RepList2[G[_]](implicit g : Generic[({type AB[A] = Crush[Int,A]})#AB]) : FRep[({type AB[A] = Crush[Int,A]})#AB,List] = {
  new FRep[({type AB[X] = Crush[Int,X]})#AB,List]{
    override def frep[A](g1: Crush[Int, A]): Crush[Int, List[A]] = frepList2(g1)
  }
}




def crush[B,A,F[_]](asc : Assoc)(f : A => B => B)(z : B)(x : F[A])(implicit rep : FRep[({type AB[A] = Crush[B,A]})#AB,F]): B = {
	val fCrush = new Crush[B,A]{ 
	  override def selCrush= _ => f
	}
 	rep.frep(fCrush).selCrush(asc)(x)(z)
}

def crushr[B,A,F[_]](f : A => B => B)(z : B)(x : F[A])(implicit rep : FRep[({type AB[A] = Crush[B,A]})#AB,F]): B = {
	crush(AssocRight)(f)(z)(x)
}

def crushl[B,A,F[_]](f : A => B => B)(z : B)(x : F[A])(implicit rep : FRep[({type AB[A] = Crush[B,A]})#AB,F]): B = {
	crush(AssocLeft)(f)(z)(x)
}

// def plusFunction[A](x : String)(y : String) = x + y

def sum[X,B,F[_]](x : F[B])(implicit number : Number[B], rep : FRep[({type AB[X] = Crush[B,X]})#AB,F]) : B = {
	crushr(number.plus)(number.empty)(x)
}


// def hoi[F[_],B] (implicit rep : FRep[({type AB[X] = Crush[B,X]})#AB,F])  = rep
// val hoi1 = hoi

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

  trait GRep[G[_], A] {
    def grep: G[A]
  }

  def gTree[A, G[_]](implicit gg: Generic[G], a: GRep[G, A]) = new GRep[G, BinTree[A]] {
    override def grep: G[BinTree[A]] = binTree1(a.grep)
  }



//Function to test:
def increase(x : Int) = x + 1
val listInt : List[Int] = List(1,3,6,9)
val tree : BinTree[Int] = Bin(Leaf(2),Leaf(10))

