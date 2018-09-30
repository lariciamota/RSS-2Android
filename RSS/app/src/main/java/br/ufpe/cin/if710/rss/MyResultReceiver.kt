package br.ufpe.cin.if710.rss

import android.os.Bundle
import android.os.ResultReceiver


class MyResultReceiver(receiver: Receiver): ResultReceiver(null) {
    val receiver: Receiver? = receiver
    companion object {
        val INTENT_KEY = "myResultReceiver"
        val DATA_KEY = "dataKey"
    }

    interface Receiver {
        fun onReceiveResult(resultCode: Int, resultData: Bundle)

    }


    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        super.onReceiveResult(resultCode, resultData)
        receiver!!.onReceiveResult(resultCode, resultData!!)
    }
}