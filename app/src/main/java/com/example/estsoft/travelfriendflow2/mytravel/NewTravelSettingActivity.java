package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.thread.HttpConnectionThread;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class NewTravelSettingActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String LOG_TAG = "NewTravelSetting";
    private static String schInsertURL = "http://222.239.250.207:8080/TravelFriendAndroid/schedule/schInsert";

    private final Handler handler = new Handler();
    private DatePicker datePicker;
    private TextView sdate, stime, edate, etime;
    private TextView title;
    private EditText stStation, endStation;
    private Button btnConfirm;      // 확인 버튼
    private DatePicker tv2;
    private long minDate;
    static final int DATE_DIALOG_ID =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtravelsetting);

        sharedPreferences = getSharedPreferences("pref", 0);      // 사용자가 입력한 여행정보 데이터

        btnConfirm = (Button)findViewById(R.id.btn_confirm);
        title = (TextView)findViewById(R.id.edt_title);
        sdate = (TextView)findViewById(R.id.selected_sdate_textview);
        stime = (TextView)findViewById(R.id.selected_stime_textview);
        edate = (TextView)findViewById(R.id.selected_edate_textview);
        etime = (TextView)findViewById(R.id.selected_etime_textview);
        stStation = (EditText)findViewById(R.id.edt_stStation);
        endStation = (EditText)findViewById(R.id.edt_endStation);

        final ArrayAdapter<CharSequence> arrayAdapter1 = ArrayAdapter.createFromResource(this, R.array.name_of_train_station, R.layout.support_simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> arrayAdapter2 = ArrayAdapter.createFromResource(this, R.array.name_of_train_station, R.layout.support_simple_spinner_dropdown_item);
        final AutoCompleteTextView autoCompleteTextView1 = (AutoCompleteTextView)findViewById(R.id.edt_stStation);
        final AutoCompleteTextView autoCompleteTextView2 = (AutoCompleteTextView)findViewById(R.id.edt_endStation);
        autoCompleteTextView1.setAdapter(arrayAdapter1);
        autoCompleteTextView2.setAdapter(arrayAdapter2);
        autoCompleteTextView1.setTextColor(Color.BLUE);
        autoCompleteTextView2.setTextColor(Color.BLUE);

        autoCompleteTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //꼭 글자를 입력하지 않아도 AutoCompleteTextView가 눌리면 리스트가 먼저 보이게 처리
                autoCompleteTextView1.showDropDown();
            }
        });

        autoCompleteTextView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        autoCompleteTextView2.setFocusable(true);
                        autoCompleteTextView2.requestFocus();
            }
        });

        autoCompleteTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView2.showDropDown();
            }
        });

        title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId){
                    case EditorInfo.IME_ACTION_NEXT:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                title.setFocusable(false);
                                sdate.setFocusable(true);
                                sdate.requestFocus();
                            }
                        });
                    break;
                }
                return true;
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // -- 보낼 데이터
                    JSONObject sObject = new JSONObject();      //배열 내에 들어갈 json
                    sObject.put("user_no", getUserNo());

                    if( title.getText().toString().trim().equals("") ){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "여행 제목을 입력해주세요!^^", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                                title.setText("");
                            }
                        });
                        return;
                    }

                    if( sdate.getText().toString().trim().equals("") || stime.getText().toString().trim().equals("") ){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "출발 날짜 및 시간을 입력해주세요!^^", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                            }
                        });
                        return;
                    }

                    if( edate.getText().toString().trim().equals("") || etime.getText().toString().trim().equals("") ){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "도착 날짜 및 시간을 입력해주세요!^^", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                            }
                        });
                        return;
                    }

                    if( stStation.getText().toString().trim().equals("") || endStation.getText().toString().trim().equals("") ){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "출발역과 도착역을 입력해주세요!^^", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                            }
                        });
                        return;
                    }

                    String startDate = sdate.getText().toString() + " " + stime.getText().toString();
                    String endDate = edate.getText().toString()+ " " + etime.getText().toString();

                    sObject.put("title", title.getText().toString());
                    sObject.put("isPublic",  "1");               // 0:비공개, 1:공개
                    sObject.put("startDate",  startDate);
                    sObject.put("endDate",  endDate);
                    sObject.put("isfinished",  "ongoing");        // ongoing:미완, finished:완성
                    sObject.put("firstStation", stStation.getText().toString());
                    sObject.put("lastStation", endStation.getText().toString());
                    Log.e(LOG_TAG, sObject.toString());

                    new HttpConnectionThread(getApplicationContext()).execute(schInsertURL,  sObject.toString());     // Thread for Http connection

                }catch (JSONException je){
                    je.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(),SelectCityActivity.class);
                startActivity(intent);

            }
        });
    }

    public String getUserNo(){     // user_no(pk) 구하기
        String no = null;

        try {
            sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
            String userData = sharedPreferences.getString("userData", "null");
            JSONObject jsonObj = new JSONObject(userData);
            no = jsonObj.getString("no");
        }catch (JSONException je){
            je.printStackTrace();
        }

        return  no;
    }

    public void dialogSelectSDate(View view) {
        LayoutInflater inflater = getLayoutInflater();
        final View customView = inflater.inflate(R.layout.dialog_date, null);

        DatePicker datePicker = (DatePicker)customView.findViewById(R.id.datePicker);
        datePicker.setMinDate(new Date().getTime());        // Current Time

        new android.app.AlertDialog.Builder(this).
                setTitle("날짜 선택").
                setView(customView).
                setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatePicker tv = (DatePicker)customView.findViewById(R.id.datePicker);
                        String date = String.format("%d-%d-%d", tv.getYear(), tv.getMonth() + 1, tv.getDayOfMonth());
                        sdate.setText(date);

                        if( !date.equals("")) {
                            String[] sArr = date.split("-");       // [year,month,day]
                            Calendar calendar = new GregorianCalendar(Integer.parseInt(sArr[0]), Integer.parseInt(sArr[1])-1, Integer.parseInt(sArr[2]));
                            minDate = calendar.getTimeInMillis();
                        }
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
            stime.setText(String.format("%02d:%02d",hourOfDay,minute));
        }
    };

    public void dialogSelectEDate(View view) {
        LayoutInflater inflater = getLayoutInflater();
        final View customView = inflater.inflate(R.layout.dialog_date, null);

        DatePicker datePicker = (DatePicker)customView.findViewById(R.id.datePicker);
        datePicker.setMinDate(minDate);       // 출발일을 최소 날짜로 지정

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
            etime.setText(String.format("%02d:%02d",hourOfDay,minute));
        }
    };

    public String getDateString()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        String str_date = df.format(new Date());

        return str_date;
    }


}