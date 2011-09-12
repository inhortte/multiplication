package polaris.games.multiplication
import scala.util.Random

sealed class DigitCharMap {
  private val random = new Random
  private var alpha = ('A' to 'Z').toList
  private def takeFromAlpha = {
    val c = alpha(random.nextInt(alpha.size))
    alpha = alpha - c
    c.toChar
  }
  val dToCMap = (0 to 9).toList.foldLeft(Map.empty[Int, Char]) {
    (map, i) => map + (i -> takeFromAlpha)
  }
  val cToDMap = dToCMap map { case Pair(d, c) => Pair(c, d) }

  def get(x: Any) = x match {
    case x: Int if dToCMap.contains(x)	=> Some(dToCMap(x).toChar)
    case x: Char if cToDMap.contains(x) => Some(cToDMap(x))
    case _				=> None
  }

  def toChars(num: Int): String = {
    def iter(crypt: String, num: Int): String = {
      if (num == 0) crypt
      else iter(dToCMap(num % 10) + crypt, num / 10)
    }
    iter("", num)
  }

  def toDigits(crypt: String) = crypt.toList.map { cToDMap }.foldLeft("") { (acc, c) => acc + c }.toInt
}
