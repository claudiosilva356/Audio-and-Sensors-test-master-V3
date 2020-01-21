package com.test.audioandaccelerometerrecord.sensors

import android.Manifest
import android.support.test.InstrumentationRegistry
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import android.support.test.rule.GrantPermissionRule
import org.junit.Rule

class AudioRecordTest {

    val fileName = "/TEST_AUDIO_FILE.3gp"
    lateinit var audioRecord: AudioRecord
    val context = InstrumentationRegistry.getTargetContext()

    @get:Rule
    val mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @Before
    fun setUp() {
        audioRecord = AudioRecord(context.cacheDir.absolutePath,fileName)
        audioRecord.record(context.packageManager)
    }


    @Test
    fun testShouldInitRecord() {
        assertTrue(audioRecord.isRecording)
    }

    @Test
    fun testShouldCreateFile() {
        val filePath = File(context.cacheDir.absolutePath + fileName)
        assertTrue(filePath.isFile)
        assertTrue(filePath.exists())
    }

    @Test
    fun testGetAplitude() {
        val amplitude = audioRecord.getAmplitude()
        if(amplitude.isInfinite()){
            assertSame("If infinite should return -1",amplitude, -1.0)
        }else{
            assertTrue("If finite should return some finite value",amplitude.isFinite())
        }
    }

    @Test
    fun testStopRecord() {
        assertTrue(audioRecord.isRecording)
        audioRecord.stopRecord()
        assertFalse(audioRecord.isRecording)
    }

}