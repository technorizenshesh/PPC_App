package com.codeinger.ppcapp.retrofit

class Constant {

    companion object {

        var token: String = "Token"
        var Fname: String = "fname"
        var Lname: String = "lname"
        var Email: String = "email"
        var PlanId: String = "membershipPlanId"
        var PlanAmount: String = "plan_amount"
        var PlanYears: String = "plan_years"
        var membership_get: String = "membership"
        var isPaidMember: String = "isPaidMember"
        var Tokenn: String = "isPaidMember"




        var BaseUrl: String = "http://ec2-99-79-193-116.ca-central-1.compute.amazonaws.com/ppc/"
       // var BaseUrl: String = "http://ec2-3-143-169-211.us-east-2.compute.amazonaws.com/"

        var AppUrl: String = BaseUrl + "api/"


    }

}