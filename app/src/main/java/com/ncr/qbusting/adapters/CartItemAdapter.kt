package com.ncr.qbusting.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ncr.qbusting.R
import com.ncr.qbusting.viewmodels.LineItem

class CartItemAdapter(items: MutableList<LineItem>): RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {
    private var items:MutableList<LineItem> = items
    private val logTag = "CartItemAdapter"
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private val logTag = "CartItemAdapter"
        lateinit var itemId: TextView
        lateinit var itemQty: TextView
        lateinit var itemPrice: TextView
        init{
            Log.d(logTag, "ViewHolder init")
            itemId = itemView.findViewById(R.id.itemid)
            itemQty = itemView.findViewById(R.id.itemqty)
            itemPrice = itemView.findViewById(R.id.itemprice)

            itemView.setOnClickListener{
                Log.d(logTag, "ViewHolder init")
                val position:Int = adapterPosition
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(logTag, "onCreateViewHolder")
        val v = LayoutInflater.from(parent.context).inflate(R.layout.lineitem, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(logTag, "onBindViewHolder")
        holder.itemId.text = items[position].itemName
        holder.itemQty.text = items[position].itemQy
        holder.itemPrice.text = "$ " +  items[position].itemPrice
    }

    override fun getItemCount(): Int {
        Log.d(logTag, "getItemCount = " + items.size)
        return items.size
    }


    fun addLineItem(item: LineItem){
        Log.d(logTag, "addScriptItem")
        items.add(item);
        notifyItemInserted(items.size-1)
    }
    fun ClearLineItems(){
        Log.d(logTag, "ClearLineItems")
        items.clear();
        notifyDataSetChanged()
    }
}