package com.example.medicare.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast

import com.example.medicare.R
import com.example.medicare.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {
    private var binding: ActivitySignInBinding? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
       auth = FirebaseAuth.getInstance()

        binding?.btnSignIn?.setOnClickListener {
            signInRegisteredUser()
        }
        binding?.forgotText?.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
        setupActionBar()
    }
   fun signInSuccess(user: com.example.medicare.models.User?){
       hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()

   }
    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignInActivity)

        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarSignInActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }
    private fun signInRegisteredUser(){
        val email: String  = binding?.etEmail?.text.toString().trim { it <= ' ' }
        val password: String = binding?.etPassword?.text.toString().trim { it <= ' ' }

        if(validateForm(email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
//                        FirestoreClass().loadUserData(this)

                    } else {
                        // If sign in fails, display a message to the user.

                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }
        }
    }
    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

}