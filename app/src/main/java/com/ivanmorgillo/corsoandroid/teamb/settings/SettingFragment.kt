package com.ivanmorgillo.corsoandroid.teamb.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.apperol.R
import com.apperol.databinding.FragmentSettingBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.disableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.utils.enableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import org.koin.androidx.viewmodel.ext.android.viewModel

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
        observeStates()

        binding.checkOfScreenActive.setOnCheckedChangeListener { _, isChecked ->
            viewModel.send(SettingScreenEvents.OnScreenSwitchClick(isChecked))
        }
        viewModel.send(SettingScreenEvents.OnReady)
    }

    private fun observeStates() {
        viewModel.states.observe(viewLifecycleOwner, {
            when (it) {
                is SettingScreenStates.Content -> {
                    binding.settingsProgressBar.visibility = View.GONE
                    setupTheme(it)
                    setupScreen(it)
                }
                SettingScreenStates.Loading -> binding.settingsProgressBar.visibility = View.VISIBLE
            }.exhaustive
        })
    }

    private fun setupScreen(it: SettingScreenStates.Content): Unit? {
        binding.checkOfScreenActive.isChecked = it.screenActiveOn
        return if (it.screenActiveOn) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun setupTheme(it: SettingScreenStates.Content) {
        if (it.changeThemeOn) {
            enableDarkMode()
        } else {
            disableDarkMode()
        }
        binding.checkOfTheme.isChecked = it.changeThemeOn
    }
}
