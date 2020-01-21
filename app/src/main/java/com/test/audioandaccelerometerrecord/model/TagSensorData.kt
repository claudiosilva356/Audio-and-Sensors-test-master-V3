package com.test.audioandaccelerometerrecord.model

data class TagSensorData(
    val batteryLevel: Value,
    val temperature: Value,
    val pressure : Value,
    val humidity : Value,
    val power : Value)