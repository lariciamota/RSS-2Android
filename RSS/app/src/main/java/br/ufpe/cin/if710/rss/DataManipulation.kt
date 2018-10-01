package br.ufpe.cin.if710.rss

import android.content.Context

class DataManipulation (context: Context){
    val db = SQLiteRSSHelper.getInstance(context)
    fun insert(items: List<ItemRSS>): Boolean{
        var houveMudanca: Boolean = false
        for(i in items.indices){
            var a = db.insertItem(items[i]) //inserindo item no banco de dados
            if (a != 0.toLong()){
                houveMudanca = true
            }
        }
        return houveMudanca
    }

    fun insertItem(item: ItemRSS){
        db.insertItem(item) //inserindo item no banco de dados
    }

    fun getItems(): List<ItemRSS>{
        var cursor = db.items
        var list = arrayListOf<ItemRSS>()
        if(cursor!!.moveToFirst()){
            cursor.moveToFirst()
            while (!cursor.isAfterLast){
                list.add(db.getItemRSS(cursor.getString(1))!!)
                cursor.moveToNext()
            }
        }
        return list
    }

    fun markAsRead(item: ItemRSS){
        db.markAsRead(item.link) //marcar noticia como lida ao clicar nela
    }
}