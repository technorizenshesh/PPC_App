package com.codeinger.ppc_app.retrofit

class Constant {
    companion object {
        var token: String = "token"
        var Fname: String = "fname"
        var Lname: String = "lname"
        var Email: String = "email"
        var PlanId: String = "membershipPlanId"
        var PlanAmount: String = "plan_amount"
        var PlanYears: String = "plan_years"


        var BaseUrl: String = "http://ec2-3-143-169-211.us-east-2.compute.amazonaws.com/"
        var AppUrl: String = BaseUrl + "api/api/"
    }

}