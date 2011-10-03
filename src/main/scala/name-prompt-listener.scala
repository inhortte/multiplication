package polaris.games.multiplication

import _root_.android.content.DialogInterface
import _root_.android.content.DialogInterface.OnClickListener
import _root_.android.view.View
import _root_.android.widget.EditText

class NamePromptListener(val view: View, var result: String = "") extends OnClickListener {
  override def onClick(v: DialogInterface, buttonId: Int) =
    result = if (buttonId == DialogInterface.BUTTON_POSITIVE) getName
	     else ""

  private def getName: String = {
    view.findViewById(R.id.namePrompt).asInstanceOf[EditText].getText.toString
  }
}
