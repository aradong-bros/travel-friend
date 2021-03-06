package com.example.estsoft.travelfriendflow2.lookaround;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.estsoft.travelfriendflow2.AttractionActivity;
import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.basic.MainActivity;
import com.example.estsoft.travelfriendflow2.map.CustomSchemeURL;
import com.example.estsoft.travelfriendflow2.map.MapApiConst;
import com.example.estsoft.travelfriendflow2.map.PinItem;
import com.example.estsoft.travelfriendflow2.map.PostItem;
import com.example.estsoft.travelfriendflow2.thread.Preference;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class OthersPlanMapActivity extends AppCompatActivity implements MapView.POIItemEventListener{
    private static final String LOG_TAG = "OthersPlanMapActivity";
    private static String SCHEDULE_CITY_URL = "http://222.239.250.207:8080/TravelFriendAndroid/android/selectCityListBySchedule";   // 해당 스케쥴의 도시 순서 출력
    private static String SCHEDULE_POST_URL = "http://222.239.250.207:8080/TravelFriendAndroid/android/selectPostListBySchedule";   // 해당 스케쥴의 관광지 순서 출력
    private static String SCHEDULE_NO = "";

    private static final String CITY_TAG_RESULTS="cityListBySchedule";
    private static final String POST_TAG_RESULTS="postListBySchedule";
    private static final String ATTR_TAG_RESULTS="atrListByPost";
    private static final String TAG_NO="no";    // city_no, post_no
    private static final String TAG_SCHNO="schedule_no";
    private static final String TAG_CITYNO="cityList_no";
    private static final String TAG_STATUS="status";
    private static final String TAG_CITYORDER="cityOrder";

    private MapView mapView;
    private HashMap<Integer, PostItem> mTagItemMap = new HashMap<Integer, PostItem>();
    private HashMap<Integer, MapPolyline> mTagPolylineMap = new HashMap<Integer, MapPolyline>();
    private int polylineChkNum = 0;
    private SendMassgeHandler mMainHandler = null;

    private RelativeLayout layout_route;
    private TextView txt_routeStart, txt_routeEnd;
    private Button btn_routeLeft,btn_routeRight,btn_routeTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        mapView = (MapView)findViewById(R.id.map_view);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mapView.setHDMapTileEnabled(true);      // 고해상도 지도 타일 사용
        mapView.setPOIItemEventListener(this);
//        mapView.setMapViewEventListener(this);
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.header);
        relativeLayout.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.bottom_sheet);
        linearLayout.setVisibility(View.INVISIBLE);
        FloatingActionButton button = (FloatingActionButton)findViewById(R.id.fab);
        button.setVisibility(View.INVISIBLE);

        // selectedcity activity에서 넘어온 경우만 밑의 과정 처리
        Intent intent = getIntent();
        int group = intent.getIntExtra("group", -1);
        int otherSchNo = intent.getIntExtra("otherSchNo",-1);

        /** 1:selectedA ,2: LookAroundA,BookmarkListA, 3: MyTravelListA */
        if ( group == 1 ){
            Preference pref = new Preference(this);
            SCHEDULE_NO = pref.getValue("prefSchNo","null");
            Log.e(LOG_TAG,"schNo"+SCHEDULE_NO);
        }else if ( group == 2 || group == 3 ) {
            SCHEDULE_NO = otherSchNo != -1 ? otherSchNo + "" : null;
            Log.e(LOG_TAG,"schNo"+SCHEDULE_NO);
        }else{
            SCHEDULE_NO = null;
            Log.e(LOG_TAG, " group == -1 ");
        }
        // -----------------------------------------------------

        if( TextUtils.isEmpty(SCHEDULE_NO) ){
            Toast toast = Toast.makeText(getApplicationContext(), "값을 불러오는 과정에서 문제가 생겼습니다.\n 다시 시도해 주세요.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            return;
        }

        if( !("{}").equals(SCHEDULE_NO) ){
            new HttpParamConnThread().execute(SCHEDULE_CITY_URL, SCHEDULE_NO);
        }else {
            Log.e("XXX", "진입못함");
        }

        mMainHandler = new SendMassgeHandler(); // 메인 핸들러 생성

        layout_route = (RelativeLayout)findViewById(R.id.layout_route);
        txt_routeStart = (TextView)findViewById(R.id.txt_routeStart);
        txt_routeEnd = (TextView)findViewById(R.id.txt_routeEnd);
        btn_routeLeft = (Button)findViewById(R.id.btn_routeLeft);
        btn_routeRight = (Button)findViewById(R.id.btn_routeRight);
        btn_routeTracking = (Button)findViewById(R.id.btn_routeTracking);

        btn_routeLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click","left  "+polylineChkNum);

                if( polylineChkNum >= 1 && polylineChkNum <= mTagPolylineMap.size() ){
                    if ( polylineChkNum != 1) {
                        polylineChkNum--;
                    }
                    setTextView();

                    Message msg = new Message();
                    msg.what = 0;
                    msg.arg1 = 100+polylineChkNum;
                    mMainHandler.sendMessage(msg);
                }

            }
        });


        btn_routeRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click","right  "+polylineChkNum);
                if( polylineChkNum <= mTagPolylineMap.size() && polylineChkNum >= 0){
                    if( mTagPolylineMap.size() == 0 ){      // 핀이 1개인 경우
                        return ;
                    }

                    if ( polylineChkNum != mTagPolylineMap.size()){
                        polylineChkNum++;
                    }
                    setTextView();

                    Message msg = new Message();
                    msg.what = 0;
                    msg.arg1 = 100+polylineChkNum;
                    mMainHandler.sendMessage(msg);
                }


            }
        });


        btn_routeTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( polylineChkNum <= 0 || polylineChkNum > mTagPolylineMap.size()){
                    return;
                }
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = 100+polylineChkNum;
                mMainHandler.sendMessage(msg);
            }
        });

    }

    private void setTextView() {
        String strStart = "", strEnd = "";    // 출발지 + 도착지

        strStart = mTagItemMap.get(polylineChkNum).getTitle();
        strEnd = mTagItemMap.get(polylineChkNum+1).getTitle();

        txt_routeStart.setText(strStart);
        txt_routeEnd.setText(strEnd);
    }

    private Intent onRoute(String startPoint, String endPoint){
        String urlShceme = "daummaps://route?";
        String sp = "sp="+startPoint;
        String ep = "&ep="+endPoint;
        String by = "&by="+"CAR";              //  > by: 교통 수단 (CAR, PUBLICTRANSIT, FOOT)

        String url = urlShceme+sp+ep+by;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        CustomSchemeURL daummap = new CustomSchemeURL(this, intent) {
            @Override
            public boolean canOpenDaummapURL() {
                return super.canOpenDaummapURL();
            }
        };

        if(daummap.existDaummapApp()){
            return intent;
        } else {
            CustomSchemeURL.openDaummapDownloadPage(this);
        }
        return null;
    }

    // Handler 클래스
    class SendMassgeHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("arg1111",msg.arg1+"");

            switch (msg.what) {
                case 1:
                    MapPolyline p = mTagPolylineMap.get(msg.arg1);
                    MapPoint[] pnt = p.getMapPoints();
                    String startPoint = "";
                    String endPoint = "";

                    for(int i=0; i<pnt.length; i++){
                        Log.e("polyline_getPoint"+i, pnt[i].getMapPointGeoCoord().latitude+","+pnt[i].getMapPointGeoCoord().longitude);
                    }

                    if ( pnt.length == 2 ){
                        startPoint = pnt[0].getMapPointGeoCoord().latitude+","+pnt[0].getMapPointGeoCoord().longitude;
                        endPoint = pnt[1].getMapPointGeoCoord().latitude+","+pnt[1].getMapPointGeoCoord().longitude;
                    }

                    // 길찾기로 넘어가게
                    Intent intent = onRoute(startPoint, endPoint);

                    if(intent!=null)
                        startActivity(intent);

                    break;

                default:
                    final MapPolyline polyline = mTagPolylineMap.get(msg.arg1);   // 101부터 시작
                    polyline.setLineColor(Color.argb(255, 255, 187, 0));

                    for(int i = 0; i<3; i++) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mapView.addPolyline(polyline);
                                try {
                                    Thread.sleep(200);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                mapView.removePolyline(polyline);
                                try {
                                    Thread.sleep(200);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }); // runOnUiThread_end
                    }
                    break;
            }
        }

    };

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.title_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            if (poiItem == null) return null;

            ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());
