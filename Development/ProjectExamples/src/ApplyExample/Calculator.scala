package ApplyExample

/**
  * Created by maffh on 12/3/16.
  */
object Calculator {
  def plus(x : Int)(y : Int)  = x + y
  def minus(x : Int,y : Int) =  y - x
}


object PlusOne{
  def add(x : Int) : Int = x + 1
}

object plusOne{
  def apply(x : Int) : Int = x + 1
}



