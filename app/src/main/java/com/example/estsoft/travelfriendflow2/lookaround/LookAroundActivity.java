package com.example.estsoft.travelfriendflow2.lookaround;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;

import java.util.ArrayList;

public class LookAroundActivity extends Activity {
//1515
    //ㄴㅇㄹㄴㅇㄹ
    ArrayList<Travel> tr = new ArrayList<Travel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookaround);

        tr.add(new Travel("여행가쟈","2016/04/21","4박5일","여름",R.drawable.hadong));
        tr.add(new Travel("아라동 여행","2016/06/25","4박5일","여름",R.drawable.gyeongju));
        tr.add(new Travel("내일로 ㄱㄱ","2016/07/11","5박6일","여름",R.drawable.boseong));
        tr.add(new Travel("우정여행","2016/05/04","7박8일","여름",R.drawable.yeosoo));
        tr.add(new Travel("퇴근하고 싶당","2016/05/21","5박6일","여름",R.drawable.andong));
        tr.add(new Travel("여러븐 화이띵","2016/06/05","4박5일","여름",R.drawable.busan));

        MyAdapter adapter = new MyAdapter(getApplicationContext(),R.layout.row,tr);
        ListView lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(),"234",Toast.LENGTH_SHORT).show();
                String title = tr.get(i).getTitle();
                Intent intent = new Intent(getApplicationContext(),OthersPlanActivity.class);
                intent.putExtra("title",title);
                startActivity(intent);
            }
        });

    }

}

class MyAdapter extends BaseAdapter{
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
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = inf.inflate(layout, null);
        }

        TextView title = (TextView)convertView.findViewById(R.id.lookAroundTextBox);
        TextView txt_creationDate = (TextView)convertView.findViewById(R.id.txt_creationDate);
        TextView plan_time = (TextView)convertView.findViewById(R.id.plan_time);
        TextView plan_season = (TextView)convertView.findViewById(R.id.plan_season);
        LinearLayout background = (LinearLayout)convertView.findViewById(R.id.row_layout);

        Travel t = tr.get(position);
        title.setText(t.title);
        txt_creationDate.setText(t.txt_creationDate);
        plan_time.setText(t.planTime);
        plan_season.setText(t.planSeason);
        background.setBackgroundResource(t.background);
        return convertView;
    }
}

class Travel{
    String title = "";
    String txt_creationDate;
    String planTime;
    String planSeason;
    int background;

    public Travel(String title, String txt_creationDate, String planTime, String planSeason,int background){
        this.title = title;
        this.txt_creationDate = txt_creationDate;
        this.planTime = planTime;
        this.planSeason = planSeason;
        this.background = background;
    }

    public Travel(){}

    public String getTitle() {
        return title;
    }
}