//            mCalloutBalloon.setX(100.0f);
//            mCalloutBalloon.setY(50.0f);
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }


    public class HttpParamConnThread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path){
            // URL 연결이 구현될 부분
            URL url;
            String response = "";
            String CONNURL = path[0];
            String VALUE = path[1];
            HttpURLConnection conn = null;
            try {
                url = new URL(CONNURL+"/"+VALUE);
                Log.e(LOG_TAG, CONNURL+"/"+VALUE);
                conn = (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                conn.setDoInput(true);

                int responseCode = conn.getResponseCode();
                Log.e("http response code", responseCode+"");

                if ( responseCode == HttpURLConnection.HTTP_OK ) { // 연결에 성공한 경우
                    Log.e(LOG_TAG, "연결 성공");
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                    while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                        response += line;
                    }

                    br.close();
                    conn.disconnect();
                    Log.e("RESPONSE", "The response is: " + response);
                    return response;

                }else{
                    Log.e(LOG_TAG, "서버 접속 실패");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                        }
                    });
                    //  로딩바 띄우기
                }

            }catch (ConnectTimeoutException ue){
                Log.e(LOG_TAG, "ConnectTimeoutException");
            }catch (IOException e) {
                e.printStackTrace();
            }finally{
                conn.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if( ("").equals(result) || TextUtils.isEmpty(result) ) { //  로딩바 띄우기
                Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                return;
            }
            Log.e("ㅁㅁㅁ",result);
            String str = result.substring(2,4);     // city or post
            if( ("ci").equals(str) ){
                List<PinItem> itemList = parsePinData(result);

                if( itemList != null )
                    makePOIItem(itemList);
                else
                    Toast.makeText(getApplicationContext(), "itemList == null", Toast.LENGTH_SHORT).show();
            }else if( ("at").equals(str) ){
                List<PostItem> itemList =  parsePostPinData(result);

                if( itemList != null) {
                    // layout visible
                    layout_route.setVisibility(View.VISIBLE);
                    txt_routeStart.setVisibility(View.VISIBLE);
                    txt_routeEnd.setVisibility(View.VISIBLE);
                    btn_routeLeft.setVisibility(View.VISIBLE);
                    btn_routeRight.setVisibility(View.VISIBLE);
                    btn_routeTracking.setVisibility(View.VISIBLE);

                    makePostPOIItem(itemList);
                }else
                    Toast.makeText(getApplicationContext(), "postitemList == null", Toast.LENGTH_SHORT).show();
            }


        }

    }   // End_HttpParamConnThread

    protected List<PostItem> parsePostPinData(String myJSON){
        List<PostItem> itemList = new ArrayList<PostItem>();

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray postDatas = jsonObj.getJSONArray(POST_TAG_RESULTS);
            JSONArray attrDatas = jsonObj.getJSONArray(ATTR_TAG_RESULTS);

            for(int i = 0; i< postDatas.length(); i++){
                JSONObject postObject = postDatas.getJSONObject(i);
                JSONObject attrObject = attrDatas.getJSONObject(i);

                PostItem item = new PostItem();
                item.no = postObject.getString("no");      // post_table_no(pk)
                item.city_no = postObject.getString("city_no");
                item.postOrder = postObject.getString("postOrder");

                if( !postObject.getString("postList_no").equals(attrObject.getString("no")) ){
                    return null;
                }
                item.postList_no = postObject.getString("postList_no");
                item.title = attrObject.getString("name");

                String location = attrObject.getString("location");
                String[] arr = location.split(",");

                item.latitude = ( !("").equals(arr[0]) ? Double.parseDouble(arr[0]) : null );
                item.longitude = ( !("").equals(arr[1]) ? Double.parseDouble(arr[1]) : null );

                item.picture = attrObject.getString("picture");
                item.info = attrObject.getString("info");
                item.category = attrObject.getString("category");
                item.address = attrObject.getString("address");

                itemList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return itemList;
    }// End_parsePostPinData


    protected List<PinItem> parsePinData(String myJSON){
        List<PinItem> itemList = new ArrayList<PinItem>();

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray datas = jsonObj.getJSONArray(CITY_TAG_RESULTS);

            for(int i = 0; i< datas.length(); i++){
                JSONObject object = datas.getJSONObject(i);

                PinItem item = new PinItem();
                item.city_pkno = object.getInt(TAG_NO);             // city_table_no (pk)
                item.no = object.getString(TAG_CITYNO);
                item.order =  object.getInt(TAG_CITYORDER);       // 1 ~ datas.length()
                item.status = object.getString(TAG_STATUS);

                itemList.add(settingInfo(item));                  // title, position setting
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return itemList;
    }// End_parsePinData



    private void makePOIItem(List<PinItem> itemList) {          // city POIItem
        MapPointBounds mapPointBounds = new MapPointBounds();
//        MapPOIItem[] mapPOIItems = new MapPOIItem[itemList.size()];

        for(int i=0; i<itemList.size(); i++){
            PinItem item = itemList.get(i);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.title);
            poiItem.setTag(item.city_pkno);
            poiItem.setUserObject(item.no);     // 도시 테이블 no

            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);

            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            if( item.getStatus().equals("start") || item.getOrder() == 1 ){
                poiItem.setCustomImageResourceId(R.drawable.pin_start);
            }else if( item.getStatus().equals("end") ){
                if( item.getOrder().equals("1") )
                    poiItem.setCustomImageResourceId(R.drawable.pin_none);

                poiItem.setCustomImageResourceId(R.drawable.pin_end);
            }else{
                poiItem.setCustomImageResourceId(R.drawable.pin_none);
            }
            poiItem.setCustomImageAutoscale(false);               // 마커 크기 큰 상태!
            poiItem.setCustomImageAnchor(0.5f, 1.0f);
//            poiItem.setShowAnimationType(MapPOIItem.ShowAnimationType.DropFromHeaven);

            mapView.addPOIItem(poiItem);
//            mTagItemMap.put(poiItem.getTag(), item);

            // ------- polyline 연결하기
            if( i== 0 ){
                continue;
            }
            MapPolyline polyline = new MapPolyline();
            polyline.setTag(i);             // 도시 polyline은 1 ~ 99로 tag
            polyline.setLineColor(Color.argb(255, 255, 0, 0)); // Polyline 컬러 지정

            // Polyline 좌표 지정.
            polyline.addPoint(MapPoint.mapPointWithGeoCoord( itemList.get(i-1).getLatitude() , itemList.get(i-1).getLongitude() ));  // i-1 item
            polyline.addPoint(MapPoint.mapPointWithGeoCoord( itemList.get(i).getLatitude() , itemList.get(i).getLongitude() ));      // i item

            // Polyline 지도에 올리기.
            mapView.addPolyline(polyline);

        }
//        mapPOIItems = mapView.getPOIItems();
        MapPolyline[] mapPolylines = mapView.getPolylines();

//        showAll(mapPointBounds);
        showTrackingAll(mapPointBounds, mapPolylines);

    }

    private void showTrackingAll(final MapPointBounds mapPointBounds, final MapPolyline[] mapPolylines) {
        int padding = 250;
        float minZoomLevel = 5;
        float maxZoomLevel = 12;

        mapView.removeAllPolylines();
        for(int i = 0; i<mapPolylines.length; i++){
            mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding, minZoomLevel, maxZoomLevel));

