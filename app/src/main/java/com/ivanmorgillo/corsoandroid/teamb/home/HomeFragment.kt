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
import com.ivanmorgillo.corsoandroid.teamb.CocktailAdapter
import com.ivanmorgillo.corsoandroid.teamb.ErrorStates
import com.ivanmorgillo.corsoandroid.teamb.MainScreenActions
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvents
import com.ivanmorgillo.corsoandroid.teamb.MainScreenStates
import com.ivanmorgillo.corsoandroid.teamb.MainViewModel
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.exhaustive
import com.ivanmorgillo.corsoandroid.teamb.gone
import com.ivanmorgillo.corsoandroid.teamb.visible
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_error.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private val viewModel: MainViewModel by viewModel() // Ci permette di comunicare con Koin

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
        // L'activity quando è pronta (dopo aver creato adapter e associato a questa recycler view
        // comunica che è pronta
        // observe prende 2 argomenti:  lifecycle(main activity è una livecycle)
        // e un observable. Questa è una lambda in quanto contiene una sola funzione
        viewModel.states.observe(viewLifecycleOwner, { state ->
            Timber.d(state.toString())
            when (state) {
                is MainScreenStates.Content -> {
                    cocktail_List_ProgressBar.gone()
                    adapter.setCocktailsList(state.cocktails)
                }
                is MainScreenStates.Error -> {
                    when (state.error) {
                        ErrorStates.ShowNoInternetMessage -> {
                            errorCustom("No Internet Connection")
                            buttonError.visibility = View.VISIBLE
                        }
                        ErrorStates.ShowNoCocktailFound -> {
                            errorCustom("No Cocktail Found")
                        }
                        ErrorStates.ShowServerError -> {
                            errorCustom("Server Error")
                        }
                        ErrorStates.ShowSlowInternet -> {
                            errorCustom("SlowInternet")
                        }
                    }
                }
                // quando l'aopp è in loading mostriamo progress bar
                MainScreenStates.Loading -> {
                    cocktail_List_ProgressBar.visible()
                }
            }.exhaustive
        })
        viewModel.actions.observe(viewLifecycleOwner, { action ->
            Timber.d(action.toString())
            when (action) {
                is MainScreenActions.NavigateToDetail -> {
                    Toast.makeText(view.context, "working progress navigate to detail", Toast.LENGTH_SHORT).show()
                }
                MainScreenActions.NavigateToSettings -> {
                    Timber.d(action.toString())
                    Log.d("NavigateToSettings", "Button Clicked!")
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                }
            }.exhaustive
        })
        viewModel.send(MainScreenEvents.OnReady)
    }

    private fun errorCustom(errore: String) {
        cocktail_List_ProgressBar.gone()
        swiperefresh.isRefreshing = false
        innerLayout2.visibility = View.VISIBLE
        imageViewError.setImageResource(R.drawable.errorimage)
        textViewError.setText(errore)
    }
}
