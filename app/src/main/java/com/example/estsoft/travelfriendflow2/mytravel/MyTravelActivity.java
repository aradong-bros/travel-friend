package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.CircleTransform;
import com.example.estsoft.travelfriendflow2.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class MyTravelActivity extends TabActivity {

    ArrayList<Travel> tr = new ArrayList<Travel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytravel);
        String name="";
        String picture="";
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        try {
            JSONObject userData = new JSONObject(pref.getString("userData", ""));
            name = userData.getString("name");
            picture = userData.getString("picture");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView imageView = (ImageView)findViewById(R.id.profile);
        Picasso.with(getApplicationContext()).load(picture).transform(new CircleTransform()).into(imageView);
        ((TextView)findViewById(R.id.userId)).setText(name);

        //프레그먼트 탭
        TabHost mTab = getTabHost();
        TabHost.TabSpec spec;
        TabWidget tabWidget = mTab.getTabWidget();
        Intent intent;

        mTab.addTab(mTab.newTabSpec("tab1").setIndicator("나의 여행",getResources().getDrawable(R.drawable.lookaround)).setContent(new Intent(this,MyTravelListActivity.class)));
        mTab.addTab(mTab.newTabSpec("tab2").setIndicator("보관함",getResources().getDrawable(R.drawable.lookaround)).setContent(new Intent(this,BookmarkListActivity.class)));

//        tr.add(new Travel("제목"));
//        tr.add(new Travel("제목2"));
//        tr.add(new Travel("제목3"));
//        tr.add(new Travel("제목4"));


        ImageView newTravel = (ImageView)findViewById(R.id.newtravel);
        newTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NewTravelSettingActivity.class);
                startActivity(intent);
            }
        });

    }

}
