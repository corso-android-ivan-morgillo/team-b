package com.ivanmorgillo.corsoandroid.teamb

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.apperol.R
import com.apperol.databinding.FragmentCustomDrinkListBinding
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
                customViewModel.send(CustomScreenEvent.OnDeleteClick(it))
            },
            onDrinkClick = { item ->
                customViewModel.send(CustomScreenEvent.OnCustomClick(item))
            })
        binding.customDrinkList.adapter = customAdapter
        observeStates(customAdapter)
        observeActions()
        customViewModel.send(CustomScreenEvent.OnReady)
    }

    private fun observeActions() {
        customViewModel.actions.observe(viewLifecycleOwner, { action ->
            when (action) {
                is CustomScreenAction.NavigateToDetail -> {
                    Timber.d("NavigateToDetail Custom ")
                    /*
                    DA IMPLEMENTARE L'AZIONE NEL NAV_GRAPH
                    val directions = CustomListFragmentDirections
                        .actionCustomListFragmentToCustomFormFragment(action.cocktail.drinkId)
                    findNavController().navigate(directions)
                     */
                }
            }.exhaustive
        })
    }

    private fun observeStates(customAdapter: CustomDrinkAdapter) {
        customViewModel.states.observe(viewLifecycleOwner, { state ->
            when (state) {
                is CustomScreenStates.Content -> customAdapter.setCustomList(state.customDrink)
                is CustomScreenStates.Error -> {
                    when (state.error) {
                        CustomErrorStates.CustomListEmpty -> {
                            binding.customProgressBar.visibility = View.GONE
                            customAdapter.setCustomList(emptyList())
                            Toast.makeText(context, "Nessun Custom Drink trovato", Toast.LENGTH_SHORT).show()
                        }
                        CustomErrorStates.ShowNoInternetMessage -> {
                            binding.customProgressBar.visibility = View.GONE
                            binding.innerLayoutNoInternetSlowInternet.textViewNoInternetError.text = "No Internet"
                        }
                        CustomErrorStates.ShowServerError -> {
                            binding.customProgressBar.visibility = View.GONE
                            binding.innerLayoutServerError.textViewServerError.text = "Server Error"
                        }
                        CustomErrorStates.ShowSlowInternet -> {
                            binding.customProgressBar.visibility = View.GONE
                            binding.innerLayoutNoInternetSlowInternet.textViewNoInternetError.text = "Slow Internet"
                        }
                    }.exhaustive
                }
                CustomScreenStates.Loading -> binding.customProgressBar.visibility = View.VISIBLE
            }.exhaustive
        })
    }
}
