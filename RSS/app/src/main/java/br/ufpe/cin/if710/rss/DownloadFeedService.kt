package br.ufpe.cin.if710.rss

import android.app.IntentService
import android.content.Intent
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadFeedService: IntentService("DownloadFeedService") {

    override fun onHandleIntent(intent: Intent?) {
        try {
            val feed = getRssFeed(intent!!.getStringExtra("url"))
            val feedXML = ParserRSS.parse(feed) //RSS passando pelo parser, retorno é uma lista de ItemRSS
            val db = DataManipulation(applicationContext)
            val houveMudanca = db.insert(feedXML)
            val broadcastIntent = Intent("br.ufpe.cin.if710.rss")
            sendBroadcast(broadcastIntent)
            if (houveMudanca){
                val broadcastStaticIntent = Intent("br.ufpe.cin.if710.rss.static")
                broadcastStaticIntent.putExtra("foreground", intent.extras.getBoolean("foreground"))
                sendBroadcast(broadcastStaticIntent)
            }
        } catch (e2: IOException) {
            Log.e(javaClass.getName(), "Exception durante download", e2)
        }

    }

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
        } catch (e: Exception) {
            e.printStackTrace();
        } finally {
            `in`?.close()
        }
        return rssFeed
    }
}