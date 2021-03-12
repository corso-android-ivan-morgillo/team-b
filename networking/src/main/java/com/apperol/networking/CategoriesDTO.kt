package com.apperol.networking

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CategoriesDTO(
    @SerializedName("drinks")
    val categories: List<CategoryDTO>
) {
    @Keep
    data class CategoryDTO(
        @SerializedName("strCategory")
        val strCategory: String
    )
}
