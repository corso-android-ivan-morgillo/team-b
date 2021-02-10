package com.ivanmorgillo.corsoandroid.teamb

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_error.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel() // Ci permette di comunicare con Koin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // carichiamo layout
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
            when (state) {
                is MainScreenStates.Content -> {
                    cocktail_List_ProgressBar.gone()
                    adapter.setCocktailsList(state.cocktails)
                }
                MainScreenStates.Error -> {
                    cocktail_List_ProgressBar.gone()
                    Snackbar.make(cocktail_List_Root, getString(R.string.main_screen_error), Snackbar.LENGTH_SHORT)
                        .show()
                }
                // quando l'aopp è in loading mostriamo progress bar
                MainScreenStates.Loading -> {
                    cocktail_List_ProgressBar.visible()
                }
            }
        })
        viewModel.actions.observe(this, { action ->
            when (action) {
                is MainScreenActions.NavigateToDetail -> {
                    Toast.makeText(this, "working progress navigate to detail", Toast.LENGTH_SHORT).show()
                }
                MainScreenActions.ShowNoInternetMessage -> {
                    cocktail_List_ProgressBar.gone()
                    setContentView(R.layout.layout_error)
                    textViewError.setText("No Internet Connection")
                    imageViewError.setImageResource(R.drawable.errorimage)
                }
                MainScreenActions.ShowNoCocktailFound -> {
                    cocktail_List_ProgressBar.gone()
                    setContentView(R.layout.layout_error)
                    textViewError.setText("No Cocktail Found")
                    imageViewError.setImageResource(R.drawable.errorimage)
                }
                MainScreenActions.ShowServerError -> {
                    cocktail_List_ProgressBar.gone()
                    setContentView(R.layout.layout_error)
                    textViewError.setText("Server Error")
                    imageViewError.setImageResource(R.drawable.errorimage)
                }
                MainScreenActions.ShowSlowInternet -> {
                    cocktail_List_ProgressBar.gone()
                    setContentView(R.layout.layout_error)
                    textViewError.setText("SlowInternet")
                    imageViewError.setImageResource(R.drawable.errorimage)
                }
            }.exhaustive
        })
        viewModel.send(MainScreenEvents.OnReady)
    }
}// startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
