package com.ivanmorgillo.corsoandroid.teamb

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_error.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel() // Ci permette di comunicare con Koin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // carichiamo layout
        swiperefresh.setOnRefreshListener {
            viewModel.send(MainScreenEvents.OnRefreshClicked)
        }
        // collega i dati alla UI, per far cio serve adapter
        val adapter = CocktailAdapter {
            viewModel.send(MainScreenEvents.OnCocktailClick(it))
        } // creamo adapter
        // Mettiamo in comunicazione l'adapter con la recycleview
        cocktails_List.adapter = adapter
        // Chiede la lista dei cocktail tramite il ViewModel
        /*val cocktailList = viewModel.getCocktails()
        adapter.setCocktailsList(cocktailList)*/
        // L'activity quando è pronta (dopo aver creato adapter e associato a questa recycler view
        // comunica che è pronta
        // observe prende 2 argomenti:  lifecycle(main activity è una livecycle)
        // e un observable. Questa è una lambda in quanto contiene una sola funzione
        viewModel.states.observe(this, { state ->
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
                            buttonError.visibility = VISIBLE
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
        viewModel.actions.observe(this, { action ->
            Timber.d(action.toString())
            when (action) {
                is MainScreenActions.NavigateToDetail -> {
                    Toast.makeText(this, "working progress navigate to detail", Toast.LENGTH_SHORT).show()
                }
                MainScreenActions.NavigateToSettings -> {
                    Timber.d(action.toString())
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    // "android.settings.WIFI_SETTINGS"
                }
            }.exhaustive
        })
        viewModel.send(MainScreenEvents.OnReady)
    }

    private fun errorCustom(errore: String) {
        cocktail_List_ProgressBar.gone()
        swiperefresh.isRefreshing = false
        innerLayout2.visibility = VISIBLE
        imageViewError.setImageResource(R.drawable.errorimage)
        textViewError.setText(errore)
    }
}
