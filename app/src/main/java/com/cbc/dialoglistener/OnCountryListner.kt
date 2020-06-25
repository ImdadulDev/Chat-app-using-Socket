package com.cbc.dialoglistener

interface OnCountryListner {
    fun SelectedCode(code: String)
    fun SelectedCountry(name: String)
    fun selectedImages(drwable: Int)
}