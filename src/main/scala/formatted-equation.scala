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

  def arrayForGrid: java.util.ArrayList[String] = {
    ("%6s".format(op1Veiled) + "%6s".format(op2Veiled) + "------" + "%6s".format(interm1Veiled) + "%6s".format(interm2Veiled + " ") + "------" + "%6s".format(resultVeiled)).toList.foldLeft(new java.util.ArrayList[String]) {
      (acc, c) => acc.add(c.toString)
      acc
    }
  }
}

