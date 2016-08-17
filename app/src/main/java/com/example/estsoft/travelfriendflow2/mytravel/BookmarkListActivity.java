package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.lookaround.OthersPlanActivity;


import java.util.ArrayList;

public class BookmarkListActivity extends Activity {

    ArrayList<Travel> tr = new ArrayList<Travel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarklist);

//        tr.add(new Travel("겨"));
//        tr.add(new Travel("울"));
//        tr.add(new Travel("여"));
//        tr.add(new Travel("름"));
//        tr.add(new Travel("름"));
//        tr.add(new Travel("름"));

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

