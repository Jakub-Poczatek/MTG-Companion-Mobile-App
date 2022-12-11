package org.wit.mtgcompanion.activities

import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        auth = Firebase.auth
        setContentView(binding.root)

        app = application as MainApp
        i("Auth Activity started...")
        binding.authLoginBtn.setOnClickListener(){
            val email = binding.authEmailTxt.text.toString()
            val password = binding.authPassTxt.text.toString()
            signUp(email, password)
        }
    }

    fun signIn(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                task ->
                if(task.isSuccessful) {
                    i("Login Successful")
                    setResult(RESULT_OK)
                    finish()
                } else {
                    i("Login Failed")
                    Snackbar.make(binding.authLoginBtn, R.string.invalidLogin, Snackbar.LENGTH_LONG).show()
                }
            }
    }

    fun signUp(email: String, password: String){
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
}