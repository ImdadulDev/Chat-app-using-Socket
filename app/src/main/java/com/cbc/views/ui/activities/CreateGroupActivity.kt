package com.cbc.views.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lv.chi.photopicker.ChiliPhotoPicker
import lv.chi.photopicker.PhotoPickerFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import kotlin.random.Random


class CreateGroupActivity : AppCompatActivity(), KodeinAware, AddItemOnListListener, RemoveItemListener, PhotoPickerFragment.Callback {

    override val kodein by kodein()
    private val groupChatRepository: GroupChatRepository by instance()
    private lateinit var cbcDatabase: CBCDatabase
    var selectContactAdapter: SelectContactAdapter? = null
    private var selectContactList = ArrayList<ContactEntity>()
    private var selectedContactList = ArrayList<ContactEntity>()
    var selectedContactAdapter: SelectedContactAdapter? = null
    lateinit var rVSelectedContacts: RecyclerView
    lateinit var rVSelectContact: RecyclerView
    private lateinit var pref: AppSharePref
    lateinit var prefUserDetails: LoginResponse
    private var groupName = ""
    private var groupImage = ""
    private var activityName = ""
    private var receiveMsgUserName = ""
    private var receiveMsgRemoteId = ""
    private var receiveMsgCbcId = ""
    private var receiveMsgCbcImage = ""
    private var receiveUserMobNo = ""

