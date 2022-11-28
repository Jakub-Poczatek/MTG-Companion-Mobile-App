package org.wit.mtgcompanion.helpers

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import org.wit.mtgcompanion.R

fun showImagePicker(intentLauncher: ActivityResultLauncher<Intent>){
    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
    chooseFile.type = "image/*"
    chooseFile = Intent.createChooser(chooseFile, R.string.select_card_art.toString())
    intentLauncher.launch(chooseFile)
}