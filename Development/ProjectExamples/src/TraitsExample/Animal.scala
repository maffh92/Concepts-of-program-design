package TraitsExample

/**
  * Created by maffh on 12/3/16.
  */
trait Animal {
  def noise : String
  def dance : Unit = println("Dance and say" + noise + "!")
}

trait Pet extends  Animal{
  override def dance : Unit = println("Dance and say" + "D" + "!")
}




class Dog(name : String){
  def sound : Unit = println("Woof!")
  def movement : String = "Walk"
}

class Human(name : String, age : Int){
  def description : Unit = {
    println("Name: " + name)
    println("Age: " + age)
  }
}



case class Person(name : String) extends Animal{
  def noise = "I am a person"
}
case class Tiger() extends Animal{
  override def noise : String = "Grr"
}

case class Frog() extends Animal{
  def noise : String = "CROAK"
}

object AnimalNoise{
  def mkSound(animal : Animal) : Unit =
    animal match {
      case Frog() => println(Frog().noise)
      case Person(name) => println(name)
      case x => println(x.noise)
    }
}

//Object do not allow parameters
object pattern{
  def all(allTypes : Any) : Unit = {
    allTypes match{
      case (x,1) => println("(x,1)")
      case (x,y) => println("(x,y)")
      case x : String => println(x)
      case Tiger() => println(Tiger().noise)
      case 1 => println("One")
      case true => print("True")
    }
  }
}



//Abstract classes not be instantiated, but I don't see any other difference in comaring to the normal classes
abstract class Matthew(age: Int){
  def getAge : Int = age
}


