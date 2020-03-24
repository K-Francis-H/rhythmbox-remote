package com.kyle.rhythmboxremote

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_scanner.*
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import java.nio.ByteOrder
import kotlin.concurrent.thread

class ScannerActivity : AppCompatActivity() {
    private val TAG = "ScannerActivity"

    private lateinit var list: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        list = findViewById<ListView>(R.id.saved_server_list)
        val scanStatus = findViewById<TextView>(R.id.scanning_status)
        val progress = findViewById<ProgressBar>(R.id.progress)

        var array = ArrayList<String>()
        list.adapter = ServerAdapter(this, array)

        scan()
    }

    private fun scan(){
        thread{
            val wifiMan = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            var rawIp = wifiMan.connectionInfo.ipAddress
            if(ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)){
                rawIp = Integer.reverseBytes(rawIp)
            }
            val ipBytes = rawIp.toBigInteger().toByteArray()
            val ip = InetAddress.getByAddress(ipBytes).hostAddress//InetAddress.getLocalHost().hostAddress

            Log.d(TAG, "ip: $ip")
            val ipParts = ip.split(".")
            val netMask = ipParts[0]+"."+ipParts[1]+"."+ipParts[2]

            //now scan the network on X.X.X.y where y is on [0,255] until we find a port open and get a return from our ping
            for(i in 0..255){
                Log.d(TAG, "checking $netMask.$i")
                if(i.toString() != ipParts[3]){
                    try {
                        //URL("http://$netMask.$i:10803/rhythmbox/ping").
                        with(URL("http://$netMask.$i:10803/rhythmbox/ping").openConnection() as HttpURLConnection) {
                            connectTimeout = 100 //ms , fail fast if no reply
                            Log.v(TAG, "scan(): $responseCode")
                            if (responseCode == 200) {
                                Log.d(TAG, "found server on $netMask.$i")
                                //TODO use this to create list item for user to select
                                runOnUiThread {
                                    //add an item to the list and continue scanning
                                    val updatedArray = (list.adapter as ServerAdapter).getData()
                                    updatedArray.add("$netMask.$i")
                                    (list.adapter as ServerAdapter).notifyDataSetChanged()
                                }
                                return@thread
                            }
                        }
                    }catch (e : Exception){
                        //no server continue
                        Log.d(TAG, "no server at $netMask.$i")
                    }
                }
            }

            runOnUiThread {
                progress.visibility = View.INVISIBLE
                scanning_status.text = getString(R.string.scan_complete)
            }
        }
    }
}