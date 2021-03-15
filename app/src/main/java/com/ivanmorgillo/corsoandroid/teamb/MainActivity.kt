package com.ivanmorgillo.corsoandroid.teamb

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.apperol.R
import com.apperol.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.DisableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.EnableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFacebook
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFavorite
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFeedBack
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToRandom
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToSearch
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToSettingMenu
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToTwitter
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

interface CleanSearchField {
    fun cleanSearchField()
}

private const val RC_SIGN_IN: Int = 1234

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, CleanSearchField {

    private var searchView: SearchView? = null
    private val mainActivityViewModel: MainActivityViewModel by viewModel()
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeActions()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.settingsFragment
        )

        appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations)
            .setOpenableLayout(binding.drawerLayout)
            .build()

        // Set up ActionBar
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Set up navigation menu
        binding.navView.setupWithNavController(navController)
        // naviga nelle pagine del navigation drawer
        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.itemIconTintList = null
    }

    override fun onSupportNavigateUp(): Boolean {
        Timber.d("Hamburger CLicked")
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return false
        } else {
            return NavigationUI.navigateUp(navController, appBarConfiguration)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        val searchItem = menu.findItem(R.id.search_name)
        searchView = searchItem.actionView as SearchView

        searchView!!.queryHint = "Search cocktail by name"
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView!!.setQuery(query, false)
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
                is NavigateToSearch -> {
                    val bundle = Bundle()
                    bundle.putString("query", action.query)
                    navController.navigate(R.id.searchFragment, bundle)
                }
                NavigateToSettingMenu -> {
                    navController.navigate(R.id.settingsFragment)
                }
                NavigateToFacebook -> openNewTabWindow("https://www.facebook.com", this)
                NavigateToTwitter -> openNewTabWindow("https://twitter.com", this)
                NavigateToFeedBack -> openNewTabWindow(getString(R.string.feedback_link), this)
                DisableDarkMode -> Unit
                EnableDarkMode -> Unit
                NavigateToFavorite -> navController.navigate(R.id.favoritesFragment)
                NavigateToRandom -> navController.navigate(R.id.randomCocktailFragment)
            }.exhaustive
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_customCocktail -> {
                Timber.d("CustomCocktail")
            }
            R.id.nav_favorites -> {
                mainActivityViewModel.send(MainScreenEvent.OnFavoriteClick)
            }
            R.id.nav_settings -> {
                mainActivityViewModel.send(MainScreenEvent.OnMenuClick)
            }
            R.id.nav_facebook -> {
                mainActivityViewModel.send(MainScreenEvent.OnFacebookClick)
            }
            R.id.nav_twitter -> {
                mainActivityViewModel.send(MainScreenEvent.OnTwitterClick)
            }
            R.id.nav_feedback -> {
                mainActivityViewModel.send(MainScreenEvent.OnFeedBackClick)
            }
            R.id.sign_in -> {
                Timber.d("Contacts")
                // Choose authentication providers
                val providers = arrayListOf(
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                )
                // Create and launch sign-in intent
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                    RC_SIGN_IN
                )
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_randomCocktail -> {
                mainActivityViewModel.send(MainScreenEvent.OnRandomClick)
            }

            /*
            R.id.nav_share -> {
                Timber.d("Share")
            }
            R.id.nav_evaluate -> {
                Timber.d("Evaluate")
            }
             */
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openNewTabWindow(urls: String, context: Context) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }

    override fun cleanSearchField() {
        searchView?.setQuery("", false)
        searchView?.isIconified = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Timber.d("USER: $user")
                // ...
            } else {
                Timber.e("AUTHENTICATION ERROR: ${response?.error?.errorCode}")
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
