package com.example.medicare.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.example.medicare.R
import com.example.medicare.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    private  lateinit var etPassword : EditText
    private  lateinit var btnResetPassword : Button
    private lateinit var auth : FirebaseAuth
   private var binding : ActivityResetPasswordBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()

        etPassword = findViewById(R.id.et_Reset_email)
        btnResetPassword = findViewById(R.id.btn_reset)
        auth = FirebaseAuth.getInstance()
        btnResetPassword.setOnClickListener {
            val sPassword  = etPassword.text.toString()
            if(sPassword.isNotEmpty()){
                auth.sendPasswordResetEmail(sPassword).addOnSuccessListener {
                    Toast.makeText(this,"Please Check Your Email", Toast.LENGTH_LONG).show()

                }
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()



            }
            else{
                Toast.makeText(this,"Please Write Your Email", Toast.LENGTH_SHORT).show()
            }

        }

    }
    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarResetActivity)

        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarResetActivity?.setNavigationOnClickListener{
            onBackPressed()
        }

    }

}