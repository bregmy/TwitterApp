package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button

    lateinit var  client: TwitterClient
    lateinit var charCount: TextView

    val charCounter=280







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        etCompose=findViewById(R.id.etTweetCompose)
        btnTweet=findViewById(R.id.btnTweet)
        charCount = findViewById(R.id.charCount)
        client= TwitterApplication.getRestClient(this)

        val etValue = findViewById(R.id.etTweetCompose) as EditText
        etValue.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Fires right as the text is being changed (even supplies the range of text)

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Fires right before text is changing
            }

            override fun afterTextChanged(s: Editable) {
                // Fires right after the text has changed
                //tvDisplay.setText(s.toString())
                var remChars = "Remaining Charcters: "+(charCounter - s.length)
                charCount.setText(remChars)

                if (s.length > charCounter){
                    // Disable the tweet button
                    btnTweet.setEnabled(false)
                    // Make the count red
                    charCount.setTextColor(Color.parseColor("#FF0000"))

                    var remChars = "Exceeded Charcters: "+(charCounter - s.length)*(-1)
                    charCount.setText(remChars)

                }else{
                    // Disable the tweet button
                    btnTweet.setEnabled(true)
                    // Make the count red
                    charCount.setTextColor(Color.parseColor("#000000"))
                }
            }
        })














        btnTweet.setOnClickListener{
            val tweetContent = etCompose.text.toString()

            if (tweetContent.isEmpty()){

                Toast.makeText(this, "empty tweets not allowed", Toast.LENGTH_SHORT).show()

            }else
            if (tweetContent.length>140){
                Toast.makeText(this ,"Tweet is too Long! Limit is 140 character",Toast.LENGTH_SHORT).show()

            }else{
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler (){

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG,"Successfully publish tweet!")

                        val tweet= Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet",tweet)
                        setResult(RESULT_OK,intent)
                        finish()

                    }



                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish the tweet",throwable)

                    }


                })

            }

        }
    }
    companion object{
        val TAG ="composeActivity"
    }
}