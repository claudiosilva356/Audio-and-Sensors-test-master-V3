package com.test.audioandaccelerometerrecord

import android.app.Application
import com.test.audioandaccelerometerrecord.module.restModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RecordApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Android context
            androidContext(this@RecordApp)
            modules(listOf(restModule))
        }
        instance = this
    }


    companion object {
        private val TAG = RecordApp::class.java.simpleName

        lateinit var instance: RecordApp
            private set
    }

}