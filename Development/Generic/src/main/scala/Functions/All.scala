package Functions

import Functions.CrushObject._
/**
  * Created by maffh on 14-1-17.
  */
object All {
  implicit def mkCrush[Z] = new crushC[Z]
}
