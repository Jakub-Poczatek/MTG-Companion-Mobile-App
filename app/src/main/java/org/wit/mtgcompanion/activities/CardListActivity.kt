package org.wit.mtgcompanion.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import org.wit.mtgcompanion.R
import org.wit.mtgcompanion.adapters.CardAdapter
import org.wit.mtgcompanion.adapters.CardListener
import org.wit.mtgcompanion.databinding.ActivityCardListBinding
import org.wit.mtgcompanion.main.MainApp
import org.wit.mtgcompanion.models.CardModel

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
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = CardAdapter(app.cards.findAll(), this)

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
            (binding.recyclerView.adapter)?.notifyItemRangeChanged(0, app.cards.findAll().size)
        }
    }

    override fun onCardClick(card: CardModel, pos: Int) {
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
            (binding.recyclerView.adapter)?.notifyItemRangeChanged(0, app.cards.findAll().size)
        } else
            if(it.resultCode == 99)
                (binding.recyclerView.adapter)?.notifyItemRemoved(position)
    }
}

