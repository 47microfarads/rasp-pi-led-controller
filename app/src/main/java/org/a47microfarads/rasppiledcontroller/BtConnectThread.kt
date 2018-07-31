package org.a47microfarads.rasppiledcontroller

import android.bluetooth.BluetoothSocket
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class BtConnectThread(private val bTDevice: BluetoothDevice,
                      uuid: UUID,
                      private val btSocketErrorListener: BtSocketErrorListener) : Thread() {
    val bTSocket: BluetoothSocket?
    var cancelReq: Boolean = false

    init {
        var tmp: BluetoothSocket? = null

        try {
            tmp = this.bTDevice.createRfcommSocketToServiceRecord(uuid)
        } catch (e: IOException) {
            Log.d("CONNECTTHREAD", "Could not start listening for RFCOMM")
        }

        bTSocket = tmp
    }

    fun connect(): Boolean {

        try {
            bTSocket!!.connect()
        } catch (e: IOException) {
            Log.d("CONNECTTHREAD", "Could not connect: " + e.toString())
            try {
                bTSocket!!.close()
            } catch (close: IOException) {
                Log.d("CONNECTTHREAD", "Could not close connection:" + e.toString())
                btSocketErrorListener.onBtSocketError(close)
                return false
            }
        }

        return true
    }

    fun cancel(): Boolean {
        cancelReq = true
        try {
            bTSocket!!.close()
        } catch (e: IOException) {
            Log.d("CONNECTTHREAD", "Could not close connection:" + e.toString())
            btSocketErrorListener.onBtSocketError(e)
            return false
        }

        return true
    }

    @Throws(IOException::class)
    fun sendData(socket: BluetoothSocket?, data: Int) {
        val output = ByteArrayOutputStream(4)
        output.write(data)
        val outputStream = socket?.outputStream
        outputStream?.write(output.toByteArray())
    }

    @Throws(IOException::class)
    fun sendData(socket: BluetoothSocket?, data: String) {
        val output = ByteArrayOutputStream(data.length)
        output.write(data.toByteArray())
        val outputStream = socket?.outputStream
        outputStream?.write(output.toByteArray())
    }

    @Throws(IOException::class)
    fun receiveData(socket: BluetoothSocket?): Int {
        val buffer = ByteArray(4)
        val input = ByteArrayInputStream(buffer)
        val inputStream = socket?.inputStream
        inputStream?.read(buffer)
        return input.read()
    }

    val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // process incoming messages here
            // this will run in non-ui/background thread
            val color = msg.data.getString("color")
            val action = msg.data.getString("action")

            // only care up and down, ignore the rest
            if ( ( action == "UP") || (action == "DOWN") ) {
                val dataToSend = "$color,$action"
                Log.d("BTThread", "Sending '$dataToSend' to BT")
                try {
                    sendData(bTSocket, dataToSend)
                } catch (e: Exception) {
                    Log.d("CONNECTTHREAD", "Cannot send data:" + e.toString())
                    cancel()
                    btSocketErrorListener.onBtSocketError(e)
                }
            }
        }
    }

    fun sendMessage(color: String, action: String) {
        var msg = Message()
        var data = Bundle().apply {
            putString("color", color)
            putString("action", action)
        }
        msg.data = data
        mHandler.sendMessage(msg)
    }
}
