package com.apperol.networking

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CategoryDTO(
    @SerializedName("drinks")
    val categories: List<Category>
) {
    @Keep
    data class Category(
        @SerializedName("strCategory")
        val strCategory: String
    )
}
