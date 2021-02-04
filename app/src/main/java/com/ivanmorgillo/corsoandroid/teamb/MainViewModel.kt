package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val image_cocktail = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
    private val cocktailList = listOf<CocktailUI>(
        CocktailUI(
            cocktailName = "Mojito0",
            image = image_cocktail
        ),
        CocktailUI(
            cocktailName = "Mojito1",
            image = image_cocktail
        ),
        CocktailUI(
            cocktailName = "Mojito2",
            image = image_cocktail
        ),
        CocktailUI(
            cocktailName = "Mojito3",
            image = image_cocktail
        ),
        CocktailUI(
            cocktailName = "Mojito4",
            image = image_cocktail
        ),
        CocktailUI(
            cocktailName = "Mojito5",
            image = image_cocktail
        ),
        CocktailUI(
            cocktailName = "Mojito6",
            image = image_cocktail
        ),
        CocktailUI(
            cocktailName = "Mojito7",
            image = image_cocktail
        ),
        CocktailUI(
            cocktailName = "Mojito8",
            image = image_cocktail
        ),
        CocktailUI(
            cocktailName = "Mojito9",
            image = image_cocktail
        ),
    )

    fun getCocktails() = cocktailList
}
