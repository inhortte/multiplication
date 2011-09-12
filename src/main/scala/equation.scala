package polaris.games.multiplication
import scala.util.Random

class Equation {
  private val random = new Random
  private val dcm = new DigitCharMap
  private var digitsDiscovered = dcm.cToDMap map {
    case Pair(d, _) => Pair(d, false)
  }

  // The first operend is three digits
  val op1 = random.nextInt(900) + 100
  // The second is two digits
  val op2 = random.nextInt(90) + 10

  // Intermediate values
  val interm1 = op1 * (op2 % 10)
  val interm2 = op1 * (op2 / 10)

  val result = op1 * op2

  def op1Veiled = unveil(op1)
  def op2Veiled = unveil(op2)
  def interm1Veiled = unveil(interm1)
  def interm2Veiled = unveil(interm2)
  def resultVeiled = unveil(result)

  def guess(c: Char, i: Int): (Char, Any) = {
    if (digitsDiscovered(c)) {
      Pair(c, true)
    } else if (dcm.get(c).get == i) {
      digitsDiscovered = digitsDiscovered.updated(c, true)
      Pair(c, i)
    } else {
      Pair(c, false)
    }
  }

  override def toString() = {
    "%5s".format(op1Veiled) + "\n" + "%5s".format(op2Veiled) + "\n----------\n" + "%5s".format(interm1Veiled) + "\n" + "%5s".format(interm2Veiled + " ") + "\n----------\n" + "%5s".format(resultVeiled)
  }

  private def unveil(n: Int) = dcm.toChars(n).toList.map { c => if (digitsDiscovered(c)) dcm.get(c).get else c }.foldLeft("") { (acc, c) => acc + c }
}
