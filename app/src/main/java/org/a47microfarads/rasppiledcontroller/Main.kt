package org.a47microfarads.rasppiledcontroller

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.support.design.R.id.message
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.a47microfarads.rasppiledcontroller.BtConnections
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.widget.Toast
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

class Main : AppCompatActivity(), BtConnections.OnFragmentInteractionListener {

    override fun onClick(btDevice: BluetoothDevice) {
        val addr = btDevice.address
        Toast.makeText(this@Main, "Connecting to $addr", Toast.LENGTH_SHORT).show()
        connectedBtThread = BtConnectThread(btDevice, UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
        if (connectedBtThread?.connect() == false) {
            Toast.makeText(this@Main, "Connecting to $addr failed!!", Toast.LENGTH_LONG).show()
            connectedBtThread = null
        }

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

    private fun createFragments() {
        mainFragments.add(BtConnections.newInstance())
        mainFragments.add(LedController())
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
}
