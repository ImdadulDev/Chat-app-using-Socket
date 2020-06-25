package com.cbc.views.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbc.R
import com.cbc.interfaces.RecyclerTouchListener
import com.cbc.interfaces.RecyclerTouchListener.ClickListener
import com.cbc.models.DeviceContentModel
import com.cbc.utils.getStringSizeLengthFile
import com.cbc.utils.hide
import com.cbc.utils.show
import com.cbc.views.adapters.DeviceContentAdapter
import kotlinx.android.synthetic.main.activity_all_files.*
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.apache.commons.io.filefilter.TrueFileFilter

class AllFilesActivity : AppCompatActivity() {
    private val contentList: ArrayList<DeviceContentModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_files)

        allFileToolbar.setNavigationOnClickListener {
            finish()
        }

        val intent = intent
        if (intent != null){
            all_content_progressbar.show()
            when(intent.getStringExtra("file_type")){
                "audio" -> {
                    getAllFiles("mp3", "audio")
                }
                "video" -> {
                    getAllFiles("mp4", "video")
                }
                "doc" -> {
                    getAllFiles("docx", "doc")
                    getAllFiles("doc", "doc")
                }

                "pdf" -> {
                    getAllFiles("pdf", "doc")
                }
            }
            populateRvContent()
            all_content_progressbar.hide()
        }
    }

    private fun getAllFiles(type: String, serverSendDataType: String) {
        try {
            val iterator = FileUtils.iterateFiles(
                Environment.getExternalStorageDirectory(),
                FileFilterUtils.suffixFileFilter(type),
                TrueFileFilter.INSTANCE)
            while (iterator.hasNext()) {
                val fileINeed = iterator.next()
                Log.d("----", "File Path: ${fileINeed.path}")
                /*Log.d("----", " Name: ${fileINeed.name}")
                Log.d("----", " uri: ${fileINeed.toURI()}")
                Log.d("----", " length: ${fileINeed.length()/1024}")*/

                val fileLength = getStringSizeLengthFile(fileINeed.length())

                val fileUri = fileINeed.toURI().toString()

                contentList.add(DeviceContentModel(fileINeed.name, fileINeed.path, fileLength , serverSendDataType ,fileUri ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateRvContent() {
        // Creates a vertical Layout Manager
        rv_device_content_list.layoutManager = LinearLayoutManager(applicationContext)

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
        //  rv_animal_list.layoutManager = GridLayoutManager(this, 2)

        // Access the RecyclerView Adapter and load the data into it
        val adapter = DeviceContentAdapter(contentList, applicationContext)
        rv_device_content_list.adapter = adapter

        // row click listener
        rv_device_content_list.addOnItemTouchListener(
            RecyclerTouchListener(
                applicationContext,
                rv_device_content_list,
                object : ClickListener {
                    override fun onClick(view: View, position: Int) {
                        val returnIntent =  Intent()
                        returnIntent.putExtra("title", contentList[position].title)
                        returnIntent.putExtra("path", contentList[position].path)
                        returnIntent.putExtra("size", contentList[position].size)
                        returnIntent.putExtra("type", contentList[position].type)
                        returnIntent.putExtra("uri", contentList[position].uri)
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish()
                    }

                    override fun onLongClick(view: View, position: Int) {}
                })
        )
    }
}