//            Log.e("mapPolylines[i]", mapPolylines[i].getTag()+"");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(700);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }); // runOnUiThread_end

            mapView.addPolyline(mapPolylines[i]);

        }

    }

    private void makePostPOIItem(List<PostItem> itemList) {     //  관광지 POIItem
        MapPointBounds mapPointBounds = new MapPointBounds();

        for(int i=0; i<itemList.size(); i++){
            PostItem item = itemList.get(i);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.title);
            poiItem.setTag(Integer.parseInt(item.no));
            poiItem.setUserObject(item.getPostList_no());

            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);

            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            if( itemList.size() == 1 ){         // item이 1개인 경우
                Log.e("itemList_size", "1");
                switch ( item.getCategory() ){
                    case "tour":
                        poiItem.setCustomImageResourceId(R.drawable.pin_play);
                        break;
                    case "inn":
                        poiItem.setCustomImageResourceId(R.drawable.pin_stay);
                        break;
                    case "food":
                        poiItem.setCustomImageResourceId(R.drawable.pin_eat);
                        break;
                }
            }else if( item.getPostOrder().equals("1") ){    // item이 2개 이상이며, postOrder 값이 1인 경우
                switch ( item.getCategory() ){
                    case "tour":
                        poiItem.setCustomImageResourceId(R.drawable.pin_play_start);
                        break;
                    case "inn":
                        poiItem.setCustomImageResourceId(R.drawable.pin_stay_start);
                        break;
                    case "food":
                        poiItem.setCustomImageResourceId(R.drawable.pin_eat_start);
                        break;
                }
            }else if( item.getPostOrder().equals(itemList.size()+"") ){// item이 2개 이상이며, postOrder 값이 끝인 경우
                switch ( item.getCategory() ){
                    case "tour":
                        poiItem.setCustomImageResourceId(R.drawable.pin_play_end);
                        break;
                    case "inn":
                        poiItem.setCustomImageResourceId(R.drawable.pin_stay_end);
                        break;
                    case "food":
                        poiItem.setCustomImageResourceId(R.drawable.pin_eat_end);
                        break;
                }
            }else{
                switch ( item.getCategory() ){  // item이 2개 이상이며, 그 외의 경우
                    case "tour":
                        poiItem.setCustomImageResourceId(R.drawable.pin_play);
                        break;
                    case "inn":
                        poiItem.setCustomImageResourceId(R.drawable.pin_stay);
                        break;
                    case "food":
                        poiItem.setCustomImageResourceId(R.drawable.pin_eat);
                        break;
                }
            }
            poiItem.setCustomImageAutoscale(false);               // 마커 크기 큰 상태!
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mapView.addPOIItem(poiItem);
            mTagItemMap.put(Integer.parseInt(item.postOrder), item);        // post order 순서대로 item 저장!!!
