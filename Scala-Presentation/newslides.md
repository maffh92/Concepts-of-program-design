---
author: Carlos Tomé Cortiñas, Renate Eilers and Matthew Swart
title: Scala
subtitle: My title here
theme: uucs
mainfont: Ubuntu Light
sansfont: Ubuntu Light
---

# Table of Contents

* Introduction
* Static semantics
* Dynamic semantics

---

# History

* 2001
* École Polytechnique Fédérale de Lausanne (EPFL) by Martin Odersky

---

# Companies (Not sure)

* Twitter
* Linkedin
* The Guardian
* FourSquare
* Sony
* etc.

---

# Projects

* PlayFramework
* Akka

---

# Scala (Still need to add a picture)

* Object oriented
* Functional
* 

---

# REPL


![](img/Repl-Example.png "Alt caption"){ width=80% }

---

# Type system


![](img/classhierarchy.png "Alt caption"){ width=80% }

---

# Classes

```scala
class Dog(name : String){
  def sound : Unit = println("Woof!")
  def movement : String = "Walk"
}
```

```java
class Dog{
  private String name;
  public Dog(String name){
    this.name = name;
  }
  public void sound(){
    System.out.println("Woof!")
  }
  public String movement(){
    return("Walk")
  }
}
```
---

# Abstract classes

---

# Object

Examples.

---


# Classes

|              | Class | Abstract class | Object | Trait |
|:------------:|:-----:|:--------------:|:------:|:-----:|
| Inherentence |       |                |        |       |
|  Composition |       |                |        |       |
|  Parameters  |       |                |        |       |
|              |       |                |        |       |
|              |       |                |        |       |



---

# Traits
## Introduction

Look a lot like class definitions.
Difference with classes:

* No class parameters:
```scala
class Point(x : Int, y : Int)
```
```scala
trait Point(x : Int, y : Int) // Doesn't compile
```
* ``super`` calls are dynamically bound

* Classes do not inherit traits: traits are mixed in

---

# Traits
Example with queue

---

# Mixin traits
Extend queue example with double/increment
Linearization

---
# Modelling Haskell's type classes with traits
Example: 'Ordered'
Explain implicit

---

# GADTs in Scala
* simpe Expr language in Scala using case classes
* pattern matching (show using eval function)

---
# Functions
Several approaches:
* Methods of objects
* First class functions, allowing higher order functions

---

# Anonymous function

```scala
scala> values.map(x => x + 1)
res3: List[Int] = List(2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
```
---

# Function types

A => B is an abbrevication for the class scala.Function1[A,B]
package scala
```scala
trait Function1[A,B]{
  def apply(x : A) : B
}
```
```scala 
traits Function2...Function22
```
---

# Anonymous function

Anonymous function
(x : Int) => x + 1

This will be expanded to  (Same syntax as Java)
```scala
new Function1[Int,Int]{
	def apply(x : Int) : Int = x + 1
}
```

---

# Apply method


---

# Currying


---

# Currying - Anonymous function
```scala
var f = (x : Int) => (y : Int) => x + 1
f: Int => (Int => Int) = $$Lambda$1113/551797833@2c58dcb1

scala> f(1)
res0: Int => Int = $$Lambda$1130/1813375175@56380231

scala> f(1)(2)
res1: Int = 2
```

---

# Case Classes

```scala
case class Person(name : String) extends Animal{
  def noise = "I am a person"
}
case class Tiger() extends Animal{
  override def noise : String = "Grr"
}

case class Frog() extends Animal{
  def noise : String = "CROAK"
}
```
---

# Pattern Match

```scala
object AnimalNoise{
  def mkSound(animal : Animal) : Unit =
    animal match {
      case Frog() => println(Frog().noise)
      case Person(name) => println(name)
      case x => println(x.noise)
    }
}
```
---

# Pattern Match

```scala
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
```

---

# Evaluation
```scala
class LazyMethod(values : () => List[List[String]]) {
    def heavyComputation : List[String] = {
      for(value <- values; n <- value)
        for(value1 <- values; n1 <- value)
          for(value2 <- values; n2 <- value)
            for(value3 <- values; n3 <- value)
              for(value4 <- values; n4 <- value)
    }
}
```
...

---

# Conclusion



<!-- Local Variables:  -->
<!-- pandoc/write: beamer -->
<!-- pandoc/latex-engine: "xelatex" -->
<!-- pandoc/template: "beamer-template.tex" -->
<!-- End:  -->
