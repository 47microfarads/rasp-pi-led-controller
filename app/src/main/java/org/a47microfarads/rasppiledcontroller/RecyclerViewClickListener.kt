package org.a47microfarads.rasppiledcontroller
import android.bluetooth.BluetoothDevice
import android.view.View

interface RecyclerViewClickListener {
    fun onClick(view: View, section: Int, position: Int)
}

interface BtClickListener {
    fun onClick(btDevice: BluetoothDevice)
}
