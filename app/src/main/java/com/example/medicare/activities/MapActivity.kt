package com.example.medicare.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.medicare.R

class MapActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog
    private lateinit var webView: WebView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        showProgressDialog("Please Wait")
        val address = intent.getStringExtra("ADDRESS_EXTRA")
        val hospital = intent.getStringExtra("HOSPITAL_EXTRA")
        webView = findViewById(R.id.map)
        webviewSetup()
        // Check and request location permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestLocationPermission()
        } else {
            // Load the WebView with a search query when permission is not needed
            loadWebViewWithSearchQuery(hospital, address)
        }
    }

    @SuppressLint("NewApi")
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)

        val tvProgressText: TextView = mProgressDialog.findViewById(R.id.tv_progress_text)
        tvProgressText.text = text

        mProgressDialog.show()
    }

    @SuppressLint("NewApi")
    private fun webviewSetup() {
        hideProgressDialog()
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.safeBrowsingEnabled = true

        // Enable geolocation permissions for the WebView
        webView.settings.setGeolocationEnabled(true)
        webView.settings.javaScriptCanOpenWindowsAutomatically = true

        // Set up geolocation permission request handling
        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                callback?.invoke(origin, true, false)
            }
        }
    }

    private fun loadWebViewWithSearchQuery(hospital: String?, address: String?) {
        if (!hospital.isNullOrEmpty() && !address.isNullOrEmpty()) {
            // Construct the Google Maps URL with a search query
            val query = "$hospital $address"
            val mapsUrl = "https://www.google.com/maps?q=$query"
            webView.loadUrl(mapsUrl)
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission granted, you can now check location services and load the WebView
            checkLocationServicesAndLoadMap()
        }
    }

    private fun checkLocationServicesAndLoadMap() {
        val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        if (locationSettingsIntent.resolveActivity(packageManager) != null) {
            // Location services are disabled, prompt the user to enable them
            startActivityForResult(locationSettingsIntent, LOCATION_SETTINGS_REQUEST_CODE)
        } else {
            // Handle the case where location settings activity cannot be opened
            // You may want to display a message to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            // Check if the user has enabled location services
            if (isLocationServicesEnabled()) {
                // Location services are enabled, load the WebView with a search query
                val address = intent.getStringExtra("ADDRESS_EXTRA")
                val hospital = intent.getStringExtra("HOSPITAL_EXTRA")
                loadWebViewWithSearchQuery(hospital, address)
            } else {
                // Location services are still disabled
                // You may want to display a message to the user
            }
        }
    }

    private fun isLocationServicesEnabled(): Boolean {
        // Check if location services are enabled
        val locationMode: Int
        try {
            locationMode = Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.LOCATION_MODE
            )
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            return false
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }




    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
        private const val LOCATION_SETTINGS_REQUEST_CODE = 124
    }
}
