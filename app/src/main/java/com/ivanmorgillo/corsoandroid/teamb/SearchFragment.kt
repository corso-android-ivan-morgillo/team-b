package com.ivanmorgillo.corsoandroid.teamb

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchFragment : Fragment() {
    private val searchViewModel: SearchViewModel by viewModel()
    private var lastClickedItem: View? = null
    //private val args: MainA by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("SearchFragmentCreated")
        /*val adapter = CocktailAdapter { item, view ->
            lastClickedItem = view
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            searchViewModel.send(SearchScreenEvent.OnCocktailClick(item))
        }
        cocktails_search_List.adapter = adapter*/
        //observeStates(adapter)
        observeActions(view)
        searchViewModel.send(SearchScreenEvent.OnReady("margarita"))
    }

    private fun observeActions(view: View) {
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

    /*private fun observeStates(adapter: CocktailAdapter) {
        searchViewModel.states.observe(viewLifecycleOwner, { state ->
            Timber.d(state.toString())
            when (state) {
                is SearchScreenStates.Content -> adapter.setCocktailsList(state.cocktails)
                is SearchScreenStates.Error -> Timber.d("Errore")
                SearchScreenStates.Loading -> Timber.d("Sto caricando")
            }
        })
    }*/
}
