package com.ncr.qbusting.viewmodels

import android.icu.util.Calendar
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ncr.qbusting.api.Order
import com.ncr.qbusting.datamodels.OrderRequest
import com.ncr.qbusting.datamodels.OrderResponse
import com.ncr.qbusting.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderViewModel() : ViewModel() {


    var orderSuccessResponse: MutableLiveData<Boolean> = MutableLiveData()
    var orderFailResponse: MutableLiveData<Boolean> = MutableLiveData()

    fun submitOrder(androidID: String){

        val orderApi = ServiceBuilder.buildService(Order::class.java)
        var orderId = java.lang.System.currentTimeMillis()
        val orderStatus = "Request For Validation"
        var ordr = OrderRequest(androidID)
        val orderCall = orderApi.placeOrder(ordr)
        orderCall.enqueue(object: Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    Log.w("MainActivity", "Order placed successful")
                    Log.i("MainActivity", response.body().toString())
                    orderSuccessResponse.value = true
                } else {
                    orderSuccessResponse.value = false
                    Log.w("MainActivity", "Invalid order")
                }
            }
            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                orderFailResponse.value = true
                //Log.w("MainActivity", "Server Error")

            }
        })

    }
}