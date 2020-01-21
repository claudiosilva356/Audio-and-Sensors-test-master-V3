package com.test.audioandaccelerometerrecord

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.audioandaccelerometerrecord.model.Request
import kotlinx.android.synthetic.main.request_list_item.view.*

class RequestAdapter(val items : ArrayList<Request>, val context: Context) : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.request_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder?.time?.text = item.time
        holder?.success?.text = if (item.success) "success" else "fail"
        holder?.success?.setTextColor(if (item.success) Color.GREEN else Color.RED)
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val time = view.time
        val success = view.success
    }
}
