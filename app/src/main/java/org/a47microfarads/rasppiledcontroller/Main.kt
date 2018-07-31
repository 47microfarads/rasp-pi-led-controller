package org.a47microfarads.rasppiledcontroller

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.R.id.message
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.a47microfarads.rasppiledcontroller.BtConnections
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_bt_connections.*
import org.a47microfarads.rasppiledcontroller.R.id.text_bt_connection_status
import java.util.*


// 1
class MainPagerAdapter(fragmentManager: FragmentManager, private val fragments: ArrayList<Fragment>) :
        FragmentStatePagerAdapter(fragmentManager) {

    // 2
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    // 3
    override fun getCount(): Int {
        return fragments.size
    }
}

class Main :
        AppCompatActivity(),
        BtConnections.OnFragmentInteractionListener,
        LedController.OnFragmentInteractionListener,
        BtSocketErrorListener {

    override fun onClick(btDevice: BluetoothDevice) {
        val name = btDevice.name
        val addr = btDevice.address
        Toast.makeText(this@Main, "Connecting to $addr", Toast.LENGTH_SHORT).show()
        connectedBtThread = BtConnectThread(btDevice, UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"), this)
        if (connectedBtThread?.connect() == false) {
            Toast.makeText(this@Main, "Connecting to $addr failed!!", Toast.LENGTH_LONG).show()
            connectedBtThread = null
        } else {
            btConnectionFragment.text_bt_connection_status.text = "Connected to $name ($addr)."
             connectedBtThread?.start()
        }
    }

    override fun onButtonAction(buttonColor: String, buttonAction: String) {
        Log.d("UIThread", "Color: $buttonColor Action: $buttonAction")
        connectedBtThread?.sendMessage(buttonColor, buttonAction)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_connections -> {
                viewpager.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_led_controls -> {
                viewpager.currentItem = 1
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private val mainFragments = ArrayList<Fragment>()
    private var connectedBtThread: BtConnectThread? = null
    private val btConnectionFragment = BtConnections.newInstance()
    private val ledControllerFragment = LedController()

    private fun createFragments() {
        mainFragments.add(btConnectionFragment)
        mainFragments.add(ledControllerFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create fragments
        createFragments()
        viewpager.adapter = MainPagerAdapter(getSupportFragmentManager(), mainFragments)

        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            var prevMenuItem : MenuItem? = null;

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                var isChecked = bottom_navigation.getMenu().getItem(position).isChecked()
                if (!isChecked) {
                    bottom_navigation.getMenu().getItem(position).setChecked(true)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        // set select listener
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onDestroy() {
        super.onDestroy()

        // ensure cancel
        connectedBtThread?.cancel()
        connectedBtThread = null
    }

    override fun onBtSocketError(e: Exception?) {
        this@Main.runOnUiThread(java.lang.Runnable {
            this.btConnectionFragment.text_bt_connection_status.text = "No BT connections."
        })
    }
}
