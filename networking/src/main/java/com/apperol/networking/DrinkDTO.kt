package com.apperol.networking

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DrinkDTO(
    @SerializedName("drinks")
    val drinks: List<Drink>
) {
    @Keep
    data class Drink(
        @SerializedName("idDrink")
        val idDrink: String,
        @SerializedName("strDrink")
        val strDrink: String,
        @SerializedName("strDrinkThumb")
        val strDrinkThumb: String
    )
}
