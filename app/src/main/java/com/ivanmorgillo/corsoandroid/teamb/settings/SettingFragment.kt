package com.ivanmorgillo.corsoandroid.teamb.settings

import android.app.UiModeManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.android.synthetic.main.fragment_setting.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SettingFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchMaterialTheme = check_ofTheme.findViewById<SwitchMaterial>(R.id.check_ofTheme)
        val switchMaterialScreen = check_ofScreenActive.findViewById<SwitchMaterial>(R.id.check_ofScreenActive)
        val currentNightMode = (resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK)

        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                switchMaterialTheme.isChecked = false
            }
            Configuration.UI_MODE_NIGHT_YES, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                switchMaterialTheme.isChecked = true
            }
        }
        switchMaterialTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.send(SettingScreenEvents.OnThemeSwitchClick(isChecked))
        }
        observeActions()

        fun onSwitch() {

            val sharedPreferences: SharedPreferences? = activity?.getSharedPreferences("value", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            if (sharedPreferences != null) {
                switchMaterialScreen.isChecked = sharedPreferences.getBoolean("value", true)
                if (switchMaterialScreen.isChecked) {
                    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            switchMaterialScreen.setOnCheckedChangeListener { _, isChecked ->
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
