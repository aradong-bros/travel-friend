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

public class TransferPathActivity extends AppCompatActivity {

    ArrayList<TrainTransfer> tr = new ArrayList<TrainTransfer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_path);

        tr.add(new TrainTransfer("서울역","대전역","04:20","06:40","무궁화호","1234",
                "부산역","04:20","06:40","무궁화호","1234"));
        tr.add(new TrainTransfer("광주역","울산역","03:12","08:12","새마을호","2345",
                "부산역","04:20","06:40","무궁화호","1234"));
        tr.add(new TrainTransfer("서울역","부산역","05:24","01:43","새마을호","4534",
                "부산역","04:20","06:40","무궁화호","1234"));
        tr.add(new TrainTransfer("서울역","부산역","06:20","03:52","무궁화호","3645",
                "부산역","04:20","06:40","무궁화호","1234"));

        TrainTransferAdapter adapter = new TrainTransferAdapter(getApplicationContext(),R.layout.train_transfer,tr);
        ListView lv = (ListView)findViewById(R.id.trainlist);
        lv.setAdapter(adapter);

    }

}


class TrainTransferAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<TrainTransfer> tr;
    LayoutInflater inf;

    public TrainTransferAdapter(Context context, int layout, ArrayList<TrainTransfer> tr){
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
        TextView mStation1 = (TextView)convertView.findViewById(R.id.station_mid);
        TextView sTime = (TextView)convertView.findViewById(R.id.time_start);
        TextView mTime1 = (TextView)convertView.findViewById(R.id.time_mid);
        TextView trainType1 = (TextView)convertView.findViewById(R.id.train_type1);
        TextView trainNum1 = (TextView)convertView.findViewById(R.id.train_num1);

        TextView mStation2 = (TextView)convertView.findViewById(R.id.station_mid2);
        TextView eStation = (TextView)convertView.findViewById(R.id.station_end);
        TextView mTime2 = (TextView)convertView.findViewById(R.id.time_mid2);
        TextView eTime = (TextView)convertView.findViewById(R.id.time_end);
        TextView trainType2 = (TextView)convertView.findViewById(R.id.train_type2);
        TextView trainNum2 = (TextView)convertView.findViewById(R.id.train_num2);

        TrainTransfer t = tr.get(position);

        sStation.setText(t.sStation);
        mStation1.setText(t.mStation);
        sTime.setText(t.sTime);
        mTime1.setText(t.mTime1);
        trainType1.setText(t.trainType1);
        trainNum1.setText(t.trainNum1);

        mStation2.setText(t.mStation);
        eStation.setText(t.eStation);
        mTime2.setText(t.mTime2);
        eTime.setText(t.eTime);
        trainType2.setText(t.trainType2);
        trainNum2.setText(t.trainNum2);

        return convertView;
    }
}



class TrainTransfer{
    String sStation;
    String mStation;
    String sTime;
    String mTime1;
    String trainType1;
    String trainNum1;

    String eStation;
    String mTime2;
    String eTime;
    String trainType2;
    String trainNum2;

    public TrainTransfer(String sStation, String mStation, String sTime, String mTime1, String trainType1, String trainNum1,
                         String eStation, String mTime2, String eTime, String trainType2, String trainNum2){

        this.sStation = sStation;
        this.mStation = mStation;
        this.sTime = sTime;
        this.mTime1 = mTime1;
        this.trainType1 = trainType1;
        this.trainNum1 = trainNum1;

        this.eStation = eStation;
        this.mTime2 = mTime2;
        this.eTime = eTime;
        this.trainType2 = trainType2;
        this.trainNum2 = trainNum2;
    }


}