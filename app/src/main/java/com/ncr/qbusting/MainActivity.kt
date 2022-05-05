package com.ncr.qbusting

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.ncr.qbusting.UIUtils.DialogDisplay
import com.ncr.qbusting.adapters.CartItemAdapter
import com.ncr.qbusting.viewmodels.LineItem
import com.ncr.qbusting.viewmodels.MQViewModel
import com.ncr.qbusting.viewmodels.OrderViewModel
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import org.w3c.dom.Text
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {

    var subscribeThread: Thread? = null
    private var QUEUE_NAME: String = ""
    private var ROUTING_KEY: String = ""
    private val logTag: String = "MainActivity"
    private lateinit var mqViewModel: MQViewModel
    private lateinit var orderViewModel: OrderViewModel

    var isOrderStatusValid: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var lineItemsRecyclerView: RecyclerView
    private lateinit var lineItemsRecyclerAdapter: CartItemAdapter
    private var displayLineitems = mutableListOf<LineItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mqViewModel = ViewModelProvider(this).get(MQViewModel::class.java)
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        QUEUE_NAME = getString(R.string.rabbitmq_queue_name)
        ROUTING_KEY = Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID)

        observeMessageQueueResponse()
        observeOrderResponse()
        observeOrderStatus()

        lineItemsRecyclerView = findViewById(R.id.cartitems)
        lineItemsRecyclerAdapter = CartItemAdapter(displayLineitems)
        lineItemsRecyclerView.adapter = lineItemsRecyclerAdapter



        findViewById<Button>(R.id.addItemToCart).setOnClickListener{
            var itemId:String = findViewById<TextView>(R.id.line_item_id).text.toString()
            if(itemId.length == 0){
                itemId = "Item" + (lineItemsRecyclerAdapter.itemCount+1)
            }
            lineItemsRecyclerAdapter.addLineItem(LineItem(
                itemId,
                "Product" + (lineItemsRecyclerAdapter.itemCount+1),"10.00","1"))
            findViewById<TextView>(R.id.line_item_id).text = ""
        }
        findViewById<Button>(R.id.placeorder).setOnClickListener{
            var buttonTxt = findViewById<Button>(R.id.placeorder).text
            if(buttonTxt == getString(R.string.placeorder))
                submitOrder()
            else{
                DialogDisplay.promptOkDialog("Payment", "Payment Successful", false, this)
                lineItemsRecyclerAdapter.ClearLineItems()
                findViewById<TextView>(R.id.orderstatus).text = ""
                findViewById<Button>(R.id.placeorder).text = getString(R.string.placeorder)
            }
        }
    }
    fun submitOrder(){
        //var androidID = Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID)
       // orderViewModel.submitOrder(ROUTING_KEY)
        getorderStatus()
    }
    private fun observeOrderStatus(){
        isOrderStatusValid.let{
            isOrderStatusValid.observe(this, Observer{ isValid ->
                if(!isValid){
                    DialogDisplay.promptOkDialog("Order", "Order Invalid", true,this)
                    isOrderStatusValid.value = true
                }
            })
        }
    }

    private fun observeOrderResponse() {
        orderViewModel.orderSuccessResponse.let {
            orderViewModel.orderSuccessResponse.observe(this, Observer { orderRegStatus ->

                if (orderRegStatus) {
                    DialogDisplay.promptOkDialog("Order", "Order placed, wait for status", false,this)
                    orderViewModel.orderSuccessResponse.value = false
                }
            })
        }

        orderViewModel.orderFailResponse.let {
            orderViewModel.orderFailResponse.observe(this, Observer { failed ->
                if(failed) {
                    DialogDisplay.promptOkDialog("Order", "Unable to place order", true,this)
                    Log.w(/*AppTag.getAppTag() +*/ logTag, "Unable to register device ")
                    mqViewModel.mqFailResponse.value = false
                }

            })
        }
    }


    fun getorderStatus(){
        subscribe();
    }

    private fun observeMessageQueueResponse() {
        Log.i("MainActivity", "observeMessageQueueResponse..")

        mqViewModel.mqSuccessResponse.let {
            mqViewModel.mqSuccessResponse.observe(this, Observer { mqRegStatus ->

                Log.i("MainActivity", "mqSuccessResponse ${mqRegStatus}")
                if (mqRegStatus) {

                    DialogDisplay.promptOkDialog("Message Broker", "Device registered successfully..!!", false,this)
                    mqViewModel.mqSuccessResponse.value = false
                }
            })
        }

        mqViewModel.mqFailResponse.let {
            mqViewModel.mqFailResponse.observe(this, Observer { failed ->
                Log.i("MainActivity", "mqFailResponse = ${failed} ")
                if(failed) {
                    DialogDisplay.promptOkDialog("Message Broker", "Unable to register device", true,this)
                    Log.w(/*AppTag.getAppTag() +*/ logTag, "Unable to register device ")
                    mqViewModel.mqFailResponse.value = false
                }

            })
        }
    }

    fun subscribe() {
        subscribeThread = Thread {
                try {
                    val factory = ConnectionFactory()
                    factory.host = getString(R.string.RabbitMQHost)
                    factory.port = 5672
                    factory.username = getString(R.string.RabbitMQUser)
                    factory.password = getString(R.string.RabbitMQPassword)
                    val connection =  factory.newConnection()
                    val channel = connection.createChannel()
                    var deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
                        val message = String(delivery.body, Charset.defaultCharset())
                        if (delivery.envelope.routingKey == ROUTING_KEY) {
                            Log.i("Intended GetMessage"," [x] Received '$message'")
                            findViewById<TextView>(R.id.orderstatus).text = message
                            if(message == "Invalid")
                            {
                               // isOrderStatusValid.value = false
                            }
                            else
                                findViewById<Button>(R.id.placeorder).text = "Pay"
                        }
                        //Log.i("GetMessage"," [x] Received '$message'")
                    }
                    channel.basicConsume(QUEUE_NAME, true, deliverCallback, { consumerTag -> })
                } catch (e: InterruptedException) {
                    e.printStackTrace()

                } catch (e1: Exception) {
                    Log.d("", "Connection broken: " + e1.javaClass.name)
                    e1.printStackTrace()
                    try {
                        Thread.sleep(5000) //sleep and then try again
                    } catch (e: InterruptedException) {
                        e.printStackTrace()

                    }
                }
        }
        subscribeThread!!.start()
    }
}
