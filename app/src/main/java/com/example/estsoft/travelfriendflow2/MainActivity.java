package com.example.estsoft.travelfriendflow2;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //앱 최초 실행
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        int firstviewshow = pref.getInt("First", 0);

        if (firstviewshow != 1) {
            Intent showIntent = new Intent(this, FirstStartActivity.class);
            startActivity(showIntent);

        }

        //프레그먼트 탭
        TabHost mTab = getTabHost();
        TabHost.TabSpec spec;
        TabWidget tabWidget = mTab.getTabWidget();
        Intent intent;

        mTab.addTab(mTab.newTabSpec("tab1").setIndicator("둘러보기",getResources().getDrawable(R.drawable.lookaround)).setContent(new Intent(this,LookAroundActivity.class)));
        mTab.addTab(mTab.newTabSpec("tab2").setIndicator("기차 정보",getResources().getDrawable(R.drawable.lookaround)).setContent(new Intent(this,KorailActivity.class)));
        mTab.addTab(mTab.newTabSpec("tab3").setIndicator("실시간 톡",getResources().getDrawable(R.drawable.lookaround)).setContent(new Intent(this,ChatActivity.class)));
        mTab.addTab(mTab.newTabSpec("tab4").setIndicator("할인 정보",getResources().getDrawable(R.drawable.lookaround)).setContent(new Intent(this,SaleActivity.class)));
        mTab.addTab(mTab.newTabSpec("tab5").setIndicator("나의 여행",getResources().getDrawable(R.drawable.lookaround)).setContent(new Intent(this,MyTravelActivity.class)));

        //상단 바 터치 - 설정
        ImageView setting = (ImageView)findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
            }
        });


        //하단 탭 커스터마이징
       final TabWidget tw = (TabWidget)mTab.findViewById(android.R.id.tabs);
        for (int i = 0; i < tw.getChildCount(); ++i)
        {
            final View tabView = tw.getChildTabViewAt(i);
            final TextView tv = (TextView)tabView.findViewById(android.R.id.title);
            tv.setTextSize(8);
            tabView.setBackgroundColor(Color.parseColor("#eb9b00"));

            final TextView tvv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(this.getResources().getColorStateList(R.color.text_tab_indicator));

        }


    }

}

