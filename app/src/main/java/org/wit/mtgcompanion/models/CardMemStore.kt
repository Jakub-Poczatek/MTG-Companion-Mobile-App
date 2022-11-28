package org.wit.mtgcompanion.models

import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class CardMemStore: CardStore {

    val cards = ArrayList<CardModel>()

    override fun findAll(): List<CardModel> {
        return cards
    }

    override fun create(card: CardModel){
        card.id = getId()
        cards.add(card)
        logAll()
    }

    override fun update(card: CardModel) {
        var foundCard: CardModel? = cards.find {c -> c.id == card.id}
        if(foundCard != null){
            foundCard.name = card.name
            foundCard.type = card.type
            foundCard.power = card.power
            foundCard.toughness = card.toughness
            foundCard.neutral = card.neutral
            foundCard.white = card.white
            foundCard.black = card.black
            foundCard.red = card.red
            foundCard.blue = card.blue
            foundCard.green = card.green
            foundCard.description = card.description
            foundCard.image = card.image
            logAll()
        }
    }

    fun logAll() {
        cards.forEach{
            i("$it")
        }
    }
}