package com.ivanmorgillo.corsoandroid.teamb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel() // Ci permette di comunicare con Koin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // carichiamo layout
        // collega i dati alla UI, per far cio serve adapter
        val adapter = CocktailAdapter() // creamo adapter
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
                is MainScreenState.Content -> {
                    cocktail_List_ProgressBar.gone()
                    adapter.setCocktailsList(state.coctails)
                }
                MainScreenState.Error -> TODO()
                // quando l'aopp è in loading mostriamo progress bar
                MainScreenState.Loading -> {
                    cocktail_List_ProgressBar.visible()
                }
            }
        })
        viewModel.send(MainScreenEvents.OnReady)
    }
}

data class CocktailUI(
    val cocktailName: String,
    val image: String,
)
