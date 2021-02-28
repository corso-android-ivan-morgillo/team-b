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
import com.ivanmorgillo.corsoandroid.teamb.CocktailAdapter
import com.ivanmorgillo.corsoandroid.teamb.ErrorStates
import com.ivanmorgillo.corsoandroid.teamb.MainScreenActions
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvents
import com.ivanmorgillo.corsoandroid.teamb.MainScreenStates
import com.ivanmorgillo.corsoandroid.teamb.MainViewModel
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.exhaustive
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_error.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

const val CORNER_RADIUS = 3
const val BAR_MARGIN = 3f

class HomeFragment : Fragment() {
    private val viewModel: MainViewModel by viewModel()
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
            viewModel.send(MainScreenEvents.OnRefreshClicked)
        }

        val categoryAdapter: CategoryAdapter = CategoryAdapter()
        // collega i dati alla UI, per far cio serve adapter
        val adapter = CocktailAdapter { item, view ->
            lastClickedItem = view
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            viewModel.send(MainScreenEvents.OnCocktailClick(item))
        }
        buttonError.setOnClickListener {
            viewModel.send(MainScreenEvents.OnSettingClick)
        }
        // creamo adapter
        // Mettiamo in comunicazione l'adapter con la recycleview
        cocktails_List.adapter = adapter
        category_list.adapter = categoryAdapter

        val image = "https://i.pinimg.com/originals/99/5b/30/995b30f3bee71a43297f2dc731ba86c1.png"
        val categoryList: List<Category> = listOf(
            Category("Categoria1", image),
            Category("categoria2", image),
            Category("categoria3", image),
            Category("categoria4", image),
            Category("categoria5", image),
            Category("categoria6", image),
            Category("categoria7", image),
            Category("categoria8", image),
            Category("categoria9", image),
            Category("categoria10", image),
        )
        categoryAdapter.setCategoryList(categoryList)

        indexBarCustom()

        // Chiede la lista dei cocktail tramite il ViewModel
        /*val cocktailList = viewModel.getCocktails()
        adapter.setCocktailsList(cocktailList)*/
        observeStates(adapter)
        observeActions()
        viewModel.send(MainScreenEvents.OnReady)
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
                is MainScreenActions.NavigateToDetail -> {
                    lastClickedItem?.run {
                        val extras = FragmentNavigatorExtras(this to "cocktail_transition_item")
                        val directions =
                            HomeFragmentDirections.actionHomeFragmentToDetailFragment(action.cocktail.id)
                        Log.d("HomeID", " = ${action.cocktail.id}")
                        findNavController().navigate(directions, extras)
                    }
                }
                MainScreenActions.NavigateToSettings
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
    private fun observeStates(adapter: CocktailAdapter) {
        viewModel.states.observe(viewLifecycleOwner, { state ->
            Timber.d(state.toString())
            when (state) {
                is MainScreenStates.Content -> {
                    swiperefresh.isRefreshing = false
                    adapter.setCocktailsList(state.cocktails)
                    errorVisibilityGone()
                }
                is MainScreenStates.Error -> {
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
                    }
                }
                // quando l'aopp è in loading mostriamo progress bar
                MainScreenStates.Loading -> {
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

        swiperefresh.isRefreshing = false

        imageViewError.setImageResource(R.drawable.errorimage)
        textViewError.text = errore
    }
}
