package com.ivanmorgillo.corsoandroid.teamb

import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
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
            .setDrawerLayout(drawer_layout)
            .build()

        // Set up ActionBar
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        nav_view.setupWithNavController(navController)

        observeActions()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

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
            /*
            R.id.action_name -> {
                Timber.d("TI PIACE QUANDO CLICCO IL TUO DADO?")
                true
            }
             */
            R.id.home -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.search_name -> {
                Timber.d("TI PIACE QUANDO CLICCO IL TUO SEARCH?")
                true
            }
            R.id.menu_name -> {
                Toast.makeText(
                    applicationContext,
                    "Work in progress Navigate to Settings",
                    R.integer.motion_duration_large
                ).show()
                Timber.d("TI PIACE QUANDO CLICCO IL TUO MENU?")
                mainActivityViewModel.send(MainActivityScreenEvent.OnMenuClick)
                true
            }
            R.id.theme_name -> {

                val currentNightMode = (resources.configuration.uiMode
                        and Configuration.UI_MODE_NIGHT_MASK)
                when (currentNightMode) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                    }
                    Configuration.UI_MODE_NIGHT_YES,
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                    }
                }
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
                    val bundle = Bundle()
                    bundle.putString("query", action.query)
                    val fragInfo = SearchFragment()
                    fragInfo.arguments = bundle
                    navController.navigate(R.id.searchFragment, fragInfo.arguments)
                }
                MainActivityScreenAction.NavigateToSettingMenu -> {
                    Timber.d(action.toString())
                    Timber.d("NavigateToSettingMenu", "Menu Button Clicked")
                    navController.navigate(R.id.settingsFragment)
                }
            }.exhaustive
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_messages -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_friends -> {
                Toast.makeText(this, "Friends clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_update -> {
                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
