package com.cbc.views.ui.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import android.view.ActionMode
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cbc.R
import com.cbc.data.repositories.GroupChatRepository
import com.cbc.interfaces.AddItemOnListListener
import com.cbc.interfaces.RemoveItemListener
import com.cbc.localdb.CBCDatabase
import com.cbc.localdb.ContactEntity
import com.cbc.localdb.entities.GroupMemberEntity
import com.cbc.models.LoginResponse
import com.cbc.networkutils.Klog
import com.cbc.utils.*
import com.cbc.views.MainActivity
import com.cbc.views.adapters.SelectContactAdapter
import com.cbc.views.adapters.SelectedContactAdapter
import com.cbc.views.loader.GlideImageLoader
import kotlinx.android.synthetic.main.activity_edit_group.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lv.chi.photopicker.ChiliPhotoPicker
import lv.chi.photopicker.PhotoPickerFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import kotlin.random.Random

class EditGroupActivity : AppCompatActivity() , KodeinAware, AddItemOnListListener, PhotoPickerFragment.Callback , RemoveItemListener{

    override val kodein by kodein()
    private val groupChatRepository: GroupChatRepository by instance()
    private lateinit var cbcDatabase: CBCDatabase
    var actionMode: ActionMode? = null
    var selectContactAdapter: SelectContactAdapter? = null
    private var selectContactList = ArrayList<ContactEntity>()
    private var selectedContactList = ArrayList<ContactEntity>()
    var selectedContactAdapter: SelectedContactAdapter? = null
    lateinit var rVSelectedContacts: RecyclerView
    lateinit var rVSelectContact: RecyclerView
    private var groupName  = ""
    private var groupId  = ""
    private var groupImage  = ""
    private var groupImageName  = ""
    lateinit var prefUserDetails: LoginResponse
    private lateinit var pref: AppSharePref

