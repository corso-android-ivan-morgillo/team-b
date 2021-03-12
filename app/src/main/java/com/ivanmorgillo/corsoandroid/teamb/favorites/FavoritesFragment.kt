package com.ivanmorgillo.corsoandroid.teamb.favorites

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.databinding.FragmentFavoritesBinding
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteErrorStates.FavoriteListEmpty
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteErrorStates.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteErrorStates.ShowServerError
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteErrorStates.ShowSlowInternet
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteScreenEvent.OnReady
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteScreenStates.Content
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteScreenStates.Error
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoritesFragmentDirections.Companion.actionFavoritesFragmentToDetailFragment
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {
    private val binding by viewBinding(FragmentFavoritesBinding::bind)
    private val favoriteViewModel: FavoriteViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val favoriteAdapter = FavoriteCocktailAdapter { item, view ->
            when (view.id) {
                R.id.favorite_cocktail_delete -> favoriteViewModel.send(FavoriteScreenEvent.OnDeleteClick(item))
                R.id.favorite_cocktail_root -> favoriteViewModel.send(FavoriteScreenEvent.OnCocktailClick(item))
            }
        }
        binding.cocktailsFavoritesList.adapter = favoriteAdapter
        observeStates(favoriteAdapter)
        observeActions()
        favoriteViewModel.send(OnReady)
    }

    private fun observeActions() {
        favoriteViewModel.actions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is NavigateToDetail -> {
                    val directions = actionFavoritesFragmentToDetailFragment(action.cocktail.id)
                    findNavController().navigate(directions)
                }
            }.exhaustive
        })
    }

    private fun observeStates(adapter: FavoriteCocktailAdapter) {
        favoriteViewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is Content -> {
                    adapter.setFavoriteList(state.favorites)
                }
                is Error -> {
                    when (state.error) {
                        ShowNoInternetMessage -> {
                            errorCustom("No Internet Connection")
                        }
                        ShowServerError -> {
                            errorCustom("Server Error")
                        }
                        ShowSlowInternet -> {
                            errorCustom("Slow Internet")
                        }
                        FavoriteListEmpty -> {
                            adapter.setFavoriteList(emptyList())
                            binding.favoriteProgressBar.visibility = View.GONE
                            Toast.makeText(context, "Nessun preferito trovato", Toast.LENGTH_SHORT).show()
                        }
                    }.exhaustive
                }
                Loading -> binding.favoriteProgressBar.visibility = View.VISIBLE
            }
        })
    }

    private fun errorCustom(error: String) {
        binding.favoriteProgressBar.visibility = View.GONE
        binding.innerLayoutNoInternetSlowInternet.textViewNoInternetError.text = error
    }
}
