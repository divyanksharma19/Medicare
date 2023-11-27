package com.example.medicare.activities

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.medicare.R
import okhttp3.WebSocket

class ChatBot : AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)
        setupActionBar()
        showProgressDialog("Please Wait")
        webviewsetup()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun webviewsetup(){
        hideProgressDialog()
        val webview = findViewById<WebView>(R.id.chatbot)
        webview.webViewClient = WebViewClient()
        webview.apply {
            loadUrl("https://medisearch.io/")
            settings.javaScriptEnabled = true
            settings.safeBrowsingEnabled = true

        }

    }
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        val tvProgressText: TextView = mProgressDialog.findViewById(R.id.tv_progress_text)
        tvProgressText.text = text

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_chatbot)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_black_color_back_24dp)

        setSupportActionBar(findViewById(R.id.toolbar_chatbot))

        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_chatbot).setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val webview = findViewById<WebView>(R.id.chatbot)
        if(webview.canGoBack()){
            webview.goBack()
        }
        else {
            super.onBackPressed()
            // Navigate to MainActivity with the home menu item selected
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("selectedItemId", R.id.home) // Pass the ID of the home menu item
            startActivity(intent)
            finish()
        }
    }

}