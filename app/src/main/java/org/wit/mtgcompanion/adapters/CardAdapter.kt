package org.wit.mtgcompanion.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.wit.mtgcompanion.databinding.CardCardBinding
import org.wit.mtgcompanion.models.CardModel

interface CardListener {
    fun onCardClick(card: CardModel, position: Int)
}

class CardAdapter constructor(private var cards: List<CardModel>, private val listener: CardListener):
        RecyclerView.Adapter<CardAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int){
        val card = cards[holder.adapterPosition]
        holder.bind(card, listener)
    }

    override fun getItemCount(): Int = cards.size

    class MainHolder(private val binding: CardCardBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(card: CardModel, listener: CardListener) {
            binding.cardCardNameTxt.text = card.name
            binding.cardCardTypeTxt.text = card.type
            val powerToughnessString = "${card.power}/${card.toughness}"
            binding.cardCardPowerToughnessTxt.text = powerToughnessString
            val costString = "${card.neutral}/${card.white}/${card.black}/${card.red}/${card.blue}/${card.green}"
            binding.cardCardCostTxt.text = costString
            binding.cardCardDescriptionTxt.text = card.description
            Picasso.get().load(card.image).into(binding.cardCardArtImgView)
            binding.root.setOnClickListener {
                listener.onCardClick(card, adapterPosition)
            }
        }
    }
}