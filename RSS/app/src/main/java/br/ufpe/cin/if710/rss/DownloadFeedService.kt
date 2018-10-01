package br.ufpe.cin.if710.rss

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.os.ResultReceiver
import android.util.Log
import org.jetbrains.anko.getStackTraceString
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class DownloadFeedService: IntentService("DownloadFeedService") {
    var myReceiver: ResultReceiver? = null

    override fun onHandleIntent(intent: Intent?) {
        try {
            Log.i("xablau", "ASYNC")
            val feed = getRssFeed(intent!!.getStringExtra("url"))
            Log.i("xablau", "DOWNLOADED FEED")
            val feedXML = ParserRSS.parse(feed) //RSS passando pelo parser, retorno é uma lista de ItemRSS
            Log.i("xablau", feedXML.size.toString())
            val db = DataManipulation(applicationContext)
            db.insert(feedXML)
//            val bundle = Bundle().apply {
//                putParcelableArrayList(MyResultReceiver.DATA_KEY, feedXML as ArrayList<out Parcelable>)
//            }
//            myReceiver = intent.getParcelableExtra(MyResultReceiver.INTENT_KEY)
//            myReceiver!!.send(1, bundle)
            val broadcastIntent = Intent("br.ufpe.cin.if710.rss")
            sendBroadcast(broadcastIntent)
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
            Log.i("xablau", rssFeed);
        } catch (e: Exception) {
            Log.i("xablau", "ERRO");
            e.printStackTrace();
            Log.i("xablau", e.getStackTraceString())
        } finally {
            Log.i("xablau", "FINALLY");
            `in`?.close()
        }
        return rssFeed
    }
}