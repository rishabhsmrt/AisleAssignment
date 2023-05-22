package com.example.aisleassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import okhttp3.*
import java.io.IOException
import org.json.JSONObject
import org.json.JSONException
import android.util.Log

class MainActivity : AppCompatActivity() {
    private lateinit var btnContinue: Button
    private lateinit var countryCode: EditText
    private lateinit var phoneNumber: EditText
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnContinue = findViewById(R.id.btnContinue)
        countryCode = findViewById(R.id.countryCode)
        phoneNumber = findViewById(R.id.phoneNumber)

        btnContinue.setOnClickListener {
            // Make API call
            val userCountryCode: String = countryCode.text.toString()
            val userPhoneNumber: String = phoneNumber.text.toString()
            makePhoneNumberAPICall(userCountryCode, userPhoneNumber)
        }
    }

    private fun makePhoneNumberAPICall(userCountryCode: String, userPhoneNumber: String) {
        val requestBody = FormBody.Builder()
            .add("number", userCountryCode + userPhoneNumber)
            .build()

        val request = Request.Builder()
            .url("https://testa2.aisle.co/V1/users/phone_number_login")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the failure case
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("TAG", "Response Data: $responseData")

                try {
                    val jsonResponse = JSONObject(responseData)
                    val status = jsonResponse.getBoolean("status")

                    if (status) {
                        val userCountryCode: String = countryCode.text.toString()
                        val userPhoneNumber: String = phoneNumber.text.toString()

                        val intent = Intent(this@MainActivity, VerifyOTP::class.java).apply {
                            putExtra("countryCode", userCountryCode)
                            putExtra("phoneNumber", userPhoneNumber)
                        }
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

        })
    }
}
