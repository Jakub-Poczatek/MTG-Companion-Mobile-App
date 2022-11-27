package org.wit.mtgcompanion.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import org.wit.mtgcompanion.R
import org.wit.mtgcompanion.databinding.ActivityCardBinding
import org.wit.mtgcompanion.main.MainApp
import org.wit.mtgcompanion.models.CardModel
import timber.log.Timber
import timber.log.Timber.i

class CardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardBinding
    lateinit var app: MainApp
    var card = CardModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
        i("Card Activity started...")

        binding.addCardBtn.setOnClickListener(){
            card.name = binding.cardNameTxt.text.toString()
            card.type = binding.cardTypeSpinner.selectedItem.toString()
            if(binding.cardPowerNumTxt.text.isNotEmpty()){
                card.power = binding.cardPowerNumTxt.text.toString().toShort()
            }

            if(card.name.isNotEmpty()) {
                app.cards.add(card.copy())
                i("add button pressed: $card.name \t $card.type \t $card.power")
                for (i in app!!.cards.indices) {
                    i("Card[$i]: ${this.app.cards[i]}")
                }
                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(it, "Please Enter a card name", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}