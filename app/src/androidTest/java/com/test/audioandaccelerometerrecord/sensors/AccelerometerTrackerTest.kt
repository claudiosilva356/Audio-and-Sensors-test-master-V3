package com.test.audioandaccelerometerrecord.sensors

import android.support.test.InstrumentationRegistry
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class AccelerometerTrackerTest {

    lateinit var accelerometerTracker: AccelerometerTracker
    val context = InstrumentationRegistry.getTargetContext()


    @Before
    fun setUp() {
        accelerometerTracker = AccelerometerTracker(context)
    }


    @Test
    fun registerAcceleromenter() {
        assertTrue(accelerometerTracker.registerAcceleromenter())

    }

    @Test
    fun restart() {
        assertTrue(accelerometerTracker.restart())
    }

    @Test
    fun pause(){
        assertTrue(accelerometerTracker.pause())
    }

    @Test
    fun testGetAccelerometerData() {
        assertTrue(accelerometerTracker.deltaX.isFinite())
        assertTrue(accelerometerTracker.deltaY.isFinite())
        assertTrue(accelerometerTracker.deltaZ.isFinite())
    }
}