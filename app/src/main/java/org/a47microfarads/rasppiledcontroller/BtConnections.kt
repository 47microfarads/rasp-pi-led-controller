package org.a47microfarads.rasppiledcontroller

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_bt_connections.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BtConnections.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BtConnections.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BtConnections : Fragment() {

    // keep track of bonded BT profiles and other BT profiles
    val usersBonded = ArrayList<BTClient>()
    val usersOthers = ArrayList<BTClient>()
    var listener : Context? = null
    private val filter = IntentFilter()

    // Broadcast receiver for callbacks
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
                text_bt_connection_status.text = "Scanning" //resources.getString(R.string.bt_scanning)
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                text_bt_connection_status.text = "No active connections" // resources.getString(R.string.bt_no_connections)
            } else  if (BluetoothDevice.ACTION_FOUND == action) {
                // A Bluetooth device was found
                // Getting device information from the intent
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // name maybe null
                usersOthers.add(BTClient(device))
                recyclerView.adapter.notifyDataSetChanged()
            }
        }
    }

    fun populateBondedDevices() {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        for (bondedDevice in btAdapter.bondedDevices) {
            usersBonded.add(BTClient(bondedDevice))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setup filter once on the creation
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set click listeners
        button_scan_bt.setOnClickListener {
            _: View? ->
            onBTScanClick()
        }
    }

    fun onBTScanClick() {
        val REQUEST_ENABLE_BT = 1
        val MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1

        val bluetoothManager: BluetoothManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val mBluetoothAdapter = bluetoothManager.adapter

        // unregister mReceiver
        try {
            activity?.unregisterReceiver(mReceiver)
        } catch (e: Exception) {
            // could be the first time we get in...
        }

        usersBonded.clear()
        usersOthers.clear()

        populateBondedDevices()
        recyclerView.adapter.notifyDataSetChanged()
        recyclerView.invalidate()

        if (mBluetoothAdapter == null || !(mBluetoothAdapter.isEnabled)) {
            Toast.makeText(activity, "BT not enabled. Requesting BT...", Toast.LENGTH_SHORT).show()
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        else if (mBluetoothAdapter.isDiscovering) {
            // Bluetooth is already in mode discovery mode, we cancel to restart it again
            mBluetoothAdapter.cancelDiscovery()
        }

        // Register the broadcast receiver
        activity?.registerReceiver(mReceiver, filter)

        requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)
        mBluetoothAdapter.startDiscovery()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        populateBondedDevices()

        recyclerView.adapter = BtConnViewSectionsAdapter(usersBonded, usersOthers, listener /*activity?.applicationContext*/)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bt_connections, container, false)
    }

    /*
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }
    */

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onClick(btDevice: BluetoothDevice)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BtConnections.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                BtConnections().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
