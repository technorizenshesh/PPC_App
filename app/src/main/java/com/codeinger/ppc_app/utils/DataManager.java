package com.codeinger.ppc_app.utils;

import android.content.Context;

import com.codeinger.ppc_app.model.MemberShipDetailModel;
import com.codeinger.ppc_app.retrofit.Constant;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DataManager {
    MemberShipDetailModel shipDetailModel;




    public static DataManager getOurInstance() {
        return ourInstance;
    }

    private static final DataManager ourInstance = new DataManager();

    public MemberShipDetailModel getMemberShipDetails(Context context) {
        String data = SessionManager.Companion.readString(context, Constant.Companion.getMembership_get(), "");
        shipDetailModel = new Gson().fromJson(data, MemberShipDetailModel.class);
        return shipDetailModel;
    }

}
