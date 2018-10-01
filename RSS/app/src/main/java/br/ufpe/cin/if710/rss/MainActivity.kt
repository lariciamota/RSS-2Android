package br.ufpe.cin.if710.rss

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.os.ResultReceiver
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import br.ufpe.cin.if710.rss.ParserRSS.parse
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener, MyResultReceiver.Receiver {
    private val intentFilter = IntentFilter("br.ufpe.cin.if710.rss")
    private val receiver = MyBroadcastReceiver()

    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        val data = resultData.get(MyResultReceiver.DATA_KEY) as List<ItemRSS>
        val adapter = RecyclerCustomAdapter(data) //personalizado para mostrar titulo e data
        doAsync {
            uiThread{
                conteudoRSS!!.adapter = adapter //colocando o conteudo de fato na view
            }
        }

    }

    private fun configureReceiver() {
        registerReceiver(receiver, intentFilter);
        Toast.makeText(this, "Registrando...", Toast.LENGTH_SHORT).show()
    }

    private fun deactivateReceiver() {
        unregisterReceiver(receiver);
        Toast.makeText(this, "Removendo registro...", Toast.LENGTH_SHORT).show()
    }

    var preference: Prefs? = null

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

        preference = Prefs(this)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStart() {
        super.onStart()
        configureReceiver()
        Log.i("xablau", "ONSTART")
        try {
            doAsync {
                val downloadServiceIntent = Intent(applicationContext, DownloadFeedService::class.java)
                downloadServiceIntent.putExtra("url", preference!!.rssFeed)
//                val myreceiver = MyResultReceiver(this@MainActivity)
//                downloadServiceIntent.putExtra(MyResultReceiver.INTENT_KEY, myreceiver)
                startService(downloadServiceIntent)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onRestart() {
        super.onRestart()
        configureReceiver()
    }

    override fun onResume() {
        super.onResume()
        configureReceiver()
    }

//    override fun onPause() {
//        deactivateReceiver()
//        super.onPause()
//    }

    override fun onStop() {
        deactivateReceiver()
        super.onStop()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, p1: String?) {
        val url = sharedPreferences!!.getString("rssfeed", "")
        preference!!.rssFeed = url
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_pref, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        if (id == R.id.action_prefs) {
            startActivity(Intent(applicationContext,PrefsFragmentActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)

    }

}