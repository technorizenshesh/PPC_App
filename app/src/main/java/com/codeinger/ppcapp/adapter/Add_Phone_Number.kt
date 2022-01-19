package com.codeinger.ppcapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codeinger.ppcapp.R
import java.util.ArrayList

class Add_Phone_Number(context: Context, listPhoto: ArrayList<String>) :

    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listPhoto: ArrayList<String>? = listPhoto
    private var context: Context? = context

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.add_phone_number, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.etPhone.setText(listPhoto!!.get(position))

        }
    }

    override fun getItemCount(): Int {
        return listPhoto?.size!!
    }

    private fun getItem(position: Int): String {
        return listPhoto!![position]
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val etPhone: TextView

        init {
            etPhone = itemView.findViewById(R.id.etPhone)
        }
    }

}