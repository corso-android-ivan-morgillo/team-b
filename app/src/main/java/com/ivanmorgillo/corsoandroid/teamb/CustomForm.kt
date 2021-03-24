package com.ivanmorgillo.corsoandroid.teamb

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.apperol.R
import com.apperol.databinding.CustomFormBinding
import com.ivanmorgillo.corsoandroid.teamb.CustomFormStates.Error
import com.ivanmorgillo.corsoandroid.teamb.CustomFormStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.CustomFormStates.Success
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import org.koin.androidx.viewmodel.ext.android.viewModel

class CustomForm : Fragment(R.layout.custom_form) {
    private val viewModel: CustomFormViewModel by viewModel()
    private val binding by viewBinding(CustomFormBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.states.observe(viewLifecycleOwner) {
            when (it) {
                is Error -> TODO()
                Loading -> TODO()
                is Success -> {
                    binding.customDrinkName.setText(it.content.name)
                    binding.customDrinkCategory.setText(it.content.type)
                    binding.customDrinkGlass.setText(it.content.glass)
                    binding.customDrinkIsAlcoholic.isChecked = it.content.isAlcoholic
                    it.content.ingredients.forEach {
                        val textView = TextView(view.context)
                        textView.text = it
                        binding.customDrinkIngredientsList.addView(textView)
                    }
                    binding.customIngredientName.setText(it.content.ingredientName)
                    binding.customIngredientQuantity.setText(it.content.ingredientQty)
                    // INSTRUCTIONS
                }
            }.exhaustive
        }
        binding.customDrinkAddIngredient.setOnClickListener {
            val ingredientName = binding.customIngredientName.text.toString()
            val ingredientQty = binding.customIngredientQuantity.text.toString()
            // Mandare unità di misura presa dal dialog
        }
        binding.customDrinkSave.setOnClickListener { }
        viewModel.send(CustomFormEvents.OnReady)
    }
}

data class CustomFormUI(
    val name: String,
    val type: String,
    val isAlcoholic: Boolean,
    val glass: String,
    val ingredients: List<String>,
    val ingredientName: String,
    val ingredientQty: String,
    val instructions: String

)
