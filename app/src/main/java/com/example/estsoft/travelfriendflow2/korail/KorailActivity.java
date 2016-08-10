package com.example.estsoft.travelfriendflow2.korail;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.estsoft.travelfriendflow2.R;

import java.util.ArrayList;

public class KorailActivity extends Activity {

    private final Handler handler = new Handler();
    private DatePicker datePicker;
    private TextView sdate;
    private TextView stime;
    private Button btnSearch;

    ArrayList<Train> tr = new ArrayList<Train>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_korail);

        btnSearch = (Button)findViewById(R.id.search_btn);
        sdate = (TextView)findViewById(R.id.selected_sdate_textview);
        stime = (TextView)findViewById(R.id.selected_stime_textview);

        tr.add(new Train("서울역","부산역","04:20","06:40"));
        tr.add(new Train("서울역","부산역","03:12","08:12"));
        tr.add(new Train("서울역","부산역","05:24","01:43"));
        tr.add(new Train("서울역","부산역","06:20","03:52"));

        TrainAdapter adapter = new TrainAdapter(getApplicationContext(),R.layout.train,tr);
        ListView lv = (ListView)findViewById(R.id.trainlist);
        lv.setAdapter(adapter);
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

class TrainAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<Train> tr;
    LayoutInflater inf;

    public TrainAdapter(Context context, int layout, ArrayList<Train> tr){
        this.context = context;
        this.layout = layout;
        this.tr = tr;
        this.inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return tr.size();
    }

    @Override
    public Object getItem(int position){
        return tr.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = inf.inflate(layout, null);
        }

        TextView sStation = (TextView)convertView.findViewById(R.id.station_start);
        TextView eStation = (TextView)convertView.findViewById(R.id.station_end);
        TextView sTime = (TextView)convertView.findViewById(R.id.time_start);
        TextView eTime = (TextView)convertView.findViewById(R.id.time_end);
        Train t = tr.get(position);
        sStation.setText(t.sStation);
        eStation.setText(t.eStation);
        sTime.setText(t.sTime);
        eTime.setText(t.eTime);

        return convertView;
    }
}

class Train{
    String sStation = "";
    String eStation = "";
    String sTime = "";
    String eTime = "";

    public Train(String sStation, String eStation, String sTime,  String eTime){
        this.sStation = sStation;
        this.eStation = eStation;
        this.sTime = sTime;
        this.eTime = eTime;
    }


}