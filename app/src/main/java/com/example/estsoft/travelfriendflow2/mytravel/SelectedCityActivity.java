package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.map.MapViewActivity;

import java.util.ArrayList;
import java.util.HashMap;

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

    ArrayList<City> city = new ArrayList<City>();
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarklist);

        Intent intent = getIntent();
        ArrayList<String> selCity= intent.getStringArrayListExtra("selCity");
        for(int i=0; i<selCity.size(); i++){
            Log.e(LOG_TAG, selCity.get(i).toString());
        }
        // --> 선택된 애들로만 작업해주면 됨

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

        CityAdapter cityAdapter = new CityAdapter(getApplicationContext(),R.layout.city,city);
        lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(cityAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(),MapViewActivity.class);
               // Log.e(LOG_TAG, position+"");
                int num = (position+1);

                if( num == 5 ){
                    intent.putExtra("pos",9);
                }else if( num == 6 ){
                    intent.putExtra("pos",5);
                }else if( num == 7 ){
                    intent.putExtra("pos",6);
                }else if( num == 9 ){
                    intent.putExtra("pos",7);
                }else if( num == 10 ){
                    intent.putExtra("pos",11);
                }else if( num == 11 ){
                    intent.putExtra("pos",10);
                }else{
                    intent.putExtra("pos",position+1);
                }
                startActivity(intent);
            }
        });
        /*
            우선은 앞의 selectactivity의 선택된 값으로 XXX -> 나중에 화면 다 구현되면 이어서 하기
            서울은 0번부터 시작
            정이의 UI 순서와 DB의 순서가 안맞으므로 if문으로 조건 따짐!
         */

    }
    @Override
    protected void onPause() {
        super.onPause();
        unbindDrawables(lv);
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

