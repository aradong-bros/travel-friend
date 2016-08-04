package com.example.estsoft.travelfriendflow2.lookaround;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.mytravel.BookmarkListActivity;

public class OthersPlanActivity extends TabActivity {


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
        t.setText(title);


        mTab.addTab(mTab.newTabSpec("tab1").setIndicator("지도로 보기",getResources().getDrawable(R.drawable.lookaround)).setContent(new Intent(this,OthersPlanMapActivity.class)));
        mTab.addTab(mTab.newTabSpec("tab2").setIndicator("계획표로 보기",getResources().getDrawable(R.drawable.lookaround)).setContent(new Intent(this,OthersPlanTableActivity.class)));

    }

}

