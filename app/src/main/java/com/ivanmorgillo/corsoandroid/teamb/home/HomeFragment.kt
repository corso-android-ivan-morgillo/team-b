package com.ivanmorgillo.corsoandroid.teamb.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teamb.CocktailAdapter
import com.ivanmorgillo.corsoandroid.teamb.ErrorStates
import com.ivanmorgillo.corsoandroid.teamb.MainScreenActions
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvents
import com.ivanmorgillo.corsoandroid.teamb.MainScreenStates
import com.ivanmorgillo.corsoandroid.teamb.MainViewModel
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.exhaustive
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_error.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment() {
    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swiperefresh.setOnRefreshListener {
            viewModel.send(MainScreenEvents.OnRefreshClicked)
        }
        // collega i dati alla UI, per far cio serve adapter
        val adapter = CocktailAdapter {
            viewModel.send(MainScreenEvents.OnCocktailClick(it))
        }
        buttonError.setOnClickListener {
            viewModel.send(MainScreenEvents.OnSettingClick)
        }
        // creamo adapter
        // Mettiamo in comunicazione l'adapter con la recycleview
        cocktails_List.adapter = adapter
        // Chiede la lista dei cocktail tramite il ViewModel
        /*val cocktailList = viewModel.getCocktails()
        adapter.setCocktailsList(cocktailList)*/
        observeStates(adapter)
        observeActions(view)
        viewModel.send(MainScreenEvents.OnReady)
    }

    private fun observeActions(view: View) {
        viewModel.actions.observe(viewLifecycleOwner, { action ->
            Timber.d(action.toString())
            when (action) {
                is MainScreenActions.NavigateToDetail -> {
                    Toast.makeText(view.context, "working progress navigate to detail", Toast.LENGTH_SHORT).show()
                    val directions = HomeFragmentDirections.actionHomeFragmentToDetailFragment(action.cocktail.id)
                    findNavController().navigate(directions)
                }
                MainScreenActions.NavigateToSettings
                -> {
                    Timber.d(action.toString())
                    Log.d("NavigateToSettings", "Button Clicked!")
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                }
            }.exhaustive
        })
    }

    // L'activity quando è pronta (dopo aver creato adapter e associato a questa recycler view
    // comunica che è pronta
    // observe prende 2 argomenti:  lifecycle(main activity è una livecycle)
    // e un observable. Questa è una lambda in quanto contiene una sola funzione
    private fun observeStates(adapter: CocktailAdapter) {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            Timber.d(state.toString())
            when (state) {
                is MainScreenStates.Content -> {
                    swiperefresh.isRefreshing = false
                    adapter.setCocktailsList(state.cocktails)
                    errorVisibilityGone()
                }
                is MainScreenStates.Error -> {
                    when (state.error) {
                        ErrorStates.ShowNoInternetMessage -> {
                            innerLayoutNoInternet_SlowInternet.visibility = View.VISIBLE
                            errorCustom("No Internet Connection")
                            buttonError.visibility = View.VISIBLE
                        }
                        ErrorStates.ShowNoCocktailFound -> {
                            errorCustom("No Cocktail Found")
                            innerLayoutNoCocktailFound.visibility = View.VISIBLE
                        }
                        ErrorStates.ShowServerError -> {
                            errorCustom("Server Error")
                            innerLayoutServerError.visibility = View.VISIBLE
                        }
                        ErrorStates.ShowSlowInternet -> {
                            errorCustom("SlowInternet")
                            innerLayoutNoInternet_SlowInternet.visibility = View.VISIBLE
                        }
                    }
                }
                // quando l'aopp è in loading mostriamo progress bar
                MainScreenStates.Loading -> {
                    swiperefresh.isRefreshing = true
                }
            }.exhaustive
        })
    }

    private fun errorVisibilityGone() {
        innerLayoutNoInternet_SlowInternet.visibility = View.GONE
        innerLayoutNoCocktailFound.visibility = View.GONE
        innerLayoutServerError.visibility = View.GONE
    }

    private fun errorCustom(errore: String) {

        swiperefresh.isRefreshing = false

        imageViewError.setImageResource(R.drawable.errorimage)
        textViewError.setText(errore)
    }
}
