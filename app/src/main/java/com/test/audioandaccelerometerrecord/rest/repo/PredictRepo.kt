package com.test.audioandaccelerometerrecord.rest.repo

import com.test.audioandaccelerometerrecord.model.Predict
import com.test.audioandaccelerometerrecord.rest.BaseAPI
import com.test.audioandaccelerometerrecord.rest.api.PredictAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

class PredictRepo(val callback: Callback) : KoinComponent{

    val baseAPI : BaseAPI by inject()

    interface Callback {
        fun onSuccess()
        fun onError(message: String)
    }

    fun postPredict(predict: Predict, serverUrl: String ) {
        var exchangeRateAPI: PredictAPI = baseAPI.createService(PredictAPI::class.java, serverUrl)
        val postLogin = exchangeRateAPI.postPredict(predict)
        postLogin.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ callback.onSuccess() },
                { ex -> callback.onError(BaseAPI.getErrorMessage(ex)) })
    }
}
