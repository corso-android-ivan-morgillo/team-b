package com.ivanmorgillo.corsoandroid.teamb.settings

import android.app.UiModeManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.databinding.FragmentSettingBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SettingFragment : Fragment(R.layout.fragment_setting) {

    private val viewModel: SettingViewModel by viewModel()
    private val binding by viewBinding(FragmentSettingBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentNightMode = (resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK)

        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.checkOfTheme.isChecked = false
            }
            Configuration.UI_MODE_NIGHT_YES, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                binding.checkOfTheme.isChecked = true
            }
        }
        binding.checkOfTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.send(SettingScreenEvents.OnThemeSwitchClick(isChecked))
        }
        observeActions()

        fun onSwitch() {

            val sharedPreferences: SharedPreferences? = activity?.getSharedPreferences("value", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            if (sharedPreferences != null) {
                binding.checkOfScreenActive.isChecked = sharedPreferences.getBoolean("value", true)
                if (binding.checkOfScreenActive.isChecked) {
                    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            binding.checkOfScreenActive.setOnCheckedChangeListener { _, isChecked ->
                viewModel.send(SettingScreenEvents.OnScreenSwitchClick(isChecked, editor))
            }
        }
        onSwitch()
    }

    private fun observeActions() {
        viewModel.actions.observe(viewLifecycleOwner, { action ->
            Timber.d(action.toString())
            when (action) {
                is SettingScreenActions.ChangeTheme -> {
                    if (action.isChecked) {
                        AppCompatDelegate.setDefaultNightMode(UiModeManager.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(UiModeManager.MODE_NIGHT_NO)
                    }
                }
                is SettingScreenActions.ChangeScreen -> {
                    if (action.isChecked) {
                        action.editor?.putBoolean("value", true)
                        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    } else {
                        action.editor?.putBoolean("value", false)
                        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                    action.editor?.apply()
                }
            }.exhaustive
        })
    }
}
