package com.apperol

data class Detail(
    val name: String,
    val image: String,
    val id: Long,
    val isAlcoholic: Boolean,
    val glass: String,
    val ingredients: List<Ingredient>,
    val youtubeLink: String?,
    val instructions: String,
    val category: String,
)
