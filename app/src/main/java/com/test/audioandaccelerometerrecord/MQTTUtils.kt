package com.test.audioandaccelerometerrecord

import android.content.Context

import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

import android.util.Log.d

class MQTTUtils @Throws(MqttException::class)
constructor(
    internal var context: Context,
    internal var host: String,
    internal var topics: Array<String>
) {
    internal var client: MqttAndroidClient
    internal var clientId: String
    internal var connected = false


    init {

        clientId = MqttClient.generateClientId()
        client = MqttAndroidClient(context, host, clientId)
        val options = MqttConnectOptions()
        options.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1
        val token = client.connect(options)

    }

    @Throws(MqttException::class)
    fun putMessage(topic: String, msg: String) {
        if (client.isConnected) {
            val message = MqttMessage(msg.toByteArray())
            message.qos = 0
            message.isRetained = false
            client.publish(topic, message)
            d("CONNECTED?", "" + client.isConnected)
            d("Sent to Broker", "true")
        } else {
            reconnect()
        }
    }

    @Throws(MqttException::class)
    fun reconnect() {
        client = MqttAndroidClient(context, host, clientId)
        val token = client.connect()

    }
}
