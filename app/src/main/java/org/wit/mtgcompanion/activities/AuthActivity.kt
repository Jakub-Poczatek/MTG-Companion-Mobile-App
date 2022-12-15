package org.wit.mtgcompanion.activities

import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.wit.mtgcompanion.R
import org.wit.mtgcompanion.databinding.ActivityAuthBinding
import org.wit.mtgcompanion.main.MainApp
import timber.log.Timber.e
import timber.log.Timber.i

class AuthActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAuthBinding
    lateinit var app: MainApp
    private var existing = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        auth = Firebase.auth

        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

            exitTransition = Slide(Gravity.LEFT)
        }

        setContentView(binding.root)

        app = application as MainApp
        i("Auth Activity started...")

        if(intent.hasExtra("sign_up")){
            existing = false
            binding.authAlternativeTxt.setText(R.string.authAlreadyRegistered)
            binding.authLoginBtn.setText(R.string.authRegisterBtn)
        }

        if(intent.hasExtra("sign_in")){
            existing = true
            binding.authAlternativeTxt.setText(R.string.authNotRegistered)
            binding.authLoginBtn.setText(R.string.authLoginBtn)
        }

        binding.authLoginBtn.setOnClickListener{
            val email = binding.authEmailTxt.text.toString()
            val password = binding.authPassTxt.text.toString()
            if(existing) signIn(email, password) else signUp(email, password)
        }

        binding.authClickHereLink.setOnClickListener{
            flipFunction(savedInstanceState)
        }
    }

    private fun signIn(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                task ->
                if(task.isSuccessful) {
                    i("Login Successful")
                    startActivity(Intent(this, CardListActivity::class.java))
                } else {
                    i("Login Failed")
                    Snackbar.make(binding.authLoginBtn, R.string.invalidLogin, Snackbar.LENGTH_LONG).show()
                }
            }
    }

    private fun signUp(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                task ->
                if(task.isSuccessful) {
                    i("Sign Up Successful")
                    startActivity(Intent(this, CardListActivity::class.java))
                } else {
                    e("Sign Up Failed")
                    Snackbar.make(binding.authLoginBtn, R.string.invalidLogin, Snackbar.LENGTH_LONG).show()
                }
            }
    }

    private fun flipFunction(savedInstanceState: Bundle?){
        val launcherIntent = Intent(this, AuthActivity::class.java)
        if(existing) launcherIntent.putExtra("sign_up", savedInstanceState)
        else launcherIntent.putExtra("sign_in", savedInstanceState)
        startActivity(launcherIntent)
        if(existing){
            binding.authLoginBtn.setText(R.string.authLoginBtn)
            binding.authAlternativeTxt.setText(R.string.authNotRegistered)
        } else {
            binding.authLoginBtn.setText(R.string.authRegisterBtn)
            binding.authAlternativeTxt.setText(R.string.authAlreadyRegistered)
        }
    }
}