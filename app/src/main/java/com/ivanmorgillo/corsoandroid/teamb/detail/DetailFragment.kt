package com.ivanmorgillo.corsoandroid.teamb.detail

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.AttrRes
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.databinding.FragmentDetailBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.android.synthetic.main.layout_error.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val COCKTAILIDDEFAULT = -666L

class DetailFragment : Fragment(R.layout.fragment_detail) {
    private val viewModel: DetailViewModel by viewModel()
    private val args: DetailFragmentArgs by navArgs()
    private val binding by viewBinding(FragmentDetailBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    // Equivalente alla onCreate di un activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DetailScreenAdapter()
        // Mettiamo in comunicazione l'adapter con la recycleview
        binding.detailScreenRecycleview.adapter = adapter

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
                        DetailErrorStates.ShowNoInternetMessage -> {
                            errorCustom("No Internet Connection")
                        }
                        DetailErrorStates.ShowNoCocktailFound -> {
                            errorCustom("No Cocktail Found")
                        }
                        DetailErrorStates.ShowServerError -> {
                            errorCustom("Server Error")
                        }
                        DetailErrorStates.ShowSlowInternet -> {
                            errorCustom("SlowInternet")
                        }
                        DetailErrorStates.ShowNoDetailFound -> {
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
        binding.detailCard.imageViewError.setImageResource(R.drawable.errorimage)
        binding.detailCard.textViewError.text = errore
    }

    fun Context.themeColor(
        @AttrRes themeAttrId: Int
    ): Int {
        return obtainStyledAttributes(
            intArrayOf(themeAttrId)
        ).use {
            it.getColor(0, Color.MAGENTA)
        }
    }
}
