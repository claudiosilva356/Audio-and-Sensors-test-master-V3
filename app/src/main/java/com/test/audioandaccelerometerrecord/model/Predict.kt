package com.test.audioandaccelerometerrecord.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class Predict(var tagId : String = "tagvibration1",
                   @Expose @SerializedName("tagSensorData")val tagSensorData: TagSensorData,
                   @Expose @SerializedName("geographic")val geographic: Geographic)