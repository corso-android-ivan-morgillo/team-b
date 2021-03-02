package com.ivanmorgillo.corsoandroid.teamb.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialElevationScale
import com.ivanmorgillo.corsoandroid.teamb.DrinkAdapter
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_error.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

const val CORNER_RADIUS = 3
const val BAR_MARGIN = 3f

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModel()
    private var lastClickedItem: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        swiperefresh.setOnRefreshListener {
            viewModel.send(HomeScreenEvents.OnRefreshClicked)
        }
        // collega i dati alla UI, per far cio serve adapter
        val drinkAdapter = DrinkAdapter { item, view ->
            lastClickedItem = view
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            viewModel.send(HomeScreenEvents.OnCocktailClick(item))
        }
        buttonError.setOnClickListener {
            viewModel.send(HomeScreenEvents.OnSettingClick)
        }
        // creamo adapter
        // Mettiamo in comunicazione l'adapter con la recycleview
        cocktails_List.adapter = drinkAdapter
        indexBarCustom()

        val categoryAdapter: CategoryAdapter = CategoryAdapter { item: CategoryUI, view: View ->
            viewModel.send(HomeScreenEvents.OnCategoryClick(item))
        }
        category_list.adapter = categoryAdapter

        // Chiede la lista dei cocktail tramite il ViewModel
        /*val cocktailList = viewModel.getCocktails()
        adapter.setCocktailsList(cocktailList)*/
        observeStates(drinkAdapter, categoryAdapter)
        observeActions()
        viewModel.send(HomeScreenEvents.OnReady)
        // viewModel.send(MainScreenEvents.OnMenuClick)
    }

    private fun indexBarCustom() {
        cocktails_List.setIndexBarTransparentValue(0.0f)
        cocktails_List.setIndexBarTextColor("#7f7f7f")
        cocktails_List.setIndexbarMargin(0.0f)
        cocktails_List.setIndexBarCornerRadius(CORNER_RADIUS)
        cocktails_List.setIndexBarStrokeVisibility(false)
        cocktails_List.setIndexbarMargin(BAR_MARGIN)
    }

    private fun observeActions() {
        viewModel.actions.observe(viewLifecycleOwner, { action ->
            Timber.d(action.toString())
            when (action) {
                is HomeScreenActions.NavigateToDetail -> {
                    lastClickedItem?.run {
                        val extras = FragmentNavigatorExtras(this to "cocktail_transition_item")
                        val directions =
                            HomeFragmentDirections.actionHomeFragmentToDetailFragment(action.drinks.id)
                        Log.d("HomeID", " = ${action.drinks.id}")
                        findNavController().navigate(directions, extras)
                    }
                }
                HomeScreenActions.NavigateToSettings
                -> {
                    Timber.d(action.toString())
                    Log.d("NavigateToSettings", "Button Clicked!")
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                }

            }.exhaustive
        })
    }

    // L'activity quando è pronta (dopo aver creato adapter e associato a questa recycler view
    // comunica che è pronta
    // observe prende 2 argomenti:  lifecycle(main activity è una livecycle)
    // e un observable. Questa è una lambda in quanto contiene una sola funzione
    private fun observeStates(adapter: DrinkAdapter, categoryAdapter: CategoryAdapter) {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            Timber.d(state.toString())
            when (state) {
                is HomeScreenStates.Content -> {
                    swiperefresh.isRefreshing = false
                    adapter.setDrinksList(state.generalContent.drinksList)
                    categoryAdapter.setCategoryList(state.generalContent.categoryList)
                    errorVisibilityGone()
                    innerLayout.visibility = View.VISIBLE
                    category_layout.visibility = View.VISIBLE
                }
                is HomeScreenStates.Error -> {
                    when (state.error) {
                        ErrorStates.ShowNoInternetMessage -> {
                            innerLayoutNoInternet_SlowInternet.visibility = View.VISIBLE
                            errorCustom("No Internet Connection")
                            buttonError.visibility = View.VISIBLE
                        }
                        ErrorStates.ShowNoCocktailFound -> {
                            errorCustom("No Cocktail Found")
                            innerLayoutNoCocktailFound.visibility = View.VISIBLE
                        }
                        ErrorStates.ShowServerError -> {
                            errorCustom("Server Error")
                            innerLayoutServerError.visibility = View.VISIBLE
                        }
                        ErrorStates.ShowSlowInternet -> {
                            errorCustom("SlowInternet")
                            innerLayoutNoInternet_SlowInternet.visibility = View.VISIBLE
                        }
                        ErrorStates.ShowNoCategoriesFound -> {
                            errorCustom("No Categories Found")
                            innerLayoutNoCocktailFound.visibility = View.VISIBLE
                        }
                    }
                }
                // quando l'aopp è in loading mostriamo progress bar
                HomeScreenStates.Loading -> {
                    swiperefresh.isRefreshing = true
                }
            }.exhaustive
        })
    }

    private fun errorVisibilityGone() {
        innerLayoutNoInternet_SlowInternet.visibility = View.GONE
        innerLayoutNoCocktailFound.visibility = View.GONE
        innerLayoutServerError.visibility = View.GONE
    }

    private fun errorCustom(errore: String) {
        innerLayout.visibility = View.GONE
        category_layout.visibility = View.GONE
        swiperefresh.isRefreshing = false

        imageViewError.setImageResource(R.drawable.errorimage)
        textViewError.text = errore
    }
}
