package polaris.games.multiplication

import _root_.android.app.{Activity, AlertDialog}
import _root_.android.os.Bundle
import _root_.android.content.{Context, Intent}
import _root_.android.content.res.Configuration
import _root_.android.view.ViewGroup.LayoutParams
import _root_.android.view.{View, KeyEvent, LayoutInflater, ViewGroup, Menu, MenuInflater, MenuItem}
import _root_.android.widget.{TextView, GridView, Button, LinearLayout, ArrayAdapter, EditText, AdapterView, AutoCompleteTextView}
import _root_.android.view.View.{OnClickListener, OnKeyListener}
import _root_.android.widget.AdapterView.OnItemClickListener
import _root_.android.text.{SpannableStringBuilder, TextWatcher, Editable, InputType}
import _root_.android.graphics.Typeface
import _root_.android.util.TypedValue
import scala.util.Random
import _root_.android.util.Log

class BeginActivity extends Activity {
  val context				= BeginActivity.this

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.begin)

    lazy val inflater: LayoutInflater	= context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
    lazy val dialogView			= inflater.inflate(R.layout.name_prompt,
							   findViewById(R.layout.begin).asInstanceOf[ViewGroup])
    lazy val builder			= new AlertDialog.Builder(context)
    lazy val npListener			= new NamePromptListener(dialogView, this)

    val namePrompt = dialogView.findViewById(R.id.namePrompt).asInstanceOf[AutoCompleteTextView]
    val completions = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, (new Player(this)).getPlayerNames)
    namePrompt.setAdapter(completions)
  
    builder.setTitle("Multiplication!")
    builder.setView(dialogView)
    builder.setPositiveButton("Set", npListener)
    builder.setNegativeButton("Ignore", npListener)

    val dialog: AlertDialog = builder.create
    dialog.show
  }

  def goToGame(playerName: String) = {
    Log.i("goToGame", "dialog result = " + playerName)
    val intent = new Intent(context, classOf[GameActivity])
    intent.putExtra("player_name", playerName)
    startActivity(intent)
  }
}

class GameActivity extends Activity {
  private val random	= new Random
  private var equation	= new FormattedEquation
  private val player    = Player(GameActivity.this)

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    // The player_name will always be there, actually.
    val extras: Bundle = getIntent.getExtras
    player.setName(if (extras != null) extras.getString("player_name")
		   else "")
    player.newGame

    initializeUI
  }

  override def onConfigurationChanged(newConfig: Configuration) = {
    super.onConfigurationChanged(newConfig)
    setContentView(R.layout.main)
    initializeUI
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    val inflater: MenuInflater = getMenuInflater
    inflater.inflate(R.menu.game_menu, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    item.getItemId match {
      case R.id.menu_restartGame => {
	equation = new FormattedEquation
	player.newGame
	refreshDisplay()
      }
      case R.id.menu_myScores => { }
      case R.id.menu_allScores => { }
      case R.id.menu_hint => {
	equation.hint
	player.hint
	refreshDisplay()
      }
      case R.id.menu_solve => {
	equation.solve
	player.unsaveable
	refreshDisplay()
      }
      case R.id.menu_quit => this.finish
      case _ => { }
    } 
    false
  }

  private def refreshDisplay(status: String = getNeutralStatus) = {
    setStatus(status)
    clearFields
    setEquation
    setCompletions
  }

  private def initializeUI = {
    findViewById(R.id.title).asInstanceOf[TextView].setText("You are " + player.name.capitalize)
    setEquation
    setStatus(getNeutralStatus)
    setListeners
    setCompletions

    findViewById(R.id.digitGuess).asInstanceOf[EditText].setInputType(InputType.TYPE_NULL)
    findViewById(R.id.characterGuess).asInstanceOf[EditText].setInputType(InputType.TYPE_NULL)
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
	  player.mistake
	  getNeStatus
	}
	case Pair(c: Char, d: Int)	=> {
	  if (equation.solved) {
	    player.solved
	    getNeutralStatus
	  } else getYusStatus
	}
	case _				=> getNeutralStatus
      }
      case _ => getNeutralStatus
    }
    refreshDisplay(status)
  }

  private def gridClick(target: View) = {
    val c = target.asInstanceOf[TextView].getText.toString
    if (c.size > 0 && c(0).isLetter)
      findViewById(R.id.characterGuess).asInstanceOf[EditText].setText(c)
  } 

  // Only allow one character in the input fields.
  private def editableTextChanged(t: Editable) = {
    if (t.toString.size > 1) {
      t.replace(0, t.toString.size - 1, "")
    }
  }

  private def setStatus(status: String) =
    findViewById(R.id.statusLabel).asInstanceOf[TextView].setText(status + "\n" + "[ " + getResources.getQuantityString(R.plurals.mistakes, player.mistakes, player.mistakes.asInstanceOf[java.lang.Object]) + " ]\n" + "[ " + getResources.getQuantityString(R.plurals.hints, player.hints, player.hints.asInstanceOf[java.lang.Object]) + " ]")

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
