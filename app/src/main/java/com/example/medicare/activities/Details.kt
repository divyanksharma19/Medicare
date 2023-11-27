package com.example.medicare.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.medicare.Firebase.FirestoreClass

import com.example.medicare.databinding.ActivityDetailsBinding
import com.example.medicare.models.User
import com.google.firebase.auth.*

class Details : BaseActivity() {
    private var binding: ActivityDetailsBinding? = null
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = FirebaseAuth.getInstance()

        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarSignUpActivity)

        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(com.example.medicare.R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarSignUpActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnRegister?.setOnClickListener {
            registerUser()
        }
    }

    fun userRegisteredSuccess() {
        Toast.makeText(
            this,
            "You Have Successfully Registered",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun registerUser() {
        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")

        val gender: String = binding?.spinnerGender?.selectedItem.toString()
        val age: Long = binding?.etAge?.text.toString().toLongOrNull() ?: 0L
        val blood: String = binding?.spinnerBlood?.selectedItem.toString()
        val diabetic: String = binding?.diabites?.selectedItem.toString()
        val mobile: Long = binding?.etPhone?.text.toString().toLongOrNull() ?: 0L
        val weight: Long = binding?.etWeight?.text.toString().toLongOrNull() ?: 0L
        val height: Long = binding?.etHeight?.text.toString().toLongOrNull() ?: 0L

        if (validateForm(age.toString(), mobile.toString(), weight.toString(), height.toString())) {
            showProgressDialog(resources.getString(com.example.medicare.R.string.please_wait))
            if (email != null && password != null) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val registeredEmail = firebaseUser.email!!
                            val user = User(
                                firebaseUser.uid,
                                name ?: "",
                                registeredEmail,
                                "", // Set the image to an empty string or provide the image URL if you have it.
                                mobile,
                                age,
                                blood,
                                height,
                                weight,
                                diabetic,
                                gender
                            )
                            FirestoreClass().registerUser(this, user)
                        } else {
                            hideProgressDialog()
                            showErrorSnackBar("Registration Failed")
                        }
                    }
            } else {
                showErrorSnackBar("Error")
            }
        }
    }


    private fun validateForm(
        age: String?,
        mobile: String,
        weight: String,
        height: String
    ): Boolean {
        return when {
            age.isNullOrEmpty() -> {
                showErrorSnackBar("Please enter age.")
                false
            }
            age.toIntOrNull() == null || age.toInt() !in 1..130 -> {
                showErrorSnackBar("Please enter age between 1 to 130 years.")
                false
            }
            TextUtils.isEmpty(mobile) -> {
                showErrorSnackBar("Please enter mobile.")
                false
            }
            mobile.length > 10 -> {
                showErrorSnackBar("Please enter a valid mobile number.")
                false
            }
            TextUtils.isEmpty(weight) -> {
                showErrorSnackBar("Please enter weight.")
                false
            }
            TextUtils.isEmpty(height) -> {
                showErrorSnackBar("Please enter height.")
                false
            }
            height.toLongOrNull() == null || height.toLong() !in 50L..280L -> {
                showErrorSnackBar("Height must be between 50 and 280 cm.")
                false
            }
            weight.toLongOrNull() == null || weight.toLong() !in 20L..250L -> {
                showErrorSnackBar("Weight must be between 20 and 250 kg.")
                false
            }
            else -> {
                true
            }
        }
    }



}