package com.example.estsoft.travelfriendflow2.lookaround;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.map.MapApiConst;
import com.example.estsoft.travelfriendflow2.mytravel.BookmarkListActivity;
import com.example.estsoft.travelfriendflow2.mytravel.MyTravelListActivity;

import net.daum.mf.map.api.MapView;

public class OthersPlanMapActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        mapView = (MapView)findViewById(R.id.map_view);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mapView.setHDMapTileEnabled(true);      // 고해상도 지도 타일 사용
//        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());   //구현한 CalloutBalloonAdapter 등록
//        mapView.setPOIItemEventListener(this);

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.header);
        relativeLayout.setVisibility(View.INVISIBLE);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.bottom_sheet);
        linearLayout.setVisibility(View.INVISIBLE);

        FloatingActionButton button = (FloatingActionButton)findViewById(R.id.fab);
        button.setVisibility(View.INVISIBLE);

    }

}

