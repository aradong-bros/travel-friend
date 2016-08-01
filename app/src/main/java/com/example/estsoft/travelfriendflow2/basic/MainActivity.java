package com.example.estsoft.travelfriendflow2.basic;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.os.Bundle;
import android.view.View;

import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.basic.FirstStartActivity;
import com.example.estsoft.travelfriendflow2.basic.SettingActivity;
import com.example.estsoft.travelfriendflow2.chat.ChatActivity;
import com.example.estsoft.travelfriendflow2.korail.KorailActivity;
import com.example.estsoft.travelfriendflow2.lookaround.LookAroundActivity;
import com.example.estsoft.travelfriendflow2.mytravel.MyTravelActivity;
import com.example.estsoft.travelfriendflow2.sale.SaleActivity;

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

        mTab.addTab(mTab.newTabSpec("tab1").setIndicator("",getResources().getDrawable(R.drawable.icon1)).setContent(new Intent(this,LookAroundActivity.class)));
        mTab.addTab(mTab.newTabSpec("tab2").setIndicator("",getResources().getDrawable(R.drawable.icon2)).setContent(new Intent(this,KorailActivity.class)));
        mTab.addTab(mTab.newTabSpec("tab3").setIndicator("",getResources().getDrawable(R.drawable.icon3)).setContent(new Intent(this,ChatActivity.class)));
        mTab.addTab(mTab.newTabSpec("tab4").setIndicator("",getResources().getDrawable(R.drawable.icon4)).setContent(new Intent(this,SaleActivity.class)));
        mTab.addTab(mTab.newTabSpec("tab5").setIndicator("",getResources().getDrawable(R.drawable.icon5)).setContent(new Intent(this,MyTravelActivity.class)));

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
            tabView.setBackgroundColor(Color.parseColor("#FFFFFF"));

            final TextView tvv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(this.getResources().getColorStateList(R.color.text_tab_indicator));

        }


    }

}

