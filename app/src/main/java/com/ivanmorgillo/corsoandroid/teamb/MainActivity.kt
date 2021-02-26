package com.ivanmorgillo.corsoandroid.teamb

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModel()
    private val navController by lazy { Navigation.findNavController(this, R.id.nav_host_fragment) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // carichiamo layout
        observeActions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        val searchItem = menu.findItem(R.id.search_name)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search cocktail by name"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                Timber.d("Aggiorno il testo..")
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.setQuery(query, false)
                Timber.d("Ricerca in corso.. $query")
                mainActivityViewModel.send(MainActivityScreenEvent.OnSearchClick(query.orEmpty()))
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_name -> {
                Timber.d("TI PIACE QUANDO CLICCO IL TUO DADO?")
                true
            }
            R.id.search_name -> {
                Timber.d("TI PIACE QUANDO CLICCO IL TUO SEARCH?")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeActions() {
        mainActivityViewModel.actions.observe(this, { action ->
            Timber.d(action.toString())
            when (action) {
                is MainActivityScreenAction.NavigateToSearch -> {
                    navController.navigate(R.id.searchFragment)
                }
            }.exhaustive
        })
    }
}
