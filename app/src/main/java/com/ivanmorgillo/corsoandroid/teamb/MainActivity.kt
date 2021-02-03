package com.ivanmorgillo.corsoandroid.teamb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel() //Ci permette di comunicare con Koin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //collega i dati alla UI, per far cio serve adapter
        val adapter: CocktailAdapter = CocktailAdapter()
        //Mettiamo in comunicazione l'adapter con la recycleview
        cocktails_List.adapter = adapter
        //Chiede la lista dei cocktail tramite il ViewModel
        val cocktailList = viewModel.getCocktails()
        adapter.setCocktails_List(cocktailList)
    }
}

data class CocktailUI(
    val cocktailName: String,
    val image: String,
)