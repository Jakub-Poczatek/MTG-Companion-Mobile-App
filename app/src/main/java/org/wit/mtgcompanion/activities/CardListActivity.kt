package org.wit.mtgcompanion.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import org.wit.mtgcompanion.R
import org.wit.mtgcompanion.adapters.CardAdapter
import org.wit.mtgcompanion.adapters.CardListener
import org.wit.mtgcompanion.databinding.ActivityCardListBinding
import org.wit.mtgcompanion.main.MainApp
import org.wit.mtgcompanion.models.CardModel
import timber.log.Timber.i

class CardListActivity: AppCompatActivity(), CardListener{

    private lateinit var binding: ActivityCardListBinding
    lateinit var app: MainApp
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
        val layoutManager = GridLayoutManager(this, 2)
        binding.cardListRecycleView.layoutManager = layoutManager
        binding.cardListRecycleView.adapter = CardAdapter(app.cards.findAll(), this)
        binding.menuToolbar.title = title
        setSupportActionBar(binding.menuToolbar)

        binding.menuFloatingAddButton.setOnClickListener{
            binding.cardListRecycleView.adapter = CardAdapter(app.cards.findAll(), this)
            val launcherIntent = Intent(this, CardActivity::class.java)
            getResult.launch(launcherIntent)
        }

        binding.cardListSearchTxt.addTextChangedListener {
            var query = binding.cardListSearchTxt.text.toString().lowercase().trim()
            var cards = app.cards.findAll()
            var filteredCards = ArrayList<CardModel>()
            if(query.isEmpty()) {
                binding.cardListRecycleView.adapter = CardAdapter(app.cards.findAll(), this)
            } else if(binding.cardListSearchBySpinner.selectedItem.toString() == "name") {
                for (card in cards) {
                    if (query in card.name.lowercase().trim())
                        filteredCards.add(card)
                }
                binding.cardListRecycleView.adapter = CardAdapter(filteredCards, this)
            } else if (binding.cardListSearchBySpinner.selectedItem.toString() == "type") {
                for (card in cards) {
                    if (query in card.type.lowercase().trim())
                        filteredCards.add(card)
                }
                binding.cardListRecycleView.adapter = CardAdapter(filteredCards, this)
            } else {
                binding.cardListRecycleView.adapter = CardAdapter(app.cards.findAll(), this)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            R.id.menu_goto_map -> {
                startActivity(Intent(this, CardsMapActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
            registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK) {
            binding.cardListSearchTxt.setText(R.string.testSearchQuery)
            binding.cardListSearchTxt.text.clear()
            (binding.cardListRecycleView.adapter)?.notifyItemRangeChanged(0, app.cards.findAll().size)
        }
    }

    override fun onCardClick(card: CardModel, pos: Int) {
        binding.cardListSearchTxt.text.clear()
        val launcherIntent = Intent(this, CardActivity::class.java)
        launcherIntent.putExtra("card_edit", card)
        position = pos
        getClickResult.launch(launcherIntent)
    }

    private val getClickResult =
            registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK) {
            binding.cardListSearchTxt.setText(R.string.testSearchQuery)
            binding.cardListSearchTxt.text.clear()
            (binding.cardListRecycleView.adapter)?.notifyItemRangeChanged(0, app.cards.findAll().size)
        } else
            if(it.resultCode == 99) {
                binding.cardListSearchTxt.setText(R.string.testSearchQuery)
                binding.cardListSearchTxt.text.clear()
                (binding.cardListRecycleView.adapter)?.notifyItemRemoved(position)
            }
    }
}

