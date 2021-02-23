package com.ivanmorgillo.corsoandroid.teamb

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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
            else -> super.onOptionsItemSelected(item)
        }
    }
}
