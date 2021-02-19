package com.ivanmorgillo.corsoandroid.teamb.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ivanmorgillo.corsoandroid.teamb.ErrorStates
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.exhaustive
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.layout_error.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val COCKTAILIDDEFAULT = -666L

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

        val adapter = DetailScreenAdapter()
        // Mettiamo in comunicazione l'adapter con la recycleview
        detail_screen_recycleview.adapter = adapter

        val cocktailId = args.cocktailId
        if (cocktailId == COCKTAILIDDEFAULT) {
            // Torna indietro nella schermata da cui provieni.
            findNavController().popBackStack()
        } else {
            Timber.d("CocktailId= $cocktailId")
            viewModel.send(DetailScreenEvents.OnReady(cocktailId))
        }
        observeStates(adapter)
        viewModel.send(DetailScreenEvents.OnReady(cocktailId))
    }

    private fun observeStates(adapter: DetailScreenAdapter) {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            Timber.d(state.toString())
            when (state) {
                is DetailScreenStates.Content -> {
                    adapter.items = state.details
                }
                is DetailScreenStates.Error -> {
                    when (state.error) {
                        ErrorStates.ShowNoInternetMessage -> {
                            errorCustom("No Internet Connection")
                        }
                        ErrorStates.ShowNoCocktailFound -> {
                            errorCustom("No Cocktail Found")
                        }
                        ErrorStates.ShowServerError -> {
                            errorCustom("Server Error")
                        }
                        ErrorStates.ShowSlowInternet -> {
                            errorCustom("SlowInternet")
                        }
                        ErrorStates.ShowNoDetailFound -> {
                            errorCustom("No Detail Found")
                        }
                    }
                }
                // quando l'aopp è in loading mostriamo progress bar
                DetailScreenStates.Loading -> {
                    Timber.d("STATE LOADING")
                }
            }.exhaustive
        })
    }

    private fun errorCustom(errore: String) {
        imageViewError.setImageResource(R.drawable.errorimage)
        textViewError.text = errore
    }
}
