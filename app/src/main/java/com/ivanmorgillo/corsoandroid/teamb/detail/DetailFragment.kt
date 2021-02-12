package com.ivanmorgillo.corsoandroid.teamb.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ivanmorgillo.corsoandroid.teamb.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DetailFragment : Fragment() {
    private val viewModel: DetailViewModel by viewModel()
    private val args: DetailFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    // Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cocktailId = args.cocktailId
        if (cocktailId == 0L) {
            // Torna indietro nella schermata da cui provieni.
            findNavController().popBackStack()
        } else {
            Timber.d("CocktailId= $cocktailId")
        }
    }
}

class DetailViewModel : ViewModel()
