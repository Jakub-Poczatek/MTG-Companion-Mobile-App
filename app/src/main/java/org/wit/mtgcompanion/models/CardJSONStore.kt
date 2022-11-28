package org.wit.mtgcompanion.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.wit.mtgcompanion.helpers.exists
import org.wit.mtgcompanion.helpers.read
import org.wit.mtgcompanion.helpers.write
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

const val JSON_FILE = "cards.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
        .registerTypeAdapter(Uri::class.java, UriParser())
        .create()
val listType: Type = object: TypeToken<ArrayList<CardModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class CardJSONStore(private val context: Context): CardStore {

    var cards = mutableListOf<CardModel>()

    init{
        if(exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<CardModel> {
        logAll()
        return cards
    }

    override fun create(card: CardModel){
        card.id = generateRandomId()
        cards.add(card)
        serialize()
    }

    override fun update(card: CardModel){
        //
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(cards, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize(){
        val jsonString = read(context, JSON_FILE)
        cards = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll(){
        cards.forEach{ Timber.i("$it")}
    }
}

class UriParser: JsonDeserializer<Uri>, JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}