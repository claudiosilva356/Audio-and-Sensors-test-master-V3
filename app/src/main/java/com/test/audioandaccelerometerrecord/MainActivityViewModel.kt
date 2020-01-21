package com.test.audioandaccelerometerrecord

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.CountDownTimer
import android.os.Environment
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.ListView
import com.test.audioandaccelerometerrecord.TaskManager.ClientSocketSendReceive
import com.test.audioandaccelerometerrecord.model.Geographic
import com.test.audioandaccelerometerrecord.model.Predict
import com.test.audioandaccelerometerrecord.model.TagSensorData
import com.test.audioandaccelerometerrecord.model.Value
import com.test.audioandaccelerometerrecord.rest.repo.PredictRepo
import com.test.audioandaccelerometerrecord.sensors.AccelerometerTracker
import com.test.audioandaccelerometerrecord.sensors.AudioRecord
import com.test.audioandaccelerometerrecord.sensors.BatteryLevelTracker
import com.test.audioandaccelerometerrecord.sensors.LocationTracker
import com.test.audioandaccelerometerrecord.utils.SECOND
import com.test.audioandaccelerometerrecord.utils.techGPS
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import android.view.View
import android.widget.AdapterView


class MainActivityViewModel() : ViewModel(){

    lateinit var audioRecord: AudioRecord
    var accelerometerTracker : AccelerometerTracker? = null
    var batteryLevelTracker: BatteryLevelTracker? = null
    lateinit var locationTracker: LocationTracker
    var isRecording = false
    var tagId = "tagvibration1"
    var serverUrl = "https://locationbasedservices.lab.acs.altran.fr"
    private lateinit var mqttAndroidClient: MqttAndroidClient
    internal var topics = arrayOf(
        "F0B5D1A4A5040005/SENSOR/1",
        "F0B5D1A4A5040005/SENSOR/2",
        "F0B5D1A4A5040005/SENSOR/0"
    )
    internal var BrokerUrl = "tcp://test.mosquitto.org:1883"
    lateinit var context: Context

    lateinit var mainActivity: MainActivity
    lateinit var listView : ListView


    var timer : CountDownTimer? = null


    //User ACtivities sent by Server
    var taskArr: Array<String> = arrayOf(
        ""
    )

    var userName: String = "User_XPTO1"
    var Ip: String = "10.0.2.2"
    var Port: String = "8080"

    lateinit var clientCore : ClientSocketSendReceive

    fun initActivity(mainActivity: MainActivity){
        this.mainActivity = mainActivity
    }

    @SuppressLint("ResourceType")
    fun setListViewHandler(){
        listView = mainActivity.findViewById(R.id.activitiesListView)
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, position, l ->
                // TODO Auto-generated method stub
                println("Click on item$view")
                System.out.println(adapterView.getItemAtPosition(position).toString())
                var sendMessage = ClientSocketSendReceive(Ip, Port.toInt(),this@MainActivityViewModel)
                sendMessage.setAction("SetActivityUpdate")
                sendMessage.setUser(userName)
                sendMessage.setTask(parseTaskName(adapterView.getItemAtPosition(position).toString()))
                sendMessage.setEvent("Done")
                sendMessage.start()
                val I = Intent(context, ListViewClickActivity::class.java)
                mainActivity.startActivity(I)
            }
    }

    fun parseTaskName(string: String) : String{
        var str = string.substring(string.indexOf("*") + 1, string.lastIndexOf("*"));
        return str;
    }

    fun startTracking(context: Context){
        locationTracker = LocationTracker(context)
        initTracking()
        audioRecord = AudioRecord(Environment.getExternalStorageDirectory().getAbsolutePath(),"/predictAudio.3gp")
        accelerometerTracker = AccelerometerTracker(context)
        batteryLevelTracker = BatteryLevelTracker(context)
        initAccelerometer()

    }

    private fun initAccelerometer(){
        accelerometerTracker!!.registerAcceleromenter()


    }

    fun initTracking(){
        locationTracker.start()
    }

    fun restartAccelerometer(){
        if(accelerometerTracker!=null) {
            accelerometerTracker!!.restart()
        }
        locationTracker!!.start()
    }

    fun pauseAccelerometer(){
        if(accelerometerTracker!=null) {
            accelerometerTracker!!.pause()
        }
        locationTracker.stop()
    }

    private fun initSendData(predictRepo: PredictRepo) {
        timer = object: CountDownTimer(Long.MAX_VALUE,SECOND) {
            @SuppressLint("ResourceType")
            override fun onTick(millisUntilFinished: Long) {
                var geographic: Geographic
                if(locationTracker.location != null){
                    val location = locationTracker.location
                    geographic = Geographic(techGPS,location!!.latitude,location.longitude,"")
                }else{
                    geographic = Geographic("GPS",41.13663679, -8.6191077, "null")
                }
                val predict = Predict(tagId,
                    TagSensorData(
                        Value(batteryLevelTracker!!.getBatteryLevel()),
                        Value(accelerometerTracker!!.deltaX),
                        Value(accelerometerTracker!!.deltaY),
                        Value(accelerometerTracker!!.deltaZ),
                        Value(audioRecord.getAmplitude())
                    ),
                    geographic
                )
                if(!serverUrl.isNullOrEmpty()) {
                    predictRepo.postPredict(predict, serverUrl)
                }
                predictRepo.postPredict(predict,"https://locationbasedservices.lab.acs.altran.fr")
            //send to broker
                mqttPublish(topics[0],""+accelerometerTracker!!.deltaX)
                mqttPublish(topics[1],""+accelerometerTracker!!.deltaY)
                mqttPublish(topics[2],""+accelerometerTracker!!.deltaZ)

                clientCore = ClientSocketSendReceive(Ip, Port.toInt(),this@MainActivityViewModel)
                clientCore.setUser(userName)
                clientCore.setAction("GetActivities")
                clientCore.start()

                //mainActivity.array = taskArr
                //mainActivity.updateListView()
                val adapter = ArrayAdapter(context, R.layout.listview_item, taskArr)
                val listView: ListView = mainActivity.findViewById(R.id.activitiesListView)
                listView.adapter = adapter


            }

            override fun onFinish() {
                timer!!.cancel()
                timer!!.start()
            }
        }
        timer!!.start()
    }

    fun record(
        packageManager: PackageManager,
        predictRepo: PredictRepo
    ): Boolean{
        isRecording = audioRecord.record(packageManager)
        if(isRecording){
            initSendData(predictRepo)
        }
        return isRecording
    }

    fun stopRecord(){
        isRecording = false
        timer!!.cancel()
        audioRecord.stopRecord()
    }

    fun mqttConnect() {
        var clientId = MqttClient.generateClientId()
        mqttAndroidClient = MqttAndroidClient ( this.context,BrokerUrl,clientId )
        try {
            val token = mqttAndroidClient.connect()
        } catch (e: MqttException) {
            // Give your callback on connection failure here
            e.printStackTrace()
        }
    }

    fun mqttsetContext(applicationContext : Context) {
        this.context = applicationContext;
    }

    fun mqttPublish(topic: String, data: String) {
        val encodedPayload : ByteArray
        if(mqttAndroidClient.isConnected()) {
            try {
                encodedPayload = data.toByteArray(charset("UTF-8"))
                val message = MqttMessage(encodedPayload)
                message.qos = 0
                message.isRetained = false
                mqttAndroidClient.publish(topic, message)
            } catch (e: Exception) {
                // Give Callback on error here
            } catch (e: MqttException) {
                // Give Callback on error here
            }
        }else{
            mqttConnect()
        }

    }
}