package polaris.games.multiplication

import _root_.android.widget.{ArrayAdapter, TextView}
import _root_.android.view.{View, ViewGroup}
import _root_.android.content.Context
import _root_.android.graphics.Color
import _root_.android.util.Log

class ArrayColorAdapter(context: Context, resource: Int, objects: java.util.List[String]) extends ArrayAdapter(context, resource, objects) {
  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val view = super.getView(position, convertView, parent)
    if (view.asInstanceOf[TextView].getText.toString()(0).isDigit)
      view.asInstanceOf[TextView].setTextColor(Color.rgb(0,0,255))
    view
  }
}
