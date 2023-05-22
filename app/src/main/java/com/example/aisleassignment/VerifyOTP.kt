package com.example.aisleassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.content.Intent
import okhttp3.*
import java.io.IOException
import org.json.JSONObject
import org.json.JSONException
import android.util.Log
import android.os.CountDownTimer

class VerifyOTP : AppCompatActivity() {
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var timerTextView: TextView
    private val client = OkHttpClient() // Create an instance of OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_otp)

        timerTextView = findViewById(R.id.timerTextView)

        // Start the countdown timer for 1 minute (60000 milliseconds)
        startCountdownTimer(60000)

        val intent = intent
        val countryCodeValue: String? = intent.getStringExtra("countryCode")
        val phoneNumberValue: String? = intent.getStringExtra("phoneNumber")

        val btnContinue: Button = findViewById(R.id.btnContinueToFeed)
        val countryCode: TextView = findViewById(R.id.countryCode)
        val phoneNumber: TextView = findViewById(R.id.phoneNumber)
        val otpNumber: EditText = findViewById(R.id.editOTPNumber)


        countryCode.text = countryCodeValue
        phoneNumber.text = phoneNumberValue

        val otp: String = otpNumber.text.toString()
        btnContinue.setOnClickListener {
            val userCountryCode: String = countryCode.text.toString()
            val userPhoneNumber: String = phoneNumber.text.toString()
            val otp: String = otpNumber.text.toString()
            makeOTPAPICall(otp, countryCodeValue + phoneNumberValue)
        }
    }
    private fun makeOTPAPICall(otp: String, number: String){

        val requestBody = FormBody.Builder()
            .add("number", number)
            .add("otp", otp)
            .build()

        val request = Request.Builder()
            .url("https://testa2.aisle.co/V1/users/verify_otp")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            private fun extractTokenFromJson(json: JSONObject): String? {
                return try {
                    json.getString("token")
                } catch (e: JSONException) {
                    e.printStackTrace()
                    null
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("TAG", "Response Data: $responseData")

                try {
                    val jsonResponse = JSONObject(responseData)

                    if (true) {
                        val token = extractTokenFromJson(jsonResponse)
                        if (token != null) {
                            // Token extraction successful, do something with the token
                            Log.d("TAG", "Token: $token")
                            val intentFeed = Intent(this@VerifyOTP, Feed::class.java).apply {
                                // Add any extra data or flags to the intent if needed
                                putExtra("token", token)
                            }
                            startActivity(intentFeed)
                        } else {
                            // Token extraction failed, handle the error
                            Log.d("TAG", "Failed to extract token from JSON")
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

        })
    }
    private fun startCountdownTimer(millisInFuture: Long) {
        countdownTimer = object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text = "00:$secondsRemaining"
            }

            override fun onFinish() {
                timerTextView.text = "Time's up!"
                // Perform any actions you want to take after the timer finishes
            }
        }

        countdownTimer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the countdown timer to avoid memory leaks
        if (::countdownTimer.isInitialized) {
            countdownTimer.cancel()
        }
    }
}
