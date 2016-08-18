package com.example.estsoft.travelfriendflow2.thread;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by YeonJi on 2016-08-11.
 * HttpParamConnThread
 * get방식으로 parameter로 넘길 경우
 * !MyTravelListActivity, BookMarkListActivity에서 사용! -> ui문제때문에 결국 activity에서 따로 구현...
 * ui 문제 없는 단순 조회기능(BookMarkList확인용)에서만 사용
 *
 * MySettingActivity에서 사용
 * ex) http://222.239.250.207:8080/TravelFriendAndroid/schedule/schSelect/{no}
 * ex) http://222.239.250.207:8080/TravelFriendAndroid/favorite/selectFavoriteList/{user_no 값}
 */
public class HttpParamConnThread extends AsyncTask<String, Void, String> {
    private static String LOG_TAG = "HttpParamConnThread";
    @Override
    protected String doInBackground(String... path){
        // URL 연결이 구현될 부분
        URL url;
        String response = "";
        String CONNURL = path[0];
        String VALUE = path[1];
        HttpURLConnection conn = null;
        try {

            url = new URL(CONNURL+"/"+VALUE);
            Log.e(LOG_TAG, CONNURL+"/"+VALUE);
            conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(3000);
            conn.setDoInput(true);

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
        if(result==null)
            return;
        // ...
    }

}   // End_HttpParamConnThread