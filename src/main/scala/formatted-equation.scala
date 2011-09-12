package polaris.games.multiplication

class FormattedEquation extends Equation {
  def formatted = 
    toString.toList.foldLeft("") {
      (acc, c) => acc + (if (c.isLetterOrDigit || c.isSpaceChar) {
                           c.toString + " " }
			 else {
			   c.toString
			 })
  }
}

/*
object Game {
  var equation = new FormattedEquation

  def checkIt(c: Char, d: Int): Boolean = {
    equation.guess(c, d) match {
      case Pair(c: Char, b: Boolean) => if (b) {
	println("You've already guessed that!")
      } else {
	println("Nope!")
      }
      case Pair(c: Char, i: Int) => println("Very good.")
      case _ => println("Something malfunctioned.")
    }
    true
  }

  def start = {
    while(true) {
      println(equation.formatted)
      print("Guess a letter and a number:")
      readLine().trim.split(" ") match {
	case Array(c: String, d: String) if (c(0) >= 65 && c(0) <= 90 && d(0) >= 48 && d(0) <= 57) => checkIt(c(0), d.toInt)
	case _ => false
      }
    }
  }
}

object ConsoleGame {
  def main(args: Array[String]) {
    println("Here we go!")
    val game = Game
    game.start
  }
}
*/