//            mTagItemMap.put(poiItem.getTag(), item);
//            Log.e("poiItem.getTag()", poiItem.getTag()+"");
            // ------- polyline 연결하기
            if( i== 0 ){
                continue;
            }
            MapPolyline polyline = new MapPolyline();
            polyline.setTag(100+i);                 // 관광지 polyline은 101 ~ 으로 tag
            polyline.setLineColor(Color.argb(255, 1, 0, 255)); // Polyline 컬러 지정

            // Polyline 좌표 지정.
            polyline.addPoint(MapPoint.mapPointWithGeoCoord( itemList.get(i-1).getLatitude() , itemList.get(i-1).getLongitude() ));  // i-1 item
            polyline.addPoint(MapPoint.mapPointWithGeoCoord( itemList.get(i).getLatitude() , itemList.get(i).getLongitude() ));      // i item

            // Polyline 지도에 올리기.
            mapView.addPolyline(polyline);
            mTagPolylineMap.put(polyline.getTag(), polyline);
        }

        MapPolyline[] mapPolylines = mapView.getPolylines();
        showTrackingPostAll(mapPointBounds, mapPolylines);

//        showAll(mapPointBounds);

    }

    private void showTrackingPostAll(final MapPointBounds mapPointBounds, final MapPolyline[] mapPolylines) {
        int padding = 250;
        float minZoomLevel = 5;
        float maxZoomLevel = 12;

        mapView.removeAllPolylines();
        Log.e("mapPolylines_length: ",""+mapPolylines.length);
        for(int i = 0; i<mapPolylines.length; i++){
            mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding, minZoomLevel, maxZoomLevel));

            Log.e("mapPolylines_post[i]", mapPolylines[i].getTag()+"");
            if ( mapPolylines[i].getTag() < 100){
                mapView.addPolyline(mapPolylines[i]);
                continue;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(700);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }); // runOnUiThread_end

            mapView.addPolyline(mapPolylines[i]);
        }

    }

