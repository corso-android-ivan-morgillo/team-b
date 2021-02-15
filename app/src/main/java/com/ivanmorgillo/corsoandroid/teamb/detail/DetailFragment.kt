package com.ivanmorgillo.corsoandroid.teamb.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.ivanmorgillo.corsoandroid.teamb.DetailCocktailAdapter
import com.ivanmorgillo.corsoandroid.teamb.Ingredient
import com.ivanmorgillo.corsoandroid.teamb.R
import kotlinx.android.synthetic.main.fragment_detail.*
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

        val ingredientList1 = listOf<Ingredient>(
            Ingredient("Ingr1", "Qty1"),
            Ingredient("Ingr2", "Qty2"),
            Ingredient("Ingr3", "Qty3"),
            Ingredient("Ingr4", "Qty4"),
            Ingredient("Ingr5", "Qty5"),
            Ingredient("Ingr6", "Qty6"),
        )

        val adapter = DetailCocktailAdapter()
        // Mettiamo in comunicazione l'adapter con la recycleview
        ingredientsList.adapter = adapter

        adapter.setCocktailsList(ingredientList1)
        val title = view.findViewById<TextView>(R.id.NomeCocktail)
        val image = view.findViewById<ImageView>(R.id.immagineCocktail)
        val alcoholic = view.findViewById<TextView>(R.id.Alcoholic)
        val glass = view.findViewById<TextView>(R.id.Glass)
        val instructions = view.findViewById<TextView>(R.id.instructions)
        title.setText("Margarita Cocktail")
        image.load("https://www.thecocktaildb.com/images/media/drink/5noda61589575158.jpg")
        alcoholic.text = "si"
        glass.text = "Flute"
        instructions.text = "Queste sono le istruzioni di preparazione del cocktail Margarita"

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
