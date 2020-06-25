package com.cbc.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cbc.R
import com.cbc.models.DeviceContentModel
import kotlinx.android.synthetic.main.content_device_content_items.view.*

class DeviceContentAdapter(private val items : ArrayList<DeviceContentModel>, val context: Context) : RecyclerView.Adapter<DeviceContentViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceContentViewHolder {
        return DeviceContentViewHolder(LayoutInflater.from(context).inflate(R.layout.content_device_content_items, parent, false))
    }

    // Binds each item in the ArrayList to a view
    override fun onBindViewHolder(holder: DeviceContentViewHolder, position: Int) {
        val deviceContentModel = items[position]
        holder.tvTitle.text = deviceContentModel.title
        holder.tvSize.text = "Size: " + deviceContentModel.size
    }
}

class DeviceContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvTitle: TextView = view.tvContentTitle
    val tvSize: TextView = view.tvContentSize
}
