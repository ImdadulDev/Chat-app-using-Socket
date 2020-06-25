package com.cbc.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.cbc.dialogs.CountryBottomSheetDialog
import com.cbc.R
import com.cbc.dialoglistener.OnCountryListner
import com.cbc.utils.CuntryHelper
import kotlinx.android.synthetic.main.activity_mobile.*

class MobileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile)
        supportActionBar!!.title = "Enter your phone number"

        EDX_COUNTRY.setOnClickListener {
            val cdialog = CountryBottomSheetDialog(object : OnCountryListner {
                override fun SelectedCountry(name: String) {
                    EDX_COUNTRY.setText(name)
                }

                override fun SelectedCode(code: String) {
                    EDX_COUNTRY_CODE.setText(code)
                }

                override fun selectedImages(drwable: Int) {
                }

            })
            cdialog.showNow(supportFragmentManager, "")
        }
        val countryList = CuntryHelper.GetAllList(this)
        EDX_COUNTRY_CODE.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                countryList.country.forEach {
                    if (p0.toString().equals(it.callingCode)) {
                        EDX_COUNTRY.setText(it.name)
                    }
                }

            }

        })


        BTN_NEXT.setOnClickListener {
            val intent = Intent()
            intent.putExtra("code", EDX_COUNTRY_CODE.text.toString())
            intent.putExtra("number", EDX_NUMBER.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }


}
