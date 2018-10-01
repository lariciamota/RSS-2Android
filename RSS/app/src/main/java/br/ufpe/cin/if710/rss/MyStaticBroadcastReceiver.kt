package br.ufpe.cin.if710.rss

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast

class MyStaticBroadcastReceiver : BroadcastReceiver() {
    // elementos de texto
    private val contentTitle = "Novas noticias"
    private val contentText = "App de feed de noticias esta com novas noticias!"

    // Actions (intents a serem transmitidos)
    private var mNotificationIntent: Intent? = null
    private var mContentIntent: PendingIntent? = null

    internal var mNotifyManager: NotificationManager? = null

    private val NOTIFICATION_CHANNEL_ID = "br.ufpe.cin.if710.rss.notification"
    private val MY_NOTIFICATION_ID = 1

    override fun onReceive(context: Context, intent: Intent) {
        val isForeground = intent.getBooleanExtra("foreground", false)
        if (!isForeground){
            mNotifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            Log.i("xablau", "ONNOTIFICATION")
            //notificacao
            mNotificationIntent = Intent(context, MainActivity::class.java)
            //flag activity new task
            mContentIntent = PendingIntent.getActivity(context, 0, mNotificationIntent, 0)

            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setAutoCancel(true)
                    .setContentIntent(mContentIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()

            // passa notificacao para o notification manager
            mNotifyManager!!.notify(MY_NOTIFICATION_ID, notification)
            Log.i("xablau", "ONNOTIFICATION dps de notificar")
            Toast.makeText(context, "INTENT Recebido pelo StaticReceiver", Toast.LENGTH_LONG).show()

        }
    }
}
