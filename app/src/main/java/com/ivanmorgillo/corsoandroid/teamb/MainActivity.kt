package com.ivanmorgillo.corsoandroid.teamb

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val mainActivityViewModel: MainActivityViewModel by viewModel()
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // carichiamo layout

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.settingsFragment
        )

        appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations)
            .setOpenableLayout(drawer_layout)
            .build()

        // Set up ActionBar
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Set up navigation menu
        nav_view.setupWithNavController(navController)
        // naviga nelle pagine del navigation drawer
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.itemIconTintList = null
        observeActions()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    // l'hamburger menu si trasforma in freccia per tornare indietro
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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
                // Timber.d("Ricerca in corso.. $query")
                if (!query.isNullOrEmpty()) {
                    mainActivityViewModel.send(MainScreenEvent.OnSearchClick(query))
                }
                return false
            }
        })
        return true
    }

    private fun observeActions() {
        mainActivityViewModel.actions.observe(this, { action ->
            Timber.d(action.toString())
            when (action) {
                is MainScreenAction.NavigateToSearch -> {
                    val bundle = Bundle()
                    bundle.putString("query", action.query)
                    navController.navigate(R.id.searchFragment, bundle)
                }
                MainScreenAction.NavigateToSettingMenu -> {
                    Timber.d(action.toString())
                    Timber.d("NavigateToSettingMenu", "Menu Button Clicked")
                    Toast.makeText(
                        applicationContext,
                        "Work in progress Navigate to Settings",
                        R.integer.motion_duration_large
                    ).show()
                    navController.navigate(R.id.settingsFragment)
                }
            }.exhaustive
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_customCocktail -> {
                Timber.d("CustomCocktail")
            }
            R.id.nav_favorites -> {
                Timber.d("FavoriteCocktail")
            }
            R.id.nav_settings -> {
                Timber.d("Settings")
                mainActivityViewModel.send(MainScreenEvent.OnMenuClick)
            }
            R.id.nav_facebook -> {
                Timber.d("Facebook")
                openNewTabWindow("https://www.facebook.com", this)
            }
            R.id.nav_twitter -> {
                Timber.d("Twitter")
                openNewTabWindow("https://twitter.com", this)
            }
            R.id.nav_feedback -> {
                Timber.d("FeedBack")
            }
            R.id.nav_contact -> {
                Timber.d("Contacts")
            }
            R.id.nav_share -> {
                Timber.d("Share")
            }
            R.id.nav_evaluate -> {
                Timber.d("Evaluate")
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun openNewTabWindow(urls: String, context: Context) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }
}
