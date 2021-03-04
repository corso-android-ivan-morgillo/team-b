package com.ivanmorgillo.corsoandroid.teamb.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialElevationScale
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.databinding.FragmentSearchBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchFragment : Fragment(R.layout.fragment_search) {
    private val searchViewModel: SearchViewModel by viewModel()
    private var lastClickedItem: View? = null
    private val binding by viewBinding(FragmentSearchBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val query = this.arguments?.getString("query")
        if (query.isNullOrEmpty()) {
            findNavController().popBackStack()
        } else {
            searchViewModel.send(SearchScreenEvent.OnReady(query))
        }

        val adapter = SearchCocktailAdapter { item, _ ->
            lastClickedItem = view
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            searchViewModel.send(SearchScreenEvent.OnCocktailClick(item))
        }
        binding.cocktailsSearchList.adapter = adapter
        observeStates(adapter)
        observeActions()
    }

    private fun observeActions() {
        searchViewModel.actions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is SearchScreenAction.NavigateToDetail -> {
                    lastClickedItem?.run {
                        val extras = FragmentNavigatorExtras(this to "cocktail_transition_item")
                        val directions =
                            SearchFragmentDirections.actionSearchFragmentToDetailFragment(action.cocktail.id)
                        findNavController().navigate(directions, extras)
                    }
                }
            }.exhaustive
        })
    }

    private fun observeStates(adapter: SearchCocktailAdapter) {
        searchViewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is SearchScreenStates.Content -> {
                    adapter.setSearchList(state.cocktails)
                }
                is SearchScreenStates.Error -> Timber.d("error state search")
                SearchScreenStates.Loading -> Timber.d("loading state search")
            }
        })
    }
}
