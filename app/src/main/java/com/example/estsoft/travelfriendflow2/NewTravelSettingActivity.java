package com.example.estsoft.travelfriendflow2;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class NewTravelSettingActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private DatePicker datePicker;
    private TextView sdate;
    private TextView stime;
    private TextView edate;
    private TextView etime;
    private TextView title;
    private Button button;

    int mYear;
    int mMonth;
    int mDay;

    static final int DATE_DIALOG_ID =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtravelsetting);

        button = (Button)findViewById(R.id.button);
        sdate = (TextView)findViewById(R.id.selected_sdate_textview);
        stime = (TextView)findViewById(R.id.selected_stime_textview);
        edate = (TextView)findViewById(R.id.selected_edate_textview);
        etime = (TextView)findViewById(R.id.selected_etime_textview);
        title = (TextView)findViewById(R.id.textView17);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MapActivity.class);
                intent.putExtra("sdate",sdate.getText().toString());
                intent.putExtra("stime",stime.getText().toString());
                intent.putExtra("edate",edate.getText().toString());
                intent.putExtra("etime",etime.getText().toString());
                intent.putExtra("title",title.getText().toString());
                startActivity(intent);
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
}