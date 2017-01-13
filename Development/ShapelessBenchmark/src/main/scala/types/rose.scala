package types

/*
 * data Rose a = Node a [Rose a]
 */

sealed trait Rose[A]

case class Node[A](v : A, r : List[Rose[A]]) extends Rose[A]