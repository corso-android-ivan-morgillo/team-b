package com.ivanmorgillo.corsoandroid.teamb

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.apperol.R
import com.apperol.databinding.CustomFormBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CustomForm : Fragment(R.layout.custom_form) {
    private val viewModel: CustomFormViewModel by viewModel()
    private val binding by viewBinding(CustomFormBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputText = binding.customDrinkName.editText?.text.toString()


    }
}
