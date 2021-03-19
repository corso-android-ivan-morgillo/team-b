package com.ivanmorgillo.corsoandroid.teamb

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.apperol.R
import com.apperol.R.id
import com.apperol.R.string
import com.apperol.databinding.ActivityMainBinding
import com.apperol.databinding.FragmentDrawerNavHeaderBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.DisableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.EnableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFacebook
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFavorite
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFeedBack
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToRandom
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToSearch
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToSettingMenu
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToTwitter
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.SignIn
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

interface CleanSearchField {
    fun cleanSearchField()
}

interface GoogleSignInRequest {
    fun signInWithGoogle()
}

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener, CleanSearchField, GoogleSignInRequest {

    private var searchView: SearchView? = null
    private val mainActivityViewModel: MainActivityViewModel by viewModel()
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var navViewBinding: FragmentDrawerNavHeaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeActions()
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val topLevelDestinations = setOf(
            id.homeFragment,
            id.settingsFragment
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
        return if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            false
        } else {
            if (searchView != null) {
                searchView!!.onActionViewCollapsed()
            }
            NavigationUI.navigateUp(navController, appBarConfiguration)
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
        val searchItem = menu.findItem(id.search_name)
        searchView = searchItem.actionView as SearchView

        searchView!!.queryHint = getString(string.search_cocktail_by_name)
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
                    navController.navigate(id.searchFragment, bundle)
                }
                NavigateToSettingMenu -> {
                    navController.navigate(id.settingsFragment)
                }
                NavigateToFacebook -> openNewTabWindow("https://www.facebook.com", this)
                NavigateToTwitter -> openNewTabWindow("https://twitter.com", this)
                NavigateToFeedBack -> openNewTabWindow(getString(string.feedback_link), this)
                DisableDarkMode -> Unit
                EnableDarkMode -> Unit
                NavigateToFavorite -> navController.navigate(id.favoritesFragment)
                NavigateToRandom -> navController.navigate(id.randomCocktailFragment)
                SignIn -> signInWithGoogle()
                MainScreenAction.SignOut -> signOutFromGoogle()
            }.exhaustive
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            id.nav_customCocktail -> {
                Timber.d("CustomCocktail")
            }
            id.nav_favorites -> {
                mainActivityViewModel.send(MainScreenEvent.OnFavoriteClick)
            }
            id.nav_settings -> {
                mainActivityViewModel.send(MainScreenEvent.OnMenuClick)
            }
            id.nav_facebook -> {
                mainActivityViewModel.send(MainScreenEvent.OnFacebookClick)
            }
            id.nav_twitter -> {
                mainActivityViewModel.send(MainScreenEvent.OnTwitterClick)
            }
            id.nav_feedback -> {
                mainActivityViewModel.send(MainScreenEvent.OnFeedBackClick)
            }
            id.sign_in -> {
                mainActivityViewModel.send(MainScreenEvent.OnSignInClick)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            id.sign_out -> {
                mainActivityViewModel.send(MainScreenEvent.OnSignOutClick)
            }
            id.nav_randomCocktail -> {
                mainActivityViewModel.send(MainScreenEvent.OnRandomClick)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("RestrictedApi")
    var firebaseAthenticationResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->

        val response = IdpResponse.fromResultIntent(result.data)
        val user = FirebaseAuth.getInstance().currentUser

        if (result.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            //
            // Timber.d("GOOGLE USER PIPPO: ${user.providerData}")
            Toast.makeText(applicationContext, "Benvenuto/a ${user.email}", Toast.LENGTH_SHORT)
                .show()
            binding.navView.getHeaderView(0).findViewById<TextView>(id.user_email).text = user.email
            if (user.photoUrl != null) {
                binding.navView.getHeaderView(0).findViewById<ImageView>(id.user_profile_image).load(user.photoUrl)
            }
            if (user != null) {
                // binding.navView.menu.findItem(id.sign_in).setTitle(string.sign_out)
                binding.navView.menu.findItem(id.sign_out).isVisible = true
                binding.navView.menu.findItem(id.sign_in).isVisible = false
            } else {
                // binding.navView.menu.findItem(id.sign_in).setTitle(string.sign_in)
                binding.navView.menu.findItem(id.sign_out).isVisible = false
                binding.navView.menu.findItem(id.sign_in).isVisible = true
            }

            // ...
        } else {
            if (response?.error?.errorCode == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT) {
                // Store relevant anonymous user data
                // Get the non-anoymous credential from the response
                val nonAnonymousCredential = response.credentialForLinking
                // Sign in with credential
                FirebaseAuth.getInstance().signInWithCredential(nonAnonymousCredential)
                    .addOnSuccessListener {
                        Timber.d("PIPPO merge conflict utente: ${it.user.uid}")
                    }
            }
            Timber.e("AUTHENTICATION ERROR: ${response?.error?.errorCode}")
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
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

    override fun signInWithGoogle() {
        // Choose authentication providers
        val providers = arrayListOf(
            GoogleBuilder().build(),
        )
        // Create and launch sign-in intent
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .enableAnonymousUsersAutoUpgrade()
            .build()
        firebaseAthenticationResultLauncher.launch(intent)
    }

    private fun signOutFromGoogle() {
        Timber.d("SIGNOUTFROMGOOGLE")
        Firebase.auth.signOut()
        binding.navView.menu.findItem(id.sign_out).isVisible = false
        binding.navView.menu.findItem(id.sign_in).isVisible = true
        binding.navView.getHeaderView(0).findViewById<TextView>(id.user_email).setText(string.user_email)
        binding.navView.getHeaderView(0).findViewById<ImageView>(id.user_profile_image).load(R.drawable.profile_icon)
        Toast.makeText(applicationContext, "Arrivederci", Toast.LENGTH_SHORT)
            .show()
    }
}
