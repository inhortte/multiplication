package polaris.games.multiplication

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.view.ViewGroup.LayoutParams
import _root_.android.widget.LinearLayout
import _root_.android.view.View
import _root_.android.widget.TextView
import _root_.android.widget.Button
import _root_.android.view.View.OnClickListener
import _root_.android.text.SpannableStringBuilder
import _root_.android.graphics.Typeface
import _root_.android.util.TypedValue
import scala.util.Random
import _root_.android.util.Log

class MainActivity extends Activity {
  private val random		= new Random
  private val res		= getResources
  private val neutralStati	= res.getStringArray(R.array.neutral_stati)
  private val yusStati		= res.getStringArray(R.array.yus_stati)
  private val neStati		= res.getStringArray(R.array.ne_stati)
  private val otro		= ""
  private var equation		= new FormattedEquation

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    val otroLabel = findViewById(R.id.otroLabel).asInstanceOf[TextView]
    otroLabel.setText(otro)
    setEquationText
    setStatus(getNeutralStatus)

    findViewById(R.id.guessButton).asInstanceOf[Button].setOnClickListener(new OnClickListener() {
      def onClick(v: View) = guessClick(v)
    })
  }

  def guessClick(target: View) = {
    val status = (getCharacterGuess, getDigitGuess) match {
      case Pair(Some(c: Char), Some(d: Int)) => equation.guess(c, d) match {
	case Pair(c: Char, b: Boolean)	=> if (b) getNeutralStatus else getNeStatus
	case Pair(c: Char, d: Int)	=> getYusStatus
	case _				=> getNeutralStatus
      }
      case _ => getNeutralStatus
    }
    setStatus(status)
    setEquationText
  }

  private def setStatus(status: String) =
    findViewById(R.id.statusLabel).asInstanceOf[TextView].setText(status)
  private def setEquationText = 
    findViewById(R.id.equationText).asInstanceOf[TextView].setText(equation.formatted)

  // These two methods return Some(String or Int) or None,
  // depending on the parse.
  private def getCharacterGuess: Option[Char] =
    findViewById(R.id.characterGuess).asInstanceOf[TextView].getText.asInstanceOf[SpannableStringBuilder].toString match {
      case s: String if s.size > 0	=> Some(s(0))
      case _				=> None
    }
  private def getDigitGuess: Option[Int] =
    findViewById(R.id.digitGuess).asInstanceOf[TextView].getText.asInstanceOf[SpannableStringBuilder].toString match {
      case s: String if s.size > 0	=> Some(s.toInt)
      case _				=> None
    }

  private def getNeutralStatus = neutralStati(random.nextInt(neutralStati.size))
  private def getYusStatus = yusStati(random.nextInt(yusStati.size))
  private def getNeStatus = neStati(random.nextInt(neStati.size))
}
