package org.wit.mtgcompanion.models

data class CardModel(
    var name: String = "",
    var type: String = "",
    var power: Short = 0,
    var toughness: Short = 0,
    var neutral: Short = 0,
    var white: Short = 0,
    var black: Short = 0,
    var red: Short = 0,
    var blue: Short = 0,
    var green: Short = 0,
    var description: String = ""
)
