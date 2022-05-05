package com.ncr.qbusting.api

import com.ncr.qbusting.datamodels.OrderRequest
import com.ncr.qbusting.datamodels.OrderResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Order {

    @POST("order-service/submit-order")
    fun placeOrder(@Body order: OrderRequest): Call<OrderResponse>
}