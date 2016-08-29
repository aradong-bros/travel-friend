package com.example.estsoft.travelfriendflow2.lookaround;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
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
import com.example.estsoft.travelfriendflow2.thread.Preference;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private static String lastTrainScheduleURL = "http://222.239.250.207:8080/TravelFriendAndroid/" +
            "train/selectLastTrainSchedule";
    private static String postListURL = "http://222.239.250.207:8080/TravelFriendAndroid" +
            "/android/getDetailData";

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
        if(intent.getIntExtra("group", -1) == 1){
            Preference pref = new Preference(this);
            mSchedule_no = Integer.parseInt(pref.getValue("prefSchNo","-1"));
        }else {
            mSchedule_no = intent.getIntExtra("schedule_no", -1);
        }

        //스케쥴 받아오기
        Toast.makeText(OthersPlanTableActivity.this, ""+mSchedule_no, Toast.LENGTH_SHORT).show();
        if(mSchedule_no != -1){ //스케쥴 넘버가 제대로 받아졌을 때
            getSchedule(oneSrchURL, ""+mSchedule_no);
        }else{ //스케쥴 넘버가 제대로 받아지지 않았을 때
            Toast.makeText(getApplicationContext(), "스케쥴 넘버가 제대로 받아지지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

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
        class GetLastTrainSchedule extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {
                // URL 연결이 구현될 부분
                URL url;
                String response = "";
                String CONNURL = params[0];
                String startLoc = params[1];
                String cityList_no = params[2];
                String endStation = params[3];
                String startDate = params[4];
                HttpURLConnection conn = null;
                try {

                    url = new URL(CONNURL);
                    String param = "startLoc=" + startLoc + "&cityList_no=" + cityList_no +
                            "&endStation=" + endStation + "&startDate=" + startDate;
                    Log.d(LOG_TAG, CONNURL + "?startLoc=" + startLoc + "&cityList_no=" + cityList_no +
                            "&endStation=" + endStation + "&startDate=" + startDate);
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setDoInput(true);

                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(param.getBytes());
                    outputStream.flush();
                    outputStream.close();

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
        }

        class GetPostDetail extends AsyncTask<String, Void, String>{
            @Override
            protected String doInBackground(String... params) {
                // URL 연결이 구현될 부분
                URL url;
                String response = "";
                String CONNURL = params[0];
                String postList_no = params[1];
                HttpURLConnection conn = null;
                try {

                    url = new URL(CONNURL + "/" + postList_no);
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
        }

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
                        String innerTrainSchedule = startStation + "(" + arrivalDate + ") -> \n" + endStation + "(" + departureDate + ")";
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
                        JSONObject t1 = mTrainScheduleJsonArray.getJSONObject(i);

                        String resultStr = null;
                        int tourTime = 0;
                        try {
                            int city_no = mCityJsonArray.getJSONObject(i).getInt("no");
                            int cityList_no = mCityJsonArray.getJSONObject(i).getInt("cityList_no");
                            tourTime = 0;
                            String startLoc = null;
                            for(int j=0; j<mPostJsonArray.length(); j++){
                                JSONObject post = mPostJsonArray.getJSONObject(j);
                                if(city_no == post.getInt("city_no")){
                                    int postList_no = post.getInt("postList_no");
                                    String postDetailStr = new GetPostDetail().execute(postListURL, ""+postList_no).get();
                                    JSONObject postDetailObject = new JSONObject(postDetailStr);
                                    JSONObject postDetail = postDetailObject.getJSONObject("allAtrVo");

                                    String postCategory = postDetail.getString("category");

                                    switch (postCategory){
                                        case "tour":
                                            tourTime += 3;
                                            break;
                                        case "inn":
                                            tourTime += 7;
                                            break;
                                        case "food":
                                            tourTime += 1;
                                            break;
                                        default:
                                            break;
                                    }

                                    startLoc = postDetail.getString("location"); //계속 갱신된다. 결국 마지막인 postOrder가 높은 location이 나온다.
                                }
                            }
                            String endStation = mScheduleJsonObject.getString("lastStation");

                            String startDateDATETIME = t1.getString("arrivalDate");
                            int year = Integer.parseInt(startDateDATETIME.substring(0, 4));
                            int month = Integer.parseInt(startDateDATETIME.substring(5, 7));
                            int date = Integer.parseInt(startDateDATETIME.substring(8, 10));
                            int hour = Integer.parseInt(startDateDATETIME.substring(11, 13));
                            int minute = Integer.parseInt(startDateDATETIME.substring(14, 16));
                            Calendar c = Calendar.getInstance();
                            c.set(year, month-1, date, hour, minute);
                            c.add(Calendar.MINUTE, tourTime*60);
                            java.sql.Date addDate = new java.sql.Date(c.getTimeInMillis());
                            java.sql.Time time = new java.sql.Time(c.getTimeInMillis());
                            String startDate = addDate + " " + time;

                            resultStr = new GetLastTrainSchedule().execute(lastTrainScheduleURL, startLoc, ""+cityList_no, endStation, startDate).get();
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "네트워크 상태를 확인하고 다시 해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(resultStr == null || resultStr.isEmpty()){
                            JSONObject t2 = new JSONObject(resultStr);
                            //날짜
                            String d1 = t1.getString("arrivalDate").split(" ")[0];
                            String startDateDATETIME = t1.getString("arrivalDate");
                            int year = Integer.parseInt(startDateDATETIME.substring(0, 4));
                            int month = Integer.parseInt(startDateDATETIME.substring(5, 7));
                            int date = Integer.parseInt(startDateDATETIME.substring(8, 10));
                            int hour = Integer.parseInt(startDateDATETIME.substring(11, 13));
                            int minute = Integer.parseInt(startDateDATETIME.substring(14, 16));
                            Calendar c = Calendar.getInstance();
                            c.set(year, month-1, date, hour, minute);
                            c.add(Calendar.MINUTE, tourTime*60);
                            java.sql.Date addDate = new java.sql.Date(c.getTimeInMillis());
                            java.sql.Time time = new java.sql.Time(c.getTimeInMillis());
                            String goDate = addDate + " " + time;
                            int goYear = Integer.parseInt(goDate.split("-")[0]) - 1900;
                            int goMonth = Integer.parseInt(goDate.split("-")[1]) -1;
                            int goDay = Integer.parseInt(goDate.split("-")[2]);
                            String d2 = goYear + "-" + goMonth + "-" + goDay;
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
                            String innerTrainSchedule = startStation + "(" + arrivalDate + ") -> \n" + endStation + "(" + departureDate + ")";
                            postList.add(innerTrainSchedule);

                            //관광지
                            int city_no = mCityJsonArray.getJSONObject(i).getInt("no");
                            for(int j=0; j<mPostJsonArray.length(); j++){
                                JSONObject post = mPostJsonArray.getJSONObject(j);
                                if(city_no == post.getInt("city_no")){
                                    postList.add(post.getString("name"));
                                }
                            }
                        }

                        JSONObject t2 = new JSONObject(resultStr);
                        //날짜
                        String d1 = t1.getString("arrivalDate").split(" ")[0];
                        String goDate = t2.getString("goDate");
                        int goYear = Integer.parseInt(goDate.split("-")[0]) - 1900;
                        int goMonth = Integer.parseInt(goDate.split("-")[1]) -1;
                        int goDay = Integer.parseInt(goDate.split("-")[2]);
                        String d2;
                        if(goMonth >= 10) {
                            if(goDay >= 10) {
                                d2 = goYear + "-" + goMonth + "-" + goDay;
                            }else{
                                d2 = goYear + "-" + goMonth + "-0" + goDay;
                            }
                        }else{
                            if(goDay >= 10){
                                d2 = goYear + "-0" + goMonth + "-" + goDay;
                            }else{
                                d2 = goYear + "-0" + goMonth + "-0" + goDay;
                            }
                        }
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
                        String innerTrainSchedule = startStation + "(" + arrivalDate + ") -> \n" + endStation + "(" + departureDate + ")";
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
                        boolean compareBool = compareTime(t2.getString("departureTime"), t2.getString("arrivalTime"));

                        if(compareBool){
                            String arrivalDate2 = d2 + " " + t2.getString("departureTime");
                            String departureDate2 = d2 + " " + t2.getString("arrivalTime");
                            String startStation2 = t2.getString("startStationName");
                            String endStation2 = t2.getString("endStationName");
                            String outerTrainSchedule = startStation2 + "(" + arrivalDate2 + ") -> " + endStation2 + "(" + departureDate2 + ")";
                            postList.add(outerTrainSchedule);
                        }else{
                            String arrivalDate2 = d2 + " " + t2.getString("departureTime");
                            String departureDate2 = getNextDate(d2) + " " + t2.getString("arrivalTime");
                            String startStation2 = t2.getString("startStationName");
                            String endStation2 = t2.getString("endStationName");
                            String outerTrainSchedule = startStation2 + "(" + arrivalDate2 + ") -> " + endStation2 + "(" + departureDate2 + ")";
                            postList.add(outerTrainSchedule);
                        }

                        mChildList.add(postList);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "경로를 짜는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        adapter.notifyDataSetChanged();
    }

    public boolean compareTime(String t1, String t2){
        String t1Split[] = t1.split(":");
        String t2Split[] = t2.split(":");

        if(t1Split.length != t2Split.length) return true;

        for(int i=0; i<t1Split.length; i++){
            int t1Int = Integer.parseInt(t1Split[i]);
            int t2Int = Integer.parseInt(t2Split[i]);

            if(t1Int > t2Int) {
                return false;
            }else{
                break;
            }
        }

        return true;
    }

    public String getNextDate(String date){
        String d[] = date.split("-");
        int year = Integer.parseInt(d[0]);
        int month = Integer.parseInt(d[1]);
        int day = Integer.parseInt(d[2]);
        Calendar c = Calendar.getInstance();
        c.set(year + 1900, month-1, day + 1);

        String result;
        if((c.get(Calendar.MONTH) + 1) >= 10) {
            if( (c.get(Calendar.DATE)) >= 10 ) {
                result = (c.get(Calendar.YEAR) - 1900) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);
            }else{
                result = (c.get(Calendar.YEAR) - 1900) + "-" + (c.get(Calendar.MONTH) + 1) + "-0" + c.get(Calendar.DATE);
            }
        }else{
            if( (c.get(Calendar.DATE)) >= 10 ) {
                result = (c.get(Calendar.YEAR) - 1900) + "-0" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);
            }else{
                result = (c.get(Calendar.YEAR) - 1900) + "-0" + (c.get(Calendar.MONTH) + 1) + "-0" + c.get(Calendar.DATE);
            }
        }

        return result;
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
//            viewHolder.iv_image = (ImageView)v.findViewById(R.id.iv_image);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)v.getTag();
        }

//        if(isExpanded){
//            viewHolder.iv_image.setBackgroundColor(Color.GREEN);
//        }else{
//            viewHolder.iv_image.setBackgroundColor(Color.WHITE);
//        }

        viewHolder.tv_groupName.setText(getGroup(groupPosition));

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