:paste
//import scala.language.higherKinds

/*
	Generic
*/

sealed class Unit
object Unit extends Unit

// Use tuple?
sealed trait Product[A,B]
case class Prod[A,B](a: A, b : B) extends Product[A,B]

// Use Either?
class Sum[A,B]
case class Inl[A,B](a : A) extends Sum[A,B]
case class Inr[A,B](b : B) extends Sum[A,B]

trait Iso[A,B]{
	def from : A => B
	def to   : B => A
} 

def fromList[A] : (List[A] => Sum[Unit,Product[A,List[A]]]) = l =>
	l match {
		case Nil 	   => Inl(Unit)
		case (x :: xs) => Inr(Prod(x,xs))
	}

def toList[A] : (Sum[Unit,Product[A,List[A]]] => List[A]) = r =>	
	r match {
		case Inl(_) => Nil
		case Inr(Prod(x,xs)) => x :: xs		
	}

def listIso[A]  = new Iso[List[A],Sum[Unit,Product[A,List[A]]]] {
	def from = fromList
	def to = toList
}

def rList[A,G[_]](g : G[A])(implicit gg : Generic[G]): G[List[A]] = {
	gg.view(listIso[A],() => gg.plus(gg.unit,gg.prod(g,rList[A,G](g))))
}

// Not sure if correct
abstract class GenericList[G[_]](implicit gg: Generic[G]) {
	def list[A]: G[A] => G[List[A]] = {
		x => rList(x)
	}	
}

type Arity = Int
type Name = String

trait Generic[G[_]] {
	def unit : G[Unit]
	def plus[A,B](a : G[A], b : G[B]) : G[Sum[A,B]]
	def prod[A,B](a : G[A], b : G[B]) : G[Product[A,B]]
	def constr[A](n : Name, ar : Arity, a : G[A]) : G[A] = a
	def char : G[Char]
	def int  : G[Int]
	def view[A,B](iso : Iso[B,A], a: () => G[A]) : G[B]	
}

/*
	GRep
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

class GPlus[A,B,G[_]](implicit gg : Generic[G], a : GRep[G,A], b : GRep[G,B]) extends GRep[G,Sum[A,B]] {
	def grep : G[Sum[A,B]] = gg.plus[A,B](a.grep,b.grep)
}


class GProd[A,B,G[_]](implicit gg : Generic[G], a : GRep[G,A], b : GRep[G,B]) extends GRep[G,Product[A,B]] {
	def grep : G[Product[A,B]] = gg.prod[A,B](a.grep,b.grep)
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
	def rep[G[_]](implicit gg : Generic[G]) : G[Unit] = gg.unit 
}

implicit object RepChar extends Rep[Char]{
	def rep[G[_]](implicit gg : Generic[G]) : G[Char] = gg.char 
}

implicit object RepInt extends Rep[Int]{
	def rep[G[_]](implicit gg : Generic[G]) : G[Int] = gg.int 
}

class RepPlus[A,B] (implicit a : Rep[A],b : Rep[B]) extends Rep[Sum[A,B]]{
	def rep[G[_]](implicit gg : Generic[G]) : G[Sum[A,B]] = {
		gg.plus[A,B](a.rep,b.rep)
	}
}

/*
	Encode
*/

abstract class Encode[A] {
	def encode_ :  A => List[Bit]
}

sealed class Bit
case class One() extends Bit
case class Zero() extends Bit
object One extends Bit
object Zero extends Bit

implicit object Encode extends Generic[Encode] {
	def unit : Encode[Unit] = new Encode[Unit] {def encode_ = const(Nil)}
	def plus[A,B](a : Encode[A], b : Encode[B]) : Encode[Sum[A,B]] = {
		new Encode[Sum[A,B]] 
			{
				def encode_ = x => x match {
					case Inl(l) => Zero :: a.encode_(l) 
					case Inr(r) => One :: b.encode_(r)
			}			
		}
	}
	def prod[A,B](a : Encode[A], b : Encode[B]) : Encode[Product[A,B]] = {
		new Encode[Product[A,B]]
			{
				def encode_ = x => x match {
					case  Prod(l,r) => (a.encode_(l)) ++ (b.encode_(r))
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