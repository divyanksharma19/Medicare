// DoctorDescriptionActivity.kt
package com.example.medicare.activities


import TimingAdapter
import android.annotation.SuppressLint
import kotlin.Pair

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle

import kotlinx.coroutines.launch
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicare.Firebase.FirestoreClass
import com.example.medicare.R
import com.example.medicare.activities.DoctorDescriptionActivity.TwilioConstants.ACCOUNT_SID
import com.example.medicare.adapter.TimingAdapterLatest
import com.example.medicare.models.Appointment
import com.example.medicare.models.AppointmentUser
import com.example.medicare.models.Doctor
import com.example.medicare.models.SlotAvailability
import com.example.medicare.models.Timing
import com.example.medicare.models.User
import com.example.medicare.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import okhttp3.*

import org.json.JSONObject

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import java.util.ArrayList
import java.util.Date
import kotlin.collections.List

class DoctorDescriptionActivity : BaseActivity() {
    var timeselected: String = ""
    var dayselected: String = ""
    var bookings: ArrayList<Appointment> = ArrayList()
    var appointment_by_user: Appointment = Appointment()
    var appointment_details_by_doctor: AppointmentUser = AppointmentUser()
    var timing_user: ArrayList<Timing> = ArrayList()
    var mposition: Int = 0
    var mpositiondate: Int = -1
    var userbookings: ArrayList<AppointmentUser> = ArrayList()
    var muserDetails: User = User()
    var mdoctorDetails: Doctor = Doctor()
    var datelistnew: ArrayList<String> = ArrayList()


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_doctor_description)
        val selectedDoctor = intent.getParcelableExtra<Doctor>(Constants.DOCTOR_MODEL)
        Log.e("DoctorDescriptionActivity_Doctor", "Selected Doctor: ${selectedDoctor}")
        findViewById<Button>(R.id.view_on_map).setOnClickListener {
            val intent = Intent(this@DoctorDescriptionActivity, MapActivity::class.java)
            if (selectedDoctor != null) {
                intent.putExtra("ADDRESS_EXTRA", selectedDoctor.address)
                intent.putExtra("HOSPITAL_EXTRA", selectedDoctor.hospital)
            }
            startActivity(intent)
        }
        FirestoreClass().getuserDetails(this,FirestoreClass().getCurrentUserID())
        if (selectedDoctor != null) {
            setupActionBar(selectedDoctor.name)
        }
        if (selectedDoctor != null) {
            setupui(selectedDoctor)
        }
        findViewById<Button>(R.id.call).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)

            intent.data = Uri.parse("tel:${selectedDoctor?.mobile}")


                startActivity(intent)

        }
        val uniqueDates = ArrayList<Pair<Int, String>>()

        if (selectedDoctor != null) {
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = Date() // Current date

            for (position in selectedDoctor.timing.indices) {
                val dateStr = selectedDoctor.timing[position].time

                try {
                    val date = dateFormatter.parse(dateStr)
                    if (date != null && date >= currentDate) {
                        uniqueDates.add(Pair(position, dateStr))
                    }
                } catch (e: ParseException) {
                    // Handle parsing exceptions if any
                    e.printStackTrace()
                }
            }
        }




        val datesList = getDatesList()
        dayuiset(selectedDoctor,datesList)

        findViewById<Button>(R.id.btn_book_appointment).setOnClickListener {
            if (selectedDoctor != null) {
                bookappointment(selectedDoctor)
            }
        }
    }

    fun userDetails(user: User) {
        muserDetails = user
    }

    fun doctorDetails(doctor: Doctor) {
        mdoctorDetails = doctor
    }

    private fun dayuiset(selectedDoctor: Doctor?, datesList: List<String>) {
        val rv_day_list = findViewById<RecyclerView>(R.id.dayrecyclerview)
        val timingRecyclerView = findViewById<RecyclerView>(R.id.timingrecyclerview)
        val workingHoursTextView = findViewById<TextView>(R.id.working_time)

        val layoutManager = LinearLayoutManager(this@DoctorDescriptionActivity, LinearLayoutManager.HORIZONTAL, false)
        rv_day_list.layoutManager = layoutManager
        rv_day_list.setHasFixedSize(true)

        val adapter = TimingAdapter(datesList, this@DoctorDescriptionActivity)
        rv_day_list.adapter = adapter

        adapter.setOnClickListener(object : TimingAdapter.OnClickListener {
            override fun onClick(position: Int) {
                if (mpositiondate != position) {
                    // When a new date is selected, show the timing RecyclerView and "Working Hours" TextView
                    dayselected = datesList[position]
//                    mpositiondate = datesList[position].first
                    timingRecyclerView.visibility = View.VISIBLE
                    workingHoursTextView.visibility = View.VISIBLE

                    // Update the timing RecyclerView with the new selected date position
                    var workingtime: ArrayList<SlotAvailability> = ArrayList()

                    if (selectedDoctor != null && position >= 0 && position < datesList.size) {

                        // Define a custom comparator to sort based on the time field

                        timinguiset(selectedDoctor.timeslots)
                    }
                } else {
                    // When the same date is selected again (deselected), hide the timing RecyclerView and "Working Hours" TextView
                    dayselected = ""
                    mpositiondate = -1
                    timingRecyclerView.visibility = View.GONE
                    workingHoursTextView.visibility = View.GONE
                }
            }
        })

        // Initially, hide the timing RecyclerView and "Working Hours" TextView
        timingRecyclerView.visibility = View.GONE
        workingHoursTextView.visibility = View.GONE
    }






    private fun setupui(selectedDoctor: Doctor) {
        val name = findViewById<TextView>(R.id.doctor_name_decription)
        name.text = selectedDoctor.name
        findViewById<TextView>(R.id.doctor_speciality_description).text =
            "Speciality: ${selectedDoctor.speciality}"
        findViewById<TextView>(R.id.doctordegree).text = "Degree: ${selectedDoctor.degree}"
        findViewById<TextView>(R.id.hospital_name_desciption).text = "Hospital: ${selectedDoctor.hospital}"
        findViewById<TextView>(R.id.address_doctor_description).text = "Address: ${selectedDoctor.address}"
        findViewById<TextView>(R.id.doctor_details_description).text = selectedDoctor.about
        val nav_user_image = findViewById<CircleImageView>(R.id.doctor_image_decription)
        Glide
            .with(this)
            .load(selectedDoctor.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_user_image)
    }

    fun timinguiset(slotlist: ArrayList<String>) {
        val rv_timing_list = findViewById<RecyclerView>(R.id.timingrecyclerview)

        val layoutManager =
            LinearLayoutManager(this@DoctorDescriptionActivity, LinearLayoutManager.HORIZONTAL, false)
        rv_timing_list.layoutManager = layoutManager
        rv_timing_list.setHasFixedSize(true)

        // Pass the selected date position to TimingAdapterLatest
        val adapter = TimingAdapterLatest(mpositiondate, slotlist,this@DoctorDescriptionActivity)
        rv_timing_list.adapter = adapter

        adapter.setOnClickListener(object :
            TimingAdapterLatest.OnClickListener {
            override fun onClick(position: Int) {
                timeselected = slotlist[position]
                mposition = position
            }
        })
    }

    private fun bookappointment(selectedDoctor: Doctor) {
        FirebaseFirestore.getInstance().collection(Constants.DOCTOR)
            .document(selectedDoctor.documentId)
            .get()
            .addOnSuccessListener { document ->
                mdoctorDetails = document.toObject(Doctor::class.java)!!
                val currentTimeMillis = System.currentTimeMillis()
                val twentyFourHoursInMillis = 24 * 60 * 60 * 1000 // 24 hours in milliseconds
                val isWithin24Hours = mdoctorDetails.appointment.any { appointment ->
                    appointment.date == dayselected &&
                            appointment.time == timeselected &&
                            appointment.user_id == FirestoreClass().getCurrentUserID() &&
                            (currentTimeMillis - appointment.timestamp) < twentyFourHoursInMillis
                }
                // Continue with the booking logic inside this block
                if (timeselected.isNotEmpty() && dayselected.isNotEmpty()) {
                    // Check if the selected date and time are already booked
                    val isAlreadyBookedWithOtherDoctor = muserDetails.userappointment.any { doctorAppointment ->
                            doctorAppointment.date == dayselected && doctorAppointment.time == timeselected && doctorAppointment.doctor_id != mdoctorDetails.documentId
                    }
                    val hasConflict =  mdoctorDetails.appointment.any { doctorAppointment ->
                        doctorAppointment.date == dayselected && doctorAppointment.time == timeselected && doctorAppointment.user_id == FirestoreClass().getCurrentUserID()
                    }
                    // Check if the user has already booked an appointment with this doctor
                    val isAlreadyBookedWithSameDoctor = mdoctorDetails.appointment.any { appointment ->
                        appointment.date == dayselected && appointment.time == timeselected && appointment.user_id == FirestoreClass().getCurrentUserID()
                    }
                    var isuseralreadybookedwithotherdoctor: Boolean? = null
                    var mdoctoralreadybooked: Doctor? = null

                    val doctorId = muserDetails.userappointment.find {
                        it.date == dayselected && it.time == timeselected
                    }?.doctor_id
                    Log.e("doctorid", "Doctor ID: $doctorId")

                    if (doctorId != null) {
                        FirebaseFirestore.getInstance().collection(Constants.DOCTOR)
                            .document(doctorId)
                            .get()
                            .addOnSuccessListener { document ->
                                mdoctoralreadybooked = document.toObject(Doctor::class.java)

                                // Handle any further logic related to mdoctoralreadybooked here
                            }
                    }

                    Log.e("doctorid2", "Doctor ID: ${mdoctoralreadybooked?.name}")


                    // Check if the user has already booked an appointment within the last 24 hours for the same date and time

                     if (hasConflict == true) {
                        // Display a toast message indicating that the user has already booked an appointment within 24 hours
                        Toast.makeText(
                            this,
//                            "You can't book another appointment within 24 hours for the same slot",
                            "Slot already booked on $dayselected at $timeselected ",

                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else if (isAlreadyBookedWithOtherDoctor) {
                        // Display a toast message indicating that the slot is already booked


                        Toast.makeText(
                            this,
                            "Slot already booked on $dayselected at $timeselected with other doctor. Please Check Your Appointments Once",
                            Toast.LENGTH_LONG
                        ).show()
                    }
//                    else if (isAlreadyBookedWithOtherDoctor == true) {
//                        // Display a toast message indicating that the user has already booked an appointment within 24 hours
//                        Toast.makeText(
//                            this,
////                            "You can't book another appointment within 24 hours for the same slot",
//                            "Slot already booked on $dayselected at $timeselected",
//
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
                    else {
                        val randomId = UniqueRandomIdGenerator().generateUniqueRandomId(1,100000)// Generates a random integer between 200 and 1000
                        appointment_by_user.id = randomId.toString()
                        appointment_by_user.user_id = FirestoreClass().getCurrentUserID()
                        appointment_by_user.date = dayselected
                        appointment_by_user.time = timeselected
                        appointment_by_user.status = "BOOKING CONFIRMED"
                        appointment_by_user.timestamp = currentTimeMillis // Store the current timestamp

                        // Parse the values as integers
//                        if (mpositiondate >= 0 && mpositiondate < mdoctorDetails.timing.size) {
//                            var remainingSlot = mdoctorDetails.timing[mpositiondate].dateSlotMap[mposition].remainingSlots.toInt()
//                            var totalBookedSlot = mdoctorDetails.timing[mpositiondate].dateSlotMap[mposition].totalBookedSlots.toInt()
//
//                            remainingSlot -= 1
//                            totalBookedSlot += 1
//
//                            // Update the values in the specific Timing object
//                            mdoctorDetails.timing[mpositiondate].dateSlotMap[mposition].remainingSlots = remainingSlot.toString()
//                            mdoctorDetails.timing[mpositiondate].dateSlotMap[mposition].totalBookedSlots = totalBookedSlot.toString()
//                        }

                        timing_user = mdoctorDetails.timing

                        // Add the new appointment to the doctor's list
                        mdoctorDetails.appointment.add(appointment_by_user)

                        // Update the doctor's appointment list in Firestore
                        val doctorHashMap = HashMap<String, Any>()
                        doctorHashMap[Constants.DOCUMENT_ID] = selectedDoctor.documentId
                        doctorHashMap[Constants.APPOINTMENT] = mdoctorDetails.appointment
                        doctorHashMap[Constants.TIMING] = mdoctorDetails.timing

                        FirestoreClass().updatedoctordetails(
                            this@DoctorDescriptionActivity,
                            doctorHashMap,
                            selectedDoctor.documentId
                        )

                        // Fetch the user document from Firestore
                        FirebaseFirestore.getInstance().collection(Constants.USERS)
                            .document(FirestoreClass().getCurrentUserID())
                            .get()
                            .addOnSuccessListener { userDocument ->
                                muserDetails = userDocument.toObject(User::class.java)!!
                                val userHashMap = HashMap<String, Any>()
                                userbookings = muserDetails.userappointment

                                appointment_details_by_doctor.doctor_id = selectedDoctor.documentId
                                appointment_details_by_doctor.id = randomId.toString()
                                appointment_details_by_doctor.date = dayselected
                                appointment_details_by_doctor.time = timeselected
                                appointment_details_by_doctor.status = "Booking Done"
                                appointment_details_by_doctor.timestamp = currentTimeMillis

                                userbookings.add(appointment_details_by_doctor)
                                userHashMap[Constants.USERAPPOINTMENT] = userbookings

                                // Update the user's appointment list in Firestore
                                FirestoreClass().updateuserappointmentdetails(
                                    this@DoctorDescriptionActivity,
                                    userHashMap,
                                    FirestoreClass().getCurrentUserID()
                                )
                                SendNotificationToUserAsyncTask(mdoctorDetails.name,muserDetails.fcmToken ).execute()
//
                                val twilioApiService = createTwilioApiService()
                                val toPhoneNumber = "+91"+mdoctorDetails.mobile.toString()  // Replace with the recipient's phone number
                                val fromPhoneNumber = "+14043345873" // Replace with your Twilio phone number
                                val message = "Appointment with ${muserDetails.name} on ${dayselected} Time:${timeselected}"

                                val twilioApiServiceuser = createTwilioApiService()
                                val toPhoneNumberuser = "+91"+muserDetails.mobile.toString()  // Replace with the recipient's phone number
                                val fromPhoneNumberuser = "+14043345873" // Replace with your Twilio phone number
                                val messageuser = "Appointment with ${mdoctorDetails.name} on ${dayselected} Time:${timeselected}"

// You should run this in a background thread or coroutine to avoid blocking the UI thread.
// For simplicity, we'll use a coroutine here.
                                GlobalScope.launch(Dispatchers.IO) {
                                    try {
                                        val response = twilioApiService.sendSMS(ACCOUNT_SID, toPhoneNumber, fromPhoneNumber, message).execute()
                                        if (response.isSuccessful) {

                                        } else {
                                            // SMS sending failed
                                            // You can handle the failure case here
                                        }
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                        // Handle the exception here
                                    }
                                }

                                GlobalScope.launch(Dispatchers.IO) {
                                    try {
                                        val response = twilioApiServiceuser.sendSMS(ACCOUNT_SID, toPhoneNumberuser, fromPhoneNumberuser, messageuser).execute()
                                        if (response.isSuccessful) {

                                        } else {
                                            // SMS sending failed
                                            // You can handle the failure case here
                                        }
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                        // Handle the exception here
                                    }
                                }


                                startActivity(Intent(this@DoctorDescriptionActivity, MainActivity::class.java))

                                finish()
                            }
                    }
                } else {
                    Toast.makeText(this, "Please Select the date and time", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupActionBar(name: String) {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_Doctor_Description)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_black_color_back_24dp)
        val title_toolbar = findViewById<TextView>(R.id.tv_title_DescriptionDoctor)
        title_toolbar.text = name
        setSupportActionBar(findViewById(R.id.toolbar_Doctor_Description))

        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_Doctor_Description).setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun getDatesList(): List<String> {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val datesList = mutableListOf<String>()

        for (i in 0 until 7) { // Change '5' to '7' for the next 7 days
            val formattedDate = dateFormat.format(calendar.time)
            datesList.add(formattedDate)
            calendar.add(Calendar.DAY_OF_MONTH, 1) // Increment the date by one day
        }

        return datesList
    }



    @SuppressLint("StaticFieldLeak")
    private inner class SendNotificationToUserAsyncTask(val name: String, val token: String) :
        AsyncTask<Any, Void, String>() {

        /**
         * This function is for the task which we wants to perform before background execution.
         * Here we have shown the progress dialog to user that UI is not freeze but executing something in background.
         */
        override fun onPreExecute() {
            super.onPreExecute()

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
        }

        /**
         * This function will be used to perform background execution.
         */
        override fun doInBackground(vararg params: Any): String {
            var result: String

            /**
             * https://developer.android.com/reference/java/net/HttpURLConnection
             *
             * You can use the above url for Detail understanding of HttpURLConnection class
             */
            var connection: HttpURLConnection? = null
            try {
                val url = URL(Constants.FCM_BASE_URL) // Base Url
                connection = url.openConnection() as HttpURLConnection

                /**
                 * A URL connection can be used for input and/or output.  Set the DoOutput
                 * flag to true if you intend to use the URL connection for output,
                 * false if not.  The default is false.
                 */
                connection.doOutput = true
                connection.doInput = true

                /**
                 * Sets whether HTTP redirects should be automatically followed by this instance.
                 * The default value comes from followRedirects, which defaults to true.
                 */
                connection.instanceFollowRedirects = false

                /**
                 * Set the method for the URL request, one of:
                 *  POST
                 */
                connection.requestMethod = "POST"

                /**
                 * Sets the general request property. If a property with the key already
                 * exists, overwrite its value with the new value.
                 */
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                // TODO (Step 3: Add the firebase Server Key.)
                // START
                // In order to find your Server Key or authorization key, follow the below steps:
                // 1. Goto Firebase Console.
                // 2. Select your project.
                // 3. Firebase Project Setting
                // 4. Cloud Messaging
                // 5. Finally, the SerkeyKey.
                // For Detail understanding visit the link: https://android.jlelse.eu/android-push-notification-using-firebase-and-advanced-rest-client-3858daff2f50
                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                )
                // END

                /**
                 * Some protocols do caching of documents.  Occasionally, it is important
                 * to be able to "tunnel through" and ignore the caches (e.g., the
                 * "reload" button in a browser).  If the UseCaches flag on a connection
                 * is true, the connection is allowed to use whatever caches it can.
                 *  If false, caches are to be ignored.
                 *  The default value comes from DefaultUseCaches, which defaults to
                 * true.
                 */
                connection.useCaches = false

                /**
                 * Creates a new data output stream to write data to the specified
                 * underlying output stream. The counter written is set to zero.
                 */
                val wr = DataOutputStream(connection.outputStream)

                // TODO (Step 4: Create a notification data payload.)
                // START
                // Create JSONObject Request
                val jsonRequest = JSONObject()

                // Create a data object
                val dataObject = JSONObject()
                // Here you can pass the title as per requirement as here we have added some text and board name.
                dataObject.put(Constants.FCM_KEY_TITLE, "APPOINTMENT SCHEDULE ")
                // Here you can pass the message as per requirement as here we have added some text and appended the name of the Board Admin.
                dataObject.put(
                    Constants.FCM_KEY_MESSAGE,
                    "Appointment with ${name} Confirmed"
                )

                // Here add the data object and the user's token in the jsonRequest object.
                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)
                // END

                /**
                 * Writes out the string to the underlying output stream as a
                 * sequence of bytes. Each character in the string is written out, in
                 * sequence, by discarding its high eight bits. If no exception is
                 * thrown, the counter written is incremented by the
                 * length of s.
                 */
                wr.writeBytes(jsonRequest.toString())
                wr.flush() // Flushes this data output stream.
                wr.close() // Closes this output stream and releases any system resources associated with the stream

                val httpResult: Int =
                    connection.responseCode // Gets the status code from an HTTP response message.

                if (httpResult == HttpURLConnection.HTTP_OK) {

                    /**
                     * Returns an input stream that reads from this open connection.
                     */
                    val inputStream = connection.inputStream

                    /**
                     * Creates a buffering character-input stream that uses a default-sized input buffer.
                     */
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    try {
                        /**
                         * Reads a line of text.  A line is considered to be terminated by any one
                         * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
                         * followed immediately by a linefeed.
                         */
                        while (reader.readLine().also { line = it } != null) {
                            sb.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            /**
                             * Closes this input stream and releases any system resources associated
                             * with the stream.
                             */
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                } else {

                    result = connection.responseMessage
                }

            } catch (e: SocketTimeoutException) {
                result = "Connection Timeout"
            } catch (e: Exception) {
                result = "Error : " + e.message
            } finally {
                connection?.disconnect()
            }

            // You can notify with your result to onPostExecute.
            return result
        }

        /**
         * This function will be executed after the background execution is completed.
         */
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            hideProgressDialog()

            // JSON result is printed in the log.
            Log.e("JSON Response Result", result)
        }
    }
}
