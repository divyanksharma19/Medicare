package com.example.medicare.activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.medicare.R
import com.example.medicare.adapter.MessagingAdapter
import com.example.medicare.models.Message
import com.example.medicare.utils.BotResponse
import com.example.medicare.utils.Constants.OPEN_GOOGLE
import com.example.medicare.utils.Constants.OPEN_SEARCH
import com.example.medicare.utils.Constants.RECEIVE_ID
import com.example.medicare.utils.Constants.SEND_ID
import com.example.medicare.utils.Time
//import com.google.api.gax.core.FixedCredentialsProvider
//import com.google.auth.oauth2.GoogleCredentials
//import com.google.auth.oauth2.ServiceAccountCredentials
//import com.google.cloud.dialogflow.v2.DetectIntentRequest
//import com.google.cloud.dialogflow.v2.DetectIntentResponse
//import com.google.cloud.dialogflow.v2.QueryInput
//import com.google.cloud.dialogflow.v2.SessionName
//import com.google.cloud.dialogflow.v2.SessionsClient
//import com.google.cloud.dialogflow.v2.SessionsSettings
//import com.google.cloud.dialogflow.v2.TextInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*

import java.util.UUID

class MediChatbot : AppCompatActivity() {
    private lateinit var adapter: MessagingAdapter
    private val client = OkHttpClient()
//    private var sessionsClient: SessionsClient? = null
//    private var sessionName: SessionName? = null
    private val uuid = UUID.randomUUID().toString()
    var messagesList = mutableListOf<Message>()
    private val botList = listOf("Peter", "Francesca", "Luigi", "Igor")
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medi_chatbot)
        recyclerView()
        clickEvents()

        val random = (0..3).random()
        customBotMessage("Hello! Today you're speaking with ${botList[random]}, how may I help?")
    }

    private fun recyclerView() {
        adapter = MessagingAdapter()
        findViewById<RecyclerView>(R.id.rv_messages).adapter = adapter
        findViewById<RecyclerView>(R.id.rv_messages).layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun clickEvents() {
        // Send a message
        findViewById<Button>(R.id.btn_send).setOnClickListener {
            sendMessage()
        }

        // Scroll back to the correct position when the user clicks on the text view
        findViewById<EditText>(R.id.et_message).setOnClickListener {
            GlobalScope.launch {
                delay(100)
                withContext(Dispatchers.Main) {
                    findViewById<RecyclerView>(R.id.rv_messages).scrollToPosition(adapter.itemCount - 1)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // In case there are messages, scroll to the bottom when reopening the app
        GlobalScope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                findViewById<RecyclerView>(R.id.rv_messages).scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun sendMessage() {
        val message = findViewById<EditText>(R.id.et_message).text.toString()
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            // Adds it to our local list
            messagesList.add(Message(message, SEND_ID, timeStamp))
            findViewById<EditText>(R.id.et_message).setText("")

            adapter.insertMessage(Message(message, SEND_ID, timeStamp))
            findViewById<RecyclerView>(R.id.rv_messages).scrollToPosition(adapter.itemCount - 1)

            botResponse(message)
        }
    }


//    fun mainans(message:String) {
//
//    }
//
//    private fun setUpBot() {
//        try {
//            val stream = this.resources.openRawResource(R.raw.credential)
//            val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
//                .createScoped("https://www.googleapis.com/auth/cloud-platform")
//            val projectId: String = (credentials as ServiceAccountCredentials).projectId
//            val settingsBuilder: SessionsSettings.Builder = SessionsSettings.newBuilder()
//            val sessionsSettings: SessionsSettings = settingsBuilder.setCredentialsProvider(
//                FixedCredentialsProvider.create(credentials)
//            ).build()
//            sessionsClient = SessionsClient.create(sessionsSettings)
//            sessionName = SessionName.of(projectId, uuid)
//            Log.d(TAG, "projectId: $projectId")
//            stream.close() // Close the input stream
//        } catch (e: IOException) {
//            Log.e(TAG, "setUpBot: " + e.message)
//            e.printStackTrace()
//        }
//    }

//    private fun sendMessageToBot(message: String) {
//        val input = QueryInput.newBuilder()
//            .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build()
//        GlobalScope.launch {
//            sendMessageInBg(input)
//        }
//    }
//
//    private suspend fun sendMessageInBg(queryInput: QueryInput) {
//        withContext(Dispatchers.Default) {
//            try {
//                val detectIntentRequest = DetectIntentRequest.newBuilder()
//                    .setSession(sessionName.toString())
//                    .setQueryInput(queryInput)
//                    .build()
//                val result = sessionsClient?.detectIntent(detectIntentRequest)
//                if (result != null) {
//                    runOnUiThread {
//                        updateUI(result)
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "sendMessageInBg: " + e.message)
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun updateUI(response: DetectIntentResponse) {
//        val timeStamp = Time.timeStamp()
//        val botReply: String = response.queryResult.fulfillmentText
//        if (botReply.isNotEmpty()) {
//            messagesList.add(Message(botReply, RECEIVE_ID, timeStamp))
//
//            // Inserts our message into the adapter
//            adapter.insertMessage(Message(botReply, RECEIVE_ID, timeStamp))
//
//            // Scrolls to the position of the latest message
//            findViewById<RecyclerView>(R.id.rv_messages).scrollToPosition(adapter.itemCount - 1)
//        } else {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
//        }
//    }
private fun botResponse(message: String) {
    val timeStamp = Time.timeStamp()

    GlobalScope.launch {
        //Fake response delay
        delay(1000)

        withContext(Dispatchers.Main) {
            //Gets the response
            val response = BotResponse.basicResponses(message)

            //Adds it to our local list
            messagesList.add(Message(response, RECEIVE_ID, timeStamp))

            //Inserts our message into the adapter
            adapter.insertMessage(Message(response, RECEIVE_ID, timeStamp))

            //Scrolls us to the position of the latest message
            findViewById<RecyclerView>(R.id.rv_messages).scrollToPosition(adapter.itemCount - 1)

            //Starts Google
            when (response) {
                OPEN_GOOGLE -> {
                    val site = Intent(Intent.ACTION_VIEW)
                    site.data = Uri.parse("https://www.google.com/")
                    startActivity(site)
                }
                OPEN_SEARCH -> {
                    val site = Intent(Intent.ACTION_VIEW)
                    val searchTerm: String? = message.substringAfterLast("search")
                    site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                    startActivity(site)
                }

            }
        }
    }
}
    private fun customBotMessage(message: String) {
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val timeStamp = Time.timeStamp()
                messagesList.add(Message(message, RECEIVE_ID, timeStamp))
                adapter.insertMessage(Message(message, RECEIVE_ID, timeStamp))
                findViewById<RecyclerView>(R.id.rv_messages).scrollToPosition(adapter.itemCount - 1)
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()

        // Navigate to MainActivity with the home menu item selected
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("selectedItemId", R.id.home) // Pass the ID of the home menu item
        startActivity(intent)
        finish()
    }
}
