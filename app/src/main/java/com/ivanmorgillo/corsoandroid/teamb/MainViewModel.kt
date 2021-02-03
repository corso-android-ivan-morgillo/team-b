package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val cocktailList = listOf<CocktailUI>(
        CocktailUI(
            cocktailName = "Mojito0",
            image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
        ),
        CocktailUI(
            cocktailName = "Mojito1",
            image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
        ),
        CocktailUI(
            cocktailName = "Mojito2",
            image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
        ),
        CocktailUI(
            cocktailName = "Mojito3",
            image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
        ),
        CocktailUI(
            cocktailName = "Mojito4",
            image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
        ),
        CocktailUI(
            cocktailName = "Mojito5",
            image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
        ),
        CocktailUI(
            cocktailName = "Mojito6",
            image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
        ),
        CocktailUI(
            cocktailName = "Mojito7",
            image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
        ),
        CocktailUI(
            cocktailName = "Mojito8",
            image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
        ),
        CocktailUI(
            cocktailName = "Mojito9",
            image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
        ),
    )

    fun getCocktails() = cocktailList
}