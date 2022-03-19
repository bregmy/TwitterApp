package com.codepath.apps.restclienttemplate

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {

    lateinit var  client: TwitterClient
    lateinit var rvTweets: RecyclerView
    lateinit var adapter: TweetsAdapter

    lateinit var swipeContainer: SwipeRefreshLayout

    val tweets = ArrayList<Tweet>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client= TwitterApplication.getRestClient(this)

        swipeContainer= findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            Log.i(TAG,"Rfereshing Timelein")
            populateHomeTimeline()
        }

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

        rvTweets=findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(tweets)
        rvTweets.layoutManager=LinearLayoutManager(this)
        rvTweets.adapter=adapter


        populateHomeTimeline()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.compose){
            val intent= Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE){

            //Getting the data from our Intent (our tweet)
            val tweet = data?.getParcelableExtra<Tweet>("tweet") as Tweet

            // Update Timeline
            // Modifying the data source of our tweets
            tweets.add(0, tweet)

            // Update the adapter
            adapter.notifyItemInserted(0)
            rvTweets.smoothScrollToPosition(0)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    var editActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // If the user comes back to this activity from EditActivity
        // with no error or cancellation
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            // Get the data passed from EditActivity
            if (data != null) {
                val editedString = data.extras!!.getString("newString")
            }
        }
    }




    fun populateHomeTimeline(){
        client.getHomeTimeline(object: JsonHttpResponseHandler(){


            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "onSuccess")
                val jsonArray = json.jsonArray
                try {
                    adapter.clear()
                    val listOfNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                    tweets.addAll(listOfNewTweetsRetrieved)
                    adapter.notifyDataSetChanged()
                    swipeContainer.setRefreshing(false)
                }catch(e: JSONException){
                    Log.e(TAG, "JSON Exception $e")
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                TODO("Not yet implemented")
            }



        })
    }
    companion object{
         val TAG="TimelineActivity"
        val REQUEST_CODE=10
    }
}