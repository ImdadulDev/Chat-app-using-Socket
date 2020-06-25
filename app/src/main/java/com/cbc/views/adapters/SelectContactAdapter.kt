package com.cbc.views.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cbc.R
import com.cbc.interfaces.AddItemOnListListener
import com.cbc.interfaces.ViewHolderClickListener
import com.cbc.localdb.ContactEntity
import com.cbc.networkutils.Klog
import com.cbc.utils.Constants
import com.cbc.views.ui.activities.CreateGroupActivity
import com.cbc.views.viewholder.SelectContactViewHolder
import kotlin.collections.ArrayList

class SelectContactAdapter(
    val context: Context,
    private val addItemOnListListener: AddItemOnListListener,
    val selectContactList: ArrayList<ContactEntity>
) :
    RecyclerView.Adapter<SelectContactViewHolder>(), ViewHolderClickListener, Filterable {
    private val selectedContactList: ArrayList<ContactEntity> = ArrayList()

    override fun onLongTap(index: Int) {
        if (CreateGroupActivity.isMultiSelectOn) {
            addIDIntoSelectedIds(index)
        } else {
            //Toast.makeText(context, "Clicked Item @ Position ${index + 1}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTap(index: Int) {
        doSelect(index)
    }

    fun doSelect(index: Int) {
        if (!CreateGroupActivity.isMultiSelectOn) {
            CreateGroupActivity.isMultiSelectOn = true
        }
        addIDIntoSelectedIds(index)
    }

    private fun addIDIntoSelectedIds(index: Int) {
        val id = modelList[index].id
        if (selectedIds.contains(id)){
            selectedIds.remove(id)
            selectedContactList.remove(modelList[index])
        }
        else {
            selectedIds.add(id)
            selectedContactList.add(modelList[index])
        }
        notifyItemChanged(index)
        if (selectedIds.size < 1) CreateGroupActivity.isMultiSelectOn = false
        addItemOnListListener.addItemOnListListener(selectedContactList)
    }

    var modelList: MutableList<ContactEntity> = ArrayList()
    private val selectedIds: MutableList<String> = ArrayList()

    override fun getItemCount() = modelList.size

    fun getItemList() = selectedContactList

    override fun onBindViewHolder(holder: SelectContactViewHolder, index: Int) {
        holder.selectContactUsername.text = modelList[index].display_name
        holder.selectContactUserMob.text = modelList[index].display_mobile_no

        if ( modelList[index].image != "") {
            Glide.with(context).load(Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + modelList[index].image)
                .apply(RequestOptions.circleCropTransform().circleCrop()).error(R.drawable.ic_chat_person).into(holder.ivSelectContactUserImage)
        } else {
            holder.ivSelectContactUserImage.setBackgroundResource(R.drawable.ic_chat_person)
        }

        holder.frameLayout

        val id = modelList[index].id

        if (selectedIds.contains(id)) {
            //if item is selected then,set foreground color of FrameLayout.
            //holder.frameLayout.foreground = ColorDrawable(ContextCompat.getColor(context, R.color.grey_20))
            holder.selectedCheckedImage.visibility = View.VISIBLE

        } else {
            //else remove selected item color.
            //holder.frameLayout.foreground = ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
            holder.selectedCheckedImage.visibility = View.GONE
        }
    }

    fun deleteSelectedIds() {
        if (selectedIds.size < 1) return
        val selectedIdIteration = selectedIds.listIterator();

        while (selectedIdIteration.hasNext()) {
            val selectedItemID = selectedIdIteration.next()
            Log.d(CreateGroupActivity.TAG, "The ID is $selectedItemID")
            var indexOfModelList = 0
            val modelListIteration: MutableListIterator<ContactEntity> = modelList.listIterator();
            while (modelListIteration.hasNext()) {
                val model = modelListIteration.next()
                if (selectedItemID == model.id) {
                    modelListIteration.remove()
                    selectedIdIteration.remove()
                    notifyItemRemoved(indexOfModelList)
                }
                indexOfModelList++
            }

            CreateGroupActivity.isMultiSelectOn = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.content_select_contact_holder_layout, parent, false)
        return SelectContactViewHolder(itemView, this)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val string = p0.toString()
                Klog.d("## SEARCH-", string)
                if (string.isEmpty()) {
                    modelList = selectContactList
                } else {
                    val filteredList = ArrayList<ContactEntity>()
                    selectContactList.forEach {
                        if (it.display_name.contains(string, true)) {
                            filteredList.add(it)
                        } else if (it.display_mobile_no.contains(string, true)) {
                            filteredList.add(it)
                        }
                    }
                    modelList = filteredList
                }
                val filterResult = FilterResults()
                filterResult.values = modelList
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                modelList = p1!!.values as MutableList<ContactEntity>
                notifyDataSetChanged()
            }

        }
    }

}