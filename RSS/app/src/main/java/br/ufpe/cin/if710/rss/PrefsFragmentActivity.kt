package br.ufpe.cin.if710.rss

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceFragment

class PrefsFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Carrega um layout que contem um fragmento
        setContentView(R.layout.activity_prefs_fragment)

    }

    // Fragmento que mostra a preference com username
    class RSSPreferenceFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Carrega preferences a partir de um XML
            addPreferencesFromResource(R.xml.preferencias)

        }

        companion object {
            protected val TAG = "RSSPrefsFragment"
        }
    }
}
