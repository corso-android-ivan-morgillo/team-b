package com.ivanmorgillo.corsoandroid.teamb

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apperol.R
import com.apperol.databinding.CustomFormBinding
import com.ivanmorgillo.corsoandroid.teamb.CustomFormAction.NavigateToCustoms
import com.ivanmorgillo.corsoandroid.teamb.CustomFormEvents.AddIngredient
import com.ivanmorgillo.corsoandroid.teamb.CustomFormStates.Content
import com.ivanmorgillo.corsoandroid.teamb.CustomFormStates.Error
import com.ivanmorgillo.corsoandroid.teamb.CustomFormStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.NameField.Invalid
import com.ivanmorgillo.corsoandroid.teamb.NameField.Valid
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import com.ivanmorgillo.corsoandroid.teamb.utils.gone
import com.ivanmorgillo.corsoandroid.teamb.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class CustomForm : Fragment(R.layout.custom_form) {
    private val viewModel: CustomFormViewModel by viewModel()
    private val binding by viewBinding(CustomFormBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStates(view)

        viewModel.action.observe(viewLifecycleOwner) {
            when (it) {
                NavigateToCustoms -> findNavController().navigate(R.id.customListFragment)
            }.exhaustive
        }
        binding.customDrinkAddIngredient.setOnClickListener {
            onAddClick()
        }
        binding.customDrinkSave.setOnClickListener {

            val name = binding.customDrinkName.text.toString()
            val type = binding.customDrinkCategory.text.toString()

            val glass = binding.customDrinkGlass.text.toString()

            val isAlcoholic = binding.customDrinkIsAlcoholic.isChecked

            val ingredientName = binding.customIngredientName.text.toString()
            val ingredientQty = binding.customIngredientQuantity.text.toString()
            // Mandare unità di misura presa dal dialog
            val instructions = binding.customDrinkInstructions.text.toString()

            viewModel.send(
                CustomFormEvents.OnSaveClick(
                    name,
                    type,
                    isAlcoholic,
                    glass,
                    ingredientName,
                    ingredientQty,
                    instructions
                )
            )
        }
        viewModel.send(CustomFormEvents.OnReady)
    }

    private fun onAddClick() {
        val ingredientName = binding.customIngredientName.text.toString()
        val ingredientQty = binding.customIngredientQuantity.text.toString()
        if (ingredientName.isBlank() && ingredientQty.isBlank()) return
        // Mandare unità di misura presa dal dialog
        val name = binding.customDrinkName.text.toString()
        val type = binding.customDrinkCategory.text.toString()
        val glass = binding.customDrinkGlass.text.toString()
        val isAlcoholic = binding.customDrinkIsAlcoholic.isChecked
        val instructions = binding.customDrinkInstructions.text.toString()
        viewModel.send(
            AddIngredient(
                name,
                type,
                isAlcoholic,
                glass,
                ingredientName,
                ingredientQty,
                instructions
            )
        )
    }

    private fun observeStates(view: View) {
        viewModel.states.observe(viewLifecycleOwner) {
            when (it) {
                is Error -> TODO()
                Loading -> binding.customDrinkProgressBar.visible()
                is Content -> {
                    binding.customDrinkProgressBar.gone()
                    when (it.content.name) {
                        is Invalid -> {
                            binding.customDrinkNameLabel.setError(it.content.name.error)
                        }
                        is Valid -> binding.customDrinkName.setText(it.content.name.value)
                    }.exhaustive

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
    }
}

data class CustomFormUI(
    val name: NameField,
    val type: String,
    val isAlcoholic: Boolean,
    val glass: String,
    val ingredients: List<String>,
    val ingredientName: String,
    val ingredientQty: String,
    val instructions: String

)

sealed class NameField {
    data class Valid(val value: String) : NameField()
    data class Invalid(val error: String) : NameField()
}
