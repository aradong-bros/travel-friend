package com.example.estsoft.travelfriendflow2.korail;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.lookaround.OthersPlanMapActivity;
import com.example.estsoft.travelfriendflow2.lookaround.OthersPlanTableActivity;

import java.util.ArrayList;

public class DirectPathActivity extends AppCompatActivity {

    ArrayList<TrainDirect> tr = new ArrayList<TrainDirect>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_path);


        tr.add(new TrainDirect("서울역","부산역","04:20","06:40","무궁화호","1234"));
        tr.add(new TrainDirect("서울역","부산역","03:12","08:12","새마을호","2345"));
        tr.add(new TrainDirect("서울역","부산역","05:24","01:43","새마을호","4534"));
        tr.add(new TrainDirect("서울역","부산역","06:20","03:52","무궁화호","3645"));

        TrainDirectAdapter adapter = new TrainDirectAdapter(getApplicationContext(),R.layout.train_direct,tr);
        ListView lv = (ListView)findViewById(R.id.trainlist);
        lv.setAdapter(adapter);

    }

}

class TrainDirectAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<TrainDirect> tr;
    LayoutInflater inf;

    public TrainDirectAdapter(Context context, int layout, ArrayList<TrainDirect> tr){
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
        TextView trainType = (TextView)convertView.findViewById(R.id.train_type);
        TextView trainNum = (TextView)convertView.findViewById(R.id.train_num);
        TrainDirect t = tr.get(position);
        sStation.setText(t.sStation);
        eStation.setText(t.eStation);
        sTime.setText(t.sTime);
        eTime.setText(t.eTime);
        trainType.setText(t.trainType);
        trainNum.setText(t.trainNum);

        return convertView;
    }
}

class TrainDirect{
    String sStation = "";
    String eStation = "";
    String sTime = "";
    String eTime = "";
    String trainType="";
    String trainNum = "";

    public TrainDirect(String sStation, String eStation, String sTime,  String eTime, String trainType,String trainNum){
        this.sStation = sStation;
        this.eStation = eStation;
        this.sTime = sTime;
        this.eTime = eTime;
        this.trainType = trainType;
        this.trainNum = trainNum;
    }


}