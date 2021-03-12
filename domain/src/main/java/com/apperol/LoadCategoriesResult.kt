package com.apperol

sealed class LoadCategoriesResult {
    data class Success(val categories: List<Category>) : LoadCategoriesResult()
    data class Failure(val error: CategoriesError) : LoadCategoriesResult()
}
