package com.ivanmorgillo.corsoandroid.teamb.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialElevationScale
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.databinding.FragmentHomeBinding
import com.ivanmorgillo.corsoandroid.teamb.home.HomeFragmentDirections.Companion.actionHomeFragmentToDetailFragment
import com.ivanmorgillo.corsoandroid.teamb.home.HomeScreenActions.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamb.home.HomeScreenActions.NavigateToSettings
import com.ivanmorgillo.corsoandroid.teamb.home.HomeScreenStates.Content
import com.ivanmorgillo.corsoandroid.teamb.home.HomeScreenStates.Error
import com.ivanmorgillo.corsoandroid.teamb.home.HomeScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import com.ivanmorgillo.corsoandroid.teamb.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

const val CORNER_RADIUS = 3
const val BAR_MARGIN = 3f

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModel()
    private val binding by viewBinding(FragmentHomeBinding::bind)

    private var lastClickedItem: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.send(HomeScreenEvents.OnRefreshClicked)
        }
        val drinkAdapter = DrinkAdapter { item, v ->
            lastClickedItem = v
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            viewModel.send(HomeScreenEvents.OnCocktailClick(item))
        }
        binding.innerLayoutNoInternetSlowInternet.buttonNoInternetError.setOnClickListener {
            viewModel.send(HomeScreenEvents.OnSettingClick)
        }

        binding.cocktailsList.adapter = drinkAdapter
        indexBarCustom(binding)

        val categoryAdapter = CategoryAdapter { item: CategoryUI, _: View ->
            viewModel.send(HomeScreenEvents.OnCategoryClick(item))
        }
        binding.categoryList.adapter = categoryAdapter

        observeStates(drinkAdapter, categoryAdapter, binding)
        observeActions()
        viewModel.send(HomeScreenEvents.OnReady)
    }

    private fun indexBarCustom(indexBarBinding: FragmentHomeBinding) {
        indexBarBinding.cocktailsList.setIndexBarTransparentValue(0.0f)
        indexBarBinding.cocktailsList.setIndexBarTextColor("#7f7f7f")
        indexBarBinding.cocktailsList.setIndexbarMargin(0.0f)
        indexBarBinding.cocktailsList.setIndexBarCornerRadius(CORNER_RADIUS)
        indexBarBinding.cocktailsList.setIndexBarStrokeVisibility(false)
        indexBarBinding.cocktailsList.setIndexbarMargin(BAR_MARGIN)
    }

    private fun observeActions() {
        viewModel.actions.observe(viewLifecycleOwner, { action ->
            Timber.d(action.toString())
            when (action) {
                is NavigateToDetail -> {
                    lastClickedItem?.run {
                        val extras = FragmentNavigatorExtras(this to "cocktail_transition_item")
                        val directions = actionHomeFragmentToDetailFragment(action.drinks.id)
                        findNavController().navigate(directions, extras)
                    }
                }
                NavigateToSettings -> {
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                }
            }.exhaustive
        })
    }

    private fun observeStates(
        adapter: DrinkAdapter,
        categoryAdapter: CategoryAdapter,
        fragmentHomeBinding: FragmentHomeBinding
    ) {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            Timber.d(state.toString())
            when (state) {
                is Content -> setupContent(fragmentHomeBinding, adapter, state, categoryAdapter)
                is Error -> setupError(state, fragmentHomeBinding)
                Loading -> fragmentHomeBinding.swiperefresh.isRefreshing = true
            }.exhaustive
        })
    }

    private fun setupError(state: Error, fragmentHomeBinding: FragmentHomeBinding) {
        when (state.error) {
            ErrorStates.ShowNoInternetMessage -> {
                fragmentHomeBinding.innerLayoutNoInternetSlowInternet.root.visible()
                errorCustom("No Internet Connection", fragmentHomeBinding)
                fragmentHomeBinding.innerLayoutNoInternetSlowInternet.buttonNoInternetError.visible()
            }
            ErrorStates.ShowNoCocktailFound -> {
                errorCustom("No Cocktail Found", fragmentHomeBinding)
                fragmentHomeBinding.innerLayoutNoCocktailFound.root.visible()
            }
            ErrorStates.ShowServerError -> {
                errorCustom("Server Error", fragmentHomeBinding)
                fragmentHomeBinding.innerLayoutServerError.root.visible()
            }
            ErrorStates.ShowSlowInternet -> {
                errorCustom("SlowInternet", fragmentHomeBinding)
                fragmentHomeBinding.innerLayoutNoInternetSlowInternet.root.visible()
            }
            ErrorStates.ShowNoCategoriesFound -> {
                errorCustom("No Categories Found", fragmentHomeBinding)
                fragmentHomeBinding.innerLayoutNoCategoriesFound.root.visible()
            }
        }
    }

    private fun setupContent(
        fragmentHomeBinding: FragmentHomeBinding,
        adapter: DrinkAdapter,
        state: Content,
        categoryAdapter: CategoryAdapter
    ) {
        fragmentHomeBinding.swiperefresh.isRefreshing = false
        adapter.setDrinksList(state.generalContent.drinksList)
        categoryAdapter.setCategoryList(state.generalContent.categoryList)
        errorVisibilityGone(fragmentHomeBinding)
        fragmentHomeBinding.innerLayout.visibility = View.VISIBLE
        fragmentHomeBinding.categoryLayout.visibility = View.VISIBLE
    }

    private fun errorVisibilityGone(errorBinding: FragmentHomeBinding) {
        errorBinding.innerLayoutNoInternetSlowInternet.root.visibility = View.GONE
        errorBinding.innerLayoutNoCocktailFound.root.visibility = View.GONE
        errorBinding.innerLayoutServerError.root.visibility = View.GONE
        errorBinding.innerLayoutNoCategoriesFound.root.visibility = View.GONE
    }

    private fun errorCustom(errore: String, binding: FragmentHomeBinding) {
        binding.innerLayout.visibility = View.GONE
        binding.categoryLayout.visibility = View.GONE
        binding.swiperefresh.isRefreshing = false

        binding.innerLayoutNoInternetSlowInternet.textViewNoInternetError.text = errore
    }
}
