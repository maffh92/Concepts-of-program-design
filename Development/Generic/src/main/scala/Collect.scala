import GenericObject._

object CollectObject {
  abstract class Collect[A] {
    type F[_]
    type B
    def selCollect : A => F[B]
  }

  implicit object Collect extends Generic[Collect] {
    type F[_]
    type B
    def unit : Collect[Unit] = new Collect[Unit] {def selCollect = const()}


  }
}
