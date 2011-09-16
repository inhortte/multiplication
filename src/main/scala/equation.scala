package polaris.games.multiplication
import scala.util.Random
import java.util.Date

class Equation {
  private val random		= new Random
  private val dcm		= new DigitCharMap
  private val start_time	= (new Date).getTime
  private var end_time		= (new Date).getTime

  // The first operend is three digits
  val op1 = random.nextInt(900) + 100
  // The second is two digits
  val op2 = random.nextInt(90) + 10

  // Intermediate values
  val interm1 = op1 * (op2 % 10)
  val interm2 = op1 * (op2 / 10)

  val result = op1 * op2

  def op1Veiled		= unveil(op1)
  def op2Veiled		= unveil(op2)
  def interm1Veiled	= unveil(interm1)
  def interm2Veiled	= unveil(interm2)
  def resultVeiled	= unveil(result)

  private val includedCharacters: List[Char] = (dcm.toChars(op1) + dcm.toChars(op2) + dcm.toChars(interm1) + dcm.toChars(interm2) + dcm.toChars(result)).toList.distinct

  // Note: This is a Char to Boolean mapping. 
  private var digitsDiscovered: Map[Char, Boolean] = includedCharacters.foldLeft(Map[Char, Boolean]()) {
    (acc, c) => acc.updated(c, false)
  }

  // If the character is not found in the digitsDiscovered map,
  // Pair(c, true) is returned. The same is returned if the
  // character is in the map and the digit has already been discovered.
  // Yeah, it's confusing, but that's the way it IS.
  def guess(c: Char, i: Int): (Char, Any) = {
    if (!digitsDiscovered.contains(c) || digitsDiscovered(c)) {
      Pair(c, true)
    } else if (dcm.get(c).get == i) {
      digitsDiscovered = digitsDiscovered.updated(c, true)
      if (solved) end_time = (new Date).getTime
      Pair(c, i)
    } else {
      Pair(c, false)
    }
  }

  def remainingDigits: List[Int] = digitsDiscovered.filter {
    case Pair(_, false) => true
  }.foldLeft(List[Int]()) {
    (acc, p) => p match {
      case Pair(d, _) => dcm.get(d).get.asInstanceOf[Int] :: acc
    }
  }

  def remainingCharacters: List[Char] = digitsDiscovered.filter {
    case Pair(_, false) => true
  }.map {
    case Pair(d, _) => d
  }.asInstanceOf[List[Char]]

  def solve = if (!solved) {
    end_time = (new Date).getTime
    digitsDiscovered = digitsDiscovered map {
      case Pair(c, _) => Pair(c, true)
    }
  }

  def solved: Boolean = digitsDiscovered.foldLeft(true) {
    (acc, c) => acc && c._2
  }

  def elapsed: Long = if (solved) (end_time - start_time) / 1000
		      else ((new Date).getTime - start_time) / 1000

  override def toString() = {
    "%5s".format(op1Veiled) + "\n" + "%5s".format(op2Veiled) + "\n----------\n" + "%5s".format(interm1Veiled) + "\n" + "%5s".format(interm2Veiled + " ") + "\n----------\n" + "%5s".format(resultVeiled)
  }

  private def unveil(n: Int) = dcm.toChars(n).toList.map { c => if (digitsDiscovered(c)) dcm.get(c).get else c }.foldLeft("") { (acc, c) => acc + c }
}
