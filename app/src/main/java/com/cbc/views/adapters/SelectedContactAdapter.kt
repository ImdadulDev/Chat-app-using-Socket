package com.cbc.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cbc.R
import com.cbc.interfaces.RemoveItemListener
import com.cbc.localdb.ContactEntity

class SelectedContactAdapter(private val items: ArrayList<ContactEntity>, private val removeItemListener: RemoveItemListener):
    RecyclerView.Adapter<SelectedContactAdapter.ViewHolder>() {
    //private val items: ArrayList<ContactEntity> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.content_selected_contact_layout, parent, false)
            .run {
                ViewHolder(this)
            }

    override fun getItemCount(): Int = items.size

/*
    fun appendItem(selectedContactList: ContactEntity) =
        items.add(selectedContactList).also {
            notifyItemInserted(itemCount - 1)
        }
*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            bind(items, adapterPosition)
        }
    }

    inner class ViewHolder(
        itemView: View,
        private val username: TextView = itemView.findViewById(R.id.selectedChatUsername),
        private val userImage: ImageView = itemView.findViewById(R.id.selectedChatUserImage),
       // private val removeButton: ImageView = itemView.findViewById(R.id.selectedChatUserRemove)
        removeButton: View = itemView.findViewById(R.id.selectedChatUserRemove)
    ) : RecyclerView.ViewHolder(itemView) {

        /* private fun insert(): (View) -> Unit = {
           layoutPosition.also { currentPosition ->
               items.add(currentPosition, "")
               notifyItemInserted(currentPosition)
           }
       } */

        init {
            removeButton.setOnClickListener(remove())
        }


        private fun remove(): (View) -> Unit = {
            layoutPosition.also { currentPosition ->
                val stringData = items[currentPosition].cbc_id
                items.removeAt(currentPosition)
                notifyItemRemoved(currentPosition)
                removeItemListener.onRemoveItem(stringData)
            }
        }



        fun bind(selectContactList: ArrayList<ContactEntity>, position: Int) {
            username.text = selectContactList[position].display_name

          /*  removeButton.setOnClickListener {
                try {
                    Log.d("----","remove pos: $position" )
                    Log.d("----","remove item size: ${items.size}" )
                    removeItemListener.onRemoveItem(items[position].cbc_id)
                    items.removeAt(position)
                    notifyItemRemoved(position)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }*/
        }
    }
}