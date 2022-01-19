package com.codeinger.ppcapp.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codeinger.ppcapp.R
import com.codeinger.ppcapp.model.GetTranscationsModal

class PaymentAdapter(var callback: Context, membershipList: ArrayList<GetTranscationsModal>) :
    RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {

    private var membershipList: ArrayList<GetTranscationsModal> = membershipList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.payments, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: PaymentAdapter.ViewHolder, position: Int) {
        holder.bindItems()
        holder.title.setText(membershipList.get(position).title)
        holder.amount.setText(" $ " + membershipList.get(position).amount)
        holder.date.setText(membershipList.get(position).transactionDate)
        holder.trans_time.setText(membershipList.get(position).transactionTime)
    }

    override fun getItemCount(): Int {

        return membershipList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var title: TextView
        lateinit var amount: TextView
        lateinit var date: TextView
        lateinit var trans_time: TextView

        fun bindItems() {

            title = itemView.findViewById(R.id.title)
            amount = itemView.findViewById(R.id.amount)
            date = itemView.findViewById(R.id.date)
            trans_time = itemView.findViewById(R.id.trans_time)

            itemView.setOnClickListener {
                // callback()
            }
        }
    }
}