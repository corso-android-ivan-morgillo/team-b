package com.ivanmorgillo.corsoandroid.teamb

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
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
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.CancelClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.DisableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.EnableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFavorite
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFeedBack
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToHome
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToInstagram
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToRandom
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToSearch
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToSettingMenu
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToTwitter
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.SignIn
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.SignOut
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

interface CleanSearchField {
    fun cleanSearchField()
}

interface GoogleSignInRequest {
    fun signInWithGoogle()
}

@Suppress("TooManyFunctions")
class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener, CleanSearchField, GoogleSignInRequest {

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
        val user = FirebaseAuth.getInstance().currentUser
        userControl(user)
        // Google ads Sdk Initialization

        setupAds()
    }

    // Determine the screen width (less decorations) to use for the ad width.
    // If the ad hasn't been laid out, default to the full screen width.
    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.adViewContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    private fun setupAds() {
        MobileAds.initialize(this@MainActivity) {}
        val adView = AdView(this)
        binding.adViewContainer.addView(adView)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() = Unit

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Timber.e(Throwable("Cannot Load Ad: ${adError.code}"))
            }

            override fun onAdOpened() = Unit

            override fun onAdClicked() = Unit

            override fun onAdLeftApplication() = Unit

            override fun onAdClosed() = Unit
        }
        adView.adUnitId = "ca-app-pub-4984877505456457/4180942455"

        adView.adSize = adSize

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        val requestConfiguration = RequestConfiguration
            .Builder()
            .setTestDeviceIds(listOf("1DBF4DCCF4D941F406A3311829733E08"))
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        val adRequest = AdRequest.Builder().build()
        // Start loading the ad in the background.
        adView.loadAd(adRequest)
    }

    override fun onSupportNavigateUp(): Boolean {
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
                NavigateToInstagram -> openNewTabWindow("https://www.instagram.com/apperolteam/", this)
                NavigateToTwitter -> openNewTabWindow("https://twitter.com/Apperol2", this)
                NavigateToFeedBack -> openNewTabWindow(getString(string.feedback_link), this)
                DisableDarkMode -> Unit
                EnableDarkMode -> Unit
                NavigateToFavorite -> navController.navigate(id.favoritesFragment)
                NavigateToRandom -> navController.navigate(id.randomCocktailFragment)
                SignIn -> signInWithGoogle()
                SignOut -> signOutFromGoogle()
                is CancelClick -> action.dialog.cancel()
                NavigateToHome -> navController.navigate(id.homeFragment)
            }.exhaustive
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            id.nav_customCocktail -> {
                Timber.d("CustomCocktail")
                Toast.makeText(this, getString(string.work_in_progress), Toast.LENGTH_LONG).show()
            }
            id.nav_favorites -> {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(getString(string.Sign_in))
                        .setMessage(getString(string.popup_message))
                        .setPositiveButton(resources.getString(string.Sign_in)) { dialog, which ->
                            // Respond to positive button press
                            mainActivityViewModel.send(MainScreenEvent.OnSignInClick)
                        }
                        .setNegativeButton(getString(string.annulla)) { dialogInterface: DialogInterface, i: Int ->
                            mainActivityViewModel.send(MainScreenEvent.OnCancelClick(dialogInterface))
                        }
                        .show()
                } else {
                    mainActivityViewModel.send(MainScreenEvent.OnFavoriteClick)
                }
            }
            id.nav_settings -> {
                mainActivityViewModel.send(MainScreenEvent.OnMenuClick)
            }
            id.nav_instagram -> {
                mainActivityViewModel.send(MainScreenEvent.OnInstagramClick)
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
            // Timber.d("GOOGLE USER PIPPO: ${user.providerData}")
            val welcomeString = getString(string.welcome)
            Toast.makeText(applicationContext, "$welcomeString ${user.displayName}", Toast.LENGTH_SHORT)
                .show()
            userControl(user)
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
            // response.getError().getErrorCode() and handle the error
        }
    }

    private fun userControl(user: FirebaseUser?) {
        if (user?.displayName == null && user?.email != null) {
            binding.navView.getHeaderView(0).findViewById<TextView>(id.user_email).text = user.email
        }
        if (user?.displayName != null) {
            binding.navView.getHeaderView(0).findViewById<TextView>(id.user_email).text = user.displayName
        }
        if (user?.photoUrl != null) {
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
        binding.navView.menu.findItem(id.sign_in).isVisible = false
        binding.navView.menu.findItem(id.sign_out).isVisible = true
    }

    private fun signOutFromGoogle() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Toast.makeText(this, getString(string.logout_effettuato), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(string.logout_fallito), Toast.LENGTH_SHORT).show()
            }
        Credentials.getClient(this).disableAutoSignIn()
        binding.navView.menu.findItem(id.sign_out).isVisible = false
        binding.navView.menu.findItem(id.sign_in).isVisible = true
        binding.navView.getHeaderView(0).findViewById<TextView>(id.user_email).setText(string.user_email)
        binding.navView.getHeaderView(0).findViewById<ImageView>(id.user_profile_image).load(R.drawable.profile_icon)
        Toast.makeText(applicationContext, string.goodbye, Toast.LENGTH_SHORT)
            .show()
        mainActivityViewModel.send(MainScreenEvent.AfterSignOut)
    }
}
