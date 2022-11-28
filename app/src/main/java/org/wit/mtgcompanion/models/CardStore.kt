package org.wit.mtgcompanion.models

interface CardStore {
    fun findAll(): List<CardModel>
    fun create(card: CardModel)
    fun update(card: CardModel)
    fun delete(card: CardModel)
}