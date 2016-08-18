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

    public void put(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public String getValue(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    public void initialization(){
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }


}
