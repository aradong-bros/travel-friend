package com.example.estsoft.travelfriendflow2.mytravel;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.basic.MainActivity;
import com.example.estsoft.travelfriendflow2.thread.HttpMySetConnThread;
import com.example.estsoft.travelfriendflow2.thread.HttpParamConnThread;
import com.example.estsoft.travelfriendflow2.thread.Preference;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * MyTravelActivity -> MyTravelListActivity의 설정 버튼
 * */
public class MySettingActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MySettingActivity";
    private static String publicURL = "http://222.239.250.207:8080/TravelFriendAndroid/schedule/schModifyIsPublic";      // isPublic 수정
    private static String finishURL = "http://222.239.250.207:8080/TravelFriendAndroid/schedule/schModifyIsfinished";    // isFinished 수정
    private static String schSelURL = "http://222.239.250.207:8080/TravelFriendAndroid/schedule/schSelect";             // 스케쥴 1개 조회
    private boolean isPublic = true;

    ToggleButton tg_isPublic;
    private static final String TAG_RESULTS="schVo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);

        Button save_btn = (Button)findViewById(R.id.save_btn);

        Intent intent = getIntent();
        final int schNo = intent.getIntExtra("schNo", -1);
        if( schNo == -1 ){
            Toast.makeText(getApplicationContext(), "Schedule No Error", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e(LOG_TAG, "Schedule NO :"+schNo);
        new HttpParamConnThread().execute(schSelURL, schNo+"");

        tg_isPublic = (ToggleButton)findViewById(R.id.tg_isPublic);
        tg_isPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tg_isPublic.isChecked())    // 공개 처리
                    isPublic = true;
                else  // 비공개 처리
                    isPublic = false;
            }
        });


        final ToggleButton tg_isFinished = (ToggleButton)findViewById(R.id.tg_isFinished);
        tg_isFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tg_isFinished.isChecked()){  // 완성
                    new HttpMySetConnThread().execute(finishURL, schNo+"&isfinished=finished");
                }else{      // 미완성
                    new HttpMySetConnThread().execute(finishURL, schNo+"&isfinished=ongoing");
                }
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( isPublic ){    // 공개 처리
                    new HttpMySetConnThread().execute(publicURL, schNo+"&isPublic=1");
                }else{  // 비공개 처리
                    new HttpMySetConnThread().execute(publicURL, schNo+"&isPublic=0");
                }


                finish();
            }
        });


    }

    public class HttpParamConnThread extends AsyncTask<String, Void, String> {
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

                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                conn.setDoInput(true);


                int responseCode = conn.getResponseCode();
                Log.e("http response code", responseCode+"");

                if ( responseCode == HttpURLConnection.HTTP_OK ) { // 연결에 성공한 경우
                    Log.e(LOG_TAG, "연결 성공");
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                    while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                        response += line;
                    }

                    br.close();
                    conn.disconnect();
                    Log.e("RESPONSE", "The response is: " + response);
                    return response;

                }else{
                    Log.e(LOG_TAG, "서버 접속 실패");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                        }
                    });
                    //  로딩바 띄우기
                }

            }catch (ConnectTimeoutException ue){
                Log.e(LOG_TAG, "ConnectTimeoutException");
            }catch (IOException e) {
                e.printStackTrace();
            }finally{
                conn.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result==null) {
                //  로딩바 띄우기
                Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                return;
            }

            if( parsePinData(result) ){
                showResult();
            }

        }

    }   // End_HttpParamConnThread

    protected boolean parsePinData(String myJSON){

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONObject object = jsonObj.getJSONObject(TAG_RESULTS);

            if ( object.getInt("isPublic") == 1 )       // 공개
                isPublic = true;
            else        // 비공개
                isPublic = false;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }// End_parsePinData

    private void showResult() {
        tg_isPublic.setChecked(isPublic);
    }


}
