package com.test.audioandaccelerometerrecord.rest
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory



class BaseAPI(baseHttp : BaseHttp) {
    internal var okHttpClient: OkHttpClient? = null
    private val gson: Gson

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()

        okHttpClient = baseHttp.httpClient
    }

    fun <S> createService(serviceClass: Class<S>, serverUrl: String): S {
        val builder: Retrofit.Builder = Retrofit.Builder()
            .client(okHttpClient!!)
            .baseUrl(serverUrl + serverPath)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
        val retrofit = builder.build()
        return retrofit.create(serviceClass)
    }


    private inner class ErrorParser {
        @SerializedName("errors")
        internal var errors: JsonObject? = null

        val error: String
            get() {
                if (errors == null) {
                    return ""
                }
                val error = StringBuilder()
                for ((key, value) in errors!!.entrySet()) {
                    error.append(key).append(" ")
                    error.append(value.asString).append("; ")
                }
                return error.toString()
            }
    }

    companion object {

        private val TAG = BaseAPI::class.java.simpleName
        private const val serverPath = "/app-web/client-handling/rest/"

        fun getErrorMessage(throwable: Throwable): String {
            if (throwable is HttpException) {
                val body = throwable.response().errorBody()
                val gson = Gson()

                val adapter = gson.getAdapter(ErrorParser::class.java)
                try {
                    val errorParser = adapter.fromJson(body!!.string())
                    return errorParser.error
                } catch (e: Exception) {
                    Log.d(TAG, "Error parsing error message", e)
                }

            }
            return ""
        }
    }
}