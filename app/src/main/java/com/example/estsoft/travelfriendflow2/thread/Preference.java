package com.example.estsoft.travelfriendflow2.thread;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by YeonJi on 2016-08-11.
 */
public class Preference {
    private final String PREF_NAME = "pref";
    static Context mContext;

    public Preference(Context c) {
        mContext = c;
    }


    public String getUserNo(){     // user_no(pk) 구하기
        String no = null;

        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
            String userData = sharedPreferences.getString("userData", "null");
            JSONObject jsonObj = new JSONObject(userData);
            no = jsonObj.getString("no");
        }catch (JSONException je){
            je.printStackTrace();
        }

        return  no;
    }
}
