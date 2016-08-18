package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.basic.SettingActivity;
import com.example.estsoft.travelfriendflow2.lookaround.OthersPlanActivity;
import com.example.estsoft.travelfriendflow2.map.PinItem;
import com.example.estsoft.travelfriendflow2.thread.HttpConnectionThread;
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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MyTravelListActivity extends Activity {
    private static final String LOG_TAG = "MyTravelListActivity";
    private static String oneSrchURL = "http://222.239.250.207:8080/TravelFriendAndroid/schedule/schSelect";    // 글 1개 조회
    private static String schAllSrchURL = "http://222.239.250.207:8080/TravelFriendAndroid/schedule/schSelectByUser";    // 사용자 글 전체 조회
    private static String deleteURL = "http://222.239.250.207:8080/TravelFriendAndroid/schedule/schDelete";    // 스케쥴 1개 삭제

    private static final String TAG_RESULTS="schList";
    private static final String TAG_TITLE="title";
    private static final String TAG_SDATE="startDate";
    private static final String TAG_EDATE="endDate";
    private MyAdapter adapter;

    ArrayList<Travel> tr = new ArrayList<Travel>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytravellist);

//        new HttpParamConnThread().execute(oneSrchURL, getSchNo());
    }

    @Override
    protected void onResume() {
        super.onResume();

        tr.clear(); // 초기화
        Preference pf = new Preference(this);
        new HttpParamConnThread().execute(schAllSrchURL, pf.getUserNo());

        adapter = new MyAdapter(getApplicationContext(),R.layout.row,tr);
        ListView lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = tr.get(i).getTitle();
                Intent intent = new Intent(getApplicationContext(),OthersPlanActivity.class);
                intent.putExtra("title",title);
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new HttpParamConnThread().execute(deleteURL, Integer.toString(tr.get(position).getSchNo()));
                tr.remove(position);
                return true;
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
            JSONArray datas = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i = 0; i< datas.length(); i++){
                JSONObject object = datas.getJSONObject(i);

                Travel t = new Travel();
                t.setSchNo(object.getInt("no"));
                t.setTitle(object.getString(TAG_TITLE));

                String sdate = object.getString(TAG_SDATE);
                String edate = object.getString(TAG_EDATE);

                if( sdate == null || edate == null || object.getString("firstStation") == null || object.getString("lastStation") == null){
                    return false;
                }

                String[] s = sdate.split(" ");
                String[] e = edate.split(" ");
                t.setTxt_creationDate(s[0].replaceAll("-", "/"));

                String[] sdateArr = s[0].split("-");
                String[] edateArr = e[0].split("-");
                int sMonth = Integer.parseInt(sdateArr[1]);

                if( sMonth >= 5 && sMonth <= 9 ){
                    t.setPlanSeason("여름");
                }else if( sMonth >= 10 & sMonth <= 3 ){
                    t.setPlanSeason("겨울");
                }

                int day = Integer.parseInt(edateArr[2]) - Integer.parseInt(sdateArr[2]);
                t.setPlanTime((day-1)+"박"+day+"일");
                t.setBackground(R.drawable.hadong);    // 이미지 나중에 처리하기

                t.setSetting(true);
                tr.add(t);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }// End_parsePinData

    private void showResult() {
        adapter.notifyDataSetChanged();
    }

}


class MyAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<Travel> tr;
    LayoutInflater inf;

    public MyAdapter(Context context, int layout, ArrayList<Travel> tr){
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
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = inf.inflate(layout, null);
        }

        TextView title = (TextView)convertView.findViewById(R.id.lookAroundTextBox);
        TextView creationDate = (TextView)convertView.findViewById(R.id.txt_creationDate);
        TextView plan_time = (TextView)convertView.findViewById(R.id.plan_time);
        TextView plan_season = (TextView)convertView.findViewById(R.id.plan_season);
        LinearLayout background = (LinearLayout)convertView.findViewById(R.id.row_layout);

        ImageView heart = (ImageView)convertView.findViewById(R.id.heart);
        ImageView btn_setting = (ImageView)convertView.findViewById(R.id.btn_setting);

        heart.setVisibility(View.INVISIBLE);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context.getApplicationContext(), MySettingActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);      // 새로운 task 만들겠다는 의미
                i.putExtra("schNo", tr.get(position).getSchNo());
                context.startActivity(i);
            }
        });

        Travel t = tr.get(position);
        title.setText(t.title);
        creationDate.setText(t.getTxt_creationDate());
        plan_time.setText(t.getPlanTime());
        plan_season.setText(t.getPlanSeason());
        background.setBackgroundResource(t.getBackground());
        heart.setVisibility(View.INVISIBLE);

        if( t.isSetting() )
            btn_setting.setVisibility(View.VISIBLE);
        else
            btn_setting.setVisibility(View.INVISIBLE);

        return convertView;
    }

}
