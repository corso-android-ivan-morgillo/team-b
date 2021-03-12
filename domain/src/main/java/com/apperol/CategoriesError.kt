package com.apperol

sealed class CategoriesError {
    object NoCategoriesFound : CategoriesError()
    object NoInternet : CategoriesError()
    object SlowInternet : CategoriesError()
    object ServerError : CategoriesError()
}
