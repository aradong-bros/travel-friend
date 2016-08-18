package com.example.estsoft.travelfriendflow2.thread;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by YeonJi on 2016-08-18.
 * HttpFavorConnThread
 * get방식으로 parameter로 넘길 경우
 * LookAroundActivity) Favorite 테이블에 삽입할 때 사용
 * ex)   - url : http://222.239.250.207:8080/TravelFriendAndroid/favorite/insertFavoriteData?user_no={user_no 값}&schedule_no={schedule_no 값}
 */
public class HttpFavorConnThread extends AsyncTask<String, Void, String> {
    private static String LOG_TAG = "HttpFavorConnThread";
    @Override
    protected String doInBackground(String... path){
        // URL 연결이 구현될 부분
        URL url;
        String response = "";
        String CONNURL = path[0];
        String VALUE = "?user_no="+path[1];
        String VALUE2 = "&schedule_no="+path[2];

        HttpURLConnection conn = null;
        try {

            url = new URL(CONNURL+VALUE+VALUE2);
            Log.e(LOG_TAG, CONNURL+VALUE+VALUE2);
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

}   // End_HttpFavorInsertConnThread