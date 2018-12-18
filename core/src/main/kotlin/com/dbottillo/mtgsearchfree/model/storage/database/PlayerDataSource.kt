package com.dbottillo.mtgsearchfree.model.storage.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.util.LOG

class PlayerDataSource(private val database: SQLiteDatabase) {

    val players: List<Player>
        get() {
            LOG.d("get players")
            val query = "SELECT * FROM $TABLE order by _ID ASC"
            LOG.query(query)
            val cursor = database.rawQuery(query, null)
            val players = ArrayList<Player>()
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    players.add(fromCursor(cursor))
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return players
        }

    fun savePlayer(player: Player): Long {
        LOG.d("saving " + player.toString())
        val values = ContentValues()
        values.put("_id", player.id)
        values.put("life", player.life)
        values.put("poison", player.poisonCount)
        values.put("name", player.name)
        return database.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun removePlayer(player: Player) {
        LOG.d("remove " + player.toString())
        val args = arrayOf(player.id.toString() + "")
        val query = "DELETE FROM $TABLE where _id=? "
        LOG.query(query, player.id.toString() + "")
        val cursor = database.rawQuery(query, args)
        cursor.moveToFirst()
        cursor.close()
    }

    fun fromCursor(cursor: Cursor): Player {
        return Player(
                id = cursor.getIntFromColumn("_id"),
                name = cursor.getStringFromColumn("name"),
                life = cursor.getIntFromColumn("life"),
                poisonCount = cursor.getIntFromColumn("poison"))
    }

    companion object {

        const val TABLE = "MTGPlayer"

        fun generateCreateTable(): String {
            val builder = StringBuilder("CREATE TABLE IF NOT EXISTS ")
            builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY, ")
            builder.add(name = "name", type = "TEXT")
            builder.add(name = "life", type = "INT")
            builder.add(name = "poison", type = "INT", last = true)
            builder.append(')')
            return builder.toString()
        }
    }
}
