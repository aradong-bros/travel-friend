package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;

import java.util.ArrayList;
import java.util.HashMap;


public class SelectCityActivity extends Activity {

    ArrayList<City> city = new ArrayList<City>();
    static int nSelectedItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

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


        final CityAdapter cityAdapter = new CityAdapter(getApplicationContext(),R.layout.city,city);
        ListView lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(cityAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

//                ImageView cover = (ImageView)adapterView.findViewById(R.id.cover);
//                cover.setVisibility(View.VISIBLE);
                cityAdapter.setChecked(position);
                cityAdapter.notifyDataSetChanged();
            }
        });
    }

}

class CityAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<City> city;
    LayoutInflater inf;

    private ViewHolder viewHolder = null;
    private boolean[] isCheckedConfirm;

    public CityAdapter(Context context, int layout, ArrayList<City> city){
        this.context = context;
        this.layout = layout;
        this.city = city;
        this.inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isCheckedConfirm = new boolean[city.size()];
    }

    public void setChecked(int position){
        isCheckedConfirm[position] = !isCheckedConfirm[position];
    }

    public ArrayList<Integer> getChecked(){
        int tempSize = isCheckedConfirm.length;
        ArrayList<Integer> mArrayList = new ArrayList<Integer>();
        for(int b=0; b<tempSize; b++){
            if(isCheckedConfirm[b]){
                mArrayList.add(b);
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

        View v = convertView;
        if(v == null){
            viewHolder = new ViewHolder();
            v = inf.inflate(layout, null);

            viewHolder.title = (TextView)convertView.findViewById(R.id.citytitle);
            viewHolder.icon = (ImageView)convertView.findViewById(R.id.cityicon);

            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }


        //            ImageView cover = (ImageView) convertView.findViewById(R.id.cover);
//            cover.setVisibility(View.VISIBLE);

        ImageView icon = (ImageView)convertView.findViewById(R.id.cityicon);
        TextView title = (TextView)convertView.findViewById(R.id.citytitle);

        City c = city.get(position);
        icon.setImageResource(c.icon);
        title.setText(c.title);


        return v;
    }
}

class City{
    String title = "";
    int icon;

    public City(int icon,String title){
        this.icon = icon;
        this.title = title;
    }

    public City(){}
}

class ViewHolder{
    ImageView icon;
    TextView title;
}