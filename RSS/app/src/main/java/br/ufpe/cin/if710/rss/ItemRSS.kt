package br.ufpe.cin.if710.rss

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ItemRSS(val title: String, val link: String, val pubDate: String, val description: String): Parcelable {

    override fun toString(): String {
        return title
    }
}
