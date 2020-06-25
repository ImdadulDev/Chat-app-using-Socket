package com.cbc.views.ui.activities

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cbc.R
import com.cbc.utils.Constants
import kotlinx.android.synthetic.main.activity_web_view.*


class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var link: String
    private lateinit var attachmentType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        //https://nodeserver.brainiuminfotech.com:3011/uploads/cbc/video/28136575572a9590f0497fcc5d6849a28692f5f23.mp4

        val intent = intent
        if(intent != null){
            link = intent.getStringExtra("link")!!
            attachmentType = intent.getStringExtra("attachmentType")!!
            Log.d("----", "WebView : ${Constants.CHAT_ATTACHMENT_BASE_URL + link}")
            Log.d("----", "WebView attachmentType: ${attachmentType}")


        }

        webView = findViewById(R.id.webView_show_content)

        webViewToolbar.setNavigationOnClickListener {
            finish()
        }


        //webView.loadUrl("https://nodeserver.brainiuminfotech.com:3011/uploads/cbc/audio/Beat sad ringtone.mp3")

        val webSetting = webView.settings
        webSetting.allowContentAccess = true
        webSetting.allowFileAccess = true

        // Enable java script in web view
        webSetting.javaScriptEnabled = true

        // Enable and setup web view cache
        webSetting.setAppCacheEnabled(true)
        webSetting.cacheMode = WebSettings.LOAD_DEFAULT
        webSetting.setAppCachePath(cacheDir.path)


        // Enable zooming in web view
        webSetting.setSupportZoom(true)
        webSetting.builtInZoomControls = true
        webSetting.displayZoomControls = false



        // Enable disable images in web view
        webSetting.blockNetworkImage = false
        // Whether the WebView should load image resources
        webSetting.loadsImagesAutomatically = true


        // More web view settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSetting.safeBrowsingEnabled = true  // api 26
        }
        //settings.pluginState = WebSettings.PluginState.ON
        webSetting.useWideViewPort = true
        webSetting.loadWithOverviewMode = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.mediaPlaybackRequiresUserGesture = true


        // More optional settings, you can enable it by yourself
        webSetting.domStorageEnabled = true
        webSetting.setSupportMultipleWindows(true)
        webSetting.loadWithOverviewMode = true
        webSetting.allowContentAccess = true
        webSetting.setGeolocationEnabled(false)
        webSetting.allowUniversalAccessFromFileURLs = true
        webSetting.allowFileAccess = true
        webSetting.supportZoom()



        webView.loadUrl(Constants.CHAT_ATTACHMENT_BASE_URL + link)
        //webView.loadUrl("https://nodeserver.brainiuminfotech.com:3011/uploads/cbc/audio/820136805Dil_mera_chahe-1016eab2-bf70-478b-82f0-5fc485e8c409.mp3")

        if(attachmentType == "doc"){
            webView.loadUrl("https://docs.google.com/viewer?embedded=true&url="+ Constants.CHAT_ATTACHMENT_BASE_URL + link)
        }


        // WebView settings
        webView.fitsSystemWindows = true


        /*
            if SDK version is greater of 19 then activate hardware acceleration
            otherwise activate software acceleration
        */
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        // Set web view chrome client
        webView.webChromeClient = object: WebChromeClient(){
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progress_bar.progress = newProgress
            }
        }

        // Set web view client
        webView.webViewClient = object: WebViewClient(){

            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                // TODO Auto-generated method stub
                view.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                // Page loading started
                // Do something
                //toast("Page loading.")

                // Enable disable back forward button
               // button_back.isEnabled = webView.canGoBack()
               // button_forward.isEnabled = webView.canGoForward()
            }

            override fun onPageFinished(view: WebView, url: String) {
                // Page loading finished
                // Display the loaded page title in a toast message
                //toast("Page loaded: ${view.title}")

                // Enable disable back forward button
               // button_back.isEnabled = webView.canGoBack()
              //  button_forward.isEnabled = webView.canGoForward()
            }
        }





       /* // Load button click listener
        button_load.setOnClickListener{
            // Load url in a web view
            webView.loadUrl(url)
        }


        // Back button click listener
        button_back.setOnClickListener{
            if(webView.canGoBack()){
                // Go to back history
                webView.goBack()
            }
        }


        // Forward button click listener
        button_forward.setOnClickListener{
            if(webView.canGoForward()){
                // Go to forward history
                webView.goForward()
            }
        }*/
    }

    // Method to show app exit dialog
    private fun showAppExitDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Please confirm")
        builder.setMessage("Want to exit the app?")
        builder.setCancelable(true)

        builder.setPositiveButton("Yes") { _, _ ->
            // Do something when user want to exit the app
            // Let allow the system to handle the event, such as exit the app
            super@WebViewActivity.onBackPressed()
        }

        builder.setNegativeButton("No") { _, _ ->
            // Do something when want to stay in the app
            //toast("thank you.")
        }

        // Create the alert dialog using alert dialog builder
        val dialog = builder.create()

        // Finally, display the dialog when user press back button
        dialog.show()
    }

/*


    // Handle back button press in web view
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            // If web view have back history, then go to the web view back history
            webView.goBack()
            //toast("Going to back history")
        } else {
            // Ask the user to exit the app or stay in here
            //showAppExitDialog()
        }
    }
*/
    override fun onBackPressed() {
        finish()
    }
}


// Extension function to show toast message
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

