package com.codeinger.ppcapp.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.codeinger.ppcapp.R
import com.codeinger.ppcapp.model.MemberShipPlanModel
import com.codeinger.ppcapp.retrofit.Constant
import com.codeinger.ppcapp.utils.SessionManager

class ChooseMembershipAdapter : RecyclerView.Adapter<ChooseMembershipAdapter.ViewHolder> {

    private var row_index: Int = 0
    var context: Context? = null
    var arrayList: ArrayList<MemberShipPlanModel>? = null


    constructor(context: Context, arrayList: ArrayList<MemberShipPlanModel>) {
        this.context = context
        this.arrayList = arrayList
    }


    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        var tvYear: TextView = itemView.findViewById(R.id.tvYear)
        var rlMain: RelativeLayout = itemView.findViewById(R.id.rlMain)
        var linear_plane: LinearLayout = itemView.findViewById(R.id.linear_plane)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.choose_membership, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvPrice.text = "$" + arrayList!![position].getPrice().toString()
        holder.tvYear.text = arrayList!![position].getDurationDisplay().toString()

        holder.rlMain.setOnClickListener {

            row_index = position
            notifyDataSetChanged()
            Toast.makeText(
                context, arrayList!![position].getMembershipPlanId(),
                Toast.LENGTH_SHORT
            ).show()

            SessionManager.writeString(
                context!!,
                Constant.PlanId,
                arrayList!![position].getMembershipPlanId()
            )
            SessionManager.writeString(
                context!!,
                Constant.PlanAmount,
                arrayList!![position].getPrice().toString()
            )
            SessionManager.writeString(
                context!!,
                Constant.PlanYears,
                arrayList!![position].getDurationDisplay().toString()
            )
        }

        if (row_index == position) {

            holder.rlMain.setAlpha(1f)
            holder.tvPrice.setAlpha(1f)
            holder.linear_plane.setAlpha(1F)

        } else {
            holder.rlMain.setAlpha(0.115f)
            holder.linear_plane.setAlpha(0.5F)

        }
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }
}