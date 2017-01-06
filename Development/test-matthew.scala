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

def fromList[A] : (List[A] => Plus[Unit,Product[A,List[A]]]) = l =>
	l match {
		case Nil 	   => Inl(Unit)
		case (x :: xs) => Inr(Product(x,xs))
	}

def toList[A] : (Plus[Unit,Product[A,List[A]]] => List[A]) = r =>	
	r match {
		case Inl(_) => Nil
		case Inr(Product(x,xs)) => x :: xs		
	}

def listIso[A]  = new Iso[List[A],Plus[Unit,Product[A,List[A]]]] {
	def from = fromList
	def to = toList
}

def rList[A,G[_]](g : G[A])(implicit gg : Generic[G]): G[List[A]] = {
	gg.view(listIso[A],() => gg.plus(gg.unit,gg.product(g,rList[A,G](g))))
}

// Not sure if correct
abstract class GenericList[G[_]](implicit gg: Generic[G]) {
	def list[A]: G[A] => G[List[A]] = {
		x => rList(x)
	}	
}

type Arity = Int
type Name = String


/*
	 
*/


trait GRep[G[_],A] {
	def grep : G[A]	
}

class GUnit[G[_]](implicit gg : Generic[G]) extends GRep[G,Unit] {
	def grep : G[Unit] = gg.unit
}

class GInt[G[_]](implicit gg : Generic[G]) extends GRep[G,Int] {
	def grep : G[Int] = gg.int
}

class GChar[G[_]](implicit gg : Generic[G]) extends GRep[G,Char] {
	def grep : G[Char] = gg.char
}

class GPlus[A,B,G[_]](implicit gg : Generic[G], a : GRep[G,A], b : GRep[G,B]) extends GRep[G,Plus[A,B]] {
	def grep : G[Plus[A,B]] = gg.plus[A,B](a.grep,b.grep)
}


class Gproduct[A,B,G[_]](implicit gg : Generic[G], a : GRep[G,A], b : GRep[G,B]) extends GRep[G,Product[A,B]] {
	def grep : G[Product[A,B]] = gg.product[A,B](a.grep,b.grep)
}

class GList[A,G[_]](implicit glg : GenericList[G], a : GRep[G,A]) extends GRep[G,List[A]] {
	def grep : G[List[A]] = glg.list[A](a.grep)
}


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

implicit object RepInt extends Rep[Int]{
	def rep[G[_]](implicit gg : Generic[G]) : G[Int] = gg int 
}

implicit class RepPlus[A,B] (a : Rep[A])(implicit b : Rep[B]) extends Rep[Plus[A,B]]{
	def rep[G[_]](implicit gg : Generic[G]) : G[Plus[A,B]] = {
		gg plus[A,B](a.rep,b.rep)
	}
}

class RepProduct[A,B] (implicit a : Rep[A],b : Rep[B]) extends Rep[Product[A,B]]{
	def rep[G[_]](implicit gg : Generic[G]) : G[Product[A,B]] = {
		gg product[A,B](a.rep,b.rep)
	}
}


class RepList[A] (implicit a : Rep[A]) extends Rep[List[A]]{
	def rep[G[_]](implicit gg : Generic[G]) : G[List[A]] = {
		rList(a.rep)
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
	Encode
*/

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


trait Generic1[G[_,_]] {
	def unit : G[Unit,Unit]
	def plus[A1,A2,B1,B2](a : G[A1,A2], b : G[B1,B2]) : G[Plus[A1,B1],Plus[A2,B2]]
	def product[A1,A2,B1,B2](a : G[A1,A2], b : G[B1,B2]) : G[Product[A1,B1],Product[A2,B2]]
	def constr[A,Z](n : Name, ar : Arity, a : G[A,Z]) : G[A,Z] = a
	def char : G[Char,Char]
	def int  : G[Int,Int]
	def view[A1,A2,B1,B2](iso1 : Iso[A2,A1],iso2 : Iso[B2,B1], a: G[A1,B1]) : G[A2,B2]	
}


object myGeneric1 extends Generic1[Map]{

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

	def view[A1,A2,B1,B2](iso1 : Iso[A2,A1],iso2 : Iso[B2,B1], a: Map[A1,B1]) : Map[A2,B2] =
	{
		new Map[A2,B2]{
			def selMap = rTypeMap(iso1)(iso2)(a)
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

// rprodCrush :: Crush d a -> Crush d b -> Assoc -> a :*: b -> d -> d
// rprodCrush ra rb asc@AssocLeft  (a :*: b) = selCrush rb asc b . selCrush ra asc a
// rprodCrush ra rb asc@AssocRight (a :*: b) = selCrush ra asc a . selCrush rb asc b

// rtypeCrush :: EP b a -> Crush d a -> Assoc -> b -> d -> d
// rtypeCrush ep ra asc = selCrush ra asc . from ep

def rsumCrush[A,B,D](ra : Crush[D,A])(rb : Crush[D,B])(asc : Assoc)(plus : Plus[A,B])(d : D){
	plus match{
		case Inl(a) => ra.selCrush(asc)(a)(d)
		case Inr(b) => rb.selCrush(asc)(b)(d)
	}
}

def rprodCrush[A,B,D](ra : Crush[D,A])(rb : Crush[D,B])(asc : Assoc)(product : Product[A,B])(d : D){
	product match{
		case Product(a,b) => {
				asc match{
					case AssocLeft => rb.selCrush(asc)(b)(ra.selCrush(asc)(a)(d))
					case AssocRight => ra.selCrush(asc)(a)(rb.selCrush(asc)(b)(d))
			}
		}
	}
	
}


def rtypeCrush[A,B,D](iso1 : Iso[B,A])(ra : Crush[D,A])(asc : Assoc)(b : B)(d : D){
	ra.selCrush(asc)(iso1.from(b))(d)
}


trait genericCrush[B] extends Generic[({type AB[A] = Crush[B,A]})#AB]{

 // rsum     ra rb = Crush $ rsumCrush ra rb
 //  rprod    ra rb = Crush $ rprodCrush ra rb
 //  rtype ep ra    = Crush $ rtypeCrush ep ra

	def idCrush[A] = 
	{
		new Crush[B,A]{
			def selCrush = (_:Any) => (_:Any) => (x : B) => id[B](x)
		}
	}

 

	def unit = idCrush
	def char = idCrush
	def int  = idCrush
	// def plus(ra : _)
// 

	def plus[X,Y,D](ra : Crush[D,X], rb : Crush[D,Y]) = new Crush[B,Plus[X,Y]]{
			def selCrush = (x : Assoc) => (plus : Plus[X,Y]) => (d : D) => rsumCrush[X,Y,D](ra)(rb)(x)(plus)(d)
		}
	
	// def product[A,B](a : G[A], b : G[B]) : G[Product[A,B]]


}
