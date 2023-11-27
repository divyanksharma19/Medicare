package com.example.medicare.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Firebase.FirestoreClass
import com.example.medicare.R
import com.example.medicare.adapter.DoctorListAdapter
import com.example.medicare.models.Doctor
import com.example.medicare.models.Speciality
import com.example.medicare.utils.Constants

class Details_of_Doctors : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_of_doctors)

        val expert = intent.getStringExtra(Constants.SPECIALITY)
        if (expert != null) {
            setupActionBar(expert)
        }
        showProgressDialog("Please Wait")
        if (expert != null) {
            FirestoreClass().getDoctorsList(this@Details_of_Doctors,expert)
        }
    }

    private fun setupActionBar(expert:String) {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_Doctor_Details)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_black_color_back_24dp)
        val title_toolbar = findViewById<TextView>(R.id.tv_title_DetailsDoctor)
        title_toolbar.text=expert
        setSupportActionBar(findViewById(R.id.toolbar_Doctor_Details))

        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_Doctor_Details).setNavigationOnClickListener{
            onBackPressed()
        }
    }

    fun populateDoctorsListToUI(doctor_List: ArrayList<Doctor>) {
        hideProgressDialog()
        Log.e("populateBoardsListToUI", "Doctor List: $doctor_List")
        val rv_speciality_list = findViewById<RecyclerView>(R.id.DoctorListRecyclerView)
        rv_speciality_list.layoutManager = LinearLayoutManager(this@Details_of_Doctors)

//        rv_speciality_list.setHasFixedSize(true)

        // Create an instance of DoctorListAdapter and pass the doctor_List to it.
        val adapter = DoctorListAdapter(this@Details_of_Doctors, doctor_List)
        rv_speciality_list.adapter = adapter // Attach the adapter to the recyclerView.

        adapter.setOnClickListener(object : DoctorListAdapter.OnClickListener {
            override fun onClick(position: Int, model: Doctor) {
                val intent = Intent(this@Details_of_Doctors,DoctorDescriptionActivity::class.java)
                val selectedModel = doctor_List[position] // Use a different variable name

                intent.putExtra(Constants.DOCTOR_MODEL, selectedModel)
                startActivity(intent)
            }
        })
    }
}
