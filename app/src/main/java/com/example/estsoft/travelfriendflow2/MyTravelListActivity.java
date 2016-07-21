package com.example.estsoft.travelfriendflow2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyTravelListActivity extends Activity {

    ArrayList<Travel> tr = new ArrayList<Travel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytravellist);


        tr.add(new Travel("제목"));
        tr.add(new Travel("제목2"));


        MyAdapter adapter = new MyAdapter(getApplicationContext(),R.layout.row,tr);
        ListView lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(adapter);
    }

}
