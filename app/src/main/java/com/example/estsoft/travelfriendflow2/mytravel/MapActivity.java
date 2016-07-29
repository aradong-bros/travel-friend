package com.example.estsoft.travelfriendflow2.mytravel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.map.MapViewActivity;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity {
    ArrayList<City> city = new ArrayList<City>();
    private boolean chk = false;    // BottomSheetBehavior에서 사용

    ImageView map;
//    PhotoViewAttacher attacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map = (ImageView)findViewById(R.id.map);
//        Drawable bitmap = getResources().getDrawable(R.drawable.map);
//        map.setImageDrawable(bitmap);
//        attacher = new PhotoViewAttacher(map);

        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {



                // 화면 크기 받아오기
                DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
                int width = dm.widthPixels;
                int height = dm.heightPixels;
                Log.i("LOG-----", "width " + width + " height " + height);



                float ratioX = width/480;
                float ratioY = height/800;


                /*************************************************************
                 * 이 부분은 나중에 함수로 깔끔하게 정리할 예정입니당.
                 * 불편하더라도 우선 이렇게 보셔유 ^ ㅠ ^
                 * **************************************************************/


                //좌표 클릭
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    Log.i("LOG", "x "+x+" y "+y);
                    //서울
                    if(motionEvent.getX() >= 126*ratioX && motionEvent.getX() <= 179*ratioX && motionEvent.getY() >= 199*ratioY && motionEvent.getY() <=277*ratioY){
                        MyAdapterCity adapter = new MyAdapterCity(getApplicationContext(),R.layout.row,city);
                        ListView lv = (ListView)findViewById(R.id.citylist);
                        City Seoul = new City("서울");
                        boolean isExist = false;

                        for(City c:city){
                            if(c.getTitle() == "서울"){
                                isExist = true;
                            }
                        }

                        if(isExist == false){
                            city.add(Seoul);
                            adapter.notifyDataSetChanged();
                        }else{
                            for(int i = 0;i<city.size();i++){
                                if(city.get(i).getTitle().equals(Seoul.getTitle())) {
                                    city.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);
                    }

                    //가평
                    if(motionEvent.getX() >= 180*ratioX && motionEvent.getX()<= 230*ratioX  && motionEvent.getY() >= 128*ratioY && motionEvent.getY() <=242*ratioY){
                        MyAdapterCity adapter = new MyAdapterCity(getApplicationContext(),R.layout.row,city);
                        ListView lv = (ListView)findViewById(R.id.citylist);
                        City Gapyeong = new City("가평");
                        boolean isExist = false;

                        for(City c:city){
                            if(c.getTitle() == "가평"){
                                isExist = true;
                            }
                        }

                        if(isExist == false){
                            city.add(Gapyeong);
                            adapter.notifyDataSetChanged();
                        }else{
                            for(int i = 0;i<city.size();i++){
                                if(city.get(i).getTitle().equals(Gapyeong.getTitle())) {
                                    city.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);
                    }

                    //강릉
                    if(motionEvent.getX() >= 315*ratioX && motionEvent.getX()<= 390*ratioX  && motionEvent.getY() >= 100*ratioY && motionEvent.getY() <=220*ratioY){
                        MyAdapterCity adapter = new MyAdapterCity(getApplicationContext(),R.layout.row,city);
                        ListView lv = (ListView)findViewById(R.id.citylist);
                        City Gangreung = new City("강릉");
                        boolean isExist = false;

                        for(City c:city){
                            if(c.getTitle() == "강릉"){
                                isExist = true;
                            }
                        }

                        if(isExist == false){
                            city.add(Gangreung);
                            adapter.notifyDataSetChanged();
                        }else{
                            for(int i = 0;i<city.size();i++){
                                if(city.get(i).getTitle().equals(Gangreung.getTitle())) {
                                    city.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);
                    }

                    //안동
                    if(motionEvent.getX() >= 323*ratioX && motionEvent.getX()<= 402*ratioX  && motionEvent.getY()>= 410*ratioY  && motionEvent.getY() <=528*ratioY){
                        MyAdapterCity adapter = new MyAdapterCity(getApplicationContext(),R.layout.row,city);
                        ListView lv = (ListView)findViewById(R.id.citylist);
                        City Andong = new City("안동");
                        boolean isExist = false;

                        for(City c:city){
                            if(c.getTitle() == "안동"){
                                isExist = true;
                            }
                        }

                        if(isExist == false){
                            city.add(Andong);
                            adapter.notifyDataSetChanged();
                        }else{
                            for(int i = 0;i<city.size();i++){
                                if(city.get(i).getTitle().equals(Andong.getTitle())) {
                                    city.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);
                    }

                    //전주
                    if(motionEvent.getX() >= 135*ratioX && motionEvent.getX() <= 189*ratioX && motionEvent.getY()>= 613*ratioY  && motionEvent.getY() <=693*ratioY){

                        MyAdapterCity adapter = new MyAdapterCity(getApplicationContext(),R.layout.row,city);
                        ListView lv = (ListView)findViewById(R.id.citylist);

                        City Jeonju = new City("전주");
                        boolean isExist = false;

                        for(City c:city){
                            if(c.getTitle() == "전주"){
                                isExist = true;
                            }
                        }

                        if(isExist == false){
                            city.add(Jeonju);
                            chk = true;
                            adapter.notifyDataSetChanged();
                        }else{
                            for(int i = 0;i<city.size();i++){
                                if(city.get(i).getTitle().equals(Jeonju.getTitle())) {
                                    city.remove(i);
                                    chk = false;
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);
                    }

                    //경주
                    if(motionEvent.getX() >= 390*ratioX && motionEvent.getX() <= 468*ratioX && motionEvent.getY() >= 600*ratioY && motionEvent.getY() <=700*ratioY){
                        MyAdapterCity adapter = new MyAdapterCity(getApplicationContext(),R.layout.row,city);
                        ListView lv = (ListView)findViewById(R.id.citylist);
                        City Gyeongju = new City("경주");
                        boolean isExist = false;

                        for(City c:city){
                            if(c.getTitle() == "경주"){
                                isExist = true;
                            }
                        }

                        if(isExist == false){
                            city.add(Gyeongju);
                            adapter.notifyDataSetChanged();
                        }else{
                            for(int i = 0;i<city.size();i++){
                                if(city.get(i).getTitle().equals(Gyeongju.getTitle())) {
                                    city.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);
                    }

                    //부산
                    if(motionEvent.getX() >= 352*ratioX && motionEvent.getX() <= 444*ratioX && motionEvent.getY()>= 740*ratioY  && motionEvent.getY() <=828*ratioY){
                        MyAdapterCity adapter = new MyAdapterCity(getApplicationContext(),R.layout.row,city);
                        ListView lv = (ListView)findViewById(R.id.citylist);
                        City Busan = new City("부산");
                        boolean isExist = false;

                        for(City c:city){
                            if(c.getTitle() == "부산"){
                                isExist = true;
                            }
                        }

                        if(isExist == false){
                            city.add(Busan);
                            adapter.notifyDataSetChanged();
                        }else{
                            for(int i = 0;i<city.size();i++){
                                if(city.get(i).getTitle().equals(Busan.getTitle())) {
                                    city.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);
                    }

                    //하동
                    if(motionEvent.getX() >= 229*ratioX && motionEvent.getX()<= 296*ratioX  && motionEvent.getY()>= 754*ratioY && motionEvent.getY() <=919*ratioY){
                        MyAdapterCity adapter = new MyAdapterCity(getApplicationContext(),R.layout.row,city);
                        ListView lv = (ListView)findViewById(R.id.citylist);
                        City Hadong = new City("하동");
                        boolean isExist = false;

                        for(City c:city){
                            if(c.getTitle() == "하동"){
                                isExist = true;
                            }
                        }

                        if(isExist == false){
                            city.add(Hadong);
                            adapter.notifyDataSetChanged();
                        }else{
                            for(int i = 0;i<city.size();i++){
                                if(city.get(i).getTitle().equals(Hadong.getTitle())) {
                                    city.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);
                    }

                    //통영
                    if(motionEvent.getX() >= 305*ratioX && motionEvent.getX() <= 350*ratioX && motionEvent.getY() >= 878*ratioY && motionEvent.getY() <=934*ratioY){
                        MyAdapterCity adapter = new MyAdapterCity(getApplicationContext(),R.layout.row,city);
                        ListView lv = (ListView)findViewById(R.id.citylist);
                        City Tongyeong = new City("통영");
                        boolean isExist = false;

                        for(City c:city){
                            if(c.getTitle() == "통영"){
                                isExist = true;
                            }
                        }

                        if(isExist == false){
                            city.add(Tongyeong);
                            adapter.notifyDataSetChanged();
                        }else{
                            for(int i = 0;i<city.size();i++){
                                if(city.get(i).getTitle().equals(Tongyeong.getTitle())) {
                                    city.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);
                    }

                    //순천
                    if(motionEvent.getX() >= 169*ratioX && motionEvent.getX()<= 221*ratioX  && motionEvent.getY()>= 883*ratioY  && motionEvent.getY() <=943*ratioY){
                        MyAdapterCity adapter = new MyAdapterCity(getApplicationContext(),R.layout.row,city);
                        ListView lv = (ListView)findViewById(R.id.citylist);
                        City Sooncheon = new City("순천");
                        boolean isExist = false;

                        for(City c:city){
                            if(c.getTitle() == "순천"){
                                isExist = true;
                            }
                        }

                        if(isExist == false){
                            city.add(Sooncheon);
                            adapter.notifyDataSetChanged();
                        }else{
                            for(int i = 0;i<city.size();i++){
                                if(city.get(i).getTitle().equals(Sooncheon.getTitle())) {
                                    city.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);
                    }

                    //여수
                    if(motionEvent.getX() >= 205*ratioX && motionEvent.getX() <= 247*ratioX && motionEvent.getY() >= 945*ratioY && motionEvent.getY() <=1000*ratioY){
                        MyAdapterCity adapter = new MyAdapterCity(getApplicationContext(),R.layout.row,city);
                        ListView lv = (ListView)findViewById(R.id.citylist);
                        City Yeosoo = new City("여수");
                        boolean isExist = false;

                        for(City c:city){
                            if(c.getTitle() == "여수"){
                                isExist = true;
                            }
                        }

                        if(isExist == false){
                            city.add(Yeosoo);
                            adapter.notifyDataSetChanged();
                        }else{
                            for(int i = 0;i<city.size();i++){
                                if(city.get(i).getTitle().equals(Yeosoo.getTitle())) {
                                    city.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);
                    }

                    //보성
                    if(motionEvent.getX() >= 129*ratioX && motionEvent.getX()<= 184*ratioX  && motionEvent.getY() >= 951*ratioY && motionEvent.getY()<=1016*ratioY ){
                        MyAdapterCity adapter = new MyAdapterCity(getApplicationContext(),R.layout.row,city);
                        ListView lv = (ListView)findViewById(R.id.citylist);
                        City Boseong = new City("보성");
                        boolean isExist = false;

                        for(City c:city){
                            if(c.getTitle() == "보성"){
                                isExist = true;
                            }
                        }

                        if(isExist == false){
                            city.add(Boseong);
                            adapter.notifyDataSetChanged();
                        }else{
                            for(int i = 0;i<city.size();i++){
                                if(city.get(i).getTitle().equals(Boseong.getTitle())) {
                                    city.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);
                    }

                }
                return false;
            }


        });

        // The View with the BottomSheetBehavior
        View bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) { // React to state change
//                Log.e("onStateChanged",newState+"");

                // open - 3, close - 4
                if(newState == 3){
//                    Log.e("onStateChanged","open");
                    if(chk){
                        final TextView title = (TextView)bottomSheet.findViewById(R.id.lookAroundTextBox);
                        title.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e("texttext",title.getText().toString());
                                Intent intent = new Intent(getApplicationContext(), MapViewActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }else if(newState == 4){
//                    Log.e("onStateChanged","close");
                }

            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { // React to dragging events

                // Log.e("onSlide",slideOffset+"");
            }
        });

    }

//    attacher.update();


}

class MyAdapterCity extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<City> city;
    LayoutInflater inf;

    public MyAdapterCity(Context context, int layout, ArrayList<City> city){
        this.context = context;
        this.layout = layout;
        this.city = city;
        this.inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return city.size();
    }

    @Override
    public Object getItem(int position){
        return city.get(position);
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

        TextView title = (TextView)convertView.findViewById(R.id.lookAroundTextBox);

        City c = city.get(position);
        title.setText(c.title);
        return convertView;
    }
}

class City{
    String title = "";
    public City(String title){
        this.title = title;
    }
    public String getTitle(){return title;}
    public City(){}

}
