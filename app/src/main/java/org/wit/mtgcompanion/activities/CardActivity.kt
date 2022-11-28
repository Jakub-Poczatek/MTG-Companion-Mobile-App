package org.wit.mtgcompanion.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.mtgcompanion.R
import org.wit.mtgcompanion.databinding.ActivityCardBinding
import org.wit.mtgcompanion.helpers.showImagePicker
import org.wit.mtgcompanion.main.MainApp
import org.wit.mtgcompanion.models.CardModel
import timber.log.Timber.i

class CardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardBinding
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    lateinit var app: MainApp
    var card = CardModel()
    var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
        i("Card Activity started...")

        if(intent.hasExtra("card_edit")) {
            edit = true
            card = intent.extras?.getParcelable("card_edit")!!
            binding.addCardBtn.setText(R.string.save_card)
            binding.cardNameTxt.setText(card.name)
            binding.cardTypeSpinner.resources.getStringArray(R.array.cardTypes).forEachIndexed{ index, element ->
                if (element == card.type) {
                    binding.cardTypeSpinner.setSelection(index)
                }
            }
            binding.cardPowerNumTxt.setText(card.power.toString())
            binding.cardToughnessNumTxt.setText(card.toughness.toString())
            binding.cardNeuCostNumTxt.setText(card.neutral.toString())
            binding.cardWhtCostNumTxt.setText(card.white.toString())
            binding.cardBlkCostNumTxt.setText(card.black.toString())
            binding.cardRedCostNumTxt.setText(card.red.toString())
            binding.cardBluCostNumTxt.setText(card.blue.toString())
            binding.cardGrnCostNumTxt.setText(card.green.toString())
            binding.cardDescriptionMLTxt.setText(card.description)
            Picasso.get().load(card.image).into(binding.cardArtImgVw)
        }

        binding.addCardBtn.setOnClickListener(){
            defaultNumericFields()
            card.name = binding.cardNameTxt.text.toString()
            card.type = binding.cardTypeSpinner.selectedItem.toString()
            card.power = binding.cardPowerNumTxt.text.toString().toShort()
            card.toughness = binding.cardToughnessNumTxt.text.toString().toShort()
            card.neutral = binding.cardNeuCostNumTxt.text.toString().toShort()
            card.white = binding.cardWhtCostNumTxt.text.toString().toShort()
            card.black = binding.cardBlkCostNumTxt.text.toString().toShort()
            card.red = binding.cardRedCostNumTxt.text.toString().toShort()
            card.blue = binding.cardBluCostNumTxt.text.toString().toShort()
            card.green = binding.cardGrnCostNumTxt.text.toString().toShort()
            card.description = binding.cardDescriptionMLTxt.text.toString()

            if(card.name.isNotEmpty()) {
                if(edit) {
                    app.cards.update(card.copy())
                } else {
                    app.cards.create(card.copy())
                }
                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(it, R.string.noCardName, Snackbar.LENGTH_LONG).show()
            }
        }

        binding.toolbarAddCard.title = title
        setSupportActionBar(binding.toolbarAddCard)

        registerImagePickerCallback()
        binding.cardArtImgVw.setOnClickListener{
            showImagePicker(imageIntentLauncher)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun defaultNumericFields(){
        if(binding.cardPowerNumTxt.text.isEmpty())
            binding.cardPowerNumTxt.setText("0")
        if(binding.cardToughnessNumTxt.text.isEmpty())
            binding.cardToughnessNumTxt.setText(("0"))

        if(binding.cardNeuCostNumTxt.text.isEmpty())
            binding.cardNeuCostNumTxt.setText(("0"))
        if(binding.cardWhtCostNumTxt.text.isEmpty())
            binding.cardWhtCostNumTxt.setText(("0"))
        if(binding.cardBlkCostNumTxt.text.isEmpty())
            binding.cardBlkCostNumTxt.setText(("0"))
        if(binding.cardRedCostNumTxt.text.isEmpty())
            binding.cardRedCostNumTxt.setText(("0"))
        if(binding.cardBluCostNumTxt.text.isEmpty())
            binding.cardBluCostNumTxt.setText(("0"))
        if(binding.cardGrnCostNumTxt.text.isEmpty())
            binding.cardGrnCostNumTxt.setText(("0"))
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if(result.data != null){
                            i("Got Result ${result.data!!.data}")
                            card.image = result.data!!.data!!
                            Picasso.get().load(card.image).into(binding.cardArtImgVw)
                        }
                    }
                    RESULT_CANCELED -> {} else -> {}
                }
        }
    }
}