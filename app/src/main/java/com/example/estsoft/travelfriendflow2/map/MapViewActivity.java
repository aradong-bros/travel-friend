package com.example.estsoft.travelfriendflow2.map;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.example.estsoft.travelfriendflow2.R;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

/* 중심점 변경하기
 */

public class MapViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        MapView mapView = new MapView(this);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(35.8241706, 127.1480532),1, true); // 중심점 변경 + 줌 레벨 변경
        // !!!나중에 해당 지역의 값으로 중심값 변경하기!!!

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

    }
}
