package br.ufpe.cin.if710.rss

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyStaticBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //notificacao
        Toast.makeText(context, "INTENT Recebido pelo StaticReceiver", Toast.LENGTH_LONG).show()
    }
}
