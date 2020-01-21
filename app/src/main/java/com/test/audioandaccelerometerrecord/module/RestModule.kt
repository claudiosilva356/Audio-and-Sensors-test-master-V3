package com.test.audioandaccelerometerrecord.module

import com.test.audioandaccelerometerrecord.RecordApp
import com.test.audioandaccelerometerrecord.rest.BaseAPI
import com.test.audioandaccelerometerrecord.rest.BaseHttp
import org.koin.dsl.module

// just declare it
val restModule = module {
    single { BaseAPI(get()) }
    single { BaseHttp(RecordApp.instance.cacheDir) }
}