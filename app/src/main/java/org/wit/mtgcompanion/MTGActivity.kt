package org.wit.mtgcompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.wit.mtgcompanion.databinding.ActivityMtgBinding
import timber.log.Timber
import timber.log.Timber.i

class MTGActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMtgBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mtg)
        Timber.plant(Timber.DebugTree())
        i("MTG Activity started...")

        binding = ActivityMtgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addCardBtn.setOnClickListener(){
            i("add button pressed")
        }
    }
}