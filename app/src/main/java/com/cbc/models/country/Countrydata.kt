package com.cbc.models.country


import com.google.gson.annotations.SerializedName

data class Countrydata(
    @SerializedName("country")
    val country: List<Country>
)