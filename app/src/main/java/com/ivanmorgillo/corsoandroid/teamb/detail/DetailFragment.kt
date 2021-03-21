package com.ivanmorgillo.corsoandroid.teamb.detail

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.annotation.AttrRes
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.apperol.R
import com.apperol.R.string
import com.apperol.databinding.FragmentDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import com.ivanmorgillo.corsoandroid.teamb.GoogleSignInRequest
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowNoDetailFound
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowNoLoggedUserError
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowServerError
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowSlowInternet
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenActions.CancelClick
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenActions.NavigateToSetting
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenActions.SignIn
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenEvents.OnCancelClick
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import com.ivanmorgillo.corsoandroid.teamb.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val COCKTAILIDDEFAULT = -666L
private const val RANDOMCOCKTAIL = -1000L

@Suppress("IMPLICIT_CAST_TO_ANY")
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DetailScreenAdapter {
            viewModel.send(DetailScreenEvents.OnFavoriteClick)
        }
        binding.detailScreenRecycleview.adapter = adapter

        val cocktailId = args.cocktailId
        if (cocktailId == COCKTAILIDDEFAULT) {
            findNavController().popBackStack()
        } else if (cocktailId == RANDOMCOCKTAIL) {
            viewModel.send(DetailScreenEvents.LoadRandomDrink)
        } else {
            Timber.d("CocktailId= $cocktailId")
            viewModel.send(DetailScreenEvents.LoadDrink(cocktailId))
        }
        observeStates(adapter)
        observeActions()
        binding.innerLayoutNoInternetSlowInternet.buttonNoInternetError.setOnClickListener {
            viewModel.send(DetailScreenEvents.OnSettingClick)
        }
    }

    private fun observeActions() {
        viewModel.actions.observe(viewLifecycleOwner, { actions ->
            when (actions) {
                NavigateToSetting -> startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                SignIn -> {
                    (activity as GoogleSignInRequest).signInWithGoogle()
                }
                is CancelClick -> actions.dialog.cancel()
            }.exhaustive
        })
    }

    private fun observeStates(adapter: DetailScreenAdapter) {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            Timber.d(state.toString())
            when (state) {
                is DetailScreenStates.Content -> {
                    adapter.items = state.details
                    binding.innerLayoutNoDetailFound.root.visibility = View.GONE
                    binding.innerLayoutServerError.root.visibility = View.GONE
                    binding.innerLayoutNoInternetSlowInternet.root.visibility = View.GONE
                    binding.detailScreenRecycleview.visibility = View.VISIBLE
                }
                is DetailScreenStates.Error -> {
                    when (state.error) {
                        ShowNoInternetMessage -> {
                            errorCustom("No Internet Connection")
                            binding.innerLayoutNoInternetSlowInternet.root.visibility = View.VISIBLE
                            binding.innerLayoutNoInternetSlowInternet.buttonNoInternetError.visible()
                        }
                        ShowServerError -> {
                            errorCustom("Server Error")
                            binding.innerLayoutServerError.root.visibility = View.VISIBLE
                        }
                        ShowSlowInternet -> {
                            errorCustom("SlowInternet")
                            binding.innerLayoutNoInternetSlowInternet.root.visibility = View.VISIBLE
                        }
                        ShowNoDetailFound -> {
                            errorCustom("No Detail Found")
                            binding.innerLayoutNoDetailFound.root.visibility = View.VISIBLE
                        }
                        ShowNoLoggedUserError -> MaterialAlertDialogBuilder(requireContext())
                            .setTitle(getString(string.Sign_in))
                            .setMessage(getString(string.popup_message))
                            .setPositiveButton(resources.getString(string.Sign_in)) { dialog, which ->
                                // Respond to positive button press
                                viewModel.send(DetailScreenEvents.OnSignInClick)
                            }
                            .setNegativeButton(getString(string.annulla)) { dialogInterface: DialogInterface, i: Int ->
                                viewModel.send(OnCancelClick(dialogInterface))
                            }
                            .show()
                    }
                }
                DetailScreenStates.Loading -> {
                    Timber.d("STATE LOADING")
                }
            }.exhaustive
        })
    }

    private fun errorCustom(errore: String) {
        binding.innerLayoutNoInternetSlowInternet.textViewNoInternetError.text = errore
        binding.detailScreenRecycleview.visibility = View.GONE
    }

    fun Context.themeColor(
        @AttrRes themeAttrId: Int,
    ): Int {
        return obtainStyledAttributes(
            intArrayOf(themeAttrId)
        ).use {
            it.getColor(0, Color.MAGENTA)
        }
    }
}
