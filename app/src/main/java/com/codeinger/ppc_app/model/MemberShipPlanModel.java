package com.codeinger.ppc_app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberShipPlanModel {

    @SerializedName("membershipPlanId")
    @Expose
    public String membershipPlanId;
    @SerializedName("price")
    @Expose
    public Integer price;
    @SerializedName("durationDisplay")
    @Expose
    public String durationDisplay;

    public String getMembershipPlanId() {
        return membershipPlanId;
    }

    public void setMembershipPlanId(String membershipPlanId) {
        this.membershipPlanId = membershipPlanId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDurationDisplay() {
        return durationDisplay;
    }

    public void setDurationDisplay(String durationDisplay) {
        this.durationDisplay = durationDisplay;
    }

    public MemberShipPlanModel(String membershipPlanId, Integer price, String durationDisplay) {
        this.membershipPlanId = membershipPlanId;
        this.price = price;
        this.durationDisplay = durationDisplay;
    }
}