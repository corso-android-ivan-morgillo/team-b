package com.ivanmorgillo.corsoandroid.teamb

import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // carichiamo layout
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
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
}
