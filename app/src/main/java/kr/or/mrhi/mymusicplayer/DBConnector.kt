package kr.or.mrhi.mymusicplayer

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBConnector (context: Context, dbName: String, version: Int) : SQLiteOpenHelper(context, dbName, null, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
            CREATE TABLE musicTBL(
            id TEXT PRIMARY KEY,
            title TEXT,
            artist TEXT,
            albumId TEXT,
            duration INT,
            likes INT,
            playList INT
            )
        """.trimIndent()
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val query = """
            DROP TABLE musicTAL
        """.trimIndent()
        db?.execSQL(query)
        this.onCreate(db)
    }

    fun insertMusic(music: Music) : Boolean {
        var flag = false
        val query = """
            INSERT INTO musicTBL (id, title, artist, albumId, duration, likes, playList) 
            VALUES ('${music.id}', '${music.title}', '${music.artist}', '${music.albumId}', ${music.duration}, ${music.likes}, ${music.playList})
        """.trimIndent()
        val db = this.writableDatabase

        try {
            db.execSQL(query)
            flag = true
        } catch (e: Exception) {
            Log.d("kr.or.mrhi", "insertMusic() ${e.printStackTrace()}")
            flag= false
        } finally {
            db.close()
        }
        return flag
    }

    fun selectMusicAll(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            SELECT * FROM musicTBL ORDER BY title
        """.trimIndent()
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val playList = cursor.getInt(6)
                    val music = Music(id, title, artist, albumId, duration, likes, playList)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("kr.or.mrhi", "selectMusicAll() ${e.printStackTrace()}")
        } finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }

    fun selectMusicClicked(type: String): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            SELECT * FROM musicTBL WHERE $type = 1
        """.trimIndent()
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val playList = cursor.getInt(6)
                    val music = Music(id, title, artist, albumId, duration, likes, playList)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("kr.or.mrhi", "selectMusicClicked() ${e.printStackTrace()}")
        } finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }

    fun selectSearchMusic(searchQuery: String?) : MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            SELECT * FROM musicTBL WHERE title like '%${searchQuery}%' OR artist like '%${searchQuery}%' 
        """.trimIndent()
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val playList = cursor.getInt(6)
                    val music = Music(id, title, artist, albumId, duration, likes, playList)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("kr.or.mrhi", "selectSearchMusic() ${e.printStackTrace()}")
        } finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }

    fun selectSearchMusicClicked(searchQuery: String?, type: String) : MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            SELECT * FROM musicTBL WHERE (title like '%${searchQuery}%' OR artist like '%${searchQuery}%') AND $type = 1
        """.trimIndent()
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val playList = cursor.getInt(6)
                    val music = Music(id, title, artist, albumId, duration, likes, playList)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("kr.or.mrhi", "selectSearchMusicClicked() ${e.printStackTrace()}")
        } finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }

    fun updateIcon(music: Music) : Boolean {
        var flag = false
        val query = """
            UPDATE musicTBL SET likes = ${music.likes}, playList = ${music.playList} WHERE id = '${music.id}'
        """.trimIndent()
        val db = this.writableDatabase

        try {
            db.execSQL(query)
            flag = true
        } catch (e: Exception) {
            Log.d("kr.or.mrhi", "updateIcon() ${e.printStackTrace()}")
            flag= false
        } finally {
            db.close()
        }
        return flag
    }
}