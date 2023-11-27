import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicare.Firebase.FirestoreClass
import com.example.medicare.R
import com.example.medicare.activities.BookingActivity
import com.example.medicare.activities.DoctorDescriptionActivity
import com.example.medicare.activities.createTwilioApiService
import com.example.medicare.models.Appointment
import com.example.medicare.models.AppointmentUser
import com.example.medicare.models.Doctor
import com.example.medicare.models.Timing
import com.example.medicare.models.User
import com.example.medicare.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class BookingListAdapter(
    private val context: Context,
    private var list: ArrayList<AppointmentUser>
) : RecyclerView.Adapter<BookingListAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivDoctorImage: ImageView = view.findViewById(R.id.item_bookeddoctor_image)
        val tvDoctorName: TextView = view.findViewById(R.id.item_tv_name_doctor_details)
        val tvBookingTimeDate: TextView = view.findViewById(R.id.booking_time_and_date)
        val tvbookingid : TextView = view.findViewById(R.id.booking_id)
        var bookingStatus : TextView = view.findViewById(R.id.booking_status)
        val cancel_appointment : Button = view.findViewById(R.id.cancel_appointment)
        val reschedule: Button = view.findViewById(R.id.buttonReschedule)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        var timing_user: java.util.ArrayList<Timing> = java.util.ArrayList()
        // Fetch doctor details based on the appointment's doctor_id
        FirebaseFirestore.getInstance().collection(Constants.DOCTOR)
            .document(model.doctor_id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val doctorDetails = documentSnapshot.toObject(Doctor::class.java)

                if (doctorDetails != null) {
                    // Populate the views with doctor details
                    Glide.with(context)
                        .load(doctorDetails.image)
                        .centerCrop()
                        .placeholder(R.drawable.ic_board_place_holder)
                        .into(holder.ivDoctorImage)

                    holder.tvDoctorName.text = doctorDetails.name
                    holder.tvbookingid.text = "Booking id: ${model.id}"
                    holder.tvBookingTimeDate.text = "Time: ${model.time}, Date: ${model.date}"
                    holder.bookingStatus.text = "Status: ${model.status}"
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                    val appointmentDateTime = dateFormat.parse("${model.date} ${model.time}")

// Calculate time difference
                    val currentTime = Calendar.getInstance().time
                    val timeDifferenceMillis = appointmentDateTime.time - currentTime.time
                    val timeDifferenceHours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis)

                    if (timeDifferenceHours > 0) {
                        // The appointment is in the future, so show the cancel_appointment button
                        holder.cancel_appointment.visibility = View.VISIBLE
                        holder.reschedule.visibility = View.VISIBLE


                        holder.cancel_appointment.setOnClickListener {

                            // Parse date and time
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                            val appointmentDateTime = dateFormat.parse("${model.date} ${model.time}")

                            // Calculate time difference
                            val currentTime = Calendar.getInstance().time
                            val timeDifferenceMillis = appointmentDateTime.time - currentTime.time
                            val timeDifferenceHours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis)

                            if (timeDifferenceHours < 2) {
                                // Appointment cannot be canceled
                                val snackbarMessage = "Cannot cancel appointment. It is less than 2 hours before the appointment time."
                                val snackbar = Snackbar.make(holder.itemView, snackbarMessage, Snackbar.LENGTH_SHORT)
                                snackbar.show()
                            } else {
                                val alertDialogBuilder = AlertDialog.Builder(context)
                                alertDialogBuilder.setMessage("Are you sure you want to cancel this appointment?")
                                alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                                    val bookingIdToDelete = model.id
                                    val uniqueDates = java.util.ArrayList<String>()

                                    if (doctorDetails != null) {
                                        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


                                        for (timing in doctorDetails.timing) {

                                            val dateStr = timing.time

                                            try {
                                                val date = dateFormatter.parse(dateStr)
                                                if (date != null) {


                                                    uniqueDates.add(dateStr)

                                                }
                                            } catch (e: ParseException) {
                                                // Handle parsing exceptions if any
                                                e.printStackTrace()
                                            }
                                        }

                                    }


                                    val mPositionDate = uniqueDates.indexOf(model.date)
                                    val timeList: MutableList<String> = mutableListOf()
                                    if (mPositionDate >= 0 && mPositionDate < doctorDetails.timing.size) {
                                        val timingObject = doctorDetails.timing[mPositionDate]

                                        // Iterate through the dateSlotMap in the specific Timing object
                                        for (slotAvailability in timingObject.dateSlotMap) {
                                            // Extract the date and add it to the dateList
                                            timeList.add(slotAvailability.date)
                                        }
                                    }
                                    val mPositionTime = timeList.indexOf(model.time)
//

                                    // Update the doctor's appointment list
                                    val appointmentList = doctorDetails.appointment.toMutableList()
                                    val appointmentToDelete =
                                        appointmentList.find { it.id == bookingIdToDelete }

                                    if (appointmentToDelete != null) {
                                        appointmentList.remove(appointmentToDelete)

                                        // Update the doctorDetails object in Firestore
                                        if (mPositionDate >= 0 && mPositionDate < doctorDetails.timing.size) {
                                            val mPositionTime = // Calculate the position based on your logic
                                                if (mPositionTime >= 0 && mPositionTime < doctorDetails.timing[mPositionDate].dateSlotMap.size) {
                                                    val remainingSlot =
                                                        doctorDetails.timing[mPositionDate].dateSlotMap[mPositionTime].remainingSlots.toInt()
                                                    val totalBookedSlot =
                                                        doctorDetails.timing[mPositionDate].dateSlotMap[mPositionTime].totalBookedSlots.toInt()

                                                    // Update the values in the specific Timing object
                                                    doctorDetails.timing[mPositionDate].dateSlotMap[mPositionTime].remainingSlots =
                                                        (remainingSlot + 1).toString()
                                                    doctorDetails.timing[mPositionDate].dateSlotMap[mPositionTime].totalBookedSlots =
                                                        (totalBookedSlot - 1).toString()

                                                    val timingUser = doctorDetails.timing // Update the timing_user

                                                    val doctorHashMap = HashMap<String, Any>()
                                                    doctorHashMap[Constants.TIMING] = timingUser

                                                    FirebaseFirestore.getInstance()
                                                        .collection(Constants.DOCTOR)
                                                        .document(doctorDetails.documentId)
                                                        .update(doctorHashMap)
                                                        .addOnSuccessListener {
                                                            // Update successful
                                                            Toast.makeText(
                                                                context,
                                                                "Appointment Cancelled",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                        .addOnFailureListener { e ->
                                                            // Handle the update failure here
                                                            Toast.makeText(
                                                                context,
                                                                "Appointment Cancellation Failed: ${e.message}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                } else {

                                                }
                                        }

                                        FirebaseFirestore.getInstance().collection(Constants.DOCTOR)
                                            .document(model.doctor_id)
                                            .update("appointment", appointmentList)
                                            .addOnSuccessListener {
                                                // Update the user's appointment list
                                                FirebaseFirestore.getInstance()
                                                    .collection(Constants.USERS)
                                                    .document(FirestoreClass().getCurrentUserID())
                                                    .get()
                                                    .addOnSuccessListener { documentSnapshot ->
                                                        val userDetails =
                                                            documentSnapshot.toObject(User::class.java)
                                                        val userAppointmentList =
                                                            userDetails?.userappointment?.toMutableList()
                                                        val userAppointmentToDelete =
                                                            userAppointmentList?.find { it.id == bookingIdToDelete }

                                                        if (userAppointmentToDelete != null) {
                                                            userAppointmentList.remove(
                                                                userAppointmentToDelete
                                                            )

                                                            // Update the user's appointment list in Firestore
                                                            FirebaseFirestore.getInstance()
                                                                .collection(Constants.USERS)
                                                                .document(FirestoreClass().getCurrentUserID())
                                                                .update(
                                                                    "userappointment",
                                                                    userAppointmentList
                                                                )
                                                                .addOnSuccessListener {
                                                                    // Both updates are successful
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Appointment Cancelled",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                    notifyDataSetChanged()
                                                                    if (context is BookingActivity) {
                                                                        context.updatelist()
                                                                    }
                                                                    val twilioApiService =
                                                                        createTwilioApiService()
                                                                    val toPhoneNumber =
                                                                        "+91" + userDetails.mobile.toString()  // Replace with the recipient's phone number
                                                                    val fromPhoneNumber =
                                                                        "+14043345873" // Replace with your Twilio phone number
                                                                    val message =
                                                                        "Appointment with ${doctorDetails.name}, Booking Id: ${bookingIdToDelete} is Cancelled"

// You should run this in a background thread or coroutine to avoid blocking the UI thread.
// For simplicity, we'll use a coroutine here.
                                                                    GlobalScope.launch(Dispatchers.IO) {
                                                                        try {
                                                                            val response =
                                                                                twilioApiService.sendSMS(
                                                                                    DoctorDescriptionActivity.TwilioConstants.ACCOUNT_SID,
                                                                                    toPhoneNumber,
                                                                                    fromPhoneNumber,
                                                                                    message
                                                                                ).execute()
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
                                                                    val twilioApiServicedoctor =
                                                                        createTwilioApiService()
                                                                    val toPhoneNumberdoctor =
                                                                        "+91" + doctorDetails.mobile.toString()  // Replace with the recipient's phone number
                                                                    val fromPhoneNumberdoctor =
                                                                        "+14043345873" // Replace with your Twilio phone number
                                                                    val messagedoctor =
                                                                        "Appointment with ${userDetails.name}, Booking Id: ${bookingIdToDelete} is Cancelled"

// You should run this in a background thread or coroutine to avoid blocking the UI thread.
// For simplicity, we'll use a coroutine here.
                                                                    GlobalScope.launch(Dispatchers.IO) {
                                                                        try {
                                                                            val response =
                                                                                twilioApiServicedoctor.sendSMS(
                                                                                    DoctorDescriptionActivity.TwilioConstants.ACCOUNT_SID,
                                                                                    toPhoneNumberdoctor,
                                                                                    fromPhoneNumberdoctor,
                                                                                    messagedoctor
                                                                                ).execute()
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
                                                                }
                                                                .addOnFailureListener { e ->
                                                                    // Handle user appointment update failure
                                                                }
                                                        } else {
                                                            // Handle case where user appointment not found
                                                        }
                                                    }
                                                    .addOnFailureListener { e ->
                                                        // Handle user appointment fetch failure
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                // Handle doctor appointment update failure
                                            }
                                    }

                                }
                                alertDialogBuilder.setNegativeButton("No") { _, _ ->
                                    // Dismiss the dialog when "No" is clicked
                                }
                                val alertDialog = alertDialogBuilder.create()
                                alertDialog.show()
                            }


                            // Set click listener for the button

                            // Apply animation
                            setAnimation(holder.itemView)
                        }
                    } else {
                        // The appointment has already passed, so hide the cancel_appointment button
                        holder.cancel_appointment.visibility = View.GONE
                    }
                    if (timeDifferenceHours > 0) {
                        // The appointment is in the future, so show the cancel_appointment button
                        holder.reschedule.visibility = View.VISIBLE

                        holder.reschedule.setOnClickListener {

                            // Parse date and time
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                            val appointmentDateTime = dateFormat.parse("${model.date} ${model.time}")

                            // Calculate time difference
                            val currentTime = Calendar.getInstance().time
                            val timeDifferenceMillis = appointmentDateTime.time - currentTime.time
                            val timeDifferenceHours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis)

                            if (timeDifferenceHours < 2) {
                                // Appointment cannot be canceled
                                val snackbarMessage = "Cannot cancel appointment. It is less than 2 hours before the appointment time."
                                val snackbar = Snackbar.make(holder.itemView, snackbarMessage, Snackbar.LENGTH_SHORT)
                                snackbar.show()
                            } else {

                                val alertDialogBuilder = AlertDialog.Builder(context)
                                alertDialogBuilder.setMessage("Are you sure you want to Reschedule this appointment?")
                                alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                                    val bookingIdToDelete = model.id
                                    val uniqueDates = java.util.ArrayList<String>()

                                    if (doctorDetails != null) {
                                        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


                                        for (timing in doctorDetails.timing) {

                                            val dateStr = timing.time

                                            try {
                                                val date = dateFormatter.parse(dateStr)
                                                if (date != null) {


                                                    uniqueDates.add(dateStr)

                                                }
                                            } catch (e: ParseException) {
                                                // Handle parsing exceptions if any
                                                e.printStackTrace()
                                            }
                                        }

                                    }


                                    val mPositionDate = uniqueDates.indexOf(model.date)
                                    val timeList: MutableList<String> = mutableListOf()
                                    if (mPositionDate >= 0 && mPositionDate < doctorDetails.timing.size) {
                                        val timingObject = doctorDetails.timing[mPositionDate]

                                        // Iterate through the dateSlotMap in the specific Timing object
                                        for (slotAvailability in timingObject.dateSlotMap) {
                                            // Extract the date and add it to the dateList
                                            timeList.add(slotAvailability.date)
                                        }
                                    }
                                    val mPositionTime = timeList.indexOf(model.time)
//

                                    // Update the doctor's appointment list
                                    val appointmentList = doctorDetails.appointment.toMutableList()
                                    val appointmentToDelete =
                                        appointmentList.find { it.id == bookingIdToDelete }

                                    if (appointmentToDelete != null) {
                                        appointmentList.remove(appointmentToDelete)

                                        // Update the doctorDetails object in Firestore
                                        if (mPositionDate >= 0 && mPositionDate < doctorDetails.timing.size) {
                                            val mPositionTime = // Calculate the position based on your logic
                                                if (mPositionTime >= 0 && mPositionTime < doctorDetails.timing[mPositionDate].dateSlotMap.size) {
                                                    val remainingSlot =
                                                        doctorDetails.timing[mPositionDate].dateSlotMap[mPositionTime].remainingSlots.toInt()
                                                    val totalBookedSlot =
                                                        doctorDetails.timing[mPositionDate].dateSlotMap[mPositionTime].totalBookedSlots.toInt()

                                                    // Update the values in the specific Timing object
                                                    doctorDetails.timing[mPositionDate].dateSlotMap[mPositionTime].remainingSlots =
                                                        (remainingSlot + 1).toString()
                                                    doctorDetails.timing[mPositionDate].dateSlotMap[mPositionTime].totalBookedSlots =
                                                        (totalBookedSlot - 1).toString()

                                                    val timingUser = doctorDetails.timing // Update the timing_user

                                                    val doctorHashMap = HashMap<String, Any>()
                                                    doctorHashMap[Constants.TIMING] = timingUser

                                                    FirebaseFirestore.getInstance()
                                                        .collection(Constants.DOCTOR)
                                                        .document(doctorDetails.documentId)
                                                        .update(doctorHashMap)
                                                        .addOnSuccessListener {
                                                            // Update successful
                                                            Toast.makeText(
                                                                context,
                                                                "Appointment Cancelled",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                        .addOnFailureListener { e ->
                                                            // Handle the update failure here
                                                            Toast.makeText(
                                                                context,
                                                                "Appointment Cancellation Failed: ${e.message}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                } else {

                                                }
                                        }

                                        FirebaseFirestore.getInstance().collection(Constants.DOCTOR)
                                            .document(model.doctor_id)
                                            .update("appointment", appointmentList)
                                            .addOnSuccessListener {
                                                // Update the user's appointment list
                                                FirebaseFirestore.getInstance()
                                                    .collection(Constants.USERS)
                                                    .document(FirestoreClass().getCurrentUserID())
                                                    .get()
                                                    .addOnSuccessListener { documentSnapshot ->
                                                        val userDetails =
                                                            documentSnapshot.toObject(User::class.java)
                                                        val userAppointmentList =
                                                            userDetails?.userappointment?.toMutableList()
                                                        val userAppointmentToDelete =
                                                            userAppointmentList?.find { it.id == bookingIdToDelete }

                                                        if (userAppointmentToDelete != null) {
                                                            userAppointmentList.remove(
                                                                userAppointmentToDelete
                                                            )

                                                            // Update the user's appointment list in Firestore
                                                            FirebaseFirestore.getInstance()
                                                                .collection(Constants.USERS)
                                                                .document(FirestoreClass().getCurrentUserID())
                                                                .update(
                                                                    "userappointment",
                                                                    userAppointmentList
                                                                )
                                                                .addOnSuccessListener {
                                                                    // Both updates are successful
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Appointment Cancelled, You can Reschedule Now",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                    notifyDataSetChanged()
                                                                    if (context is BookingActivity) {
                                                                        context.updatelist()
                                                                    }
                                                                    val twilioApiService =
                                                                        createTwilioApiService()
                                                                    val toPhoneNumber =
                                                                        "+91" + userDetails.mobile.toString()  // Replace with the recipient's phone number
                                                                    val fromPhoneNumber =
                                                                        "+14043345873" // Replace with your Twilio phone number
                                                                    val message =
                                                                        "Appointment with ${doctorDetails.name}, Booking Id: ${bookingIdToDelete} is Cancelled. you can reschedule your appointment now"

// You should run this in a background thread or coroutine to avoid blocking the UI thread.
// For simplicity, we'll use a coroutine here.
                                                                    GlobalScope.launch(Dispatchers.IO) {
                                                                        try {
                                                                            val response =
                                                                                twilioApiService.sendSMS(
                                                                                    DoctorDescriptionActivity.TwilioConstants.ACCOUNT_SID,
                                                                                    toPhoneNumber,
                                                                                    fromPhoneNumber,
                                                                                    message
                                                                                ).execute()
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
                                                                    val twilioApiServicedoctor =
                                                                        createTwilioApiService()
                                                                    val toPhoneNumberdoctor =
                                                                        "+91" + doctorDetails.mobile.toString()  // Replace with the recipient's phone number
                                                                    val fromPhoneNumberdoctor =
                                                                        "+14043345873" // Replace with your Twilio phone number
                                                                    val messagedoctor =
                                                                        "Appointment with ${userDetails.name}, Booking Id: ${bookingIdToDelete} is Cancelled"

// You should run this in a background thread or coroutine to avoid blocking the UI thread.
// For simplicity, we'll use a coroutine here.
                                                                    GlobalScope.launch(Dispatchers.IO) {
                                                                        try {
                                                                            val response =
                                                                                twilioApiServicedoctor.sendSMS(
                                                                                    DoctorDescriptionActivity.TwilioConstants.ACCOUNT_SID,
                                                                                    toPhoneNumberdoctor,
                                                                                    fromPhoneNumberdoctor,
                                                                                    messagedoctor
                                                                                ).execute()
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
                                                                }
                                                                .addOnFailureListener { e ->
                                                                    // Handle user appointment update failure
                                                                }
                                                        } else {
                                                            // Handle case where user appointment not found
                                                        }
                                                    }
                                                    .addOnFailureListener { e ->
                                                        // Handle user appointment fetch failure
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                // Handle doctor appointment update failure
                                            }
                                    }
                                    if (onClickListener != null) {
                                        onClickListener!!.onClick(position, model)
                                    }

                                }
                                alertDialogBuilder.setNegativeButton("No") { _, _ ->
                                    // Dismiss the dialog when "No" is clicked
                                }
                                val alertDialog = alertDialogBuilder.create()
                                alertDialog.show()
                            }


                            // Set click listener for the button

                            // Apply animation
                            setAnimation(holder.itemView)
                        }
                    } else {
                        // The appointment has already passed, so hide the cancel_appointment button
                        holder.reschedule.visibility = View.GONE
                        holder.bookingStatus.text = "Expired"
                    }

                }

            }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: AppointmentUser)
    }

    private fun setAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 1000
        view.startAnimation(anim)
    }
}
