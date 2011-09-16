package polaris.games.multiplication

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.content.Context
import _root_.android.view.ViewGroup.LayoutParams
import _root_.android.view.{View, KeyEvent}
import _root_.android.widget.{TextView, GridView, Button, LinearLayout, ArrayAdapter, EditText, AdapterView, AutoCompleteTextView}
import _root_.android.view.View.{OnClickListener, OnKeyListener}
import _root_.android.widget.AdapterView.OnItemClickListener
import _root_.android.text.{SpannableStringBuilder, TextWatcher, Editable}
import _root_.android.graphics.Typeface
import _root_.android.util.TypedValue
import scala.util.Random
import _root_.android.util.Log

class MainActivity extends Activity {
  private val random	= new Random
  private var equation	= new FormattedEquation
  private var mistakes	= 0

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    Log.i("onCreate", "The content view has been set")

    setEquation
    setStatus(getNeutralStatus)
    setListeners
    setCompletions
  }

  private def setListeners = {
    findViewById(R.id.guessButton).asInstanceOf[Button].setOnClickListener(new OnClickListener() {
      def onClick(v: View) = guessClick(v)
    })
    findViewById(R.id.equationGrid).asInstanceOf[GridView].setOnItemClickListener(new OnItemClickListener() {
      def onItemClick(aView: AdapterView[_], target: View, position: Int, id: Long) = gridClick(target)
    })
    List(R.id.characterGuess, R.id.digitGuess) foreach {
      findViewById(_).asInstanceOf[EditText].addTextChangedListener(new TextWatcher() {
	def afterTextChanged(t: Editable) = editableTextChanged(t)
	def beforeTextChanged(c: CharSequence, start: Int, count: Int, after: Int) = { }
	def onTextChanged(c: CharSequence, start: Int, before: Int, count: Int) = { }
      })
    }
  }

  private def guessClick(target: View) = {
    val status = (getCharacterGuess, getDigitGuess) match {
      case Pair(Some(c: Char), Some(d: Int)) => equation.guess(c, d) match {
	case Pair(c: Char, b: Boolean)	=> if (b) {
	  getNeutralStatus
	} else {
	  mistakes += 1
	  getNeStatus
	}
	case Pair(c: Char, d: Int)	=> getYusStatus
	case _				=> getNeutralStatus
      }
      case _ => getNeutralStatus
    }
    setStatus(status)
    clearFields
    setEquation
    setCompletions
  }

  private def gridClick(target: View) = {
    val c = target.asInstanceOf[TextView].getText.toString
    if (c.size > 0 && c(0).isLetter)
      findViewById(R.id.characterGuess).asInstanceOf[EditText].setText(c)
  } 

  // Only allow one character in the input fields.
  private def editableTextChanged(t: Editable) = {
    Log.i("editableTextChanged", "Replacing everything with the last char")
    if (t.toString.size > 1) {
      t.replace(0, t.toString.size - 1, "")
    }
  }

  private def setStatus(status: String) =
    findViewById(R.id.statusLabel).asInstanceOf[TextView].setText(status + "\n" + "[ " + mistakes + " mistakes ]")

  private def setEquation = {
    val grid = findViewById(R.id.equationGrid).asInstanceOf[GridView]
    val adapter = new ArrayColorAdapter(this,
					R.layout.grid_text_item,
					equation.arrayForGrid).asInstanceOf[ArrayAdapter[String]]
    grid.setAdapter(adapter)
  }

  private def setCompletions = {
    val digitGuess = findViewById(R.id.digitGuess).asInstanceOf[AutoCompleteTextView]
    val characterGuess = findViewById(R.id.characterGuess).asInstanceOf[AutoCompleteTextView]

    val digitAdapter = new ArrayAdapter(this,
				   android.R.layout.simple_dropdown_item_1line,
				   equation.remainingDigitsForCompletion).asInstanceOf[ArrayAdapter[String]]
    val characterAdapter = new ArrayAdapter(this,
					    android.R.layout.simple_dropdown_item_1line,
					    equation.remainingCharactersForCompletion).asInstanceOf[ArrayAdapter[String]]
    digitGuess.setAdapter(digitAdapter)
    characterGuess.setAdapter(characterAdapter)
  }

  // These two methods return Some(String or Int) or None,
  // depending on the parse.
  private def getCharacterGuess: Option[Char] =
    findViewById(R.id.characterGuess).asInstanceOf[TextView].getText.asInstanceOf[SpannableStringBuilder].toString match {
      case s: String if s.size > 0	=> Some(s(0).toUpper)
      case _				=> None
    }
  private def getDigitGuess: Option[Int] =
    findViewById(R.id.digitGuess).asInstanceOf[TextView].getText.asInstanceOf[SpannableStringBuilder].toString match {
      case s: String if s.size > 0 && s(0).isDigit	=> Some(s.toInt)
      case _						=> None
    }

  private def clearFields = List(R.id.characterGuess, R.id.digitGuess) foreach {
    findViewById(_).asInstanceOf[TextView].setText("")
  }

  private def getNeutralStatus = getStatus(getResources.getStringArray(R.array.neutral_stati))
  private def getYusStatus = getStatus(getResources.getStringArray(R.array.yus_stati))
  private def getNeStatus = getStatus(getResources.getStringArray(R.array.ne_stati))
  private def getStatus(n: Array[String]) = n(random.nextInt(n.size))
}
