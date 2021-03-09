package com.ivanmorgillo.corsoandroid.teamb.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.databinding.FragmentFavoritesBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {
    private val binding by viewBinding(FragmentFavoritesBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FavoriteCocktailAdapter()
        val imageCocktail = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
        val favoriteCocktailList = listOf<FavoriteCocktailUI>(
            FavoriteCocktailUI(
                cocktailName = "Mojito0",
                image = imageCocktail,
                id = 1,
                category = "category1"
            ),
            FavoriteCocktailUI(
                cocktailName = "Mojito1",
                image = imageCocktail,
                id = 2,
                category = "category2"
            ),
            FavoriteCocktailUI(
                cocktailName = "Mojito2",
                image = imageCocktail,
                id = 3,
                category = "category3"
            ),
            FavoriteCocktailUI(
                cocktailName = "Mojito3",
                image = imageCocktail,
                id = 4,
                category = "category4"
            ),
            FavoriteCocktailUI(
                cocktailName = "Mojito4",
                image = imageCocktail,
                id = 5,
                category = "category5"
            ),
            FavoriteCocktailUI(
                cocktailName = "Mojito5",
                image = imageCocktail,
                id = 6,
                category = "category6"
            ),
        )
        adapter.setFavoriteList(favoriteCocktailList)
        binding.cocktailsFavoritesList.adapter = adapter
    }
}
