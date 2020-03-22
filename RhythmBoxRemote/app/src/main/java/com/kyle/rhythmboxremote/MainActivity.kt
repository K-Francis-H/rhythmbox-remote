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
    private val TAG = "MainActivity"

    private val handler = Handler()

    private var volume= 0;

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

        //volume control
        val volumeLabel = findViewById<TextView>(R.id.volume_label)

        val volumeDown = findViewById<TextView>(R.id.volume_down)
        val volumeUp = findViewById<TextView>(R.id.volume_up)
        val volumeMute = findViewById<TextView>(R.id.volume_mute)

        volumeDown.setOnClickListener { setVolume(volume-5) }
        volumeUp.setOnClickListener { setVolume(volume+5) }
        volumeMute.setOnClickListener { setVolume(0) }


        //TODO make ip dynamically changeable by user and not fixed to my machine
    }

    override fun onResume() {
        super.onResume()

        updateTrackInfo()
        handler.post {infoLoop()}

        getVolume()
        handler.post {volumeLoop()}
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

    private fun stop(){
        thread{
            with(URL("http://192.168.0.15:10803/rhythmbox/stop").openConnection() as HttpURLConnection){
                Log.v(TAG, "stop(): $responseCode")
                //if its bad do something to tell user
            }
        }
    }

    private fun play(){
        thread{
            with(URL("http://192.168.0.15:10803/rhythmbox/play").openConnection() as HttpURLConnection){
                Log.v(TAG, "play(): $responseCode")
                //if its bad do something to tell user
            }
        }
    }

    private fun pause(){
        thread{
            with(URL("http://192.168.0.15:10803/rhythmbox/pause").openConnection() as HttpURLConnection){
                Log.v(TAG, "pause(): $responseCode")
                //if its bad do something to tell user
            }
        }
    }

    private fun next(){
        thread{
            with(URL("http://192.168.0.15:10803/rhythmbox/next").openConnection() as HttpURLConnection){
                Log.v(TAG, "next(): $responseCode")
                //if its bad do something to tell user
            }
        }
    }

    private fun infoLoop(){
        updateTrackInfo()
        handler.postDelayed({ infoLoop() }, 1000)
    }

    private fun updateTrackInfo(){
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

    private fun volumeLoop(){
        getVolume()
        handler.postDelayed({ volumeLoop() }, 1000)
    }

    private fun getVolume(){
        thread{
            val raw = URL("http://192.168.0.15:10803/volume/get").readText()
            val json = JSONObject(raw)
            Log.v(TAG, "volume: "+json.getInt("volume"))
            runOnUiThread{
                volume = json.getInt("volume")
                volume_label.text = getString(R.string.volume_format, json.getInt("volume"))
            }
        }
    }

    private fun setVolume(value : Int){
        volume = value
        thread{
            with(URL("http://192.168.0.15:10803/volume/set/$value").openConnection() as HttpURLConnection){
                Log.v(TAG, "setVolume(): $responseCode")
            }
        }
    }
}