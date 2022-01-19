package com.codeinger.ppcapp.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.codeinger.ppcapp.model.MemberShipDetailModel;
import com.codeinger.ppcapp.retrofit.Constant;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static void getKeyHash(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.codeinger.ppc_app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Toast.makeText(context,  Base64.encodeToString(md.digest(), Base64.DEFAULT)+"", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

}
