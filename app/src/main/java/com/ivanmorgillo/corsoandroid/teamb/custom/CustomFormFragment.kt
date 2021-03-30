package com.ivanmorgillo.corsoandroid.teamb.custom

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apperol.R
import com.apperol.R.string
import com.apperol.databinding.CustomFormBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormAction.NavigateToCustoms
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormEvents.AddIngredient
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormEvents.OnReady
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormEvents.OnSaveClick
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormStates.Content
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormStates.Error
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.custom.NameField.Invalid
import com.ivanmorgillo.corsoandroid.teamb.custom.NameField.Valid
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
        binding.customDrinkGlass.setOnClickListener {
            selectGlass()
        }
        binding.customDrinkAddIngredient.setOnClickListener {
            onAddClick()
        }
        binding.customDrinkUnitMeasure.setOnClickListener {
            selectUM()
        }
        binding.customDrinkSave.setOnClickListener {

            val name = binding.customDrinkName.text.toString()
            val type = binding.customDrinkCategory.text.toString()

            val glass = binding.customDrinkGlass.text.toString()

            val isAlcoholic = binding.customDrinkIsAlcoholic.isChecked

            val ingredientName = binding.customIngredientName.text.toString()
            val ingredientQty = binding.customIngredientQuantity.text.toString()
            // Mandare unità di misura presa dal dialog
            val ingredientUM = binding.customDrinkUnitMeasure.text.toString()
            val instructions = binding.customDrinkInstructions.text.toString()

            viewModel.send(
                OnSaveClick(
                    name,
                    type,
                    isAlcoholic,
                    glass,
                    ingredientName,
                    ingredientQty,
                    ingredientUM,
                    instructions
                )
            )
        }
        viewModel.send(OnReady)
    }

    private fun onAddClick() {
        val ingredientName = binding.customIngredientName.text.toString()
        val ingredientQty = binding.customIngredientQuantity.text.toString()
        val ingredientUM = binding.customDrinkUnitMeasure.text.toString()
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
                ingredientUM,
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
                    if (it.content.ingredients.isNotEmpty()) {
                        val ingredient = it.content.ingredients.last()
                        val textView = TextView(view.context)
                        textView.text = ingredient
                        binding.customDrinkIngredientsList.addView(textView)
                    }
                    binding.customIngredientName.setText(it.content.ingredientName)
                    binding.customIngredientQuantity.setText(it.content.ingredientQty)
                    // INSTRUCTIONS
                }
            }.exhaustive
        }
    }

    private fun selectGlass() {
        val items = arrayOf(
            "Cocktail glass",
            "Highball glass",
            "Old-fashioned glass",
            "Whiskey glass",
            "Pousse cafe glass",
            "Collins glass",
            "Champagne flute",
            "Whiskey sour glass",
            "Cordial glass",
            "Brandy snifter",
            "White wine glass",
            "Nick and Nora glass",
            "Hurricane glass",
            "Coffee mug",
            "Shot glass",
            "Jar",
            "Irish coffee cup",
            "Punch bowl",
            "Pitcher",
            "Pint glass",
            "Copper Mug",
            "Wine Glass",
            "Beer mug",
            "Margarita/Coupette glass",
            "Beer pilsner",
            "Beer glass",
            "Parfait glass",
            "Mason jar",
            "Margarita glass",
            "Martini glass",
            "Balloon glass",
            "Coupe glass"
        )
        var checkedGlass = 0
        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle(getString(string.select_glass))
            .setNeutralButton(getString(string.cancel)) { _, _ -> }
            .setPositiveButton(getString(string.ok)) { _, _ ->
                binding.customDrinkGlassLabel.editText?.setText(items[checkedGlass])
                viewModel.send(CustomFormEvents.OnGlassClicked(items[checkedGlass]))
            }
            .setSingleChoiceItems(items, checkedGlass) { _, which ->
                checkedGlass = which
            }
            .show()
    }

    private fun selectUM() {
        val items = arrayOf("ml", "cl", "oz", "g", "q.b")
        var checkedUM = 0
        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle(getString(string.select_um))
            .setNeutralButton(getString(string.cancel)) { _, _ -> }
            .setPositiveButton(string.ok) { _, _ ->
                binding.customDrinkUnitMeasure.text = items[checkedUM]
            }
            .setSingleChoiceItems(items, checkedUM) { _, which ->
                checkedUM = which
            }
            .show()
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
    val ingredientUM: String,
    val instructions: String,
)

sealed class NameField {
    data class Valid(val value: String) : NameField()
    data class Invalid(val error: String) : NameField()
}
