package benchmark

/*
 * data WTree a w = WLeaf a
 *                | WBin (WTree a w) (WTree a w)
 *                | WithWeight (WTree a w) w
 */

sealed trait WTree[A,W]

case class WLeaf[A,W](leaf : A) extends WTree[A, W]
case class WBin[A,W](left : WTree[A,W], right : WTree[A,W]) extends WTree[A,W]
case class WithWeight[A,W](tree : WTree[A,W], weight : W) extends WTree[A,W]

