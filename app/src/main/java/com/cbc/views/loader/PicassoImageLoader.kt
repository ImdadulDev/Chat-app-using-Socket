package com.cbc.views.loader

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.cbc.R
import com.squareup.picasso.Picasso
import lv.chi.photopicker.loader.ImageLoader

class PicassoImageLoader : ImageLoader {

    override fun loadImage(context: Context, view: ImageView, uri: Uri) {
        Picasso.get()
            .load(uri)
            .placeholder(R.drawable.bg_placeholder)
            .fit()
            .centerCrop()
            .into(view)
    }
}