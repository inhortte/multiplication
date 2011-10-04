package polaris.games.multiplication
import scala.util.Random
import java.util.Date

class Equation {
  private val random		= new Random
  private val dcm		= new DigitCharMap

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
      // if (solved) end_time = (new Date).getTime
      Pair(c, i)
    } else {
      Pair(c, false)
    }
  }

  def hint = {
    val char = remainingCharacters(random.nextInt(remainingCharacters.size))
    guess(char, dcm.get(char).get.asInstanceOf[Int])
  }

  // At first I just coded this to return the digits left from
  // the set of digits in the puzzle, but quickly realized that
  // was silly because it would give away which digits are actually
  // used it the puzzle, so, instead, the difference of the set of
  // all digits (0..9) and the discovered digits is used.
  def remainingDigits: List[Int] =
    (0 to 9).toList.diff(digitsDiscovered.filter {
      c => c._2
    }.map {
      c => dcm.get(c._1).get
    }.toList)

  def remainingCharacters: List[Char] = digitsDiscovered.filter {
    c => !c._2
  }.map {
    case Pair(d, _) => d
  }.asInstanceOf[List[Char]]

  def solve = if (!solved) {
    // end_time = (new Date).getTime
    digitsDiscovered = digitsDiscovered map {
      case Pair(c, _) => Pair(c, true)
    }
  }

  def solved: Boolean = digitsDiscovered.foldLeft(true) {
    (acc, c) => acc && c._2
  }

  override def toString() = {
    "%5s".format(op1Veiled) + "\n" + "%5s".format(op2Veiled) + "\n----------\n" + "%5s".format(interm1Veiled) + "\n" + "%5s".format(interm2Veiled + " ") + "\n----------\n" + "%5s".format(resultVeiled)
  }

  private def unveil(n: Int) = dcm.toChars(n).toList.map { c => if (digitsDiscovered(c)) dcm.get(c).get else c }.foldLeft("") { (acc, c) => acc + c }
}
