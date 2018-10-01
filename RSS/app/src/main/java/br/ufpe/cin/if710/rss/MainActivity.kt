package br.ufpe.cin.if710.rss

import android.content.*
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException


class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {
    var db: DataManipulation? = null

    inner class MyBroadcastReceiver: BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            printFeed()
            Toast.makeText(context, "Broadcast Intent Detected.",
                    Toast.LENGTH_LONG).show()
        }
    }

    fun printFeed(){
        Log.i("xablau", "PRINT")
        val data = db!!.getItems()
        Log.i("xablau", "Data: $data")
        val adapter = RecyclerCustomAdapter(data)
        doAsync {
            uiThread {
                conteudoRSS!!.adapter = adapter
            }
        }
    }

    private val intentFilter = IntentFilter("br.ufpe.cin.if710.rss")
    private val receiver = MyBroadcastReceiver()

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

        db = DataManipulation(applicationContext)

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