    companion object {
        var isMultiSelectOn = false
        val TAG = "CreateGroupActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        rVSelectedContacts = findViewById(R.id.rvCreateGroupSelectedContactList)
        rVSelectContact = findViewById(R.id.rvSelectContact)

        //getting login user data from sharedPref
        try {
            pref = AppSharePref(applicationContext)
            prefUserDetails = pref.GetUSERDATA()!!

            try {
                val intent = intent
                if (intent != null) {
                    activityName = intent.getStringExtra("activityName")!!
                    receiveMsgUserName = intent.getStringExtra("receiveMsgUsername")!!
                    receiveMsgRemoteId = intent.getStringExtra("receiveMsgRemoteId")!!
                    receiveMsgCbcId = intent.getStringExtra("receiveMsgCbcId")!!
                    receiveMsgCbcImage = intent.getStringExtra("receiveMsgCbcImage")!!
                    receiveUserMobNo = intent.getStringExtra("remoteUserMobNo")!!

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        toolbar_create_group_chat.setNavigationOnClickListener {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            finish()
        }

        //getting users from local db
        cbcDatabase = CBCDatabase.getDatabase(applicationContext)

        create_group_image.setOnClickListener {
            openPicker()
        }

        fabCreateGroup.setOnClickListener {
            groupName = et_create_group_name.text.toString()

            if (selectedContactList.size == 0) {
                toast("Please select participant!")
            }

            if (groupName.isEmpty()){
                et_create_group_name.error = "can't be empty"
            }

            if (groupName.isNotEmpty() && selectedContactList.isNotEmpty()) {
                getCreateGroupRes(selectedContactList)
            }
        }

        createGroupSearchFilter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(newText: Editable?) {
                selectContactAdapter!!.filter.filter(newText.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        setUpContactList()
        //rvCreateGroupSelectedContactList.setup()
        setUpSelectedList(selectedContactList)
    }

    private fun getCreateGroupRes(selectedContactList: ArrayList<ContactEntity>) = Coroutines.main {
        try {
            createGroupProgressLoading.show()
            var groupImageName = ""
            groupImageName = if (groupImage == "") {
                ""
            } else Random.nextInt(0, 1000000000).toString() + "_cbc_image.jpeg"

            val jsonArrayContactList = itemListToJsonConvert(selectedContactList)
            Log.d("----", ": $jsonArrayContactList")
            Log.d("----", "image name: $groupImage")

            val createGroupResponse = groupChatRepository.getCreateGroupChatResponseFromApi(
                jsonArrayContactList,
                prefUserDetails.id,
                pref.getUserChatToken()!!,
                groupName,
                groupImage,
                groupImageName
            )

            Log.d("----","Create Group Response : $createGroupResponse" )
            createGroupProgressLoading.hide()
            if (createGroupResponse.success) {
                toast("Group Created")

                groupChatRepository.deleteAllGroupMemberFromTable()
                //Saving group data in db
                for (groupDetailsElement in createGroupResponse.result){
                    groupChatRepository.saveGroupMemberEntity(
                        GroupMemberEntity(
                            Random.nextInt(0, 1000000000),
                            groupDetailsElement.chatUserId,
                            groupDetailsElement.group.id,
                            groupDetailsElement.groupImageName,
                            groupDetailsElement.userFullName,
                            groupDetailsElement.userimage,
                            groupDetailsElement.phone
                        )
                    )
                }

                val intent = Intent(this, GroupChatActivity::class.java)
                intent.putExtra("group_name", createGroupResponse.result[0].group.groupName)
                intent.putExtra("group_id", createGroupResponse.result[0].group.id)
                intent.putExtra("group_image", createGroupResponse.groupImageName)

                Log.d("----","1: " + createGroupResponse.result[0].group.groupName)
                Log.d("----","2: " + createGroupResponse.result[0].group.id)
                Log.d("----","3: " + createGroupResponse.groupImageName)

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun setUpContactList() = Coroutines.main {
        try {
            isMultiSelectOn = false
            rVSelectContact.layoutManager = LinearLayoutManager(this)
            selectContactAdapter = SelectContactAdapter(this, this, selectContactList)
            rVSelectContact.adapter = selectContactAdapter

            //adding exist user
            selectContactList.addAll(addChatUser())
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

    private fun addChatUser() : ArrayList<ContactEntity>{
        val list = ArrayList<ContactEntity>()
        if (activityName == "ChatActivity") {
            Log.d("----create","activityName: $activityName")

            list.add(
                ContactEntity(
                    Random.nextInt(0, 1000000000).toString(),
                    receiveMsgCbcImage,
                    receiveUserMobNo,
                    receiveUserMobNo,
                    receiveMsgUserName,
                    "",
                    "",
                    false,
                    receiveMsgCbcId
                )
            )
        }

       return list
    }

    override fun addItemOnListListener(userList: ArrayList<ContactEntity>) {
        this@CreateGroupActivity.selectedContactList = userList
        setUpSelectedList(userList)

        Log.d("----", "interface selectedContactList.size: ${userList.size}")
    }


    private fun setUpSelectedList(selectedContactList: ArrayList<ContactEntity>) {
        try {
            rVSelectedContacts.layoutManager = LinearLayoutManager(
                this@CreateGroupActivity,
                LinearLayoutManager.HORIZONTAL, false
            )
            selectedContactAdapter = SelectedContactAdapter(selectedContactList, this)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_group, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_add -> {
                //addItem(selectContactList, 0)
                true
            }

            R.id.menu_continue -> {
                //new activity
                true
            }

            else -> null
        } ?: super.onOptionsItemSelected(item)


    private suspend fun getNewContactListWithCbcIdFromDb(): ArrayList<ContactEntity> {
        val contactListFromDb: ArrayList<ContactEntity>
        val list = ArrayList<ContactEntity>()
        try {//add more user
            contactListFromDb = getContactWhereCbcIdPresent()

            for (element in contactListFromDb) {
                //Log.d(TAG, "The size is ${element.display_name}")
                if (element.cbc_id.length > 1 && !selectContactList.contains(element)) {
                    list.add(element)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }

    private suspend fun getContactWhereCbcIdPresent(): ArrayList<ContactEntity> {
        return withContext(Dispatchers.IO) {
            cbcDatabase.contctDao().getAllContactWhereCbcIdPresent() as ArrayList
        }
    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        Klog.d("### PATH-", "${photos[0]}")
        val file = File(photos[0].path)
        Glide.with(applicationContext).load("${photos[0]}")
            .apply(RequestOptions.circleCropTransform().circleCrop()).into(create_group_image)

        try {
            groupImage = convertImageFileToBase64(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    //method to convert the selected image to base64 encoded string



    override fun onRemoveItem(displayName: String) {
        selectContactList.forEach {
            Log.d("----", "remove cbc_id: ${it.cbc_id}")
            if (it.cbc_id.equals(displayName, true)) {
                Log.d("----", "remove position: ${selectContactList.indexOf(it)}")
                selectContactAdapter!!.doSelect(selectContactList.indexOf(it))
            }
        }
    }


    private fun convertImageFileToBase64(imageFile: File): String {
        return FileInputStream(imageFile).use { inputStream ->
            java.io.ByteArrayOutputStream().use { outputStream ->
                Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                    inputStream.copyTo(base64FilterStream)
                    base64FilterStream.close() // This line is required, see comments
                    outputStream.toString()
                }
            }
        }
    }
}

