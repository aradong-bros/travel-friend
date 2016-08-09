package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.example.estsoft.travelfriendflow2.R;

import java.util.ArrayList;

public class MyTravelListActivity extends Activity {
    private static final String LOG_TAG = "MyTravelListActivity";
    private SharedPreferences sharedPreferences;
    ArrayList<Travel> tr = new ArrayList<Travel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytravellist);

        // ----
        sharedPreferences = getSharedPreferences("MyBookMarkList",MODE_PRIVATE);
        String sdate = sharedPreferences.getString("sdate", "null");
        String edate = sharedPreferences.getString("edate", "null");
        String creationDate = sharedPreferences.getString("creationDate", "null");
        String title = sharedPreferences.getString("title","null");
      //  Log.e(LOG_TAG, sdate+"-"+edate+"-->"+creationDate+"-"+title);
        // ----

        tr.add(new Travel(title,creationDate));

//        tr.add(new Travel("제목"));
//        tr.add(new Travel("제목2"));

        MyAdapter adapter = new MyAdapter(getApplicationContext(),R.layout.row,tr);
        ListView lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(adapter);
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
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = inf.inflate(layout, null);
        }

        TextView title = (TextView)convertView.findViewById(R.id.lookAroundTextBox);
        TextView creationDate = (TextView)convertView.findViewById(R.id.txt_creationDate);
        Travel t = tr.get(position);
        title.setText(t.title);
        creationDate.setText(t.creationDate);

        return convertView;
    }
}

class Travel{
    String title = "";
    String creationDate = "";

    public Travel(String title, String creationDate){ this.title = title; this.creationDate = creationDate; }
    public Travel(String title){
        this.title = title;
    }
    public Travel(){}
}