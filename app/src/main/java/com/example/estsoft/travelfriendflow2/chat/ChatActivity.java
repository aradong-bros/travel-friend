package com.example.estsoft.travelfriendflow2.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Activity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);
        intent = new Intent(ChatActivity.this, ChatMainActivity.class);

        ImageView seoul = (ImageView)findViewById(R.id.seoul);
        ImageView gangwon = (ImageView)findViewById(R.id.gangwon);
        ImageView gyeongbook = (ImageView)findViewById(R.id.gyeongbook);
        ImageView jeonbook = (ImageView)findViewById(R.id.jeonbook);

        seoul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long regionNum = 1L;
                intent.putExtra("RegionNum",regionNum);
                startActivity(intent);
            }
        });

        gangwon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long regionNum = 2L;
                intent.putExtra("RegionNum",regionNum);
                startActivity(intent);
            }
        });

        gyeongbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long regionNum = 3L;
                intent.putExtra("RegionNum",regionNum);
                startActivity(intent);
            }
        });

        jeonbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long regionNum = 4L;
                intent.putExtra("RegionNum",regionNum);
                startActivity(intent);
            }
        });


        //String[] LIST_MENU = {"서울/경기(가평)","강원(강릉)/충북/충남","경북(안동,경주)/경남(부산,통영)","전북(전주)/전남(보성,순천,여수,하동)"};

//        Button rbt1 = (Button)findViewById(R.id.button_region_no1);
//        Button rbt2 = (Button)findViewById(R.id.button_region_no2);
//        Button rbt3 = (Button)findViewById(R.id.button_region_no3);
//        Button rbt4 = (Button)findViewById(R.id.button_region_no4);
//
//
//        rbt1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Long regionNum = 1L;
//                intent.putExtra("RegionNum",regionNum);
//                startActivity(intent);
//                //finish();
//                //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        rbt2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Long regionNum = 2L;
//                intent.putExtra("RegionNum",regionNum);
//                startActivity(intent);
//                //finish();
//                //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        rbt3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Long regionNum = 3L;
//                intent.putExtra("RegionNum",regionNum);
//                startActivity(intent);
//                //finish();
//                //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        rbt4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Long regionNum = 4L;
//                intent.putExtra("RegionNum",regionNum);
//                startActivity(intent);
//                //finish();
//                //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//
//    public void setRegionsss(View v){
//        int viewId = v.getId();
//        Long regionNum = 1L;
//        switch(viewId){
//            case R.id.button_region_no1:
//                regionNum = 1L;
//                break;
//            case R.id.button_region_no2:
//                regionNum = 2L;
//                break;
//            case R.id.button_region_no3:
//                regionNum = 3L;
//                break;
//            case R.id.button_region_no4:
//                regionNum = 4L;
//                break;
//        }
//        intent.putExtra("RegionNum",regionNum);
//        startActivity(intent);
//        finish();
//        return;
//        //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
     }




}
