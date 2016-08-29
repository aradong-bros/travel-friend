package com.example.estsoft.travelfriendflow2.lookaround;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.basic.MainActivity;
import com.example.estsoft.travelfriendflow2.mytravel.BookmarkListActivity;
import com.example.estsoft.travelfriendflow2.mytravel.MyTravelActivity;
import com.example.estsoft.travelfriendflow2.thread.Preference;

/**
 * created by YeonJi on2016-08-11.
 * 1. 원하는 도시, 관광지 담은 후 schedule return 받은 것 화면에 출력.
 */

public class OthersPlanActivity extends TabActivity {
    private static final String LOG_TAG = "OthersPlanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_plan);

        //프레그먼트 탭
        TabHost mTab = getTabHost();
        TabHost.TabSpec spec;
        TabWidget tabWidget = mTab.getTabWidget();
        Intent intent;

        intent = getIntent();
        String title = intent.getStringExtra("title");
        TextView t = (TextView)findViewById(R.id.title);
        TextView btn_finish = (TextView)findViewById(R.id.btn_finish);

        int group = intent.getIntExtra("group", -1);    // 1:selectedA ,2: LookAroundA, 3:BookmarkListA, MyTravelListA
        int otherSchNo = intent.getIntExtra("otherSchNo", -1); // other user_sch no
        Log.e(LOG_TAG, "group----------------->"+group);
        Log.e(LOG_TAG, "otherSchNo------------------>"+otherSchNo);
        String otherTitle = intent.getStringExtra("title");

        Intent mapIntent = new Intent(getApplicationContext(),OthersPlanMapActivity.class);
        Intent tableIntent = new Intent(this,OthersPlanTableActivity.class);
        tableIntent.putExtra("schedule_no", otherSchNo);

        if( ("").equals(title) || group == 1 ){
            t.setVisibility(View.GONE);
            mapIntent.putExtra("group", 1);
            tableIntent.putExtra("group", 1);
            tableIntent.putExtra("schedule_no", otherSchNo);
        }else if( group == 2 ){
            t.setText(title);
            btn_finish.setVisibility(View.GONE);
            mapIntent.putExtra("group", 2);
            mapIntent.putExtra("otherSchNo",otherSchNo);
            tableIntent.putExtra("schedule_no", otherSchNo);
        }else if( group == 3 ){
            t.setText(otherTitle);
            btn_finish.setVisibility(View.GONE);
            mapIntent.putExtra("group", 3);
            mapIntent.putExtra("otherSchNo",otherSchNo);
            tableIntent.putExtra("schedule_no", otherSchNo);
        }else{
            Toast.makeText(getApplicationContext(), "group no error", Toast.LENGTH_SHORT).show();
        }


        mTab.addTab(mTab.newTabSpec("tab1").setIndicator("지도로 보기",getResources().getDrawable(R.drawable.lookaround)).setContent(mapIntent));
        mTab.addTab(mTab.newTabSpec("tab2").setIndicator("계획표로 보기",getResources().getDrawable(R.drawable.lookaround)).setContent(tableIntent));

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(i);
                finish();
            }
        });

    }

}

