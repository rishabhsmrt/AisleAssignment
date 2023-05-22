package com.example.aisleassignment

import okhttp3.*
import android.os.Bundle
import java.io.IOException
import org.json.JSONObject
import org.json.JSONException
import android.util.Log
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class Feed : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feed)

        val intent = intent
        val token: String? = intent.getStringExtra("token")
        token?.let { makeAuthAPICall(it) }
    }

    private fun makeAuthAPICall(token: String) {
        val request = Request.Builder()
            .url("https://testa2.aisle.co/V1/users/test_profile_list")
            .header("Authorization", token)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("TAG", "Response Data: $responseData")

                try {
                    val jsonResponse = JSONObject(responseData)
                    val invitesObject = jsonResponse.getJSONObject("invites")
                    val profilesArray = invitesObject.getJSONArray("profiles")
                    val firstProfile = profilesArray.getJSONObject(0)

                    // Extract required information from the JSON response
                    val firstName = firstProfile.getJSONObject("general_information").getString("first_name")
                    val locationSummary = firstProfile.getJSONObject("general_information").getJSONObject("location").getString("summary")
                    val avatarUrl = firstProfile.getJSONArray("photos").getJSONObject(0).getString("photo")

                    // Update UI on the main thread
                    runOnUiThread {
                        updateUI(firstName, locationSummary, avatarUrl)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun updateUI(firstName: String, locationSummary: String, avatarUrl: String) {
        // Update the UI with the extracted information
        val firstNameTextView = findViewById<TextView>(R.id.firstNameTextView)
        val locationSummaryTextView = findViewById<TextView>(R.id.locationSummaryTextView)
        val avatarImageView = findViewById<ImageView>(R.id.avatarImageView)

        firstNameTextView.text = firstName
        locationSummaryTextView.text = locationSummary

        // Use an image loading library (e.g., Picasso, Glide) to load the avatar image
        // Picasso.get().load(avatarUrl).into(avatarImageView)
    }
}
