package com.example.estsoft.travelfriendflow2.lookaround;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.ArrayList;

public class OthersPlanTableActivity extends AppCompatActivity {
    private static final String LOG_TAG = "OthersPlanTableActivity";

    private static String oneSrchURL = "http://222.239.250.207:8080/TravelFriendAndroid" +
            "/schedule/schSelect";//스케쥴 1개 조회
    private static String cityURL = "http://222.239.250.207:8080/TravelFriendAndroid" +
            "/city/selectCityBySchedule_no"; //city 조회
    private static String postURL = "http://222.239.250.207:8080/TravelFriendAndroid" +
            "/post/selectPostBySchedule_no"; //post 조회
    private static String trainScheduleURL = "http://222.239.250.207:8080/TravelFriendAndroid" +
            "/train/selectTrainScheduleBySchedule_no"; //trainSchedule 조회
    private static String trainDirectPathURL = "http://222.239.250.207:8080/TravelFriendAndroid" +
            "/train/getDirectPath";
    private static String trainTransferPathURL = "http://222.239.250.207:8080/TravelFriendAndroid" +
            "/train/getTransferPath";

    private static final String cities[] = {
            "서울","가평","강릉","안동","경주","부산",
            "통영","하동","전주","보성","순천","여수"
    }; //mChildList는 도시에만 들어가니,그룹 리스트의 글자가 도시들중 하나인지 확인하기 위한 배열

    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<String>> mChildList = null;
    private ArrayList<String> mChildListContent1 = null;
    private ArrayList<String> mChildListContent2 = null;
    private ArrayList<String> mChildListContent3 = null;
    private ExpandableListView mListView;
    private BaseExpandableAdapter adapter;

    private JSONObject mScheduleJsonObject;
    private JSONArray mCityJsonArray;
    private JSONArray mPostJsonArray;
    private JSONArray mTrainScheduleJsonArray;

