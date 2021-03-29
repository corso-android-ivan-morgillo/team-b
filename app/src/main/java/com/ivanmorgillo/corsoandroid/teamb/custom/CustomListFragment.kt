package com.ivanmorgillo.corsoandroid.teamb.custom

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apperol.R
import com.apperol.databinding.FragmentCustomDrinkListBinding
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomErrorStates.CustomListEmpty
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomErrorStates.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomErrorStates.ShowServerError
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomErrorStates.ShowSlowInternet
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenEvent.OnCustomClick
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenEvent.OnDeleteClick
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenEvent.OnReady
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenStates.Content
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenStates.Error
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class CustomListFragment : Fragment(R.layout.fragment_custom_drink_list) {
    private val binding by viewBinding(FragmentCustomDrinkListBinding::bind)
    private val customViewModel: CustomListViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val customAdapter = CustomDrinkAdapter(
            onDeleteClick = {
                customViewModel.send(OnDeleteClick(it))
            },
            onDrinkClick = { item ->
                customViewModel.send(OnCustomClick(item))
            })
        binding.customDrinkList.adapter = customAdapter
        observeStates(customAdapter)
        observeActions()
        customViewModel.send(OnReady)
    }

    private fun observeActions() {
        customViewModel.actions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is NavigateToDetail -> {
                    Timber.d("NavigateToDetailCustom ${action.cocktail.drinkId.toString()} ")
                    val directions = CustomListFragmentDirections
                        .actionCustomDrinkFragmentToDetailFragment(action.cocktail.drinkId, true)
                    findNavController().navigate(directions)
                }
            }.exhaustive
        })
    }

    private fun observeStates(customAdapter: CustomDrinkAdapter) {
        customViewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is Content -> customAdapter.setCustomList(state.customDrink)
                is Error -> {
                    when (state.error) {
                        CustomListEmpty -> {
                            binding.customProgressBar.visibility = View.GONE
                            customAdapter.setCustomList(emptyList())
                            Toast.makeText(context, "Nessun Custom Drink trovato", Toast.LENGTH_SHORT).show()
                        }
                        ShowNoInternetMessage -> {
                            binding.customProgressBar.visibility = View.GONE
                            binding.innerLayoutNoInternetSlowInternet.textViewNoInternetError.text = "No Internet"
                        }
                        ShowServerError -> {
                            binding.customProgressBar.visibility = View.GONE
                            binding.innerLayoutServerError.textViewServerError.text = "Server Error"
                        }
                        ShowSlowInternet -> {
                            binding.customProgressBar.visibility = View.GONE
                            binding.innerLayoutNoInternetSlowInternet.textViewNoInternetError.text = "Slow Internet"
                        }
                    }.exhaustive
                }
                Loading -> binding.customProgressBar.visibility = View.VISIBLE
            }.exhaustive
        })
    }
}
