package com.codeinger.ppc_app.utils;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;

public class DataManager {





    public static DataManager getOurInstance() {
        return ourInstance;
    }

    private static final DataManager ourInstance = new DataManager();

  /*  public LoginModel getLoginModel(Context context) {
        String data = SessionManager.Companion.readString(context, Constant.Companion.getUser_info(), "");
        loginModel = new Gson().fromJson(data, LoginModel.class);
        return loginModel;
    }*/

}
