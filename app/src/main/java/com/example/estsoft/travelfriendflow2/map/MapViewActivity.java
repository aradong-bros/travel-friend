package com.example.estsoft.travelfriendflow2.map;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.AttractionActivity;
import com.example.estsoft.travelfriendflow2.R;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/* 중심점 변경하기
 */

/**
 * Created by yeonji on 2016-07-29.
 * 해당 지역 선택 시 관련 MARKER 전부 보여주기
 */
public class MapViewActivity extends AppCompatActivity implements MapView.POIItemEventListener{
    private static final String LOG_TAG = "MapViewActivity";
    private static final String URL = "http://222.239.250.207:8080/TravelFriendAndroid/android/getPinData/9/";

    private static final String TAG_RESULTS="atrList";
    private static final String TAG_NO = "no";
    private static final String TAG_TITLE = "name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_CATEGORY ="category";
    private JSONArray datas = null;

    private MapView mapView;
    private HashMap<Integer, PinItem> mTagItemMap = new HashMap<Integer, PinItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        Spinner spinner = (Spinner)findViewById(R.id.spin_category);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(LOG_TAG,position+""+parent.getItemAtPosition(position));

                mapView.removeAllPOIItems(); /* 기존 검색 결과 삭제 */

                String path = null;
                if( position == 1 ){  /* eat */
                    path = URL+"1";
                }else if( position == 2 ){  /* stay */
                    path = URL+"2";
                }else if( position == 3 ){  /* play */
                    path = URL+"3";
                }else{
                    path = URL+"0";
                }
                // all은 기존 URL
                Log.e(LOG_TAG,path);
                fetchData(path);              /* 데이터 가져오고, 검색 결과 보여줌 */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mapView = (MapView)findViewById(R.id.map_view);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mapView.setHDMapTileEnabled(true);      // 고해상도 지도 타일 사용
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());   //구현한 CalloutBalloonAdapter 등록
        mapView.setPOIItemEventListener(this);

//        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(35.8241706, 127.1480532),5, true); // 중심점 변경 + 줌 레벨 변경
//        // !!!나중에 해당 지역의 값으로 중심값 변경하기!!!
//
//        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
//        mapViewContainer.addView(mapView);

        // --- DB에서 9(전주)지역의 값 가져오기
        fetchData(URL);

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Log.e(LOG_TAG, mapPOIItem.getItemName());

        Intent intent = new Intent(getApplicationContext(), AttractionActivity.class);
        intent.putExtra("no",mapPOIItem.getItemName());          // pk 넘기기
        startActivity(intent);

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            if (poiItem == null) return null;
            PinItem item = mTagItemMap.get(poiItem.getTag());

            if (item == null) return null;
            ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);
            Drawable drawable = createDrawableFromUrl(item.picture);
            if(drawable != null){
                imageViewBadge.setImageDrawable(drawable);
            }else{
                imageViewBadge.setImageResource(R.drawable.noimage);
            }

//            imageViewBadge.setImageDrawable(createDrawableFromUrl(item.picture));
            TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
            textViewTitle.setText(item.title);

            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }

    }

    public void fetchData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    java.net.URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }
                    return sb.toString().trim();
                }catch(Exception e){
                    return null;
                }

            }
            @Override
            protected void onPostExecute(String result){

                if(result == null){
                    return;
                }
                List<PinItem> itemList = parsePinData(result);

                if(itemList != null){
                    showResult(itemList);
                }

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    private void showResult(List<PinItem> itemList) {
        int padding = 10;
        float minZoomLevel = 3;
        float maxZoomLevel = 7;

        MapPointBounds mapPointBounds = new MapPointBounds();
        Log.e(LOG_TAG, "size:"+itemList.size());

        for (int i = 0; i < itemList.size(); i++) {
            PinItem item = itemList.get(i);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.no);
            poiItem.setTag(i);

            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);

            if( item.category.equals("inn")){
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.pin_stay);
            }else if( item.category.equals("food") ){
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.pin_eat);
            }else if( item.category.equals("tour") ){
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                 poiItem.setCustomImageResourceId(R.drawable.pin_play);
            }
//            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
//            poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
//            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
//            poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);
        }

        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding, minZoomLevel, maxZoomLevel));

        MapPOIItem[] poiItems = mapView.getPOIItems();
        if (poiItems.length > 0) {
            mapView.deselectPOIItem(poiItems[0]);       // 특정 POI Item을 선택 해제
        }
    }

    protected List<PinItem> parsePinData(String myJSON){
        List<PinItem> itemList = new ArrayList<PinItem>();

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            datas = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i = 0; i< datas.length(); i++){
                JSONObject object = datas.getJSONObject(i);

                PinItem pinItem = new PinItem();
                pinItem.no = object.getString(TAG_NO);
                pinItem.title = object.getString(TAG_TITLE);
                pinItem.picture = object.getString(TAG_PICTURE);

                String location = object.getString(TAG_LOCATION);
                String[] arr = location.split(",");
                pinItem.latitude = (arr[0] != null ? Double.parseDouble(arr[0]) : null );
                pinItem.longitude = (arr[1] != null ? Double.parseDouble(arr[1]) : null );

                pinItem.category = object.getString(TAG_CATEGORY);

                itemList.add(pinItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return itemList;
    }

    private Drawable createDrawableFromUrl(String url) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object fetch(String address) throws MalformedURLException,IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }

}
