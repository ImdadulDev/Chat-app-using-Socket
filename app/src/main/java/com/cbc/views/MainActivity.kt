package com.cbc.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cbc.contactsyncutils.ContactWatchService
import com.cbc.R
import com.cbc.utils.AppSharePref
import com.cbc.utils.toast
import com.cbc.views.adapters.ViewPagerAdapter
import com.cbc.views.ui.fragments.HomeTabFragment
import com.cbc.views.ui.fragments.MyMessageFragment
import com.cbc.views.ui.home.HomeFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), HomeTabFragment.EditTextValueListenerInterface {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var appshre: AppSharePref
    lateinit var intentservice: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appshre = AppSharePref(this)
        /**** AUTO SYNC SERVICE ENABLE *****/
        if (appshre.GetDeviceRegId() != null && appshre.GetDeviceRegId()
                .equals(appshre.GetUSERDATA()!!.id)
        ) {
            intentservice = Intent(this, ContactWatchService::class.java)
            startService(intentservice)
        }

        /**** END AUTOSYNC SERVICE *********/


        //val toolbar: Toolbar = findViewById(R.id.toolbarMainActivity)
        setSupportActionBar(toolbarMainActivity)
        /*toolbar.setNavigationOnClickListener {
            @Override
            fun onClick(view: View) {
                Toast.makeText(applicationContext, "Back clicked!", Toast.LENGTH_SHORT).show();
            }
        }*/

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val TXT_USERNAME = navView.getHeaderView(0).findViewById<TextView>(R.id.TXT_USERNAME)
        val TXT_email = navView.getHeaderView(0).findViewById<TextView>(R.id.TXT_email)
        val TXT_FIRST = navView.getHeaderView(0).findViewById<TextView>(R.id.TXT_FIRST)
        val IMAGE_FIRST = navView.getHeaderView(0).findViewById<ImageView>(R.id.IMAGE_FIRST)
        val FloatButton = navView.getHeaderView(0).findViewById<FloatingActionButton>(R.id.FLOAT)
        TXT_USERNAME.text = "${appshre.GetUSERDATA()?.firstName}"
        TXT_email.text = "${appshre.GetUSERDATA()?.email}"
        if (appshre.GetUSERDATA()!!.profileImage.isEmpty())
            TXT_FIRST.text = "${appshre.GetUSERDATA()?.firstName!!.first().toUpperCase()}"
        else {
            TXT_FIRST.visibility = View.GONE
            FloatButton.visibility = View.GONE
            IMAGE_FIRST.visibility = View.VISIBLE
            Glide.with(this).load(appshre.GetUSERDATA()!!.profileImage).apply(
                RequestOptions.circleCropTransform().circleCrop()
                    .placeholder(R.drawable.ic_user_placeholder)
            ).into(IMAGE_FIRST)
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_tab_home,
                R.id.nav_gallery,
//                R.id.nav_slideshow,
                R.id.nav_tools,
                R.id.nav_share,
                R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (::intentservice.isInitialized) {
            stopService(intentservice)
        }
    }

    override fun onTextChange(text: String) {
        try {
            //val fragmentHome = supportFragmentManager.findFragmentByTag("android:switcher:2131362280:0")
            //val fragmentMyMessage = supportFragmentManager.findFragmentByTag("android:switcher:2131362280:1")

            MyMessageFragment.getChangedTextFromTabHomeFragment(text)
            HomeFragment.getChangedTextFromTabHomeFragment(text)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var doubleBackToExitOnce: Boolean = false

    override fun onBackPressed() {
        if(doubleBackToExitOnce){
            finishAffinity()

            //finish()
            //System.exit(0)

            return
        } else {
            super.onBackPressed()
        }

        this.doubleBackToExitOnce = true

        //displays a toast message when user clicks exit button
        toast("Please press again to exit")

        //displays the toast message for a while
        Handler().postDelayed({
            kotlin.run { doubleBackToExitOnce = false }
        }, 2000)

    }
}
