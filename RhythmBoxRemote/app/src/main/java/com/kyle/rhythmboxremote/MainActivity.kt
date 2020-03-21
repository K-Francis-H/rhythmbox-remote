package com.kyle.rhythmboxremote

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import org.w3c.dom.Text
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //track info
        val track = findViewById<TextView>(R.id.track)
        val artist = findViewById<TextView>(R.id.artist)
        val album = findViewById<TextView>(R.id.album)
        val time = findViewById<TextView>(R.id.time)

        //media buttons
        val prev = findViewById<TextView>(R.id.previous)
        val stop = findViewById<TextView>(R.id.stop)
        val play = findViewById<TextView>(R.id.play)
        val pause = findViewById<TextView>(R.id.pause)
        val next = findViewById<TextView>(R.id.next)

        prev.setOnClickListener { previous() }
        stop.setOnClickListener { stop() }
        play.setOnClickListener { play() }
        pause.setOnClickListener { pause()}
        next.setOnClickListener { next() }



        updateTrackInfo()
        handler.post {infoLoop()}

        //TODO make ip dynamically changeable by user and not fixed to my machine
    }

    override fun onStop(){
        super.onStop()

        handler.removeCallbacksAndMessages(null)
    }

    private fun previous(){
        thread{
            with(URL("http://192.168.0.15:10803/rhythmbox/previous").openConnection() as HttpURLConnection){
                Log.v(TAG, "previous(): $responseCode")
                //if its bad do something to tell user
            }
        }
    }

    fun stop(){
        thread{
            with(URL("http://192.168.0.15:10803/rhythmbox/stop").openConnection() as HttpURLConnection){
                Log.v(TAG, "stop(): $responseCode")
                //if its bad do something to tell user
            }
        }
    }

    fun play(){
        thread{
            with(URL("http://192.168.0.15:10803/rhythmbox/play").openConnection() as HttpURLConnection){
                Log.v(TAG, "play(): $responseCode")
                //if its bad do something to tell user
            }
        }
    }

    fun pause(){
        thread{
            with(URL("http://192.168.0.15:10803/rhythmbox/pause").openConnection() as HttpURLConnection){
                Log.v(TAG, "pause(): $responseCode")
                //if its bad do something to tell user
            }
        }
    }

    fun next(){
        thread{
            with(URL("http://192.168.0.15:10803/rhythmbox/next").openConnection() as HttpURLConnection){
                Log.v(TAG, "next(): $responseCode")
                //if its bad do something to tell user
            }
        }
    }

    fun infoLoop(){
        updateTrackInfo()
        handler.postDelayed({ infoLoop() }, 1000)
    }

    fun updateTrackInfo(){
        thread {
            val raw = URL("http://192.168.0.15:10803/rhythmbox/current-song").readText()
            val json = JSONObject(raw)
            runOnUiThread{
                track.text = json.getString("track")
                artist.text = json.getString("artist")
                album.text = json.getString("album")
                time.text = getString(R.string.time_format, json.getString("elapsed"), json.getString("duration"))
            }
        }
    }

    fun getVolume(){
        thread{
            val raw = URL("http://192.168.0.15:10803/volume/get").readText()
            val json = JSONObject(raw)
            Log.v(TAG, "volume: "+json.getString("volume"))
        }
    }

    fun setVolume(value : Int){
        thread{
            with(URL("http://192.168.0.15:10803/volume/set/"+value).openConnection() as HttpURLConnection){
                Log.v(TAG, "setVolume(): $responseCode")
            }
        }
    }
}