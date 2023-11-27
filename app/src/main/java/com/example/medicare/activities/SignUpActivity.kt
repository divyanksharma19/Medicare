package com.example.medicare.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.medicare.Firebase.FirestoreClass
import com.example.medicare.R

import com.example.medicare.databinding.ActivitySignUpBinding
import com.example.medicare.models.User
import com.example.medicare.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    private var binding: ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Move the setupActionBar() method call here.
        setupActionBar()

        // Move the click listener setup here.
        binding?.btnNext?.setOnClickListener {
            // Move the val declarations here.
            val name: String = binding?.etName?.text.toString().trim()
            val email: String = binding?.etEmail?.text.toString().trim()
            val password: String = binding?.etPassword?.text.toString().trim()

            if (validateForm(name, email, password)) {
                val intent = Intent(this, Details::class.java)
                intent.putExtra("name", name)
                intent.putExtra("email", email)
                intent.putExtra("password", password)
                startActivity(intent)
                finish()
            }
        }

    }

    fun userRegisteredSuccesss(){
        Toast.makeText(
            this,
            " You Have Succesfully Registered ",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)

        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarSignUpActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
        val name: String = binding?.etName?.text.toString().trim { it <= ' ' }
        val email: String = binding?.etEmail?.text.toString().trim { it <= ' ' }
        val password: String = binding?.etPassword?.text.toString().trim { it <= ' ' }

        binding?.btnNext?.setOnClickListener {
            if (validateForm(name, email, password)) {

                showProgressDialog(resources.getString(R.string.please_wait))


                val intent = Intent(this,Details::class.java)
                startActivity(intent)


            }
        }
    }



    private fun validateForm(name: String, email: String, password: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._%+-]+@(?:gmail\\.com|yahoo\\.com|iiitu\\.ac\\.in)"


        val namePattern = "^[a-zA-Z]+( [a-zA-Z]+)*$"

        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            !name.matches(namePattern.toRegex()) -> {
                showErrorSnackBar("Name should only contain alphabetic characters with at most one space between words.")
                false
            }
            !email.matches(emailPattern.toRegex()) -> {
                showErrorSnackBar("Please enter a valid email.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            password.length < 8 -> {
                showErrorSnackBar("Password must have at least 8 characters.")
                false
            }
            else -> {
                true
            }
        }
    }



    // END
}