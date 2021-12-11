package com.codeinger.ppc_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codeinger.ppc_app.R

class PaymentAdapter(var callback: () -> Unit) : RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.payments, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: PaymentAdapter.ViewHolder, position: Int) {
        holder.bindItems()
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return 5
    }

    //the class is hodling the list view
    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems() {
            itemView.setOnClickListener {
                callback()
            }
        }
    }

}