package com.example.estsoft.travelfriendflow2.thread;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by YeonJi on 2016-08-11.
 * HttpConnectionThread
 * NewTravelSettingActivity, MapViewActivity에서 사용
 * post방식으로 넘길 경우
 */
public class HttpConnectionThread extends AsyncTask<String, Void, String> {
    private static String LOG_TAG = "HttpConnectionThread";
    static Context mContext;

    public HttpConnectionThread(Context c) {
        mContext = c;
    }

    @Override
    protected String doInBackground(String... path){
        // URL 연결이 구현될 부분
        URL url;
        String response = "";
        String CONNURL = path[0];
        String VALUE = path[1];
        HttpURLConnection conn = null;

        try {
            url = new URL(CONNURL);
            Log.e(LOG_TAG, CONNURL);
            Log.e(LOG_TAG, VALUE);

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000); // 타임아웃: 10초
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream(); // 서버로 보내기 위한 출력 스트림
            os.write(VALUE.getBytes());
            os.flush();


            Log.e("http response code", conn.getResponseCode()+"");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                Log.e(LOG_TAG, "연결 성공");
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                    response += line;
                }

                br.close();
                conn.disconnect();
                Log.e("RESPONSE", "The response is: " + response);
            }else{
                response = "failed";
            }

        }catch (IOException e) {
            e.printStackTrace();
        }finally{
            conn.disconnect();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        // UI 업데이트가 구현될 부분

        if( result.equals("failed")){
            Log.e("연결","실패");
        }else if( !result.equals("{}")) {
            Preference pref = new Preference(mContext);
            pref.put("prefSchNo", result);
            Log.e("!{}","진입");
        }


    }

}   // End_HttpConnectionThread
