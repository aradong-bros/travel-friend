package com.example.estsoft.travelfriendflow2;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyTravelActivity extends TabActivity {

    ArrayList<Travel> tr = new ArrayList<Travel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytravel);

        ImageView imageView = (ImageView)findViewById(R.id.profile);
        Picasso.with(getApplicationContext()).load("https://scontent.xx.fbcdn.net/v/t1.0-9/12011354_171091463233969_4930354003965117617_n.jpg?oh=f14da56919c0cb11290d1427135cc785&oe=58293D19").transform(new CircleTransform()).into(imageView);

        //프레그먼트 탭
        TabHost mTab = getTabHost();
        TabHost.TabSpec spec;
        TabWidget tabWidget = mTab.getTabWidget();
        Intent intent;

        mTab.addTab(mTab.newTabSpec("tab1").setIndicator("나의 여행",getResources().getDrawable(R.drawable.lookaround)).setContent(new Intent(this,MyTravelListActivity.class)));
        mTab.addTab(mTab.newTabSpec("tab2").setIndicator("보관함",getResources().getDrawable(R.drawable.lookaround)).setContent(new Intent(this,BookmarkListActivity.class)));

        tr.add(new Travel("제목"));
        tr.add(new Travel("제목2"));
        tr.add(new Travel("제목3"));
        tr.add(new Travel("제목4"));


        ImageView newTravel = (ImageView)findViewById(R.id.newtravel);
        newTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NewTravelSettingActivity.class);
                startActivity(intent);
            }
        });

    }


    //다이얼로그

//    public void dialogTravelSetting(View view) {
//        LayoutInflater inflater = getLayoutInflater();
//        final View customView = inflater.inflate(R.layout.dialog_travelsetting, null);
//
//        new android.app.AlertDialog.Builder(this).
//                setTitle("여행 기본 설정").
//                setView(customView).
//                setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
////                        TextView tv = (TextView) customView.findViewById(R.id.password);
////                        String password = tv.getText().toString();
////                        Log.d("-------->", password);
//                    }
//                }).
//                show();
//    }
}

