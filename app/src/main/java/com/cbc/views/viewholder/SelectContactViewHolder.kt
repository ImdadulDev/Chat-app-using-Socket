package com.cbc.views.viewholder

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cbc.R
import com.cbc.interfaces.ViewHolderClickListener

class SelectContactViewHolder(itemView: View, val r_tap: ViewHolderClickListener) : RecyclerView.ViewHolder(itemView),
    View.OnLongClickListener, View.OnClickListener {

    val selectContactUsername: TextView
    val selectContactUserMob: TextView
    val frameLayout: FrameLayout
    val selectedCheckedImage: ImageView
    val ivSelectContactUserImage: ImageView

    init {
        selectContactUsername = itemView.findViewById(R.id.tvselectContactUserName)
        selectContactUserMob = itemView.findViewById(R.id.tvSelectContactUserMobileNo)
        selectedCheckedImage = itemView.findViewById(R.id.selectedCheckedImage)
        ivSelectContactUserImage = itemView.findViewById(R.id.ivSelectContactUserImage)
        frameLayout = itemView.findViewById(R.id.root_layout)
        frameLayout.setOnClickListener(this)
        frameLayout.setOnLongClickListener(this)
    }

    override fun onClick(v: View?) {
        r_tap.onTap(adapterPosition)
    }

    override fun onLongClick(v: View?): Boolean {
        r_tap.onLongTap(adapterPosition)
        return true
    }
}