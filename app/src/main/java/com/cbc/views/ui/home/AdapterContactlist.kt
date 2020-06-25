package com.cbc.views.ui.home

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.cbc.R
import com.cbc.localdb.ContactEntity
import com.cbc.networkutils.Klog
import com.cbc.utils.isConnectedToNetwork
import com.cbc.views.ChatActivity
import com.cbc.views.ui.activities.GroupChatActivity
import com.cbc.views.ui.dialogs.calldialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.security.AccessController.getContext
import java.util.*
import kotlin.collections.ArrayList


class AdapterContactlist(
    val context: Context,
    val data: List<ContactEntity>,
    val listener: OnFetchDeviceContactListener
) :
    RecyclerView.Adapter<AdapterContactlist.ViewH>(), Filterable {
    private var row_index = -5
    val toast_msg = Toast.makeText(context, "Under Development", Toast.LENGTH_LONG)

    var temData = data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewH {
        return ViewH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_contat,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return temData.size
    }

    override fun onBindViewHolder(holder: ViewH, position: Int) {
        val item = temData[position]
        holder.TXT_NAME.text = item.display_name
        holder.TXT_NUMBER.text = item.display_mobile_no
        holder.TXT_FIRST.text =
            if (item.display_name.isNullOrEmpty()) "${item.mobile_no.first()}" else "${item.display_name.first()}"
        val rnd = Random()
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        val androidColors = context.resources.getIntArray(R.array.androidcolors)
        val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
        holder.FLOAT.backgroundTintList = ColorStateList.valueOf(randomAndroidColor)

        holder.contactListRootRowLayout.setOnClickListener(View.OnClickListener {
            row_index = position
            notifyDataSetChanged()
        })
        if (row_index == position) {
            holder.contactListRootRowLayout.setBackgroundColor(Color.parseColor("#0C80EE"))
            holder.TXT_NAME.setTextColor(Color.parseColor("#ffffff"))
            holder.TXT_NUMBER.setTextColor(Color.parseColor("#ffffff"))
            holder.IMG_CALL.setColorFilter(ContextCompat.getColor(context, R.color.white))
            holder.IMG_SYNC.setColorFilter(ContextCompat.getColor(context, R.color.white))
            holder.IMG_CBC_VIDEO.setColorFilter(ContextCompat.getColor(context, R.color.white))
            holder.img_cbc_invite.setColorFilter(ContextCompat.getColor(context, R.color.white))
            holder.IMG_CBC_CHAT.setColorFilter(ContextCompat.getColor(context, R.color.white))

        } else {
            holder.contactListRootRowLayout.setBackgroundColor(Color.parseColor("#ffffff"))
            holder.TXT_NAME.setTextColor(ContextCompat.getColor(context, R.color.black_700))
            holder.TXT_NUMBER.setTextColor(ContextCompat.getColor(context, R.color.black_700))
            holder.IMG_CALL.setColorFilter(ContextCompat.getColor(context, R.color.black))
            holder.IMG_CBC_CHAT.setColorFilter(ContextCompat.getColor(context, R.color.black))
            holder.IMG_CBC_VIDEO.setColorFilter(ContextCompat.getColor(context, R.color.black))
            holder.IMG_SYNC.setColorFilter(ContextCompat.getColor(context, R.color.black))
            holder.img_cbc_invite.setColorFilter(ContextCompat.getColor(context, R.color.black_700))

        }

        if (item.is_cbc_backedup) {
            holder.LL_RIGHT.visibility = View.VISIBLE
            holder.IMG_SYNC.visibility = View.GONE
        } else {
            holder.LL_RIGHT.visibility = View.GONE
            holder.IMG_SYNC.visibility = View.VISIBLE
        }
        holder.IMG_CALL.setOnClickListener {
            Klog.d("## Item-", Gson().toJson(item))
            calldialog(context, item).show()
        }

        holder.IMG_SYNC.setOnClickListener {
            val arra = ArrayList<ContactEntity>()
            item.is_cbc_backedup = true
            arra.add(item)
            listener.onFetchLocalContacts(arra, true)
        }

        if (item.cbc_id.isNotEmpty()) {
            holder.IMG_CBC_CHAT.visibility = View.VISIBLE
            holder.img_cbc_invite.visibility = View.GONE
        } else {
            holder.IMG_CBC_CHAT.visibility = View.GONE
            holder.img_cbc_invite.visibility = View.VISIBLE
        }

        holder.img_cbc_invite.setOnClickListener {
            Toast.makeText(context, "Under development", Toast.LENGTH_SHORT).show()
        }

        holder.IMG_CBC_CHAT.setOnClickListener {
            try {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("receiveMsgUsername", item.display_name)
                intent.putExtra("receiveMsgRemoteId", item.cbc_id)
                intent.putExtra("receiveMsgCbcId", item.cbc_id)
                intent.putExtra("receiveMsgCbcImage", item.image)
                intent.putExtra("remoteUserMobNo", item.mobile_no)

                //Log.d("----","receiveMsgUsername: " +  item.display_name)
                //Log.d("----","receiveMsgRemoteId: " +  item.id)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        holder.IMG_CBC_VIDEO.setOnClickListener {
            toast_msg.setGravity(Gravity.CENTER, 0, 0)
            toast_msg.show()
        }

    }

    class ViewH(itemv: View) : RecyclerView.ViewHolder(itemv) {
        val LL_RIGHT = itemv.findViewById<LinearLayout>(R.id.LL_RIGHT)
        val TXT_NAME = itemv.findViewById<TextView>(R.id.TXT_NAME)
        val IMG_CALL = itemv.findViewById<ImageView>(R.id.IMG_CALL)
        val IMG_SYNC = itemv.findViewById<ImageView>(R.id.IMG_SYNC)
        val IMG_CBC_CHAT = itemv.findViewById<ImageView>(R.id.IMG_CBC_CHAT)
        val img_cbc_invite = itemv.findViewById<ImageView>(R.id.img_cbc_invite)
        val IMG_CBC_VIDEO = itemv.findViewById<ImageView>(R.id.IMG_CBC_VIDEO)
        val TXT_FIRST = itemv.findViewById<TextView>(R.id.TXT_FIRST)
        val TXT_NUMBER = itemv.findViewById<TextView>(R.id.TXT_NUMBER)
        val FLOAT = itemv.findViewById<FloatingActionButton>(R.id.FLOAT)
        val contactListRootRowLayout = itemv.findViewById<RelativeLayout>(R.id.contactListRootRowLayout)
    }

    fun addnew(listData: List<ContactEntity>) {
        this.temData.plus(listData)
        this.notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val string = p0.toString()
                Klog.d("## SEARCH-", string)
                if (string.isEmpty()) {
                    temData = data
                } else {
                    val filteredList = ArrayList<ContactEntity>()
                    data.forEach {
                        if (it.display_name.contains(string, true)) {
                            filteredList.add(it)
                        } else if (it.display_mobile_no.contains(string, true)) {
                            filteredList.add(it)
                        }
                    }
                    temData = filteredList
                }
                val filterResult = FilterResults()
                filterResult.values = temData
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                temData = p1!!.values as List<ContactEntity>
                notifyDataSetChanged()
            }
        }
    }
}