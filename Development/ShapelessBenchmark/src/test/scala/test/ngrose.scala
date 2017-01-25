package test

import org.scalatest.FlatSpec
import shapeless._

import types.{NGRose}

import benchmark._
import benchmark._

class NGRoseTest extends FlatSpec {

  // For a type like NGRose, shapeless is not able to automatically
  // derive a Generic instance.

  val genngrose = Generic[NGRose[List,Int]]


}