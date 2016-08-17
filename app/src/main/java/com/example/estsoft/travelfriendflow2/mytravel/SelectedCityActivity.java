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
import android.widget.Button;
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

        // 루트를 짜줘 버튼
        findViewById(R.id.btn_wholeComplete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "루트짜줘어어어", Toast.LENGTH_SHORT).show();
            }
        });



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



}