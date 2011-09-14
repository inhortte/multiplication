package polaris.games.multiplication

import _root_.android.content.ContentProvider
import _root_.android.provider.BaseColumns
import _root_.android.net.Uri

object EquationProviderMetaData {
  val AUTHORITY: String			= "polaris.games.multiplication.EquationProvider"
  val DATABASE_NAME: String		= ""
  val DATABASE_VERSION: Int		= 0
  val EQUATION_TABLE_NAME: String	= "equation"

  object EquationTableMetaData extends BaseColumns {
    val TABLE_NAME: String = "equation"
    val CONTENT_URI: Uri = Uri.parse("content://" + AUTHORITY + "/equation")
    val CONTENT_TYPE: String = ""
    val CONTENT_ITEM_TYPE: String = ""
  }
}

trait EquationProvider extends ContentProvider {
  
}
