package Functions

import Base._

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

  override def string: UpdateSalary[String] = idSalary

  override def int: UpdateSalary[Arity] = idSalary

  override def view[A, B](iso: Iso[B, A], a: () => UpdateSalary[A]): UpdateSalary[B] = ???
}
