package org.wit.mtgcompanion.main

import android.app.Application
import org.wit.mtgcompanion.models.CardModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp: Application() {

    val cards = ArrayList<CardModel>()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("MTG Companion Started")
        cards.add(CardModel(
            "\"Lifetime\" Pass Holder",
            "Creature",
            2,
            1,
            black = 1,
            description = "\"Lifetime\" Pass Holder enters the battlefield tapped." +
            "\nWhen \"Lifetime\" Pass Holder dies, open an Attraction." +
            "\nWhenever you roll to visit your Attractions, if you roll a 6, you may return \"Lifetime\" Pass Holder from your graveyard to the battlefield."
            ))
        cards.add(CardModel(
            "Trespasser",
            "Creature",
            2,
            1,
            neutral = 1,
            blue = 1,
            description = "When this creature enters the battlefield, you may put a name sticker on it." +
            "\n{3xNeutral, 1xBlue}: This creature gets +1/+0 until end of turn for each name sticker on it. It can't be blocked this turn."
        ))
        cards.add(CardModel(
            "Abbey Griffin",
            "Creature",
            2,
            2,
            neutral = 3,
            white = 1,
            description = "Flying, vigilance"
        ))
    }
}