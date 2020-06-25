package com.cbc.views.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.cbc.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_custom_image_view.*


class CustomImageViewActivity : AppCompatActivity() {
    private var mCurrRotation = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_image_view)

        customImageToolbar.setNavigationOnClickListener {
            finish()
        }

        customImageRotate.setOnClickListener {
            customImageView.rotation = customImageView.rotation + 90

            mCurrRotation %= 360
            val fromRotation = mCurrRotation.toFloat()
            val toRotation = 90.let { mCurrRotation += it; mCurrRotation }.toFloat()

            val rotateAnim = RotateAnimation(
                fromRotation, toRotation,
                (customImageRotate.width / 2).toFloat(), (customImageRotate.height / 2).toFloat()
            )

            rotateAnim.duration = 200 // Use 0 ms to rotate instantly

            rotateAnim.fillAfter = true // Must be true or the animation will reset


            customImageRotate.startAnimation(rotateAnim)
        }

        try {
            val intent = intent
            if(intent != null){
                val imageUrl = intent.getStringExtra("image_url")!!
                Log.d("----", "image url : $imageUrl")

                Picasso
                    .get()
                    .load(imageUrl).resize(6000, 2000)
                    .onlyScaleDown()
                    .placeholder(R.drawable.loading_text_image)
                    .into(customImageView)

                /*try {
                    val url = URL(imageUrl)
                    val bitmapImage =
                        BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    customImageView.setBitmap(bitmapImage)

                } catch (e: IOException) {
                    System.out.println(e)
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}
