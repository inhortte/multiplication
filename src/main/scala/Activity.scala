package polaris.games.multiplication

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.view.ViewGroup.LayoutParams
import _root_.android.widget.LinearLayout
import _root_.android.widget.TextView
import _root_.android.graphics.Typeface
import _root_.android.util.TypedValue

class MainActivity extends Activity {
  val title = "Multiplication Marten"
  val otro = "YUS!"
  val eqTitle = "Pine martenish equation:"

  private lazy val titleContainer = new LinearLayout(this)
  private lazy val equationContainer = new LinearLayout(this)
  private lazy val parentContainer = new LinearLayout(this)

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    var equation = new FormattedEquation
    doTitleContainer
    doEquationContainer(equation)
    doParentContainer
    setContentView(parentContainer)
  }

  private def doTitleContainer = {
    titleContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						    LayoutParams.WRAP_CONTENT))
    titleContainer.setOrientation(LinearLayout.HORIZONTAL)
    val titleLabel = new TextView(this) {
      setText(title)
    }
    val otroLabel = new TextView(this) {
      setText(otro)
    }
    titleContainer.addView(titleLabel)
    titleContainer.addView(otroLabel)
  }

  private def doEquationContainer(equation: FormattedEquation) = {
    equationContainer.setLayoutParams(
      new LayoutParams(LayoutParams.FILL_PARENT,
		       LayoutParams.WRAP_CONTENT)
    )
    equationContainer.setOrientation(LinearLayout.VERTICAL)
    val equationLabel = new TextView(this) {
      setText(eqTitle)
    }
    val equationText = new TextView(this) {
      setTypeface(Typeface.MONOSPACE)
      setText(equation.formatted)
    }
    equationContainer.addView(equationLabel)
    equationContainer.addView(equationText)
  }

  private def doParentContainer = {
    parentContainer.setLayoutParams(
      new LayoutParams(LayoutParams.FILL_PARENT,
		       LayoutParams.FILL_PARENT)
    )
    parentContainer.setOrientation(LinearLayout.VERTICAL)
    parentContainer.addView(titleContainer)
    parentContainer.addView(equationContainer)
  }
}
