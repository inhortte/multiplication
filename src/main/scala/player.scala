package polaris.games.multiplication

import _root_.android.content.{Context, ContentValues}
import _root_.android.util.Log
import _root_.android.database.Cursor
import _root_.android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import java.util.Date

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
    db.close
  }
  override def onUpgrade(db: SQLiteDatabase, oldVer: Int, newVer: Int) = {
    Log.i("onUpdate", "updating the database")
    Log.i("onUpdate", "version " + oldVer + " to " +
          newVer + " and all the previous data gets nuked in the process")
    db.execSQL("drop table if exists " + playerTable)
    db.execSQL("drop table if exists " + scoreTable)
    onCreate(db)
  }

  def findOrCreatePlayer(name: String): Long = {
    val rDb: SQLiteDatabase = this.getReadableDatabase
    val c: Cursor = rDb.query(playerTable, playerCols, playerName + " = ?", Array(name), null, null, null)
    if (c.getCount < 1) {
      c.close
      // rDb.close
      addPlayer(name)
    } else { 
      val id = c.getLong(c.getColumnIndex(playerId))
      c.close
      // rDb.close
      id
    }
  }

  def addPlayer(name: String): Long = {
    val db: SQLiteDatabase = this.getWritableDatabase
    val cv = new ContentValues
    cv.put(playerName, name)
    val id = db.insert(playerTable, null, cv)
    db.close
    id
  }

  def renamePlayer(oldName: String, newName: String): Int = {
    val db: SQLiteDatabase = this.getWritableDatabase
    val cv = new ContentValues
    cv.put(playerName, newName)
    val res = db.update(playerTable, cv, playerName + " = ?", Array(oldName))
    db.close
    res
  }

  def newScore(p: Player) = {
    val db: SQLiteDatabase = this.getWritableDatabase
    val cv = new ContentValues
    cv.put(scorePlayerId, p.id.asInstanceOf[java.lang.Long])
    cv.put(scoreTime, p.elapsed.asInstanceOf[java.lang.Long])
    cv.put(scoreMistakes, p.mistakes.asInstanceOf[java.lang.Long])
    cv.put(scoreTimestamp, ((new Date).getTime).asInstanceOf[java.lang.Long])
    db.insert(scoreTable, null, cv)
    db.close
  }

  // I have encountered strange problems with this method.
  // Look at how I isolated 'index' instead of just doing
  // c.getString(c.getColumnName(playerName). I was being told
  // that the index was -1. I cannot verify, but suppose that getCount
  // is not returning the correct value.
  def findAllNames: Array[String] = {
    val rDb: SQLiteDatabase = this.getReadableDatabase
    val order: String = playerName + " asc"
    val c: Cursor = rDb.query(playerTable, playerCols, null, null, null, null, order)
    c.moveToFirst
    val res: Array[String] = if (c.getCount > 0) {
      var names: List[String] = List()
      var index = -1
      do {
	index = c.getColumnIndex(playerName)
	Log.i("findAllNames", "index = " + index)
	if (index != -1)
	  names ::= c.getString(index)
      } while (c.moveToNext && index != -1)
      names.toArray
    } else Array[String]()
    c.close
    // rDb.close
    res
  }
}

object DBHelper {
  def apply(c: Context) = new DBHelper(c)
}

class Player(context: Context) {
  lazy val dbHelper = DBHelper(context)
  
  private var _name: String	= "nikdo"
  private var _id: Long		= 0
  var mistakes: Int		= 0
  var hints: Int = 0
  var elapsed: Long		= 0
  private var saveable		= true
  private var startTime: Long	= (new Date).getTime
  private var endTime: Long	= (new Date).getTime
  private var gamesPlayed: Int	= 0

  def mistake = mistakes += 1
  def hint = hints += 1
  def newGame = {
    gamesPlayed += 1
    mistakes = 0
    hints = 0
    startTime = (new Date).getTime
    elapsed = 0
    saveable = true
  }
  def unsaveable = saveable = false

  def id: Long = _id
  def name: String = _name
  def setName(n: String) = {
    _id = dbHelper.findOrCreatePlayer(n.toLowerCase)
    _name = n.toLowerCase
  }

  // Solved gets called when a game is finished. The number of
  // seconds is passed in.
  def solved = {
    endTime = (new Date).getTime
    elapsed = (endTime - startTime) / 1000
    // Save the score only if this is a real player.
    if (name != "nikdo" && name != "" && saveable) dbHelper.newScore(this)
  }

  def getPlayerNames: Array[String] =  dbHelper.findAllNames
}

object Player {
  def apply(c: Context) = new Player(c)
}
