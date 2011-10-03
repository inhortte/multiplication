package polaris.games.multiplication

import _root_.android.widget.AutoCompleteTextView
import _root_.android.graphics.Rect
import _root_.android.content.Context
import _root_.android.util.AttributeSet
import _root_.android.view.MotionEvent

class ImmediateCompleteTextView(context: Context, attrs: AttributeSet, defStyle: Int) extends AutoCompleteTextView(context, attrs, defStyle) {
  def this(context: Context, attrs: AttributeSet) = this(context, attrs, android.R.attr.autoCompleteTextViewStyle)
  def this(context: Context) = this(context, Nil.asInstanceOf[AttributeSet], android.R.attr.autoCompleteTextViewStyle)

  var weHaveFocus = false

  override def onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect) = {
    if (gainFocus) showDropDown
    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    showDropDown
    true
  }

  override def enoughToFilter(): Boolean = {
    hasFocus
  }
}
