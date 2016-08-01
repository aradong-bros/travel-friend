package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;

import java.util.ArrayList;

public class SelectCityActivity extends Activity {

    ArrayList<City> city = new ArrayList<City>();

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
                City city = (City) cityAdapter.getItem(position);
                city.selected = !city.selected;
                cityAdapter.notifyDataSetChanged();

                if(city.selected){
                    Toast.makeText(getApplicationContext(),city.title+"담기 성공",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),city.title+"빼기 성공",Toast.LENGTH_SHORT).show();
                }

//                ImageView cover = (ImageView)adapterView.findViewById(R.id.cover);
//                cover.setVisibility(View.VISIBLE);
            }
        });

        Button letsgo = (Button)findViewById(R.id.letsgo);
        letsgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SelectedCityActivity.class);
                startActivity(intent);
            }
        });
    }

}

class CityAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<City> city;
    LayoutInflater inf;

    public CityAdapter(Context context, int layout, ArrayList<City> city){
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
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = inf.inflate(layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        City c = city.get(position);
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

class City{
    String title = "";
    int icon;
    boolean selected = false;

    public City(int icon,String title){
        this.icon = icon;
        this.title = title;
    }

    public City(){}
}