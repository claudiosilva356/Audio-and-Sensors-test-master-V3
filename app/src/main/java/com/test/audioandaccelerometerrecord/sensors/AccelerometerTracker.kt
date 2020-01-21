package com.test.audioandaccelerometerrecord.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class AccelerometerTracker(context: Context) : SensorEventListener {

    var deltaX :Double = 0.0
    var deltaY :Double = 0.0
    var deltaZ :Double = 0.0

    val positiveParse = 0f

    private var accelerometer: Sensor? = null
    var mSensorManager: SensorManager

    init {
        mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun registerAcceleromenter(): Boolean{
        return mSensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun restart(): Boolean{
        if (accelerometer != null) {
           return mSensorManager.registerListener(
                this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        return false
    }

    fun pause(): Boolean{
        if (accelerometer != null) {
            mSensorManager.unregisterListener(this)
            return true
        }
        return false
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("MY_APP", "$sensor - $accuracy")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event != null) {
            deltaX = Math.abs(positiveParse - event.values[0]).toDouble()
            deltaY = Math.abs(positiveParse - event.values[1]).toDouble()
            deltaZ = Math.abs(positiveParse - event.values[2]).toDouble()
        }
    }
}