package com.codeinger.ppcapp.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.codeinger.ppcapp.ui.activity.authentication.WelcomeActivity

class SessionManager {
    companion object {
        val PREF_NAME: String = "PPC_App"
        val MODE: Int = Context.MODE_PRIVATE


        fun writeString(context: Context, key: String, value: String) {
            getEditor(context)
                .putString(key, value).commit()
        }


        fun readString(context: Context, key: String, defValue: String): String? {
            return getPreferences(
                context
            ).getString(key, defValue)
        }

        fun getEditor(context: Context): SharedPreferences.Editor {
            return getPreferences(
                context
            ).edit();
        }

        fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                PREF_NAME,
                MODE
            );
        }

        fun writeInt(context: Context, key: String, value: Int) {
            getEditor(context)
                .putInt(key, value).commit()
        }


        fun readInt(context: Context, key: String, defValue: Int): Int? {
            return getPreferences(
                context
            ).getInt(key, defValue)
        }

        fun clear(context: Context) {
            getEditor(context).clear().commit()
            val intent = Intent(context, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
            context.startActivity(intent)
        }
    }


}