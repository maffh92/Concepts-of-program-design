package Functions

import Base.GenericObject._
/**
  * Created by maffh on 20-1-17.
  */

trait UpdateSalary[A]{
  def updateSalary(f : Float)(x : A) : A
}
class GUpdateSalary extends Generic[UpdateSalary]{
  def idSalary[A] = new UpdateSalary[A] {
    override def updateSalary(f : Float)(x:A) : A = x
  }
  override def unit: UpdateSalary[Unit] = idSalary

  override def plus[A, B](a: UpdateSalary[A], b: UpdateSalary[B]): UpdateSalary[Plus[A, B]] = new UpdateSalary[Plus[A, B]] {
    override def updateSalary(f: Float)(x: Plus[A, B]): Plus[A, B] = x match{
      case Inl(l) => Inl(a.updateSalary(f)(l))
      case Inr(r) => Inr(b.updateSalary(f)(r))
    }
  }

  override def product[A, B](a: UpdateSalary[A], b: UpdateSalary[B]): UpdateSalary[Product[A, B]] = new UpdateSalary[Product[A, B]] {
    override def updateSalary(f: Float)(x: Product[A, B]) = new Product(???,???)
  }

  override def char: UpdateSalary[Char] = idSalary

  override def int: UpdateSalary[Arity] = idSalary

  override def view[A, B](iso: Iso[B, A], a: () => UpdateSalary[A]): UpdateSalary[B] = ???
}


//> instance Generic UpdateSalary where
//>   unit        =  UpdateSalary (\ f x -> x)
//>   plus a b    =  UpdateSalary (\ f x -> case x of
//>                            Inl l -> Inl (updateSalary a f l)
//>                            Inr r -> Inr (updateSalary b f r))
//>   prod a b    =  UpdateSalary (\ f x -> (updateSalary a f (outl x))
//>                                     :*: (updateSalary b f (outr x)))
//>   view iso a  =  UpdateSalary (\ f x -> to iso
//>                                         (updateSalary a f (from iso x)))
//>   int         =  UpdateSalary (\ f x -> x)
//>   char        =  UpdateSalary (\ f x -> x)
//>   float       =  UpdateSalary (\ f x -> x)
//> instance GenericList UpdateSalary
