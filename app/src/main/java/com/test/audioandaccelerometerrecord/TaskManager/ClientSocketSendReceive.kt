package com.test.audioandaccelerometerrecord.TaskManager

import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.net.Socket
import AAA.Activity
import AAA.FrameActionMsg
import com.test.audioandaccelerometerrecord.MainActivityViewModel


class ClientSocketSendReceive(internal var ip: String, internal var port: Int,internal var mainActivityViewModel: MainActivityViewModel) : Thread() {
    internal lateinit var frameMessage: FrameActionMsg
    internal lateinit var outputStream: OutputStream
    internal lateinit var inputStream: InputStream
    internal lateinit var objectOutputStream: ObjectOutputStream
    internal lateinit var objectInputStream: ObjectInputStream
    internal lateinit var action: String
    internal lateinit var userName: String
    internal lateinit var socket: Socket
    internal lateinit var activities: List<Activity>
    internal lateinit var eventDetails: String
    internal lateinit var task: String


    fun setUser(userName: String) {
        this.userName = userName
    }

    fun setAction(action: String) {
        this.action = action
    }

    fun setTask(task: String) {
        this.task = task
    }

    fun setEvent(event: String){
        this.eventDetails = event
    }

    override fun run() { // send FrameMessage to Master
        try {
            //initialize Object output Streams
            frameMessage = FrameActionMsg()
            socket = Socket(ip, port)
            outputStream = this.socket.getOutputStream()
            inputStream = this.socket.getInputStream()
            objectOutputStream = ObjectOutputStream(outputStream)

            when (action) {
                "GetActivities" -> {
                    println("GetActivities")
                    val message = FrameActionMsg()
                    message.setAction("GetActivities")
                    message.setUserName(userName)
                    objectOutputStream.writeObject(message)

                    objectInputStream = ObjectInputStream(inputStream)
                    val messageRec = objectInputStream.readObject() as FrameActionMsg
                    activities = messageRec.getActivities()
                    System.out.println(messageRec.getActivities().toString())
                    //if (mainActivityViewModel.taskArr != null)
                    mainActivityViewModel.taskArr = getActivitiesArr() as Array<String>
                }
                "SetActivityUpdate" -> {
                    val message = FrameActionMsg()
                    message.setAction("SetActivityUpdate")
                    message.setUserName(userName)
                    message.setTaskName(task)
                    message.setDetails(eventDetails)
                    objectOutputStream.writeObject(message)

                    objectInputStream = ObjectInputStream(inputStream)
                    val messageRec = objectInputStream.readObject() as FrameActionMsg
                    activities = messageRec.getActivities()
                    System.out.println(messageRec.getActivities().toString())
                }
            }


        } catch (io: Exception) {
            io.printStackTrace()
        }
    }

    fun getActivitiesArr(): Array<String?> {
        val strArr = arrayOfNulls<String>(activities.size)
        var i = 0
        for (activityObj in activities) {
            strArr[i++] = "*"+activityObj.taskName + "* - " + activityObj.taskDescription
        }
        return strArr
    }
}
