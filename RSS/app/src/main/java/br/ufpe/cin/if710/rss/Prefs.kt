package br.ufpe.cin.if710.rss

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    val PREFS_FILENAME = "br.ufpe.cin.if710.prefs"
    val PREF_RSSFEED = "rssfeed"
    var prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var rssFeed: String
        get() = prefs.getString(PREF_RSSFEED, "http://leopoldomt.com/if1001/g1brasil.xml")
        set(value) = prefs.edit().putString(PREF_RSSFEED, value).apply()
}