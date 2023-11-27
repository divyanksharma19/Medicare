package com.example.medicare.activities

import BookingListAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Firebase.FirestoreClass
import com.example.medicare.R

import com.example.medicare.adapter.DoctorListAdapter
import com.example.medicare.models.AppointmentUser
import com.example.medicare.models.Doctor
import com.example.medicare.models.User
import com.example.medicare.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.text.ParseException
import java.util.Locale

class BookingActivity : AppCompatActivity() {
    var muserDetails: User = User()
    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        setupActionBar()
        FirestoreClass().getuserDetailsinbooking(this, FirestoreClass().getCurrentUserID())
        showProgressDialog("Please Wait")


    }
    fun userDetails(user: User){
        muserDetails = user
        hideProgressDialog()
        populateDoctorsListToUI(muserDetails)
    }
    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_bookings)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_black_color_back_24dp)

        setSupportActionBar(findViewById(R.id.toolbar_bookings))

        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_bookings).setNavigationOnClickListener{
            onBackPressed()
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
    override fun onBackPressed() {
        super.onBackPressed()

        // Navigate to MainActivity with the home menu item selected
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("selectedItemId", R.id.home) // Pass the ID of the home menu item
        startActivity(intent)
        finish()
    }
    val customComparator = Comparator<AppointmentUser> { appointment1, appointment2 ->
        try {
            // Define date and time formats
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            // Parse the date and time strings from the AppointmentUser objects
            val date1 = dateFormat.parse(appointment1.date)
            val date2 = dateFormat.parse(appointment2.date)

            // Compare the parsed dates
            val dateComparison = date2.compareTo(date1) // Reverse order for latest date first

            if (dateComparison == 0) {
                // Dates are the same, so compare times
                val time1 = timeFormat.parse(appointment1.time)
                val time2 = timeFormat.parse(appointment2.time)

                // Compare the parsed times in reverse order for latest time first
                time2.compareTo(time1)
            } else {
                dateComparison // Return the result of date comparison
            }
        } catch (e: ParseException) {
            // Handle parsing exceptions here if necessary
            0 // Default to no change in order
        }
    }

// Sort the appointmentList using the customComparator


    fun populateDoctorsListToUI(user:User) {
        hideProgressDialog()
        var userbookings: ArrayList<AppointmentUser> = ArrayList()
        userbookings = user.userappointment
        userbookings.sortWith(customComparator)

        Log.e("populateBoardsListToUI", "Doctor List: $user")
        val rv_speciality_list = findViewById<RecyclerView>(R.id.bookingsRecyclerView)
        rv_speciality_list.layoutManager = LinearLayoutManager(this@BookingActivity)

        rv_speciality_list.setHasFixedSize(true)

        // Create an instance of DoctorListAdapter and pass the doctor_List to it.
        val adapter = BookingListAdapter(this@BookingActivity, userbookings)
        rv_speciality_list.adapter = adapter // Attach the adapter to the recyclerView.

        adapter.setOnClickListener(object : BookingListAdapter.OnClickListener {
            override fun onClick(position: Int, model: AppointmentUser) {
                val intent = Intent(this@BookingActivity, DoctorDescriptionActivity::class.java)
                val doctorid= userbookings[position].doctor_id // Use a different variable name
                var mdoctorDetails: Doctor? = null
                FirebaseFirestore.getInstance().collection(Constants.DOCTOR)
                    .document(doctorid)
                    .get()
                    .addOnSuccessListener { document ->
                        mdoctorDetails = document.toObject(Doctor::class.java)!!
                        intent.putExtra(Constants.DOCTOR_MODEL, mdoctorDetails)
                        startActivity(intent)
                    }
            }
        })

    }
    fun updatelist(){
        showProgressDialog("Please Wait")
        FirestoreClass().getuserDetailsinbooking(this, FirestoreClass().getCurrentUserID())
    }

}