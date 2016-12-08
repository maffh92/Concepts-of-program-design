/**
  * Created by maffh on 12/5/16.
  */
object ForLoops {
  def forEach(values : List[String]) : Unit = {
    for(value <- values){
      println(value)
    }
  }
  def rangeLoop(): Unit ={
    for( i <- 0 to 10){
        print(i)
    }
  }
  def rangeUntilLoop(): Unit ={
    for( i <- 0 until 10){
      print(i)
    }
  }
  def filterLoop(values : List[Int]): Unit ={
    //Check for even number
    for( value <- values if (value % 2 == 0)){
      print(value)
    }
  }
  def concatList(values : List[List[Any]]) : List[Any] = {
    for(value <- values; n <- value) yield n
  }

}
