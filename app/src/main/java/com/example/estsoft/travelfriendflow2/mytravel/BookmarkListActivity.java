package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.estsoft.travelfriendflow2.R;


import java.util.ArrayList;

public class BookmarkListActivity extends Activity {

    ArrayList<Travel> tr = new ArrayList<Travel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarklist);

        tr.add(new Travel("겨"));
        tr.add(new Travel("울"));
        tr.add(new Travel("여"));
        tr.add(new Travel("름"));
        tr.add(new Travel("름"));
        tr.add(new Travel("름"));

        MyAdapter adapter = new MyAdapter(getApplicationContext(),R.layout.row,tr);
        ListView lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(adapter);
    }

}

