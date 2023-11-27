package com.example.medicare.utils


import android.util.Log
import com.example.medicare.Firebase.FirestoreClass
import com.example.medicare.models.Appointment
import com.example.medicare.models.AppointmentUser
import com.example.medicare.models.Doctor
import com.example.medicare.models.User
import com.example.medicare.utils.Constants.OPEN_GOOGLE
import com.example.medicare.utils.Constants.OPEN_SEARCH
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

object BotResponse {

    suspend fun basicResponses(_message: String): Any {

        val random = (0..2).random()
        val message =_message.toLowerCase()

        return when {

            //Flips a coin
            message.contains("flip") && message.contains("coin") -> {
                val r = (0..1).random()
                val result = if (r == 0) "heads" else "tails"

                "I flipped a coin and it landed on $result"
            }

            //Math calculations
            message.contains("solve") -> {
                val equation: String? = message.substringAfterLast("solve")
                return try {
                    val answer = SolveMath.solveMath(equation ?: "0")
                    "$answer"

                } catch (e: Exception) {
                    "Sorry, I can't solve that."
                }
            }

            //Hello
            message.contains("hello") -> {
                when (random) {
                    0 -> "Hello there!"
                    1 -> "Sup"
                    2 -> "Hello!!"
                    else -> "error" }
            }

            //How are you?
            message.contains("how are you") -> {
                when (random) {
                    0 -> "I'm doing fine, thanks!"
                    1 -> "I'm hungry..."
                    2 -> "Pretty good! How about you?"
                    else -> "error"
                }
            }
            message.contains("symptoms") && message.contains("fever") -> {
                "Fever symptoms: \n" +
                        "1. High body temperature\n" +
                        "2. Chills and shivering\n" +
                        "3. Sweating\n" +
                        "4. Headache\n" +
                        "5. Muscle aches\n" +
                        "6. Fatigue\n" +
                        "7. Loss of appetite\n" +
                        "8. Dehydration\n" +
                        "9. Elevated heart rate\n" +
                        "10. Irritability"

            }
            // Common Cold
            message.contains("symptoms") && (message.contains("cold") || message.contains("common cold")) -> {
                "Common cold symptoms:\n" +
                        "1. Runny or stuffy nose\n" +
                        "2. Sneezing\n" +
                        "3. Coughing\n" +
                        "4. Sore throat\n" +
                        "5. Mild headache\n" +
                        "6. Fatigue\n" +
                        "7. Watery eyes\n" +
                        "8. Low-grade fever\n" +
                        "9. Chest discomfort"
            }

            // Influenza (Flu)
            message.contains("symptoms") && message.contains("flu") -> {
                "Flu (Influenza) symptoms:\n" +
                        "1. High fever\n" +
                        "2. Chills\n" +
                        "3. Muscle aches\n" +
                        "4. Fatigue\n" +
                        "5. Cough\n" +
                        "6. Sore throat\n" +
                        "7. Runny or stuffy nose\n" +
                        "8. Headache\n" +
                        "9. Vomiting and diarrhea (in some cases)"
            }

            // COVID-19 (Coronavirus)
            message.contains("symptoms") && (message.contains("covid") || message.contains("coronavirus") || message.contains("corona")) -> {
                "COVID-19 (Coronavirus) symptoms:\n" +
                        "1. Fever\n" +
                        "2. Dry cough\n" +
                        "3. Shortness of breath or difficulty breathing\n" +
                        "4. Fatigue\n" +
                        "5. Muscle or body aches\n" +
                        "6. Headache\n" +
                        "7. New loss of taste or smell\n" +
                        "8. Sore throat\n" +
                        "9. Congestion or runny nose\n" +
                        "10. Nausea or vomiting\n" +
                        "11. Diarrhea"
            }
            message.contains("symptoms") && (message.contains("stroke") ) -> {
                "Stroke symptoms: \n"+"1. Sudden numbness or weakness in the face, arm, or leg\n" +
                        "2. Sudden confusion, trouble speaking, or understanding speech\n" +
                        "3. Sudden trouble seeing in one or both eyes\n" +
                        "4. Sudden trouble walking, dizziness, loss of balance, or lack of coordination\n" +
                        "5. Sudden severe headache"
            }
            message.contains("symptoms") && (message.contains("lung cancer") ) -> {
                "Lung Cancer symptoms: \n"+ "1. Persistent cough\n" +
                        "2. Coughing up blood\n" +
                        "3. Chest pain\n" +
                        "4. Unexplained weight loss\n" +
                        "5. Shortness of breath"
            }
//            message.contains("thanks") || (message.contains("thank you") || (message.contains("ok")) ) -> {
//                "Have a Nice Day"
//            }
           message.contains("my bookings") ||( message.contains("My bookings") ||  ( message.contains("todays appointments ") || message.contains("appointment") ))->{
               var ans = ""

               val documentSnapshot = FirebaseFirestore.getInstance()
                   .collection(Constants.USERS)
                   .document(FirestoreClass().getCurrentUserID())
                   .get()
                   .await()

               val userDetails = documentSnapshot.toObject(User::class.java)

               if (userDetails != null) {
                   val appointmentdetails = userDetails.userappointment
                   val currentDate = Date(System.currentTimeMillis())
                   val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                   val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                   val upcomingAppointments = appointmentdetails.filter { appointment ->
                       val appointmentDate = dateFormat.parse(appointment.date)

                       if (appointmentDate.after(currentDate)) {
                           true
                       } else {
                           val appointmentTime = timeFormat.parse(appointment.time)
                           val currentTime = timeFormat.parse(timeFormat.format(java.util.Date()))

                           if (dateFormat.format(appointmentDate) == dateFormat.format(currentDate)) {
                               if (appointmentTime < currentTime) {
                                   false
                               } else {
                                   true
                               }
                           } else {
                               false
                           }
                       }
                   }


                   if (upcomingAppointments.isEmpty()) {
                       ans = "You have No Upcoming Appointment"
                   } else {
                       val doctorDetailsList = mutableListOf<Doctor>()

                       for (appointment in upcomingAppointments) {
                           val doctorDocument = FirebaseFirestore.getInstance()
                               .collection(Constants.DOCTOR)
                               .document(appointment.doctor_id)
                               .get()
                               .await()

                           val doctor = doctorDocument.toObject(Doctor::class.java)
                           doctor?.let {
                               doctorDetailsList.add(it)
                           }
                       }

                       ans = upcomingAppointments.mapIndexed { index, it ->
                           "Your Booking Id: ${it.id} on ${it.date}, Timing: ${it.time} with  ${doctorDetailsList.getOrNull(index)?.name} \n"
                       }.joinToString("\n")
                   }
               }

// Handle the response or return the result as needed within your app.
// For example, you can send 'ans' to a chat interface or display it to the user.

// If this code is part of a function, you may need to replace the "return ans" statement with appropriate code to deliver or display the response
               return ans
            }
            message.contains("thanks") || (message.contains("bye") || message.contains("thank"))  -> {
                "Have a Nice Today"
            }

                message.contains("time") && message.contains("?")-> {
                val timeStamp = Timestamp(System.currentTimeMillis())
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val date = sdf.format(Date(timeStamp.time))

                date.toString()
            }


            //Open Google
            message.contains("open") && message.contains("google")-> {
                OPEN_GOOGLE
            }

            //Search on the internet
            message.contains("search")-> {
                OPEN_SEARCH
            }

            //When the programme doesn't understand...
            else -> {
                when (random) {
                    0 -> "I don't understand..."
                    1 -> "Try asking me something different"
                    2 -> "Idk"
                    else -> "error"
                }
            }
        }
    }
}