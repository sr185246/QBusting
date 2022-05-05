package com.ncr.qbusting.datamodels

data class OrderResponse(
    val mobileDeviceId: String,
    val orderId: Long,
    val otherDetails: String
)

data class OrderRequest(
    val mobileDeviceId: String
)