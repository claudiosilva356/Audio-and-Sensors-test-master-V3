package com.test.audioandaccelerometerrecord.rest

import com.test.audioandaccelerometerrecord.model.Predict
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

class BaseAPITest {

    lateinit var baseHttp: BaseHttp
    lateinit var baseAPI: BaseAPI
    @Before
    fun setUp() {
        baseHttp = BaseHttp(createTempDir("/tempDir","test", null))
        baseAPI = BaseAPI(baseHttp)
    }

    @Test
    fun createService() {
        val testAPI: TestAPI =  baseAPI.createService(TestAPI::class.java)
        assertNotNull(testAPI)
    }


    interface TestAPI {

        @POST("data")
        @Headers("Content-type: application/json")
        fun postPredict(@Body predit: Predict): Single<String>

    }
}