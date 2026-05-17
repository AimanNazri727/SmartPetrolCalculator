package com.example.smartpetrolcalculator.data

import com.google.gson.annotations.SerializedName

data class FuelPrice(
    @SerializedName("date")   val date: String,
    @SerializedName("ron95")  val ron95: Double?,
    @SerializedName("ron97")  val ron97: Double?,
    @SerializedName("diesel") val diesel: Double?
)