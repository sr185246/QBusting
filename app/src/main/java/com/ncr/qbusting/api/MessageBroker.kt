package com.ncr.qbusting.api

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface MessageBroker {

    @POST
    fun registerMobileDevice(@Url url: String, @Query("routingKey") routingKey:String): Call<String>

    /*fun registerMobileDevice( @Query("exchangeName") exchangeName: String,
                        @Query("routingKey") routingKey:String,
                        @Query("messageData") messageData:String): Call<String>*/
}