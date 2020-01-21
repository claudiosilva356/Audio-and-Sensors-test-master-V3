package com.test.audioandaccelerometerrecord.rest

import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class BaseHttpTest {

    lateinit var baseHttp: BaseHttp
    @Before
    fun setUp() {
        baseHttp = BaseHttp(createTempDir("/tempDir","test", null))
    }

    @Test
    fun testClientNotNull(){
        assertNotNull(baseHttp.httpClient)
    }
    @Test
    fun testConectTimeout(){
        assertEquals(baseHttp.httpClient!!.connectTimeoutMillis(),60000)
    }

    @Test
    fun testReadTimeout(){
        assertEquals(baseHttp.httpClient!!.readTimeoutMillis(),60000)
    }

    @Test
    fun testInterceptor(){
        assertEquals(baseHttp.httpClient!!.interceptors().size,1)
    }

    @Test
    fun testCacheSize(){
        assertEquals(baseHttp.httpClient!!.cache()!!.size(),0)
    }


}