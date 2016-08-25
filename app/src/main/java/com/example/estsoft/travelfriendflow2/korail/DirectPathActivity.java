package com.example.estsoft.travelfriendflow2.korail;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectPathActivity extends AppCompatActivity {

    ArrayList<TrainDirect> tr = new ArrayList<TrainDirect>();

    JSONObject requestJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_path);

        Intent intent = getIntent();
        String goDate = intent.getStringExtra("goDate");
        String goTime = intent.getStringExtra("goTime") + ":00";
        String startStation = intent.getStringExtra("startStation");
        String endStation = intent.getStringExtra("endStation");

        Log.d("intent Extra ---->", "goDate : " + goDate + ", goTime : " + goTime + ", startStation : " + startStation + ", endStation : " + endStation);

        getData("http://222.239.250.207:8080/TravelFriendAndroid/train/getDirectPath?" +
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
            Toast.makeText(DirectPathActivity.this, "시간표 데이터를 받던중 오류가 났습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setTr(JSONObject requestJson) throws JSONException{
        Log.d("setTr requestJson---->", String.valueOf(requestJson));
        JSONArray trainOperationList = requestJson.getJSONArray("trainTimeList");
        Log.d("setTr----------->", String.valueOf(trainOperationList));

        for(int i=0; i<trainOperationList.length(); i++){
            JSONObject trainOperation = trainOperationList.getJSONObject(i);
            String startStationName = trainOperation.getString("startStationName");
            String endStationName = trainOperation.getString("endStationName");
            String departureTimeStrings[] = trainOperation.getString("departureTime").split(":");
            String departureTime = departureTimeStrings[0] + ":" + departureTimeStrings[1];
            String arrivalTimeStrings[] = trainOperation.getString("arrivalTime").split(":");
            String arrivalTime = arrivalTimeStrings[0] + ":" + arrivalTimeStrings[1];
            String trainModel = trainOperation.getString("trainModel");
            String trainNum = trainOperation.getString("trainNum");
            tr.add(new TrainDirect(startStationName,endStationName,departureTime,arrivalTime,trainModel,trainNum));
        }

        TrainDirectAdapter adapter = new TrainDirectAdapter(getApplicationContext(),R.layout.train_direct,tr);
        ListView lv = (ListView)findViewById(R.id.trainlist);
        lv.setAdapter(adapter);
    }

    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DirectPathActivity.this, "Please Wait", null, true, true);
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

                    Log.d("Asynctask result---->", sb.toString());

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