package benchmark

/*
 * data BinTree a = Leaf a | Bin (BinTree a) (BinTree a)
 */

sealed trait BinTree[T]

case class Leaf[T](leaf : T) extends BinTree[T]
case class Bin[T](left : BinTree[T], right : BinTree[T])
