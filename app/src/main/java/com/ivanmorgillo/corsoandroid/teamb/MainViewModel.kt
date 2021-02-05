package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val imageCocktail = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
    private val cocktailList = listOf<CocktailUI>(
        CocktailUI(
            cocktailName = "Mojito0",
            image = imageCocktail
        ),
        CocktailUI(
            cocktailName = "Mojito1",
            image = imageCocktail
        ),
        CocktailUI(
            cocktailName = "Mojito2",
            image = imageCocktail
        ),
        CocktailUI(
            cocktailName = "Mojito3",
            image = imageCocktail
        ),
        CocktailUI(
            cocktailName = "Mojito4",
            image = imageCocktail
        ),
        CocktailUI(
            cocktailName = "Mojito5",
            image = imageCocktail
        ),
        CocktailUI(
            cocktailName = "Mojito6",
            image = imageCocktail
        ),
        CocktailUI(
            cocktailName = "Mojito7",
            image = imageCocktail
        ),
        CocktailUI(
            cocktailName = "Mojito8",
            image = imageCocktail
        ),
        CocktailUI(
            cocktailName = "Mojito9",
            image = imageCocktail
        ),
    )

    fun getCocktails() = cocktailList
}
