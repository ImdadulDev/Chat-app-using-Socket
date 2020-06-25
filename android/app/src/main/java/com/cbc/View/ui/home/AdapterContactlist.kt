package com.cbc.View.ui.home

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cbc.Localdb.ContactEntity
import com.cbc.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint.Align
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint
import com.cbc.NetworkUtils.Klog
import java.util.*


class AdapterContactlist(val context: Context, val data: List<ContactEntity>) :
    RecyclerView.Adapter<AdapterContactlist.ViewH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewH {
        return ViewH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_contat, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewH, position: Int) {
        val item = data[position]
        holder.TXT_NAME.setText(item.displayname)
        holder.TXT_NUMBER.setText(item.mobileno)
        holder.TXT_FIRST.text = "${item.displayname!!.first()}"
        val rnd = Random()
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        Klog.d("## COLOe", "-$color")

        val androidColors = context.resources.getIntArray(R.array.androidcolors)
        val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
        holder.FLOAT.backgroundTintList = ColorStateList.valueOf(randomAndroidColor)
    }

    class ViewH(itemv: View) : RecyclerView.ViewHolder(itemv) {
        val TXT_NAME = itemv.findViewById<TextView>(R.id.TXT_NAME)
        val TXT_FIRST = itemv.findViewById<TextView>(R.id.TXT_FIRST)
        val TXT_NUMBER = itemv.findViewById<TextView>(R.id.TXT_NUMBER)
        val FLOAT = itemv.findViewById<FloatingActionButton>(R.id.FLOAT)
    }


}