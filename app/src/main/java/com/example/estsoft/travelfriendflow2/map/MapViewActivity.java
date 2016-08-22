package com.example.estsoft.travelfriendflow2.map;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.AttractionActivity;
import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.thread.HttpConnectionThread;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yeonji on 2016-07-29.
 * 해당 지역 선택 시 관련 MARKER 전부 보여주기
 *
 * 08-19
 * 조회순으로 100위까지만 보여주기
 */
public class MapViewActivity extends AppCompatActivity implements MapView.POIItemEventListener{
    private static final String LOG_TAG = "MapViewActivity";
    private static final String URL = "http://222.239.250.207:8080/TravelFriendAndroid/android/getPinData/";
    private static final String POSTINSERTURL = "http://222.239.250.207:8080/TravelFriendAndroid/post/postInsert";

    private static final String TAG_RESULTS="atrList";
    private static final String TAG_NO = "no";
    private static final String TAG_TITLE = "name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_CATEGORY ="category";
    private JSONArray datas = null;

    private MapView mapView;
    private HashMap<Integer, PinItem> mTagItemMap = new HashMap<Integer, PinItem>();
    private TextView btn_complete;
    private HashMap<String, String> likeMap = new HashMap<String, String>();        //<postList_no, location>
    public static final int REQUEST_CODE = 1001;
    private static Intent attrIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        btn_complete = (TextView)findViewById(R.id.btn_selComplete);

        mapView = (MapView)findViewById(R.id.map_view);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mapView.setHDMapTileEnabled(true);      // 고해상도 지도 타일 사용
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());   //구현한 CalloutBalloonAdapter 등록
        mapView.setPOIItemEventListener(this);


        Intent intent = getIntent();
        final int cityList_no = intent.getIntExtra("cityList_no", -1);
        final int city_no = intent.getIntExtra("city_no", -1);

        if( cityList_no == -1 || city_no == -1 ){
            Log.e(LOG_TAG, "intent value error");
            Toast.makeText(getApplicationContext(),"intent value error",Toast.LENGTH_SHORT).show();
            return;
        }

       // fetchData(URL+pos);

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONArray jArray = new JSONArray();
                    Iterator<String> iterator = likeMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                        String key = (String) iterator.next();

                        sObject.put("city_no", city_no);
                        sObject.put("postList_no", key);
                        sObject.put("postOrder", "-1");
                        jArray.put(sObject);
                    }
                    Log.e(LOG_TAG, jArray.toString());
                    new HttpConnectionThread(getApplicationContext()).execute(POSTINSERTURL, jArray.toString());     // Thread for Http connection   // POST TABLE_INSERT

                    finish();
                }catch (JSONException je){
                    je.printStackTrace();
                }

            }
        });


        Spinner spinner = (Spinner)findViewById(R.id.spin_category);
        spinner.setSelection(0);    // default
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(LOG_TAG,position+""+parent.getItemAtPosition(position));

                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                mapView.removeAllPOIItems(); /* 기존 검색 결과 삭제 */

                String path = URL + cityList_no;
                if( position == 1 ){  /* eat */
                    path += "/1";
                }else if( position == 2 ){  /* stay */
                    path += "/2";
                }else if( position == 3 ){  /* play */
                    path += "/3";
                }else{
                    path += "/0";
                }
                // all은 기존 URL
                Log.e(LOG_TAG,path);
                fetchData(path);              /* 데이터 가져오고, 검색 결과 보여줌 */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Log.e(LOG_TAG, mapPOIItem.getItemName());

        attrIntent = new Intent(getApplicationContext(), AttractionActivity.class);
        attrIntent.putExtra("no",mapPOIItem.getItemName());          // postList_no(pk) 넘기기
        startActivityForResult(attrIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE) {   // 액티비티가 정상적으로 종료되었을 경우
            if(resultCode == RESULT_OK) {   // requestCode==1 로 호출한 경우에만 처리
                String getNo = data.getStringExtra("no");
                String getLoc = data.getStringExtra("location");

                if( getNo != null && getLoc != null ){
                    likeMap.put(getNo, getLoc);
                }

            }
        }

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    // CustomCalloutBalloon
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

            TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
            textViewTitle.setText(item.title);
//            textViewTitle.setText(poiItem.getItemName());

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
        int padding = 50;
        float minZoomLevel = 7;
        float maxZoomLevel = 10;

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

            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);
        }

        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding, minZoomLevel, maxZoomLevel));

        MapPOIItem[] poiItems = mapView.getPOIItems();
        if (poiItems.length > 0) {
            mapView.deselectPOIItem(poiItems[0]);       // 특정 POI Item 선택 해제
        }
    }

    protected List<PinItem> parsePinData(String myJSON){
        List<PinItem> itemList = new ArrayList<PinItem>();

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            datas = jsonObj.getJSONArray(TAG_RESULTS);
            Log.e("datas_length", datas.length()+"");

//            for(int i = 0; i<100; i++){     // 1위 ~ 100위
            for(int i = 0; i< datas.length(); i++){
                JSONObject object = datas.getJSONObject(i);

                PinItem pinItem = new PinItem();
                pinItem.no = object.getString(TAG_NO);
                pinItem.title = object.getString(TAG_TITLE);
                pinItem.picture = object.getString(TAG_PICTURE);

                String location = object.getString(TAG_LOCATION);
                String[] arr = location.split(",");

                pinItem.latitude = ( !("").equals(arr[0]) ? Double.parseDouble(arr[0]) : null );
                pinItem.longitude = ( !("").equals(arr[1]) ? Double.parseDouble(arr[1]) : null );

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