    private int mSchedule_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_plan_table);

        setLayout();

        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<String>>();
        mChildListContent1 = new ArrayList<String>();
        mChildListContent2 = new ArrayList<String>();
        mChildListContent3 = new ArrayList<String>();

        Intent intent = getIntent();
        mSchedule_no = intent.getIntExtra("schedule_no", -1);

        //스케쥴 받아오기
        Toast.makeText(OthersPlanTableActivity.this, ""+mSchedule_no, Toast.LENGTH_SHORT).show();
        if(mSchedule_no != -1){ //스케쥴 넘버가 제대로 받아졌을 때
            getSchedule(oneSrchURL, ""+mSchedule_no);
        }else{ //스케쥴 넘버가 제대로 받아지지 않았을 때
            Toast.makeText(getApplicationContext(), "스케쥴 넘버가 제대로 받아지지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

//        mGroupList.add("2016.08.23");
//        mGroupList.add("서울");
//        mGroupList.add("2016.08.24");
//        mGroupList.add("서울");
//        mGroupList.add("가평");
//
//        mChildListContent1.add("1");
//        mChildListContent1.add("2");
//        mChildListContent1.add("3");
//
//        mChildListContent2.add("4");
//        mChildListContent2.add("5");
//        mChildListContent2.add("6");
//
//        mChildListContent3.add("7");
//        mChildListContent3.add("8");
//        mChildListContent3.add("9");
//
//        mChildList.add(null);
//        mChildList.add(mChildListContent1);
//        mChildList.add(null);
//        mChildList.add(mChildListContent2);
//        mChildList.add(mChildListContent3);

        adapter = new BaseExpandableAdapter(this, mGroupList, mChildList);
        mListView.setAdapter(adapter);
    }

    private void setLayout(){
        mListView = (ExpandableListView)findViewById(R.id.elv_list);
    }

    //인수로 받은 string이 도시이름인지 확인하는 함수
    private boolean isStringCity(String string){
        for(String city : cities){
            if(string.equals(city)) return true;
        }
        return false;
    }

    private void getSchedule(String url, String schedule_no) {
        class GetScheduleJson extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... path) {
                // URL 연결이 구현될 부분
                URL url;
                String response = "";
                String CONNURL = path[0];
                String VALUE = path[1];
                HttpURLConnection conn = null;
                try {

                    url = new URL(CONNURL + "/" + VALUE);
                    Log.e(LOG_TAG, CONNURL + "/" + VALUE);
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setDoInput(true);

                    int responseCode = conn.getResponseCode();
                    Log.e("http response code", responseCode + "");

                    if (responseCode == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                        Log.e(LOG_TAG, "연결 성공");
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                        while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                            response += line;
                        }

                        br.close();
                        conn.disconnect();
                        Log.e("RESPONSE", "The response is: " + response);
                        return response.trim();

                    } else {
                        Log.e(LOG_TAG, "서버 접속 실패");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } catch (ConnectTimeoutException ue) {
                    Log.e(LOG_TAG, "ConnectTimeoutException");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (("").equals(result) || TextUtils.isEmpty(result)) {
                    Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    mScheduleJsonObject = jsonObject.getJSONObject("schVo");

                    getCity(cityURL, ""+mSchedule_no);
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "response string이 json 형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        GetScheduleJson getScheduleJson = new GetScheduleJson();
        getScheduleJson.execute(url, schedule_no);
    }

    private void getCity(String url, String schedule_no){
        class GetCityJson extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... path) {
                // URL 연결이 구현될 부분
                URL url;
                String response = "";
                String CONNURL = path[0];
                String VALUE = path[1];
                HttpURLConnection conn = null;
                try {

                    url = new URL(CONNURL + "/" + VALUE);
                    Log.e(LOG_TAG, CONNURL + "/" + VALUE);
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setDoInput(true);

                    int responseCode = conn.getResponseCode();
                    Log.e("http response code", responseCode + "");

                    if (responseCode == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                        Log.e(LOG_TAG, "연결 성공");
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                        while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                            response += line;
                        }

                        br.close();
                        conn.disconnect();
                        Log.e("RESPONSE", "The response is: " + response);
                        return response.trim();

                    } else {
                        Log.e(LOG_TAG, "서버 접속 실패");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } catch (ConnectTimeoutException ue) {
                    Log.e(LOG_TAG, "ConnectTimeoutException");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (("").equals(result) || TextUtils.isEmpty(result)) {
                    Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    mCityJsonArray = jsonObject.getJSONArray("cityList");

                    getPost(postURL, ""+mSchedule_no);
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "response string이 json 형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        GetCityJson getCityJson = new GetCityJson();
        getCityJson.execute(url, schedule_no);
    }

    private void getPost(String url, String schedule_no){
        class GetPostJson extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... path) {
                // URL 연결이 구현될 부분
                URL url;
                String response = "";
                String CONNURL = path[0];
                String VALUE = path[1];
                HttpURLConnection conn = null;
                try {

                    url = new URL(CONNURL + "/" + VALUE);
                    Log.e(LOG_TAG, CONNURL + "/" + VALUE);
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setDoInput(true);

                    int responseCode = conn.getResponseCode();
                    Log.e("http response code", responseCode + "");

                    if (responseCode == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                        Log.e(LOG_TAG, "연결 성공");
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                        while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                            response += line;
                        }

                        br.close();
                        conn.disconnect();
                        Log.e("RESPONSE", "The response is: " + response);
                        return response.trim();

                    } else {
                        Log.e(LOG_TAG, "서버 접속 실패");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } catch (ConnectTimeoutException ue) {
                    Log.e(LOG_TAG, "ConnectTimeoutException");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (("").equals(result) || TextUtils.isEmpty(result)) {
                    Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    mPostJsonArray = jsonObject.getJSONArray("postList");

                    getTrainSchedule(trainScheduleURL, ""+mSchedule_no);
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "response string이 json 형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        GetPostJson getPostJson = new GetPostJson();
        getPostJson.execute(url, schedule_no);
    }

    private void getTrainSchedule(String url, String schedule_no){
        class GetTrainScheduleJson extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... path) {
                // URL 연결이 구현될 부분
                URL url;
                String response = "";
                String CONNURL = path[0];
                String VALUE = path[1];
                HttpURLConnection conn = null;
                try {

                    url = new URL(CONNURL + "/" + VALUE);
                    Log.e(LOG_TAG, CONNURL + "/" + VALUE);
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setDoInput(true);

                    int responseCode = conn.getResponseCode();
                    Log.e("http response code", responseCode + "");

                    if (responseCode == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                        Log.e(LOG_TAG, "연결 성공");
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                        while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                            response += line;
                        }

                        br.close();
                        conn.disconnect();
                        Log.e("RESPONSE", "The response is: " + response);
                        return response.trim();

                    } else {
                        Log.e(LOG_TAG, "서버 접속 실패");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } catch (ConnectTimeoutException ue) {
                    Log.e(LOG_TAG, "ConnectTimeoutException");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (("").equals(result) || TextUtils.isEmpty(result)) {
                    Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    mTrainScheduleJsonArray = jsonObject.getJSONArray("trainScheduleList");

                    showTable();
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "response string이 json 형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        GetTrainScheduleJson getTrainScheduleJson = new GetTrainScheduleJson();
        getTrainScheduleJson.execute(url, schedule_no);
    }

    private void showTable(){
        Log.d(LOG_TAG, ""+mScheduleJsonObject);
        Log.d(LOG_TAG, ""+mCityJsonArray);
        Log.d(LOG_TAG, ""+mPostJsonArray);
        Log.d(LOG_TAG, ""+mTrainScheduleJsonArray);

        if(mScheduleJsonObject.isNull("no") || mCityJsonArray.length()==0 || mPostJsonArray.length()==0 || mTrainScheduleJsonArray.length()==0){ //정보가 제대로 안 들어왔을 떄
            Toast.makeText(getApplicationContext(), "경로를 짜기에 입력값이 부족합니다.", Toast.LENGTH_SHORT).show();
            return;
        }else{ //정보가 제대로 들어왔을 떄
            if(mCityJsonArray.length() != mTrainScheduleJsonArray.length()){
                Toast.makeText(getApplicationContext(), "정보가 제대로 들어오지 않았습니다.("+mCityJsonArray.length()+","+mTrainScheduleJsonArray.length()+")", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                for (int i = 0; i < mCityJsonArray.length(); i++) {
                    if (i + 1 < mCityJsonArray.length()) { //처음도시부터 마지막-1도시까지
                        JSONObject t1 = mTrainScheduleJsonArray.getJSONObject(i);
                        JSONObject t2 = mTrainScheduleJsonArray.getJSONObject(i+1);
                        //날짜
                        String d1 = t1.getString("arrivalDate").split(" ")[0];
                        String d2 = t2.getString("startDate").split(" ")[0];
                        mGroupList.add(d1 + " ~ " + d2);

                        mChildList.add(null);

                        //도시
                        String cityName = mCityJsonArray.getJSONObject(i).getString("name");
                        mGroupList.add(cityName);

                        ArrayList<String> postList = new ArrayList<>();
                        //들어오는 trainSchedule
                        String arrivalDate = t1.getString("startDate");
                        String departureDate = t1.getString("arrivalDate");
                        String startStation = t1.getString("startStation");
                        String endStation = t1.getString("endStation");
                        String innerTrainSchedule = startStation + "(" + arrivalDate + ") -> " + endStation + "(" + departureDate + ")";
                        postList.add(innerTrainSchedule);

                        //관광지
                        int city_no = mCityJsonArray.getJSONObject(i).getInt("no");
                        for(int j=0; j<mPostJsonArray.length(); j++){
                            JSONObject post = mPostJsonArray.getJSONObject(j);
                            if(city_no == post.getInt("city_no")){
                                postList.add(post.getString("name"));
                            }
                        }

                        //나가는 trainSchedule
                        String arrivalDate2 = t2.getString("startDate");
                        String departureDate2 = t2.getString("arrivalDate");
                        String startStation2 = t2.getString("startStation");
                        String endStation2 = t2.getString("endStation");
                        String outerTrainSchedule = startStation2 + "(" + arrivalDate2 + ") -> " + endStation2 + "(" + departureDate2 + ")";
                        postList.add(outerTrainSchedule);

                        mChildList.add(postList);
                    } else { //마지막도시는 트레인 스케쥴에 마지막도시->도착역이 없어서 따로
//                        JSONObject t1 = mTrainScheduleJsonArray.getJSONObject(i);
//                        String resultStr = null;
//                        try {
//                            resultStr = new GetLastTrainSchedule.execute().get();
//                        }catch (Exception e){
//                            Toast.makeText(getApplicationContext(), "네트워크 상태를 확인하고 다시 해주세요.", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        if(resultStr == null || resultStr.isEmpty()){
//                            Toast.makeText(getApplicationContext(), "네트워크 상태를 확인하고 다시 해주세요.", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        JSONObject t2 = new JSONObject(resultStr);
//                        //날짜
//
//
//                        //도시
//                        String cityName = mCityJsonArray.getJSONObject(i).getString("name");
//                        mGroupList.add(cityName);
//
//                        ArrayList<String> postList = new ArrayList<>();
//                        //들어오는 trainSchedule
//                        //관광지
//                        int city_no = mCityJsonArray.getJSONObject(i).getInt("no");
//                        for(int j=0; j<mPostJsonArray.length(); j++){
//                            JSONObject post = mPostJsonArray.getJSONObject(j);
//                            if(city_no == post.getInt("city_no")){
//                                postList.add(post.getString("name"));
//                            }
//                        }
//                        //나가는 trainSchedule
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "경로를 짜는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        /*
        경로를 여기다 짠다.
         */

        adapter.notifyDataSetChanged();
    }


}

class BaseExpandableAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> groupList = null;
    private ArrayList<ArrayList<String>> childList = null;
    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;

    public BaseExpandableAdapter(Context c, ArrayList<String> groupList, ArrayList<ArrayList<String>> childList){

        super();
        this.inflater = LayoutInflater.from(c);
        this.groupList = groupList;
        this.childList = childList;
    }

    @Override
    public String getGroup(int groupPosition){
        return groupList.get(groupPosition);
    }

    @Override
    public int getGroupCount(){
        return groupList.size();
    }

    @Override
    public long getGroupId(int groupPosition){
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null){
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.test, parent, false);
            viewHolder.tv_groupName = (TextView)v.findViewById(R.id.tv_group);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)v.getTag();
        }


        viewHolder.tv_groupName.setText(getGroup(groupPosition));
        if(!isExpanded) {
            if (groupPosition % 2 == 0) {
                viewHolder.tv_groupName.setBackgroundColor(Color.parseColor("#eb9b00"));
                viewHolder.tv_groupName.setTextColor(Color.WHITE);
            }else{
                viewHolder.tv_groupName.setBackgroundColor(Color.WHITE);
                viewHolder.tv_groupName.setTextColor(Color.parseColor("#eb9b00"));
            }
        }


        return v;
    }

    @Override
    public String getChild(int groupPosition, int childPosition){
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition){
        if(childList.get(groupPosition)==null){
            return 0;
        }
        return childList.get(groupPosition).size();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition){
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null){
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.test, null);
            viewHolder.tv_childName = (TextView) v.findViewById(R.id.tv_child);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)v.getTag();
        }

        viewHolder.tv_childName.setText(getChild(groupPosition, childPosition));

        return v;
    }

    @Override
    public boolean hasStableIds(){return true;}

    @Override
    public boolean isChildSelectable(int groupPostion, int childPosition){return true;}



    class ViewHolder{
        public ImageView iv_image;
        public TextView tv_groupName;
        public TextView tv_childName;
    }
}