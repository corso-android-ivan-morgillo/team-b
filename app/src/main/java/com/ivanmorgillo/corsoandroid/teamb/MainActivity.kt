package com.ivanmorgillo.corsoandroid.teamb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //collega i dati alla UI, per far cio serve adapter
        val adapter: CocktailAdapter= CocktailAdapter()
        cocktails_List.adapter=adapter
        adapter.setCocktails_List(cocktails)
    }
}

data class CocktailUI(
    val cocktailName: String,
    val image: String,
)

val cocktails= listOf<CocktailUI>(
    CocktailUI(cocktailName = "Mojito0", image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"),
    CocktailUI(cocktailName = "Mojito1", image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"),
    CocktailUI(cocktailName = "Mojito2", image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"),
    CocktailUI(cocktailName = "Mojito3", image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"),
    CocktailUI(cocktailName = "Mojito4", image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"),
    CocktailUI(cocktailName = "Mojito5", image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"),
    CocktailUI(cocktailName = "Mojito6", image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"),
    CocktailUI(cocktailName = "Mojito7", image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"),
    CocktailUI(cocktailName = "Mojito8", image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"),
    CocktailUI(cocktailName = "Mojito9", image = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"),
)