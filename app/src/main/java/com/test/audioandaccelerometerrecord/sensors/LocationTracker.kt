package com.test.audioandaccelerometerrecord.sensors

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import com.test.audioandaccelerometerrecord.utils.SECOND


class LocationTracker(val context: Context) : LocationCallback() {

    var location : Location? = null
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationRequest: LocationRequest = LocationRequest.create()

    init {
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = SECOND * 20
    }

    @SuppressLint("MissingPermission")
    fun start(){
        fusedLocationClient.requestLocationUpdates(locationRequest, this, null)
    }

    fun stop(){
        fusedLocationClient.removeLocationUpdates(this)
    }

    override fun onLocationResult(locationResult: LocationResult?) {
        locationResult ?: return
        for (location in locationResult.locations){
            this.location = location
        }
    }

}
