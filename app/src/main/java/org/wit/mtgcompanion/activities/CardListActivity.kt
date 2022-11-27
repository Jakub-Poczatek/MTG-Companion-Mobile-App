package org.wit.mtgcompanion.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.wit.mtgcompanion.R
import org.wit.mtgcompanion.databinding.ActivityCardListBinding
import org.wit.mtgcompanion.databinding.CardCardBinding
import org.wit.mtgcompanion.main.MainApp
import org.wit.mtgcompanion.models.CardModel

class CardListActivity: AppCompatActivity(){

    private lateinit var binding: ActivityCardListBinding
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = CardAdapter(app.cards)

        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            R.id.item_add -> {
                val launcherIntent = Intent(this, CardActivity::class.java)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
            if(it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.notifyItemRangeChanged(0, app.cards.size)
            }
        }
}

class CardAdapter constructor(private var cards: List<CardModel>):
        RecyclerView.Adapter<CardAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int){
        val card = cards[holder.adapterPosition]
        holder.bind(card)
    }

    override fun getItemCount(): Int = cards.size

    class MainHolder(private val binding: CardCardBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(card: CardModel) {
            binding.cardCardNameTxt.text = card.name
            binding.cardCardTypeTxt.text = card.type
            var powerToughnessString = "${card.power}/${card.toughness}"
            binding.cardCardPowerToughnessTxt.text = powerToughnessString
            var costString = "${card.neutral}/${card.white}/${card.black}/${card.red}/${card.blue}/${card.green}"
            binding.cardCardCostTxt.text = costString
            binding.cardCardDescriptionTxt.text = card.description
        }
    }
}