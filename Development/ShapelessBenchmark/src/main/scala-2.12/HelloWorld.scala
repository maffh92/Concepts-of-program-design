/**
  * Created by Matthew on 11/24/2016.
  */
import shapeless.labelled.{KeyTag, FieldType}
import shapeless.syntax.singleton._


object HelloWorld {
  val someNumber = 123
  val numCherries = "numCherries" ->> someNumber
  def main(args: Array[String]): Unit = {
    println("Hello, world!" + numCherries)
  }
}