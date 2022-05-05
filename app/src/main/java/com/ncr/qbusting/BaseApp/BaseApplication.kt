package com.ncr.qbusting.BaseApp

import android.app.Application
import android.provider.Settings
import android.util.Log
import com.ncr.qbusting.R
import com.ncr.qbusting.UIUtils.DialogDisplay
import com.ncr.qbusting.api.MessageBroker
import com.ncr.qbusting.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BaseApplication : Application() {
    private var logTag = "Application"
    private var ROUTING_KEY: String = ""

    override fun onCreate() {
        super.onCreate()
        registerMobileDevice()
    }

    fun registerMobileDevice(){
        ROUTING_KEY = Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID)

            val messageBrokerApi = ServiceBuilder.buildService(MessageBroker::class.java)
            val messageQueueCall = messageBrokerApi.registerMobileDevice(getString(R.string.RegisterMobileURL),ROUTING_KEY)
            // launching a new coroutine
            messageQueueCall.enqueue(object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.w(logTag, "On response")
                    Log.w(logTag, "response.isSuccessful = ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        Log.w(logTag, "Mobile device registration is  successful")
                        Log.i(logTag, response.body().toString())
                        // (getApplicationContext()).getCurrentActivity()
                       // DialogDisplay.promptOkDialog("Message Broker", "Device registered successfully..!!", false,this@BaseApplication)

                    } else {


                        Log.w(logTag, "Mobile device registration is  not successful")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.w(logTag, "On failure")
                    t.message?.let { Log.w(logTag, it) }
                    t.printStackTrace();
                    //mqFailResponse.value = true;
                    //Log.w("MainActivity", "Server Error")
                }
            })

    }
}