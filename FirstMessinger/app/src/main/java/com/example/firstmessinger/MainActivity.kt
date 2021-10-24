package com.example.firstmessinger

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Messenger
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.firstmessinger.service.MessengerContract


class MainActivity : AppCompatActivity() {

    var mBound = false
    lateinit var fetchDataTextView: TextView
    lateinit var mButton: Button
    lateinit var unbindButton: Button
    lateinit var bindButton: Button

    var msgCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fetchDataTextView = findViewById(R.id.textView)

//        MessengerService().incommingHandler.onHelloReceived = {
//            Log.i("BOBAN", "RSIVED")
//            msgCount++
//            fetchDataTextView.text = msgCount.toString()
//        }

    }

    private fun onButtonClick() {
        if (boundToService) {
            Log.e("BOBAN", "is service calls null? ${serviceCallsMessenger == null}")
            val oneWayMessage = MessengerContract.SayHello.buildRequestMessage("BLA")
            serviceCallsMessenger?.send(oneWayMessage)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.e("BOBAN", "BINDING")
        bindToService()

    }

    fun bindToService() {
        bindService(
            MessengerContract.serviceBindIntent,
            messengerServiceConnection,
            BIND_AUTO_CREATE
        )
    }

    var boundToService = false
    private var serviceCallsMessenger: Messenger? = null

    private val messengerServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("BOBAN", "disconnected")
            fetchDataTextView.text = "Disconncted!"
            boundToService = false
            serviceCallsMessenger = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e("BOBAN", "conneceted")
            fetchDataTextView.text = "Connected!"
            serviceCallsMessenger = Messenger(service)

            boundToService = true
        }
    }

    override fun onStop() {
        super.onStop()
        unbindFromService()
    }

    private fun unbindFromService() {
        if (boundToService) {
            unbindService(messengerServiceConnection)
            boundToService = false
            serviceCallsMessenger = null
        }
    }
}

