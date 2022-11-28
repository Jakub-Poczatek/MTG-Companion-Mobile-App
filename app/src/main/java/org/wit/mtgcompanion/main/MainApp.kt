package org.wit.mtgcompanion.main

import android.app.Application
import org.wit.mtgcompanion.models.CardJSONStore
import org.wit.mtgcompanion.models.CardMemStore
import org.wit.mtgcompanion.models.CardModel
import org.wit.mtgcompanion.models.CardStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp: Application() {

    lateinit var cards: CardStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        cards = CardJSONStore(applicationContext)
        //cards = CardMemStore()
        i("MTG Companion Started")
    }
}