package br.ufpe.cin.if710.rss

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class SQLiteRSSHelper private constructor(internal var c: Context) : SQLiteOpenHelper(c, DATABASE_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        //Executa o comando de criação de tabela
        db.execSQL(CREATE_DB_COMMAND)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //estamos ignorando esta possibilidade no momento
        throw RuntimeException("nao se aplica")
    }

    //Implemente a manipulação de dados nos métodos auxiliares para não ficar criando consultas manualmente
    fun insertItem(item: ItemRSS): Long {
        return insertItem(item.title, item.pubDate, item.description, item.link)
    }

    fun insertItem(title: String, pubDate: String, description: String, link: String): Long {
        var ret: Long? = null
        val values = ContentValues()
        values.put(ITEM_TITLE, title)
        values.put(ITEM_DATE, pubDate)
        values.put(ITEM_DESC, description)
        values.put(ITEM_LINK, link)
        val db = this.writableDatabase

        if (getItemRSS(link) == null){
            values.put(ITEM_UNREAD, true)
            ret = db.insert(DATABASE_TABLE, null, values)
        } else {
            db.update(DATABASE_TABLE, values, "$ITEM_LINK = ?", arrayOf(link))
            ret = 0
        }
        db.close()
        Log.i("xablau", " retorno insert"+ret.toString())
        return ret
    }

    @Throws(SQLException::class)
    fun getItemRSS(link: String): ItemRSS? {
        val query = "SELECT $ITEM_TITLE, $ITEM_LINK, $ITEM_DATE, $ITEM_DESC FROM $DATABASE_TABLE WHERE $ITEM_LINK = ?"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, arrayOf(link))
        var item: ItemRSS? = null

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val title = cursor.getString(0)
            val link = cursor.getString(1)
            val date = cursor.getString(2)
            val description = cursor.getString(3)

            item = ItemRSS(title, link, date, description)

            cursor.close()
        }
        return item
    }

    val items: Cursor?
        @Throws(SQLException::class)
        get() {
            return this.writableDatabase.rawQuery("SELECT $ITEM_TITLE, $ITEM_LINK, $ITEM_DATE, $ITEM_DESC FROM $DATABASE_TABLE WHERE $ITEM_UNREAD = ?", arrayOf(true.toString()))
        }

    fun markAsUnread(link: String): Boolean {
        val values = ContentValues()
        values.put(ITEM_UNREAD, true)
        val db = this.writableDatabase
        val ret = db.update(DATABASE_TABLE, values, "$ITEM_LINK = ?", arrayOf(link))
        db.close()
        return false
    }

    fun markAsRead(link: String): Boolean {
        val values = ContentValues()
        values.put(ITEM_UNREAD, false)
        val db = this.writableDatabase
        val ret = db.update(DATABASE_TABLE, values, "$ITEM_LINK = ?", arrayOf(link))
        Log.i("xablau", "lido "+ret.toString())
        db.close()
        return false
    }

    companion object {
        //Nome do Banco de Dados
        private val DATABASE_NAME = "rss"
        //Nome da tabela do Banco a ser usada
        val DATABASE_TABLE = "items"
        //Versão atual do banco
        private val DB_VERSION = 1

        private var db: SQLiteRSSHelper? = null

        //Definindo Singleton
        fun getInstance(c: Context): SQLiteRSSHelper {
            if (db == null) {
                db = SQLiteRSSHelper(c.applicationContext)
            }
            return db as SQLiteRSSHelper
        }

        //Definindo constantes que representam os campos do banco de dados
        val ITEM_ROWID = RssProviderContract._ID
        val ITEM_TITLE = RssProviderContract.TITLE
        val ITEM_DATE = RssProviderContract.DATE
        val ITEM_DESC = RssProviderContract.DESCRIPTION
        val ITEM_LINK = RssProviderContract.LINK
        val ITEM_UNREAD = RssProviderContract.UNREAD

        //Definindo constante que representa um array com todos os campos
        val columns = arrayOf(ITEM_ROWID, ITEM_TITLE, ITEM_DATE, ITEM_DESC, ITEM_LINK, ITEM_UNREAD)

        //Definindo constante que representa o comando de criação da tabela no banco de dados
        private val CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
                ITEM_ROWID + " integer primary key autoincrement, " +
                ITEM_TITLE + " text not null, " +
                ITEM_DATE + " text not null, " +
                ITEM_DESC + " text not null, " +
                ITEM_LINK + " text not null, " +
                ITEM_UNREAD + " boolean not null);"
    }

}
