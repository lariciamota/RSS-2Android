package br.ufpe.cin.if710.rss

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import br.ufpe.cin.if710.rss.ParserRSS.parse
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : Activity() {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private var RSS_FEED: String? = null

    //OUTROS LINKS PARA TESTAR...
    //http://rss.cnn.com/rss/edition.rss
    //http://pox.globo.com/rss/g1/brasil/
    //http://pox.globo.com/rss/g1/ciencia-e-saude/
    //http://pox.globo.com/rss/g1/tecnologia/

    private lateinit var linearLayoutManager: LinearLayoutManager

    //use ListView ao invés de TextView - deixe o atributo com o mesmo nome
    private var conteudoRSS: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RSS_FEED = getString(R.string.rssfeed) //url vem do arquivo de strings
        conteudoRSS = findViewById(R.id.conteudoRSS)

        linearLayoutManager = LinearLayoutManager(this)
        conteudoRSS?.layoutManager = linearLayoutManager //tipo de layout que será colocado o conteúdo do recycler
    }

    override fun onStart() {
        super.onStart()
        try {
            doAsync {
                val feedXML = parse(getRssFeed(RSS_FEED!!)) //RSS passando pelo parser, retorno é uma lista de ItemRSS
                val adapter = RecyclerCustomAdapter(feedXML) //personalizado para mostrar titulo e data
                uiThread {
                    conteudoRSS!!.adapter = adapter //colocando o conteudo de fato na view (ja que mexe em ui deve ser feito na thread principal)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //Opcional - pesquise outros meios de obter arquivos da internet - bibliotecas, etc.
    @Throws(IOException::class)
    private fun getRssFeed(feed: String): String {
        var `in`: InputStream? = null
        var rssFeed = ""
        try {
            val url = URL(feed)
            val conn = url.openConnection() as HttpURLConnection
            `in` = conn.inputStream
            val out = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var count: Int
            count = `in`!!.read(buffer)
            while (count != -1) {
                out.write(buffer, 0, count)
                count = `in`!!.read(buffer)
            }
            val response = out.toByteArray()
            rssFeed = String(response, charset("UTF-8"))
        } finally {
            `in`?.close()
        }
        return rssFeed
    }
}