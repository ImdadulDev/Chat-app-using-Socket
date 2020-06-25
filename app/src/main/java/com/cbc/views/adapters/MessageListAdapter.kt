package com.cbc.views.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cbc.R
import com.cbc.data.apiresponse.MyMessageResponse
import com.cbc.utils.Constants
import com.cbc.utils.getTimeFromDateUsingSubStringAndSplit
import com.cbc.utils.getTimeFromDateUsingSubStringAndSplitByColon
import kotlinx.android.synthetic.main.content_my_message_layout.view.*

class MessageListAdapter(
    private val items: ArrayList<MyMessageResponse.Data.ResponseData>,
    val context: Context,
    val userId: String
) :
    RecyclerView.Adapter<MessageListViewHolder>(), Filterable {

    var myMessageList = items

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return myMessageList.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListViewHolder {
        return MessageListViewHolder(LayoutInflater.from(context).inflate(R.layout.content_my_message_layout, parent, false))
    }

    // Binds each item in the ArrayList to a view
    override fun onBindViewHolder(holder: MessageListViewHolder, position: Int) {
        val myMessageListModel = myMessageList[position]
        Log.d("----", "my msg groupImage: ${myMessageListModel.groupImage}")
        Log.d("----", "my msg groupName: ${myMessageListModel.groupName}")

        if (myMessageListModel.groupId == "") {
            try {
                if(userId == myMessageListModel.remoteUserId){
                    holder.tvTitle.text = myMessageListModel.username
                } else  holder.tvTitle.text = myMessageListModel.remoteusername

                holder.tvTime.text = getTimeFromDateUsingSubStringAndSplitByColon(myMessageListModel.chattime)

                if (myMessageListModel.remoteuserimage != "") {
                    Glide.with(context).load(Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + myMessageListModel.remoteuserimage)
                        .apply(RequestOptions.circleCropTransform().circleCrop()).error(R.drawable.ic_chat_person).into(holder.ivUserImage)
                } else {
                    holder.ivUserImage.setBackgroundResource(R.drawable.ic_chat_person)
                }

                if (myMessageListModel.msg != "") {
                    holder.tvTxt.text = myMessageListModel.msg
                } else holder.tvTxt.text = myMessageListModel.type
            } catch (e: Exception) {
                e.printStackTrace()
            }


        } else if(myMessageListModel.groupId != ""){
            try {
                holder.tvTitle.text = myMessageListModel.groupName
                if(myMessageListModel.lastmessageTime != ""){
                    holder.tvTime.text = getTimeFromDateUsingSubStringAndSplitByColon(myMessageListModel.lastmessageTime)
                }

                if (myMessageListModel.groupImage != "") {
                    Glide.with(context).load(Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + myMessageListModel.groupImage)
                        .apply(RequestOptions.circleCropTransform().circleCrop()).error(R.drawable.ic_group_img).into(holder.ivUserImage)
                } else {
                    holder.ivUserImage.setBackgroundResource(R.drawable.ic_group_img)
                }

                if (myMessageListModel.lastmessage != "") {
                    holder.tvTxt.text = myMessageListModel.lastmessage
                } else holder.tvTxt.text = myMessageListModel.lastmessageType
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val string = p0.toString()
                Log.d("----", string)
                if (string.isEmpty()) {
                    myMessageList = items
                } else {
                    val filteredList = ArrayList<MyMessageResponse.Data.ResponseData>()
                    items.forEach {
                        if (it.time.contains(string, true)) {
                            filteredList.add(it)
                        }
                    }
                    myMessageList = filteredList
                }
                val filterResult = FilterResults()
                filterResult.values = myMessageList
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                myMessageList = p1!!.values as ArrayList<MyMessageResponse.Data.ResponseData>
                notifyDataSetChanged()
            }

        }
    }
}

class MessageListViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {
    val tvTitle: TextView = view.ivMyMessageTitle
    val tvTime: TextView = view.ivMyMessageTime
    val tvTxt: TextView = view.ivMyMessageTxt
    val ivUserImage: ImageView = view.ivMyMessageUserImage
}