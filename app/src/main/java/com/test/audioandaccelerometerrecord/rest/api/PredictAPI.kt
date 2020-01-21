package com.test.audioandaccelerometerrecord.rest.api

import com.test.audioandaccelerometerrecord.model.Predict
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PredictAPI {

    @POST("data")
    @Headers("Content-type: application/json")
    fun postPredict(@Body predict: Predict): Completable

}