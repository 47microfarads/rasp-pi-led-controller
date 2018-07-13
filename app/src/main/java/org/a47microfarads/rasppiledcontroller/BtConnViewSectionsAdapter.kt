package org.a47microfarads.rasppiledcontroller

/*
 * Copyright (C) 2015 Tomás Ruiz-López.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.truizlop.sectionedrecyclerview.SimpleSectionedAdapter
import kotlinx.android.synthetic.main.bt_user_row.view.*

class BTClient (bt_id: String, bt_addr: String){
    val btID: String = bt_id
    val btAddr: String = bt_addr
}

class BtConnViewSectionsAdapter(val usersBonded: ArrayList<BTClient>, val usersOthers: ArrayList<BTClient>) : SimpleSectionedAdapter<BtConnViewSectionsAdapter.ViewHolder>() {

    override fun getSectionHeaderTitle(section: Int): String {
        return if (section == 0) "Bonded devices" else "Other devices"
    }

    override fun getSectionCount(): Int {
        return 2
    }

    override fun getItemCountForSection(section: Int): Int {
        if (section == 0) {
            return usersBonded.size
        }  else {
            return usersOthers.size
        }
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.bt_user_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindItemViewHolder(holder: ViewHolder, section: Int, position: Int) {
        if (section == 0) {
            holder.btId.text = "ID: " + usersBonded[position].btID
            holder.btAddr.text = "addr: " + usersBonded[position].btAddr
        } else {
            holder.btId.text = "ID: " + usersOthers[position].btID
            holder.btAddr.text = "addr: " + usersOthers[position].btAddr
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val btId: TextView = itemView.findViewById(R.id.bt_id)
        val btAddr: TextView = itemView.findViewById(R.id.bt_address)
    }
}
