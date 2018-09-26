package br.ufpe.cin.if710.rss

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView


class RecyclerCustomAdapter(private val items: List<ItemRSS>) : RecyclerView.Adapter<RecyclerCustomAdapter.MyViewHolder>()  {
    class MyViewHolder(val view: LinearLayout) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerCustomAdapter.MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.itemlista, parent, false)  as LinearLayout //como deverá ser a view do adapter personalizado
        return MyViewHolder(linearLayout)
    }

    override fun getItemCount(): Int {
        return items.size //quantidade de itens para poder ser feita a iteracao sobre esse conteudo
    }

    override fun onBindViewHolder(holder: RecyclerCustomAdapter.MyViewHolder, position: Int) {
        var txtViewTitle: TextView = holder.view.getChildAt(0) as TextView //a textview onde deverá ser colocado o título
        var txtViewDate: TextView = holder.view.getChildAt(1) as TextView //a textview onde deverá ser colocada a data de pub
        txtViewTitle.text = items[position].title //especificando qual texto da lista deve ser colocado na textview
        txtViewTitle.setOnClickListener {
            web_page_open(items[position].link, holder.view.context) //ao clicar no título deve abrir o navegador com o link da noticia
        }
        txtViewDate.text = items[position].pubDate //especificando qual texto da lista deve ser colocado na textview
    }

    fun web_page_open(urls: String, mContext : Context) { //método que lança a abertura de página em navegador
        val webpage = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, webpage)
        mContext.startActivity(intents)
    }

}

