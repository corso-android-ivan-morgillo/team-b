package com.ivanmorgillo.corsoandroid.teamb.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchFragment : Fragment() {
    private val searchViewModel: SearchViewModel by viewModel()
    private var lastClickedItem: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val query = this.arguments?.getString("query")
        Timber.d("QUERY ARG $query")
        if (query.isNullOrEmpty()) {
            findNavController().popBackStack()
        } else {
            searchViewModel.send(SearchScreenEvent.OnReady(query))
        }

        Timber.d("SearchFragmentCreated")
        val adapter = SearchCocktailAdapter()
        cocktails_search_List.adapter = adapter
        observeStates(adapter)
        observeActions()
        // searchViewModel.send(SearchScreenEvent.OnReady("margarita"))
    }

    private fun observeActions() {
        searchViewModel.actions.observe(viewLifecycleOwner, { action ->
            Timber.d(action.toString())
            when (action) {
                is SearchScreenAction.NavigateToDetail -> {
                    lastClickedItem?.run {
                        val extras = FragmentNavigatorExtras(this to "cocktail_transition_item")
                        val directions =
                            SearchFragmentDirections.actionSearchFragmentToDetailFragment(action.cocktail.id)
                        Log.d("SearchID", " = ${action.cocktail.id}")
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
