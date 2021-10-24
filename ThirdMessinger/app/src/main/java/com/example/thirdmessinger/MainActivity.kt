package com.example.thirdmessinger

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    var boundToService = false

    lateinit var textStatus: TextView
    lateinit var sentMessages: TextView
    lateinit var receivedMessages: TextView

    var sent = 0
    var received = 0


    // Messenger on the server
    private var serverMessenger: Messenger? = null

    // Messenger on the client
    private var clientMessenger: Messenger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: Button = findViewById(R.id.connectButton)
        button.setOnClickListener {
            if (boundToService) {
                Toast.makeText(this, "already connected!", Toast.LENGTH_SHORT).show()
            } else {
                bindToService()
            }
        }

        val sendMsgBtn: Button = findViewById(R.id.sendMessageBtn)
        sendMsgBtn.setOnClickListener {
            if (boundToService) {
                sendMessageToServer()
            } else {
                Toast.makeText(this, "connect first", Toast.LENGTH_SHORT).show()
            }
        }


        val sendBurstBtn: Button = findViewById(R.id.sendBurstMsg)
        sendBurstBtn.setOnClickListener {
            if (boundToService) {
                repeat(100) {
                    sendMessageToServer()
                }
            } else {
                Toast.makeText(this, "connect first", Toast.LENGTH_SHORT).show()
            }
        }

        val megaLoad: Button = findViewById(R.id.megaMessageLoad)
        megaLoad.setOnClickListener {
            if (boundToService) {
                GlobalScope.launch {
                    repeat(10000) {
                        sendMessageToServer()
                        val sleeptime = (1..3).random().times(100).toLong()
                        SystemClock.sleep(sleeptime)
                    }
                }
            } else {
                Toast.makeText(this, "connect first", Toast.LENGTH_SHORT).show()
            }
        }

        textStatus = findViewById(R.id.status)
        sentMessages = findViewById(R.id.sentMessages)
        receivedMessages = findViewById(R.id.recMessages)
    }

    private fun bindToService() {
        clientMessenger = Messenger(handler)
        if (!boundToService) {
            textStatus.text = "Binding"
            bindService(
                MessengerContract.serviceBindIntent,
                messengerServiceConnection,
                BIND_AUTO_CREATE
            )
        }
    }

    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // Update UI with remote process info
            MessengerContract.SayHello.parseRequestMessagePayload(msg.data).let {
                received++
                receivedMessages.text = "Receoved messages $received"
                Log.i("BOBAN", "<--- dobijam sa servera odgovor ${msg.data}")
            }
        }
    }

    private val messengerServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            textStatus.text = "Disconncted!"
            boundToService = false
            serverMessenger = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            textStatus.text = "Connected!"
            serverMessenger = Messenger(service)
            boundToService = true

            // Ready to send messages to remote service
            sendMessageToServer()
        }
    }

    private fun sendMessageToServer() {
        if (!boundToService) {
            Log.e("BOBAN", "not bound to serevcie!!! ")
            return
        }

        val oneWayMessage = MessengerContract.SayHello.buildRequestMessage("BLA").apply {
            replyTo = clientMessenger
        }
        try {
            Log.i("BOBAN", "---> saljem poruku serveru")
            sent++
            serverMessenger?.send(oneWayMessage)
            sentMessages.text = "Sent messages $sent"
        } catch (e: RemoteException) {
            e.printStackTrace()
        } finally {
            oneWayMessage.recycle()
        }
    }
}