    companion object {
        var isMultiSelectOn = false
        val TAG = "EditGroupActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_group)

        //get data from intent
        val intent = intent
        if (intent != null) {
            groupName = intent.getStringExtra("group_name")!!
            groupId = intent.getStringExtra("group_id")!!
            groupImage = intent.getStringExtra("group_image")!!
            groupImageName = groupImage
            et_edit_group_name.setText(groupName)
            // Log.d("----", "groupUserList : ${groupUserList[0].display_name}")
            Log.d("----", "receiveMsgRemoteId : $groupName")
        }

        //getting login user data from sharedPref
        try {
            pref = AppSharePref(applicationContext)
            prefUserDetails = pref.GetUSERDATA()!!
            Log.d("----", "Pref getUser : ${pref.getUserChatToken()}")

        } catch (e: Exception) {
            e.printStackTrace()
        }

        rVSelectedContacts = findViewById(R.id.rvCreateGroupSelectedContactList)
        rVSelectContact = findViewById(R.id.rvSelectContact)

        toolbar_edit_group_chat.setNavigationOnClickListener {
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            finish()
        }

        //init db
        cbcDatabase = CBCDatabase.getDatabase(applicationContext)

        if(groupImage.isNotEmpty()){
            Glide.with(applicationContext).load(Constants.CHAT_ATTACHMENT_BASE_URL + "image/" + groupImage)
                .apply(RequestOptions.circleCropTransform().circleCrop()).error(R.drawable.ic_group_img).into(edit_group_image)
        } else{
            edit_group_image.setBackgroundResource(R.drawable.ic_group_img)
        }

        edit_group_image.setOnClickListener {
            openPicker()
        }

        fabEditGroup.setOnClickListener {
            editGroup()
        }

        tv_edit_group_leave.setOnClickListener {
            leaveFromGroup()
        }

        editGroupSearchFilter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(newText: Editable?) {
                selectContactAdapter!!.filter.filter(newText.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        setUpContactList()
        setUpSelectedList(selectedContactList)
    }

    private fun addUserInGroup() = Coroutines.main{
        val  addUserInGroupRes = groupChatRepository.getAddGroupUserResponseFromApi(
            "", prefUserDetails.id,
            pref.getUserChatToken()!!,
            groupName,
            groupId,
            groupImage,
            "image.jpeg")
        Log.d("----", "addUserInGroupRes: $addUserInGroupRes")
    }

    private fun editGroup() = Coroutines.main{
        try {
            val groupName = et_edit_group_name.text.toString()
            if(selectedContactList.isEmpty()){
                toast("Please select participant!")
            }

            editGroupProgressLoading.show()
            val jsonArrayContactList = itemListToJsonConvert(selectedContactList)
            Log.d("----",": $jsonArrayContactList")
            Log.d("----", "image name edit: $groupImage")

            val  editGroupRes = groupChatRepository.getEditGroupChatResponseFromApi(
                jsonArrayContactList ,
                prefUserDetails.id,
                pref.getUserChatToken()!!,
                groupName,
                groupId,
                "",
                groupImageName
            )

            //Log.d("----", "editGroupRes: $editGroupRes")

            if (editGroupRes.success){
                //clear table data
                groupChatRepository.deleteAllGroupMemberFromTable()
                //Saving group data in db
                for (groupDetailsElement in editGroupRes.result){
                    groupChatRepository.saveGroupMemberEntity(
                        GroupMemberEntity(
                            Random.nextInt(0, 1000000000),
                            groupDetailsElement.chatUserId,
                            groupDetailsElement.group.id,
                            groupDetailsElement.groupImageName,
                            groupDetailsElement.userFullName,
                            groupDetailsElement.userimage,
                            groupDetailsElement.phone
                        ))
                }

                editGroupProgressLoading.hide()
                toast("Edited successfully")
                intent = Intent(this, GroupChatActivity::class.java)
                intent.putExtra("group_name", groupName)
                intent.putExtra("group_id", groupId)
                intent.putExtra("group_image", groupImageName)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            editGroupProgressLoading.hide()
        } catch (e: Exception) {
            e.printStackTrace()
            editGroupProgressLoading.hide()
        }
    }

    private fun leaveFromGroup() = Coroutines.main{
        try {
            editGroupProgressLoading.show()
            val leaveGroupRes = groupChatRepository.getLeaveGroupUserResponseFromApi(prefUserDetails.id, pref.getUserChatToken()!!, groupId)
            Log.d("----", "leaveGroupRes: $leaveGroupRes")

            if(leaveGroupRes.success == "true"){
                groupChatRepository.deleteGroupMemberFromDb(
                    GroupMemberEntity(
                        Random.nextInt(0, 1000000000),
                        prefUserDetails.id,
                        groupId,
                        groupName,
                        prefUserDetails.userName,
                        prefUserDetails.profileImage,
                        prefUserDetails.phone
                    )
                )

                editGroupProgressLoading.hide()
                toast("Left from group")
                val intent = Intent(this, MainActivity::class.java)
                intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                //finish()
            }

            editGroupProgressLoading.hide()

        } catch (e: Exception) {
            e.printStackTrace()
            editGroupProgressLoading.hide()
        }
    }

    private fun removeUserFromGroup(removeUserCbcId: String, index: Int) = Coroutines.main{
        try {
            editGroupProgressLoading.show()
            val removeUserFromGroupRes = groupChatRepository.getRemoveGroupUserResponseFromApi(
                prefUserDetails.id,
                pref.getUserChatToken()!!,
                groupId,
                groupImageName,
                removeUserCbcId
            )

            Log.d("----", "removeUserFromGroupRes: $removeUserFromGroupRes")

            if(removeUserFromGroupRes.success){
                selectContactAdapter!!.doSelect(index)

                //clear table data
                groupChatRepository.deleteAllGroupMemberFromTable()
                //Saving group data in db
                for (groupDetailsElement in removeUserFromGroupRes.result){
                    groupChatRepository.saveGroupMemberEntity(
                        GroupMemberEntity(
                            Random.nextInt(0, 1000000000),
                            groupDetailsElement.chatUserId,
                            groupDetailsElement.group.id,
                            groupDetailsElement.groupImageName,
                            groupDetailsElement.userFullName,
                            groupDetailsElement.userimage,
                            groupDetailsElement.phone
                        ))
                }

                editGroupProgressLoading.hide()
                toast("Removed from group")
            } else {
                toast("Could not remove user")
            }

            editGroupProgressLoading.hide()

        } catch (e: Exception) {
            e.printStackTrace()
            editGroupProgressLoading.hide()
        }
    }

    private fun setUpContactList() = Coroutines.main{
        try {
            isMultiSelectOn = false
            rVSelectContact.layoutManager = LinearLayoutManager(this)
            selectContactAdapter = SelectContactAdapter(this, this, selectContactList)
            rVSelectContact.adapter = selectContactAdapter

            //adding exist user
            selectContactList.addAll(getExistingGroupMemberFromDb())
            selectContactAdapter?.modelList = selectContactList
            for(element in selectContactList){
                if(element.cbc_id != prefUserDetails.id) selectContactAdapter!!.doSelect(selectContactList.indexOf(element))
            }
            //selectContactAdapter?.notifyDataSetChanged()


            //add new user
            selectContactList.addAll(getNewContactListWithCbcIdFromDb())
            selectContactAdapter?.modelList = selectContactList
            selectContactAdapter?.notifyDataSetChanged()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun addItemOnListListener(userList: ArrayList<ContactEntity>) {

        for (element in userList){
            if(element.cbc_id == prefUserDetails.id){
                userList.remove(element)
            }
        }
        this@EditGroupActivity.selectedContactList = userList
        setUpSelectedList(userList)

        Log.d("----","interface selectedContactList.size: ${userList.size}")
    }


    private fun setUpSelectedList(userList: ArrayList<ContactEntity>) {
        try {
            rVSelectedContacts.layoutManager = LinearLayoutManager(this@EditGroupActivity,
                LinearLayoutManager.HORIZONTAL, false)
            selectedContactAdapter = SelectedContactAdapter(userList, this)
            rVSelectedContacts.adapter = selectedContactAdapter
            selectedContactAdapter?.notifyDataSetChanged()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openPicker() {
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "lv.chi.sample.fileprovider"
        )
        PhotoPickerFragment.newInstance(
            multiple = false,
            allowCamera = true,
            maxSelection = 1,
            theme = R.style.ChiliPhotoPicker_Dark
        ).show(supportFragmentManager, "picker")
    }

    /*private fun RecyclerView.setup() {
        adapter = this@CreateGroupActivity.adapter
        layoutManager = LinearLayoutManager(this@CreateGroupActivity, LinearLayoutManager.HORIZONTAL, false)

        rvCreateGroupSelectedContactList.editOnItemTouchListener(
            RecyclerTouchListener(
                applicationContext,
                rvCreateGroupSelectedContactList,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        try {
                            val textView = view.findViewById(R.id.selectedChatUsername) as TextView
                            //Log.d("----","Text: ${textView.text}")
                            val text = textView.text.toString()
                            //toast()
                            selectContactList.forEach {
                                if (it.display_name.equals(text, true)) {
                                    Log.d("----","remove position: ${selectContactList.indexOf(it)}")
                                    editItem(selectContactList, selectContactList.indexOf(it))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onLongClick(view: View, position: Int) {}
                })
        )
    }*/


    private suspend fun getExistingGroupMemberFromDb(): ArrayList<ContactEntity> {
        val contactListFromDb: ArrayList<ContactEntity>
        val list = ArrayList<ContactEntity>()
        try {
            //add exist user from db by group id
            contactListFromDb = getGroupMemberListFromDb()
            for (element in contactListFromDb){
                list.add(element)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private suspend fun getNewContactListWithCbcIdFromDb(): ArrayList<ContactEntity> {
        val contactListFromDb: ArrayList<ContactEntity>
        val list = ArrayList<ContactEntity>()
        try {
            //add more user
            contactListFromDb = getContactWhereCbcIdPresent()

          /*  selectContactList.forEachIndexed{ index, element ->
                if(element.cbc_id.length > 1 && selectContactList[index].cbc_id != element.cbc_id ){
                    list.add(element)
                }
            }*/

          /*    contactListFromDb.forEachIndexed{ index, element ->
                Log.d("----","old list: ${element.cbc_id} , new list: ${selectContactList[index].cbc_id}")
                if(element.cbc_id.length > 1 && selectContactList[index].cbc_id != element.cbc_id ){
                    list.add(element)
                }
            }*/

            contactListFromDb.forEachIndexed{ index, element ->
                if(element.cbc_id.length > 1){
                    list.add(element)
                }
            }

            selectedContactList.forEachIndexed{ indexs, elements ->
                if(elements.cbc_id.length > 1){
                    list.forEachIndexed{ indexss, elementss ->
                        if(elements.cbc_id.length > 1 ){
                            if(selectedContactList[indexs].cbc_id == elementss.cbc_id) list.remove(elementss)
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }

    private suspend fun getGroupMemberListFromDb(): ArrayList<ContactEntity> {
        val groupMemberList = groupChatRepository.getGroupMembersByGroupIdFromDb(groupId)
        Log.d(TAG, "----groupMemberList is ${groupMemberList.size}")
        Log.d(TAG, "----groupId: $groupId")
        val list = ArrayList<ContactEntity>()
        for (element in groupMemberList){
            Log.d(TAG, "----groupMember username ${element.username}")
            list.add(ContactEntity(
                Random.nextInt(0, 1000000000).toString(),
                element.userImage,
                element.userMobNo,
                element.userMobNo,
                element.username,
                "",
                "",
                false,
                element.userId
                ))
        }
        return list
    }

    private suspend fun getContactWhereCbcIdPresent(): ArrayList<ContactEntity>{
        return withContext(Dispatchers.IO){
            cbcDatabase.contctDao().getAllContactWhereCbcIdPresent() as ArrayList
        }
    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        Klog.d("### PATH-", "${photos[0]}")
        val file = File(photos[0].path)

        try {
            val groupImageBase64 = convertImageFileToBase64(file)

            val imageUri = photos[0]
            uploadEditGroupImageToServer(imageUri, groupImageBase64)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    private fun uploadEditGroupImageToServer(imageUri: Uri, groupImage: String) = Coroutines.main {
        try {
            editGroupProgressLoading.show()
            groupImageName = Random.nextInt(0, 1000000000).toString() + "_cbc_image.jpeg"
            val res =  groupChatRepository.getEditGroupImageResponseFromApi(
                 prefUserDetails.id,
                 pref.getUserChatToken()!!,
                 groupId,
                 groupImage,
                 groupImageName
             )

            Log.d("----","edit group img res: $res")

            editGroupProgressLoading.hide()
            if (res.success){
                groupImageName = res.groupImageName
                toast("Image updated successfully")
                Glide.with(applicationContext).load(imageUri)
                    .apply(RequestOptions.circleCropTransform().circleCrop()).error(R.drawable.ic_user_placeholder).into(edit_group_image)
            } else {
                toast("Couldn't update image")
                edit_group_image.setBackgroundResource(R.drawable.ic_user_placeholder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            editGroupProgressLoading.hide()
        }
    }

    override fun onRemoveItem(displayName: String) {
        selectContactList.forEach {
            if (it.cbc_id.equals(displayName, true)) {
                Log.d("----","edit remove position: ${selectContactList.indexOf(it)}")


                /*
                for(element in selectedContactList){
                    if(element.cbc_id == ){
                        removeUserFromGroup(it.cbc_id)
                    }
                }
*/

                removeUserFromGroup(displayName, selectContactList.indexOf(it))
            }
        }
    }

    private fun convertImageFileToBase64(imageFile: File): String {
        return FileInputStream(imageFile).use { inputStream ->
            ByteArrayOutputStream().use { outputStream ->
                Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                    inputStream.copyTo(base64FilterStream)
                    base64FilterStream.close() // This line is required, see comments
                    outputStream.toString()
                }
            }
        }
    }
}

