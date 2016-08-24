package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.lookaround.OthersPlanActivity;
import com.example.estsoft.travelfriendflow2.map.MapViewActivity;
import com.example.estsoft.travelfriendflow2.thread.HttpMySetConnThread;
import com.example.estsoft.travelfriendflow2.thread.HttpSendSchNoConnThread;
import com.example.estsoft.travelfriendflow2.thread.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class SelectedCityActivity extends Activity {
    private static final String LOG_TAG = "SelectedCityActivity";
    private static final HashMap<String, Integer> cityMap = new HashMap<String, Integer>();
    static {
        cityMap.put("서울",1);
        cityMap.put("가평",2);
        cityMap.put("강릉",3);
        cityMap.put("안동",4);
        cityMap.put("경주",5);
        cityMap.put("부산",6);
        cityMap.put("통영",7);
        cityMap.put("하동",8);
        cityMap.put("전주",9);
        cityMap.put("보성",10);
        cityMap.put("순천",11);
        cityMap.put("여수",12);
    }   // DB_cityList

    private ArrayList<City> city = new ArrayList<City>();
    private ListView lv;

    private static String cityInsertURL = " http://222.239.250.207:8080/TravelFriendAndroid/city/cityInsert";       //  input : schedule_no, cityList_no, status, cityOrder
    private static final String travelRootURL = "http://222.239.250.207:8080/TravelFriendAndroid/android/getTravelRoot";    // ?schedule_no={schedule_no 값}
    private static String finishURL = "http://222.239.250.207:8080/TravelFriendAndroid/schedule/schModifyIsfinished";    // isFinished 수정

    private static HashMap<String, Integer> postMap = new HashMap<String, Integer>(); // KEY: cityList_no, VALUE:city_no(pk)

    CityAdapter cityAdapter;
    private static String SCHEDULE_NO = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarklist);

        cityInit();
        /**
         * ------------- SelectCityActivity -> SelectedCityActivity 값 가져와서 View에 뿌려주는 부분
         * */
        Button btn_wholeComplete = (Button)findViewById(R.id.btn_wholeComplete);
        btn_wholeComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( SCHEDULE_NO != null) {
                    new HttpSendSchNoConnThread().execute(travelRootURL, SCHEDULE_NO);     // Thread for Http connection
                    new HttpMySetConnThread().execute(finishURL, SCHEDULE_NO+"&isfinished=finished"); // 완성으로 스케쥴 바꾸기
                }else {
                    Toast.makeText(getApplicationContext(), "SCHEDULE NO ERROR", Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, SCHEDULE_NO);
                }
            }
        });


        try {
            JSONArray jArray = new JSONArray();

            for(int i=0; i<city.size(); i++) {
                JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                int cityNo = cityMap.get(city.get(i).title);

                sObject.put("schedule_no", SCHEDULE_NO);
                sObject.put("cityList_no", cityNo+"");    //cityList_no
                sObject.put("status", "none");
                sObject.put("cityOrder", "-1");

                jArray.put(sObject);
            }
            Log.e(LOG_TAG, jArray.toString());

            new HttpPostConnThread().execute(cityInsertURL, jArray.toString());     // Thread for Http connection

        }catch (JSONException je){
            je.printStackTrace();
        }
        // ---


    }

    @Override
    protected void onResume() {
        super.onResume();

        cityAdapter = new CityAdapter(getApplicationContext(),R.layout.city,city);
        lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(cityAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(getApplicationContext(),MapViewActivity.class);

                City city = (City) cityAdapter.getItem(position);
                Log.e(LOG_TAG, city.title);

                int cityList_No = cityMap.get(city.title);
                String ctrNo = Integer.toString(cityList_No);

                intent.putExtra("cityList_no", cityList_No);
                intent.putExtra("city_no", postMap.get(ctrNo));
                Log.e("cityList_no"+cityList_No, "city_no "+postMap.get(ctrNo));

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindDrawables(lv);
        System.gc();
    }

    public void cityInit(){
        City Seoul = new City(R.drawable.seoul,"서울");
        City Gapyoeng = new City(R.drawable.gapyeong,"가평");
        City Gangrueng = new City(R.drawable.gangrueng,"강릉");
        City Andong = new City(R.drawable.andong,"안동");
        City Jeonju = new City(R.drawable.jeonju,"전주");
        City Gyeongjoo = new City(R.drawable.gyeongju,"경주");
        City Busan = new City(R.drawable.busan,"부산");
        City Hadong = new City(R.drawable.hadong,"하동");
        City Tongyeong = new City(R.drawable.tongyeong,"통영");
        City Sooncheon = new City(R.drawable.sooncheon,"순천");
        City Boseong = new City(R.drawable.boseong,"보성");
        City Yeosoo = new City(R.drawable.yeosoo,"여수");

        city.add(Seoul);
        city.add(Gapyoeng);
        city.add(Gangrueng);
        city.add(Andong);
        city.add(Jeonju);
        city.add(Gyeongjoo);
        city.add(Busan);
        city.add(Hadong);
        city.add(Tongyeong);
        city.add(Sooncheon);
        city.add(Boseong);
        city.add(Yeosoo);

        Intent intent = getIntent();
        ArrayList<String> selCity= intent.getStringArrayListExtra("selCity");

        int cityLength = city.size();
        boolean[] chk = new boolean[cityLength];

        for(int i=0; i<selCity.size(); i++){
            for(int j=0; j<cityLength; j++) {
                if ( selCity.get(i).toString().equals(city.get(j).title) ) {   // 선택된 도시와 같은게 있는지 확인
                    chk[j] = true;
                }
            }
        }
        // 거꾸로 뒤에서부터 삭제
        int len = cityLength-1;
        for(int idx=0; idx<cityLength ; idx++){
            if( !chk[len-idx] ) {
                city.remove(len-idx);
            }
        }

        Preference pref = new Preference(this);
        SCHEDULE_NO = pref.getValue("prefSchNo","null");
        Log.e("prefSchNo",SCHEDULE_NO);

    }

    private void unbindDrawables(View view) {
        if (view == null)
            return;


        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(null);
        }
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
         //   ((BitmapDrawable)view.getBackground()).getBitmap().recycle();
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            view.setBackgroundResource(0);
            view.setBackgroundDrawable(null);
        }
    }

    public class HttpPostConnThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... path){
            // URL 연결이 구현될 부분
            URL url;
            String response = "";
            String CONNURL = path[0];
            String VALUE = path[1];
            HttpURLConnection conn = null;
            try {

                url = new URL(CONNURL);
                Log.e(LOG_TAG, CONNURL);
                Log.e(LOG_TAG, VALUE);

                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000); // 타임아웃: 10초
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream(); // 서버로 보내기 위한 출력 스트림
                os.write(VALUE.getBytes());
                os.flush();

                Log.e("http response code", conn.getResponseCode()+"");
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                    Log.e(LOG_TAG, "연결 성공");
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                    while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                        response += line;
                    }

                    br.close();
                    conn.disconnect();
                    Log.e("HttpPostConnThread", "The response is: " + response);
                }

            }catch (IOException e) {
                e.printStackTrace();
            }finally{
                conn.disconnect();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            if( ("").equals(result) )
                return;

            try {
                // cityVoList
                // The response is: {"noList":[{"no":3},{"no":4},{"no":5}]}
                JSONObject jsonObject = new JSONObject(result);
                JSONArray datas = jsonObject.getJSONArray("cityVoList");

                for(int i=0; i<datas.length(); i++){
                    JSONObject obj = datas.getJSONObject(i);

                    int no = obj.getInt("no");
                    String cityList_no = obj.getString("cityList_no");

                    postMap.put(cityList_no, no);
                }
                cityAdapter.notifyDataSetChanged();

            }catch (JSONException je){
                je.printStackTrace();
            }
        }

    }   // End_HttpPostConnThread


    public class HttpSendSchNoConnThread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path){
            // URL 연결이 구현될 부분
            URL url;
            String response = "";
            String CONNURL = path[0];
            String VALUE = "?schedule_no="+path[1];
            HttpURLConnection conn = null;
            try {

                url = new URL(CONNURL+VALUE);
                Log.e(LOG_TAG, CONNURL+VALUE);
                conn = (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(3000);

                conn.setDoInput(true);

                Log.e("http response code", conn.getResponseCode()+"");
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                    Log.e(LOG_TAG, "연결 성공");
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                    while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                        response += line;
                    }

                    br.close();
                    conn.disconnect();
                    Log.e("RESPONSE", "The response is: " + response);
                }

            }catch (IOException e) {
                e.printStackTrace();
            }finally{
                conn.disconnect();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // UI 업데이트가 구현될 부분
            if( ("").equals(result) )
                return;

            if( parsePinData(result) ){
                showResult();
            }

        }

    }   // End_HttpSendschNoConnThread

    protected boolean parsePinData(String myJSON){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            String result = jsonObj.getString("result");

            if( !result.equals("success") ){
                // fail 처리
                Log.e(LOG_TAG,"result fail");
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }// End_parsePinData

    private void showResult() {
        Intent i = new Intent(getApplicationContext(), OthersPlanActivity.class);
        i.putExtra("group", 1);
        startActivity(i);
    }

}