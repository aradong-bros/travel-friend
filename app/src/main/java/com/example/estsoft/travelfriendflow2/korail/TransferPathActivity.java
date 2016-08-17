package com.example.estsoft.travelfriendflow2.korail;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.lookaround.OthersPlanMapActivity;
import com.example.estsoft.travelfriendflow2.lookaround.OthersPlanTableActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TransferPathActivity extends AppCompatActivity {

    ArrayList<TrainTransfer> tr = new ArrayList<TrainTransfer>();

    JSONObject requestJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_path);

        Intent intent = getIntent();
        String goDate = intent.getStringExtra("goDate");
        String goTime = intent.getStringExtra("goTime") + ":00";
        String startStation = intent.getStringExtra("startStation");
        String endStation = intent.getStringExtra("endStation");

        tr.clear();
        TrainTransferAdapter adapter = new TrainTransferAdapter(getApplicationContext(),R.layout.train_transfer,tr);
        ListView lv = (ListView)findViewById(R.id.trainlist);
        lv.setAdapter(adapter);

        getData("http://222.239.250.207:8080/TravelFriendAndroid/train/getTransferPath?" +
                "goDate=" + goDate + "&" +
                "goTime=" + goTime + "&" +
                "startStation=" + startStation + "&" +
                "endStation=" + endStation);

    }

    public void setRequestJson(String jsonString){
        try {
            if(jsonString == null || jsonString == "") throw new JSONException("");
            requestJson = new JSONObject(jsonString);
            setTr(requestJson);
        } catch (JSONException jsonException){
            Toast.makeText(TransferPathActivity.this, "시간표 데이터를 받던중 오류가 났습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setTr(JSONObject requestJson) throws JSONException{
        JSONArray trainOperationList = requestJson.getJSONArray("trainTimeList");

        for(int i=0; i<trainOperationList.length(); i++){
            JSONObject trainOperation = trainOperationList.getJSONObject(i);
            String startStationName = trainOperation.getString("startStationName");
            String transferStationName = trainOperation.getString("transferStationName");
            String endStationName = trainOperation.getString("endStationName");
            String departureTimeStrings[] = trainOperation.getString("departureTime").split(":");
            String departureTime = departureTimeStrings[0] + ":" + departureTimeStrings[1];
            String transferStationArrivalTimeStrings[] = trainOperation.getString("transferStationArrivalTime").split(":");
            String transferStationArrivalTime = transferStationArrivalTimeStrings[0] + ":" + transferStationArrivalTimeStrings[1];
            String trainModel = trainOperation.getString("trainModel");
            String trainNum = trainOperation.getString("trainNum");
            String transferStationDepartureTimeStrings[] = trainOperation.getString("transferStationDepartureTime").split(":");
            String transferStationDepartureTime = transferStationDepartureTimeStrings[0] + ":" + transferStationDepartureTimeStrings[1];
            String arrivalTimeStrings[] = trainOperation.getString("arrivalTime").split(":");
            String arrivalTime = arrivalTimeStrings[0] + ":" + arrivalTimeStrings[1];
            String transferTrainModel = trainOperation.getString("transferTrainModel");
            String transferTrainNum = trainOperation.getString("transferTrainNum");
            tr.add(
                    new TrainTransfer(
                            startStationName, transferStationName, departureTime,
                            transferStationArrivalTime, trainModel, trainNum, endStationName,
                            transferStationDepartureTime, arrivalTime, transferTrainModel,
                            transferTrainNum
                    )
            );
        }

        TrainTransferAdapter adapter = new TrainTransferAdapter(getApplicationContext(),R.layout.train_transfer,tr);
        ListView lv = (ListView)findViewById(R.id.trainlist);
        lv.setAdapter(adapter);
    }

    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TransferPathActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected String doInBackground(String... params){
                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();
                }catch (Exception e){
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result){
                super.onPostExecute(result);
                loading.dismiss();
                setRequestJson(result);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
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