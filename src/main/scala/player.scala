package polaris.games.multiplication

import _root_.android.content.{Context, ContentValues}
import _root_.android.util.Log
import _root_.android.database.Cursor
import _root_.android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}

class DBHelper(context: Context, val dbName: String = "multiplicationDB.db") extends SQLiteOpenHelper(context, dbName, null, 1) {

  // These should probably be elsewhere (metadata?).
  val playerTable: String = "players"
  val playerId: String = "_id"
  val playerName: String = "name"
  val scoreTable: String = "scores"
  val scoreId: String = "_id"
  val scorePlayerId: String = "player_id"
  val scoreTime: String = "time"
  val scoreMistakes: String = "mistakes"
  val scoreTimestamp: String = "timestamp"

  val playerCols = Array(playerName)
  
  override def onCreate(db: SQLiteDatabase) = {
    Log.i("onCreate", "creating players table")
    db.execSQL("create table " + playerTable + " (" +
               playerId + " integer primary key, " +
               playerName + " text" +
               ");")
    Log.i("onCreate", "creating scores table")
    db.execSQL("create table " + scoreTable + " (" +
               scoreId + " integer primary key, " +
               scorePlayerId + " integer references player(_id) not null, " +
               scoreTime + " integer, " +
               scoreMistakes + " integer, " +
               scoreTimestamp + " integer" +
               ");")
  }
  override def onUpgrade(db: SQLiteDatabase, oldVer: Int, newVer: Int) = {
    Log.i("onUpdate", "updating the database")
    Log.i("onUpdate", "version " + oldVer + " to " +
          newVer + " and all the previous data gets nuked in the process")
    db.execSQL("drop table if exists " + playerTable)
    db.execSQL("drop table if exists " + scoreTable)
    onCreate(db)
  }

  def findOrCreatePlayer(name: String) = {
    val rDb: SQLiteDatabase = this.getReadableDatabase
    val c: Cursor = rDb.query(playerTable, playerCols, playerName + " = ?", Array(name), null, null, null)
    if (c.getCount < 1) addPlayer(name)
  }

  def addPlayer(name: String) = {
    val db: SQLiteDatabase = this.getWritableDatabase
    val cv = new ContentValues
    cv.put(playerName, name)
    db.insert(playerTable, null, cv)
    db.close
  }

  def renamePlayer(oldName: String, newName: String): Int = {
    val db: SQLiteDatabase = this.getWritableDatabase
    val cv = new ContentValues
    cv.put(playerName, newName)
    db.update(playerTable, cv, playerName + " = ?", Array(oldName))
  }

  def findAllNames: List[String] = {
    val rDb: SQLiteDatabase = this.getReadableDatabase
    val order: String = playerName + " asc"
    val c: Cursor = rDb.query(playerTable, playerCols, null, null, null, null, order)
    if (c.getCount > 0) {
      var names: List[String] = List()
      do {
	names ::= c.getString(c.getColumnIndex(playerName))
      } while (c.moveToNext)
      names
    } else List[String]()
  }
}

object DBHelper {
  def apply(c: Context) = new DBHelper(c)
}

class Player(context: Context) {
  lazy val dbHelper = DBHelper(context)
  
  private var _name: String = "nikdo"
  var mistakes: Int = 0
  private var gamesPlayed: Int = 0

  def mistake = mistakes += 1
  def newGame = {
    gamesPlayed += 1
  }

  def name: String = _name
  def setName(n: String) = {
    dbHelper.findOrCreatePlayer(n.toLowerCase)
    _name = n.toLowerCase
  }

  def getPlayerNames: List[String] =  dbHelper.findAllNames
}

object Player {
  def apply(c: Context) = new Player(c)
}
