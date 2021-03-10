package com.ivanmorgillo.corsoandroid.teamb.random

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teamb.R.layout
import com.ivanmorgillo.corsoandroid.teamb.databinding.FragmentRandomCocktailBinding
import com.ivanmorgillo.corsoandroid.teamb.random.RandomCocktailFragmentDirections.Companion.actionRandomCocktailFragmentToDetailFragment
import com.ivanmorgillo.corsoandroid.teamb.random.RandomScreenAction.NavigatetoDetail
import com.ivanmorgillo.corsoandroid.teamb.utils.bindings.viewBinding
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import java.util.Objects
import kotlin.math.sqrt
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ACCELERATION = 10f
private const val INDEX = 0.9f
private const val ACCELERATION_THRESHOLD = 12
private const val COCKTAIL_RANDOM_ID = -1000L

class RandomCocktailFragment : Fragment(layout.fragment_random_cocktail) {
    private val viewModel: RandomCocktailViewModel by viewModel()
    private val binding by viewBinding(FragmentRandomCocktailBinding::bind)
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getActivity()?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!.registerListener(
            sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        acceleration = ACCELERATION
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * INDEX + delta
            if (acceleration > ACCELERATION_THRESHOLD) {
                viewModel.send(RandomScreenEvents.OnShaking)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        sensorManager?.registerListener(
            sensorListener, sensorManager!!.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout.fragment_random_cocktail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeActions()
    }

    private fun observeActions() {
        viewModel.action.observe(viewLifecycleOwner, { action ->
            when (action) {
                NavigatetoDetail -> {
                    val directions =
                        actionRandomCocktailFragmentToDetailFragment(COCKTAIL_RANDOM_ID)
                    findNavController().navigate(directions)
                }
            }.exhaustive
        })
    }
}
