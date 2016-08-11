package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.map.PinItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class NewTravelSettingActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String LOG_TAG = "NewTravelSetting";
    private static String schInsertURL = "http://222.239.250.207:8080/TravelFriendAndroid/schedule/schInsert";
    public String sendJsonData;

    private final Handler handler = new Handler();
    private DatePicker datePicker;
    private TextView sdate;
    private TextView stime;
    private TextView edate;
    private TextView etime;
    private TextView title;
    private Button btnConfirm;      // 확인 버튼

    int mYear;
    int mMonth;
    int mDay;

    static final int DATE_DIALOG_ID =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtravelsetting);

        sharedPreferences = getSharedPreferences("pref", 0);      // 사용자가 입력한 여행정보 데이터

        btnConfirm = (Button)findViewById(R.id.btn_confirm);
        sdate = (TextView)findViewById(R.id.selected_sdate_textview);
        stime = (TextView)findViewById(R.id.selected_stime_textview);
        edate = (TextView)findViewById(R.id.selected_edate_textview);
        etime = (TextView)findViewById(R.id.selected_etime_textview);
        title = (TextView)findViewById(R.id.edt_title);


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SelectCityActivity.class);
                startActivity(intent);

                // ----
                sharedPreferences = getSharedPreferences("pref",MODE_PRIVATE);
                String userData = sharedPreferences.getString("userData", "null");

                try {
                    JSONObject jsonObj = new JSONObject(userData);
                    String no = jsonObj.getString("no");

                    String startDate = sdate.getText().toString() + " " + stime.getText().toString();
                    String endDate = edate.getText().toString()+ " " + etime.getText().toString();

                    // -- 보낼 데이터
                    JSONObject sObject = new JSONObject();      //배열 내에 들어갈 json
                    sObject.put("user_no", no);
                    sObject.put("title",  title.getText().toString());
                    sObject.put("isPublic",  "0");          // 0:비공개, 1:공개
                    sObject.put("startDate",  startDate);
                    sObject.put("endDate",  endDate);
                    sObject.put("isfinished",  "ongoing");        // 0:미완, 1:완성
                    Log.e(LOG_TAG, sObject.toString());

                    sendJsonData = sObject.toString();

                }catch (JSONException je){
                    je.printStackTrace();
                }

                new HttpConnectionThread().execute(schInsertURL);     // Thread for Http connection

            }
        });
    }

    public void dialogSelectSDate(View view) {
        LayoutInflater inflater = getLayoutInflater();
        final View customView = inflater.inflate(R.layout.dialog_date, null);
        new android.app.AlertDialog.Builder(this).
                setTitle("날짜 선택").
                setView(customView).
                setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatePicker tv = (DatePicker)customView.findViewById(R.id.datePicker);
                        String date = String.format("%d-%d-%d", tv.getYear(), tv.getMonth() + 1, tv.getDayOfMonth());
                        sdate.setText(date);
                    }
                }).
                show();
    }

    public void dialogSelectSTime(View view) {
        new TimePickerDialog(NewTravelSettingActivity.this,slistener, 0, 0, false).show();
    }

    private TimePickerDialog.OnTimeSetListener slistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
// 설정버튼 눌렀을 때
            stime.setText(hourOfDay + ":" + minute);
        }
    };

    public void dialogSelectEDate(View view) {
        LayoutInflater inflater = getLayoutInflater();
        final View customView = inflater.inflate(R.layout.dialog_date, null);

        new android.app.AlertDialog.Builder(this).
                setTitle("날짜 선택").
                setView(customView).
                setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatePicker tv = (DatePicker)customView.findViewById(R.id.datePicker);
                        String date = String.format("%d-%d-%d", tv.getYear(), tv.getMonth() + 1, tv.getDayOfMonth());
                        edate.setText(date);
                    }
                }).
                show();
    }

    public void dialogSelectETime(View view) {
        new TimePickerDialog(NewTravelSettingActivity.this,elistener, 0, 0, false).show();
    }

    private TimePickerDialog.OnTimeSetListener elistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
// 설정버튼 눌렀을 때
            etime.setText(hourOfDay + ":" + minute);
        }
    };

    public String getDateString()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        String str_date = df.format(new Date());

        return str_date;
    }




    public class HttpConnectionThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... path){
            // URL 연결이 구현될 부분
            URL url;
            String response = "";

            try {
                url = new URL(path[0]);
                Log.e(LOG_TAG, path[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000); // 타임아웃: 10초
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream(); // 서버로 보내기 위한 출력 스트림
                os.write(sendJsonData.getBytes());
                os.flush();


                Log.e("http response code", conn.getResponseCode()+"");
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                    Log.e(LOG_TAG, "연결 성공");
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                    while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                        Log.e("line",line);
                        response += line;
                    }

                    br.close();
                    conn.disconnect();
                    Log.e("RESPONSE", "The response is: " + response);
                }

            }catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // UI 업데이트가 구현될 부분
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }

    }   // End_HttpConnectionThread



}