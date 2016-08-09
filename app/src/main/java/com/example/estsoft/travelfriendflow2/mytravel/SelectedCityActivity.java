package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.map.MapViewActivity;
import com.example.estsoft.travelfriendflow2.map.PinItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

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
    private static final String URL = "http://222.239.250.207:8080/TravelFriendAndroid/android/getTravelRoot";
    private static final String URL2 = "http://222.239.250.207:8080/TravelFriendAndroid/android/object";
    private static final String URL3 = "http://222.239.250.207:8080/TravelFriendAndroid/android/list";
    private static final String URL4 = "http://222.239.250.207:8080/TravelFriendAndroid/android/array";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarklist);

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

        /**
         * ------------- SelectCityActivity -> SelectedCityActivity 값 가져와서 View에 뿌려주는 부분
         * */

    }

    @Override
    protected void onResume() {
        super.onResume();

        final CityAdapter cityAdapter = new CityAdapter(getApplicationContext(),R.layout.city,city);
        lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(cityAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(getApplicationContext(),MapViewActivity.class);

                City city = (City) cityAdapter.getItem(position);
                Log.e(LOG_TAG, city.title);
                int cityNo = -1;

                for(int i=0; i<cityMap.size(); i++)
                    cityNo = cityMap.get(city.title);

                if( cityNo != -1)
                    intent.putExtra("pos",cityNo);

                startActivity(intent);

              //  new HttpConnectionThread().execute(URL);     // Thread for Http connection

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindDrawables(lv);
        System.gc();
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


    public class HttpConnectionThread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path){
            // URL 연결이 구현될 부분
            URL url;
            String response = "";

            try {
                url = new URL(path[0]);
                Log.e(LOG_TAG, path[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000); // 타임아웃: 10초
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONArray jArray = new JSONArray();

                for(int i=0; i<5; i++){
                    JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                    sObject.put("no", i+"");
                    sObject.put("location", "1,1");
                    jArray.put(sObject);
                }
                Log.e(LOG_TAG, jArray.toString());

                OutputStream os = conn.getOutputStream(); // 서버로 보내기 위한 출력 스트림
                os.write(jArray.toString().getBytes());
                os.flush();


                Log.e("http response code", conn.getResponseCode()+"");
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                    Log.e(LOG_TAG, "연결 성공");
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                    while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                        Log.e("line",line);
                        response += line;
                    }

                    br.close();
                    conn.disconnect();
                    Log.e("RESPONSE", "The response is: " + response);
                }

            }catch (JSONException je){
                je.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // UI 업데이트가 구현될 부분
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }

    }


}