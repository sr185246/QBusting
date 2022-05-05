package com.ncr.qbusting.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ncr.qbusting.api.MessageBroker
import com.ncr.qbusting.api.Order
import com.ncr.qbusting.datamodels.OrderRequest
import com.ncr.qbusting.datamodels.OrderResponse
import com.ncr.qbusting.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MQViewModel() : ViewModel() {


    var mqSuccessResponse: MutableLiveData<Boolean> = MutableLiveData()
    var mqFailResponse: MutableLiveData<Boolean> = MutableLiveData()

    fun RegisterMobile( routingKey:String){
        val messageBrokerApi = ServiceBuilder.buildService(MessageBroker::class.java)
        //val messageQueueCall = messageBrokerApi.registerMobileDevice(exchangeName,routingKey,messageData)
        val messageQueueCall = messageBrokerApi.registerMobileDevice("http://172.18.64.17:9520/producer",routingKey)
        // launching a new coroutine
        messageQueueCall.enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.w("MQViewModel", "On response")
                Log.w("MQViewModel", "response.isSuccessful = ${response.isSuccessful}")
                if (response.isSuccessful) {
                    Log.w("MQViewModel", "Mobile device registration is  successful")
                    Log.i("MQViewModel", response.body().toString())
                    mqSuccessResponse.value = true;

                } else {

                    mqSuccessResponse.value = false;
                    Log.w("MQViewModel", "Mobile device registration is  not successful")
                }
                Log.w("MQViewModel", "mqSuccessResponse value = ${mqSuccessResponse.value}")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.w("MQViewModel", "On failure")
                t.message?.let { Log.w("MQViewModel", it) }
                t.printStackTrace();
                //mqFailResponse.value = true;
                //Log.w("MainActivity", "Server Error")
            }
        })
    }
}