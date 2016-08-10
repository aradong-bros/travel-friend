package com.example.estsoft.travelfriendflow2.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Activity {

    Intent intent;
    Button bt;

    ArrayList<EachCity> city = new ArrayList<EachCity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);
        intent = new Intent(ChatActivity.this, ChatMainActivity.class);

        EachCity Seoul = new EachCity(R.drawable.seoul, "서울/경기(가평)",1L);
        EachCity Gangrueng = new EachCity(R.drawable.gangrueng,"강원(강릉)/충북/충남",2L);
        EachCity Gyeongjoo = new EachCity(R.drawable.gyeongju,"경북(안동,경주)/경남(부산,통영)",3L);
        EachCity Jeonju = new EachCity(R.drawable.jeonju,"전북(전주)/전남(보성,순천,여수,하동)",4L);

        city.add(Seoul);
        city.add(Gangrueng);
        city.add(Gyeongjoo);
        city.add(Jeonju);


        //String[] LIST_MENU = {"서울/경기(가평)","강원(강릉)/충북/충남","경북(안동,경주)/경남(부산,통영)","전북(전주)/전남(보성,순천,여수,하동)"};
        ListView listView = (ListView)this.findViewById(R.id.listViewaaa);

        final CityAdapter cityAdapter = new CityAdapter(getApplicationContext(),R.layout.city,city);
        listView.setAdapter(cityAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EachCity city = (EachCity)cityAdapter.getItem(position);
                intent.putExtra("RegionNum",city.getRegionNum());
                startActivity(intent);
            }
        });

//        Button rbt1 = (Button)findViewById(R.id.button_region_no1);
//        Button rbt2 = (Button)findViewById(R.id.button_region_no2);
//        Button rbt3 = (Button)findViewById(R.id.button_region_no3);
//        Button rbt4 = (Button)findViewById(R.id.button_region_no4);
//
//
//        rbt1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Long regionNum = 1L;
//                intent.putExtra("RegionNum",regionNum);
//                startActivity(intent);
//                //finish();
//                //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        rbt2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Long regionNum = 2L;
//                intent.putExtra("RegionNum",regionNum);
//                startActivity(intent);
//                //finish();
//                //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        rbt3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Long regionNum = 3L;
//                intent.putExtra("RegionNum",regionNum);
//                startActivity(intent);
//                //finish();
//                //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        rbt4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Long regionNum = 4L;
//                intent.putExtra("RegionNum",regionNum);
//                startActivity(intent);
//                //finish();
//                //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//
//    public void setRegionsss(View v){
//        int viewId = v.getId();
//        Long regionNum = 1L;
//        switch(viewId){
//            case R.id.button_region_no1:
//                regionNum = 1L;
//                break;
//            case R.id.button_region_no2:
//                regionNum = 2L;
//                break;
//            case R.id.button_region_no3:
//                regionNum = 3L;
//                break;
//            case R.id.button_region_no4:
//                regionNum = 4L;
//                break;
//        }
//        intent.putExtra("RegionNum",regionNum);
//        startActivity(intent);
//        finish();
//        return;
//        //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
     }


    public class CityAdapter extends BaseAdapter {
        Context context;
        int layout;
        ArrayList<EachCity> city;
        LayoutInflater inf;

        private ViewHolder viewHolder = null;
        private boolean[] isCheckedConfirm;

        public CityAdapter(Context context, int layout, ArrayList<EachCity> city){
            this.context = context;
            this.layout = layout;
            this.city = city;
            this.inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.isCheckedConfirm = new boolean[city.size()];
        }

        public void setChecked(int position){
            isCheckedConfirm[position] = !isCheckedConfirm[position];
        }

        public ArrayList<EachCity> getChecked(){
            int tempSize = isCheckedConfirm.length;
            ArrayList<EachCity> mArrayList = new ArrayList<EachCity>();
            for(int b=0; b<tempSize; b++){
                EachCity city = new EachCity();
                if(isCheckedConfirm[b]){
                    mArrayList.add(city);
                }
            }
            return mArrayList;
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

            ViewHolder viewHolder;

            if(convertView == null){
                convertView = inf.inflate(layout, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            EachCity c = city.get(position);
            viewHolder.icon.setImageResource(c.icon);
            viewHolder.title.setText(c.title);
            viewHolder.cover.setVisibility(c.selected? View.VISIBLE : View.INVISIBLE);

            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView title;
            ImageView cover;

            public ViewHolder(View view) {
                icon = (ImageView)view.findViewById(R.id.cityicon);
                title = (TextView)view.findViewById(R.id.citytitle);
                cover = (ImageView)view.findViewById(R.id.cover);
            }
        }
    }

    public class EachCity{
        String title = "";
        Long regionNum;

        public String getTitle() {
            return title;
        }

        int icon;
        boolean selected;

        public EachCity(){}

        public Long getRegionNum() {
            return regionNum;
        }

        public EachCity(int icon, String title, Long regionNum){
            this.icon = icon;
            this.title = title;

            this.regionNum = regionNum;
        }

        //public EachCity(){}
    }

}
