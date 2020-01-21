package com.test.audioandaccelerometerrecord.sensors

import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.util.Log
import java.io.IOException

class AudioRecord(val filePath: String, val fileName: String) {

    lateinit var mediaRecorder : MediaRecorder
    var isRecording = false
    val LOG_TAG = "AudioRecord"


    fun record(pmanager: PackageManager):Boolean{
        if(!isRecording){
            val mFileName: String = filePath + fileName
            Log.d(LOG_TAG, mFileName)

            if (pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
                // Create the recorder
                mediaRecorder = MediaRecorder()
                // Set the audio format and encoder
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                // Setup the output location
                mediaRecorder.setOutputFile(mFileName)
                // Start the recording
                try {
                    mediaRecorder.prepare()
                    mediaRecorder.start()
                    isRecording = true
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "prepare() failed")
                    isRecording = false
                }finally {
                    return isRecording
                }


            }else { // no mic on device
                Log.e(LOG_TAG, "This device doesn't have a mic!")

            }
        }
        return isRecording
    }


    fun stopRecord(){
        // Stop the recording of the audio
        try{
            mediaRecorder.stop()
            mediaRecorder.reset()
            mediaRecorder.release()
            isRecording = false
        }catch (e : IllegalStateException ){
            Log.e(LOG_TAG, e.localizedMessage)
        }
    }

    fun getAmplitude(): Double {
        val value = 20 * Math.log10(mediaRecorder.maxAmplitude*1.0)
        if(value.isInfinite()){
            return -1.0
        }
        if(value > 0){
            return value
        } else{
            return value * -1
        }
    }
}