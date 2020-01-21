package com.test.audioandaccelerometerrecord

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.test.audioandaccelerometerrecord.model.Request
import com.test.audioandaccelerometerrecord.rest.repo.PredictRepo
import com.test.audioandaccelerometerrecord.utils.REQUEST_CODE
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.test.audioandaccelerometerrecord.TaskManager.ClientSocketSendReceive


class MainActivity() : AppCompatActivity() {

    val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    var permissionCheker = false
    lateinit var viewModel: MainActivityViewModel

    val requests: ArrayList<Request> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = MainActivityViewModel()
        viewModel.initActivity(this)
        viewModel.setListViewHandler()


        permissionCheker = ContextCompat.checkSelfPermission(this, permission[0]) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this,permission[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,permission[2]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,permission[3]) == PackageManager.PERMISSION_GRANTED
        if(!permissionCheker){
            ActivityCompat.requestPermissions(this, permission,REQUEST_CODE)
        }

        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = RequestAdapter(requests, this)
    }


    fun startRecord(view: View){
        permissionCheker = ContextCompat.checkSelfPermission(this, permission[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,permission[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,permission[2]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,permission[3]) == PackageManager.PERMISSION_GRANTED

        if(permissionCheker && !viewModel.isRecording) {
            showDialogNewTag()
        }else if(permissionCheker && viewModel.isRecording){
            stopRecord()
            button.text = getString(R.string.start)
            textView.visibility = View.GONE

        }

        button.isSelected = viewModel.isRecording

        //initiate the Mqtt connection
        viewModel.mqttsetContext(baseContext)
        viewModel.mqttConnect()

    }

    fun dialogTrackConfirmation(){
        viewModel.startTracking(this@MainActivity)
        val succedToRecord = trackListener()

        if(succedToRecord) {
            button.isSelected = true
            button.text = getString(R.string.stop)
            textView.visibility = View.VISIBLE
            requests.clear()
        }
    }

    private fun trackListener(): Boolean {
        val succedToRecord = viewModel.record(this.packageManager, PredictRepo(object : PredictRepo.Callback {
            override fun onSuccess() {
                Log.d("MAINACTIVITY", "success")
                requests.add(Request(getDateTime(System.currentTimeMillis()), true))
                rv_list.adapter!!.notifyDataSetChanged()
            }

            override fun onError(message: String) {
                Log.d("MAINACTIVITY", "faild to send")
                Log.d("MAINACTIVITY", message)
                requests.add(Request(getDateTime(System.currentTimeMillis()), false))
                rv_list.adapter!!.notifyDataSetChanged()
            }
        }))
        return succedToRecord
    }

    private fun getDateTime(l: Long): String {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE)
            val netDate = Date(l)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }


    private fun stopRecord(){
      viewModel.stopRecord()
    }

    private fun showDialogNewTag() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_tag_register, null)
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return

        val savedServerUrl = sharedPref.getString(getString(R.string.saved_server_url_key), "")
        val edtServerUrl = view.findViewById<EditText>(R.id.edtServerUrl)
        edtServerUrl.setText(savedServerUrl)

        val savedTagId = sharedPref.getString(getString(R.string.saved_tag_id_key), "tagvibration1")
        val edtTagId = view.findViewById<EditText>(R.id.edtTagId)
        edtTagId.setText(savedTagId)

        val savedUserName = sharedPref.getString(getString(R.string.saved_user_name), "User_XPTO1")
        val edtUserName = view.findViewById<EditText>(R.id.edtUserName)
        edtUserName.setText(savedUserName)

        //Localhost IP virtual Device = 10.0.2.2
        val savedIp = sharedPref.getString(getString(R.string.saved_Ip), "192.168.8.101")
        val edtIp = view.findViewById<EditText>(R.id.edtIp)
        edtIp.setText(savedIp)

        val savedPort = sharedPref.getString(getString(R.string.saved_Port), "8080")
        val edtPort = view.findViewById<EditText>(R.id.edtPort)
        edtPort.setText(savedPort)

        builder.setView(view)
            .setPositiveButton(android.R.string.ok) { dialog, id ->
                viewModel.userName = edtUserName.text.toString()
                viewModel.Ip = edtIp.text.toString()
                viewModel.Port = edtPort.text.toString()
                if(!edtTagId.text.isNullOrBlank()){
                    val newServerUrl = edtServerUrl.text.toString()
                    val newTagId = edtTagId.text.toString()

                    viewModel.serverUrl = newServerUrl
                    viewModel.tagId = newTagId


                    with (sharedPref.edit()) {
                        putString((getString(R.string.saved_server_url_key)), newServerUrl)
                        putString((getString(R.string.saved_tag_id_key)), newTagId)
                        apply()
                    }
                }
                dialogTrackConfirmation()
            }
        builder.create().show()
    }
}
