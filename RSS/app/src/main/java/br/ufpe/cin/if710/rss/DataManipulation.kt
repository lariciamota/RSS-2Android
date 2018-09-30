package br.ufpe.cin.if710.rss

import android.content.Context

class DataManipulation (context: Context){
    val db = SQLiteRSSHelper.getInstance(context)
    fun insert(items: List<ItemRSS>){
        for(i in items.indices){
            db.insertItem(items[i]) //inserindo item no banco de dados
        }
    }

    fun insertItem(item: ItemRSS){
        db.insertItem(item) //inserindo item no banco de dados
    }

    fun markAsRead(item: ItemRSS){
        db.markAsRead(item.link) //marcar noticia como lida ao clicar nela
    }
}