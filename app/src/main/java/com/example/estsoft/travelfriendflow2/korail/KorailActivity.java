package com.example.estsoft.travelfriendflow2.korail;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.estsoft.travelfriendflow2.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class KorailActivity extends Activity {

    private final Handler handler = new Handler();
    private DatePicker datePicker;
    private TextView sdate;
    private TextView stime;
    private TextView startStationEditText;
    private TextView endStationEditText;
    private Button btnSearch;


    static int count =0;
    ImageView path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_korail);

        btnSearch = (Button)findViewById(R.id.search_btn);
        sdate = (TextView)findViewById(R.id.selected_sdate_textview);
        stime = (TextView)findViewById(R.id.selected_stime_textview);
        startStationEditText = (TextView) findViewById(R.id.startStationEditText);
        endStationEditText = (TextView) findViewById(R.id.endStationEditText);

        path = (ImageView)findViewById(R.id.path);

        path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count ++;
                if(count%2==1) {
                    v.setSelected(true);
                }else{
                    v.setSelected(false);
                }
            }
        });


        Button search = (Button)findViewById(R.id.search_btn);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(path.isSelected()) {
                    Intent intent = new Intent(getApplicationContext(), TransferPathActivity.class);
                    intent.putExtra("goDate", sdate.getText().toString().trim());
                    intent.putExtra("goTime", stime.getText().toString().trim());
                    intent.putExtra("startStation", startStationEditText.getText().toString().trim());
                    intent.putExtra("endStation", endStationEditText.getText().toString().trim());
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(), DirectPathActivity.class);
                    intent.putExtra("goDate", sdate.getText().toString().trim());
                    intent.putExtra("goTime", stime.getText().toString().trim());
                    intent.putExtra("startStation", startStationEditText.getText().toString().trim());
                    intent.putExtra("endStation", endStationEditText.getText().toString().trim());
                    startActivity(intent);
                }
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
        new TimePickerDialog(KorailActivity.this,slistener, 0, 0, false).show();
    }

    private TimePickerDialog.OnTimeSetListener slistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
// 설정버튼 눌렀을 때
            stime.setText(hourOfDay + ":" + minute);
        }
    };
}

