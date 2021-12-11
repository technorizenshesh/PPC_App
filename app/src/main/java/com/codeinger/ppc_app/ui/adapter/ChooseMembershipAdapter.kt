package com.codeinger.ppc_app.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codeinger.ppc_app.R
import com.codeinger.ppc_app.model.MemberShipPlanModel
import com.codeinger.ppc_app.retrofit.Constant
import com.codeinger.ppc_app.utils.SessionManager

class ChooseMembershipAdapter : RecyclerView.Adapter<ChooseMembershipAdapter.ViewHolder> {

    var context: Context? = null
    var arrayList : ArrayList<MemberShipPlanModel>? = null


    constructor(context: Context, arrayList: ArrayList<MemberShipPlanModel>) {
        this.context = context
        this.arrayList = arrayList
    }


    inner class ViewHolder (var itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        var tvYear: TextView = itemView.findViewById(R.id.tvYear)
        var rlMain: RelativeLayout = itemView.findViewById(R.id.rlMain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.choose_membership, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvPrice.text = "$"+arrayList!![position].getPrice().toString()
        holder.tvYear.text = arrayList!![position].getDurationDisplay().toString()

        holder.rlMain.setOnClickListener {
            SessionManager.writeString(context!!, Constant.PlanId,arrayList!![position].getMembershipPlanId())
            SessionManager.writeString(context!!, Constant.PlanAmount, arrayList!![position].getPrice().toString())
            SessionManager.writeString(context!!, Constant.PlanYears, arrayList!![position].getDurationDisplay().toString())
        }
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }
}