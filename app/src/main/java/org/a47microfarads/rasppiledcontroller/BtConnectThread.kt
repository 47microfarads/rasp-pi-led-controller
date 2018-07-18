package org.a47microfarads.rasppiledcontroller

import android.bluetooth.BluetoothSocket
import android.bluetooth.BluetoothDevice
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class BtConnectThread(private val bTDevice: BluetoothDevice, uuid: UUID) : Thread() {
    private val bTSocket: BluetoothSocket?

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
                return false
            }

        }

        return true
    }

    fun cancel(): Boolean {
        try {
            bTSocket!!.close()
        } catch (e: IOException) {
            return false
        }

        return true
    }

}


class ManageConnectThread : Thread() {

    @Throws(IOException::class)
    fun sendData(socket: BluetoothSocket, data: Int) {
        val output = ByteArrayOutputStream(4)
        output.write(data)
        val outputStream = socket.outputStream
        outputStream.write(output.toByteArray())
    }

    @Throws(IOException::class)
    fun receiveData(socket: BluetoothSocket): Int {
        val buffer = ByteArray(4)
        val input = ByteArrayInputStream(buffer)
        val inputStream = socket.inputStream
        inputStream.read(buffer)
        return input.read()
    }
}