/*    private void showAll(MapPointBounds mapPointBounds) {
        int padding = 250;
        float minZoomLevel = 5;
        float maxZoomLevel = 12;

        // 지도뷰의 중심좌표와 줌레벨을 해당 point가 모두 나오도록 조정
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding, minZoomLevel, maxZoomLevel));
//        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
//        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
    }*/


    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        Log.e("POI_getUserObject", mapPOIItem.getUserObject()+"");
        /*
            도시 POIITEM 선택 -> 도시 테이블 pk_no
            관광지 POIITEM 선택 -> postList 테이블 pk_no
         */
        int userObj = Integer.parseInt(mapPOIItem.getUserObject()+"");
        if( userObj >= 1 && userObj <= 12 ){    // 도시 선택 시!
            Log.e("여기","진입");
            polylineChkNum = 0;     // 초기화
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        // 말풍선 클릭 시

        Log.e("B_getTag", mapPOIItem.getTag()+"");
        String userTag = mapPOIItem.getTag()+"";

        Log.e("B_getUserObject", mapPOIItem.getUserObject()+"");
        String userObj = mapPOIItem.getUserObject()+"";


        if( ("").equals(userTag) || ("null").equals(userTag) || userTag == null ){
            return;
        }


        if ( Integer.parseInt(userObj) >= 1 && Integer.parseInt(userObj) <= 12 ){   // 도시 POIITEM 선택한 경우
            new HttpParamConnThread().execute(SCHEDULE_POST_URL, userTag + "");

        }else{      // 관광지 POIITEM 선택한 경우
//            String userObj2 = mapPOIItem.getUserObject()+"";                // 여기 다시 체크!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//            Log.e("userObj2", userObj2);

            Intent attrIntent = new Intent(getApplicationContext(), AttractionActivity.class);
            attrIntent.putExtra("no",userObj);          // postList_no(pk) 넘기기
            attrIntent.putExtra("usage","result");
            startActivity(attrIntent);
        }

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }


    private PinItem settingInfo(PinItem item){
        int cityNo = Integer.parseInt(item.getNo());

        switch ( cityNo ){
            case 1:
                item.setTitle("서울");
                item.setLatitude(37.5666102);
                item.setLongitude(126.9783881);
                break;
            case 2 :
                item.setTitle("가평");
                item.setLatitude(37.8315081);
                item.setLongitude(127.5095419);
                break;
            case 3:
                item.setTitle("강릉");
                item.setLatitude(37.7521666);
                item.setLongitude(128.8758375);
                break;
            case 4 :
                item.setTitle("안동");
                item.setLatitude(36.5684830);
                item.setLongitude(128.7295370);
                break;
            case 5:
                item.setTitle("경주");
                item.setLatitude(35.8562583);
                item.setLongitude(129.2247556);
                break;
            case 6 :
                item.setTitle("부산");
                item.setLatitude(35.1798159);
                item.setLongitude(129.0750222);
                break;
            case 7:
                item.setTitle("통영");
                item.setLatitude(34.8543900);
                item.setLongitude(128.4331120);
                break;
            case 8 :
                item.setTitle("하동");
                item.setLatitude(35.0673330);
                item.setLongitude(127.7512750);
                break;
            case 9:
                item.setTitle("전주");
                item.setLatitude(35.8241706);
                item.setLongitude(127.1480532);
                break;
            case 10 :
                item.setTitle("보성");
                item.setLatitude(34.7714897);
                item.setLongitude(127.0800605);
                break;
            case 11:
                item.setTitle("순천");
                item.setLatitude(34.9507025);
                item.setLongitude(127.4872430);
                break;
            case 12:
                item.setTitle("여수");
                item.setLatitude(34.7604250);
                item.setLongitude(127.6623130);
                break;
        }
        return item;


    }

}

