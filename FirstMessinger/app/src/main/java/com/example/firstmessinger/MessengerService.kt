package com.example.firstmessinger

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import com.example.firstmessinger.service.MessengerContract
import kotlinx.coroutines.Dispatchers

class MessengerService : Service() {

    class IncomingHandler(val applicationContext: Context) : Handler(Looper.getMainLooper()) {

        lateinit var onHelloReceived: (String) -> Unit

//      Unit  scope.launch(Dispatchers.IO) {
//            Log.i("BOBAN", "received msg")
//            msg.replyTo?.send(MessengerContract.SayHello.buildRequestMessage("OK"))
//            Log.i("BOBAN", "replying to ${msg.replyTo}")
//            when (msg.what) {
//
//                MessengerContract.WHAT_SAY_HELLO -> {
//                    val incomingMessage =
//                        MessengerContract.SayHello.parseRequestMessagePayload(msg.data)
//                }
//
//                MessengerContract.WHAT_ADD_TWO_NUMBERS -> {
//                    try {
//                        val twoIntegersContainer =
//                            MessengerContract.AddTwoIntegers.parseRequestMessagePayload(msg.data)
//
//                        Toast.makeText(
//                            applicationContext,
//                            "Client sends two integers Adding ${twoIntegersContainer.first} and ${twoIntegersContainer.second}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                        val resultMessage = MessengerContract.AddTwoIntegers.buildResponseMessage(
//                            twoIntegersContainer.first + twoIntegersContainer.second
//                        )
//
//                        msg.replyTo.send(resultMessage)
//
//                    } catch (e: MessengerContract.InvalidPayloadException) {
//                        Toast.makeText(applicationContext, "Error ${e.message}", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//                else -> {
//                    (Dispatchers.Main) {
//                        Toast.makeText(
//                            applicationContext,
//                            "Error:   Client sends message with unknown WHAT ${msg?.what}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }
        override fun handleMessage(msg: Message) {
            Log.i("BOBAN", "received msg")
            msg.replyTo.send(MessengerContract.SayHello.buildRequestMessage("OK"))
            Log.i("BOBAN", "replying to ${msg.replyTo}")
            when (msg.what) {

                MessengerContract.WHAT_SAY_HELLO -> {
                    val incomingMessage =
                        MessengerContract.SayHello.parseRequestMessagePayload(msg.data)
                }

                MessengerContract.WHAT_ADD_TWO_NUMBERS -> {
                    try {
                        val twoIntegersContainer =
                            MessengerContract.AddTwoIntegers.parseRequestMessagePayload(msg.data)

                        Toast.makeText(
                            applicationContext,
                            "Client sends two integers Adding ${twoIntegersContainer.first} and ${twoIntegersContainer.second}",
                            Toast.LENGTH_SHORT
                        ).show()

                        val resultMessage = MessengerContract.AddTwoIntegers.buildResponseMessage(
                            twoIntegersContainer.first + twoIntegersContainer.second
                        )

                        msg.replyTo.send(resultMessage)

                    } catch (e: MessengerContract.InvalidPayloadException) {
                        Toast.makeText(applicationContext, "Error ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                else -> {
                    Toast.makeText(
                        applicationContext,
                        "Error:   Client sends message with unknown WHAT ${msg?.what}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    val messenger by lazy { Messenger(IncomingHandler(applicationContext)) }

    override fun onBind(intent: Intent): IBinder? {
        return messenger.binder
    }
}
