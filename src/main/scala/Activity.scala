package polaris.games.multiplication

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.content.Context
import _root_.android.view.ViewGroup.LayoutParams
import _root_.android.view.View
import _root_.android.widget.{TextView, GridView, Button, LinearLayout, ArrayAdapter, EditText, AdapterView}
import _root_.android.view.View.OnClickListener
import _root_.android.widget.AdapterView.OnItemClickListener
import _root_.android.text.SpannableStringBuilder
import _root_.android.graphics.Typeface
import _root_.android.util.TypedValue
import scala.util.Random
import _root_.android.util.Log

class MainActivity extends Activity {
  private val random		= new Random
  private var equation		= new FormattedEquation

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    Log.i("onCreate", "The content view has been set")

    setEquation
    setStatus(getNeutralStatus)

    findViewById(R.id.guessButton).asInstanceOf[Button].setOnClickListener(new OnClickListener() {
      def onClick(v: View) = guessClick(v)
    })
    findViewById(R.id.equationGrid).asInstanceOf[GridView].setOnItemClickListener(new OnItemClickListener() {
      def onItemClick(aView: AdapterView[_], target: View, position: Int, id: Long) = gridClick(target)
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
    clearFields
    setEquation
  }

  def gridClick(target: View) = {
    val c = target.asInstanceOf[TextView].getText.toString
    if (c.size > 0 && c(0).isLetter)
      findViewById(R.id.characterGuess).asInstanceOf[EditText].setText(c)
  } 

  private def setStatus(status: String) =
    findViewById(R.id.statusLabel).asInstanceOf[TextView].setText(status)

  private def setEquation = {
    Log.i("setEquation", "Initializing grid")
    val grid = findViewById(R.id.equationGrid).asInstanceOf[GridView]
    Log.i("setEquation", "Initializing adapter")
    val adapter = new ArrayColorAdapter(this,
					R.layout.grid_text_item,
					equation.arrayForGrid).asInstanceOf[ArrayAdapter[String]]
    Log.i("setEquation", "Setting adapter")
    grid.setAdapter(adapter)
    Log.i("setEquation", "Adapter set")
  }
  /*
  private def setEquationText = 
    findViewById(R.id.equationText).asInstanceOf[TextView].setText(equation.formatted) */

  // These two methods return Some(String or Int) or None,
  // depending on the parse.
  private def getCharacterGuess: Option[Char] =
    findViewById(R.id.characterGuess).asInstanceOf[TextView].getText.asInstanceOf[SpannableStringBuilder].toString match {
      case s: String if s.size > 0	=> Some(s(0).toUpper)
      case _				=> None
    }
  private def getDigitGuess: Option[Int] =
    findViewById(R.id.digitGuess).asInstanceOf[TextView].getText.asInstanceOf[SpannableStringBuilder].toString match {
      case s: String if s.size > 0	=> Some(s.toInt)
      case _				=> None
    }

  private def clearFields = List(R.id.characterGuess, R.id.digitGuess) foreach {
    findViewById(_).asInstanceOf[TextView].setText("")
  }

  private def getNeutralStatus = getStatus(getResources.getStringArray(R.array.neutral_stati))
  private def getYusStatus = getStatus(getResources.getStringArray(R.array.yus_stati))
  private def getNeStatus = getStatus(getResources.getStringArray(R.array.ne_stati))
  private def getStatus(n: Array[String]) = n(random.nextInt(n.size))
}
