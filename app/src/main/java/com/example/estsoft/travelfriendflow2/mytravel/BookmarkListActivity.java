package com.example.estsoft.travelfriendflow2.mytravel;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.lookaround.OthersPlanActivity;
import com.example.estsoft.travelfriendflow2.thread.HttpFavorConnThread;
import com.example.estsoft.travelfriendflow2.thread.HttpParamConnThread;
import com.example.estsoft.travelfriendflow2.thread.Preference;


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

public class BookmarkListActivity extends Activity {
    private static final String LOG_TAG = "BookmarkListActivity";
    private static String searchFavoriteURL = "http://222.239.250.207:8080/TravelFriendAndroid/favorite/selectFavoriteList";  // Favorite Table Search
    private static String deleteFavoriteURL = "http://222.239.250.207:8080/TravelFriendAndroid/favorite/deleteFavoriteData";  // Favorite Table Delete
    private static final String TAG_RESULTS="favoriteList";
    private static final String TAG_TITLE="title";
    private static final String TAG_SDATE="startDate";
    private static final String TAG_EDATE="endDate";
    private MyAdapter adapter;

    ArrayList<Travel> tr = new ArrayList<Travel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytravellist);

    }

    @Override
    protected void onResume() {
        super.onResume();

        tr.clear(); // 초기화
        Preference pf = new Preference(this);

        new HttpFavorConnThread().execute(searchFavoriteURL, "/"+pf.getUserNo());

        adapter = new MyAdapter(getApplicationContext(),R.layout.row,tr);
        ListView lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = tr.get(i).getTitle();
                Intent intent = new Intent(getApplicationContext(),OthersPlanActivity.class);
                intent.putExtra("title",title);
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Preference pf = new Preference(getApplicationContext());
                new HttpFavorConnThread().execute(deleteFavoriteURL, "?user_no="+pf.getUserNo()+"&schedule_no="+tr.get(position).getSchNo());
                tr.remove(position);
                return true;
            }
        });
    }

    public class HttpFavorConnThread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path){
            // URL 연결이 구현될 부분
            URL url;
            String response = "";
            String CONNURL = path[0];
            String VALUE = path[1];
            HttpURLConnection conn = null;
            try {

                url = new URL(CONNURL+VALUE);
                Log.e(LOG_TAG, CONNURL+VALUE);
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
            if(result.equals("")) {
                //  로딩바 띄우기
                Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                return;
            }

            if( parsePinData(result) ){
                showResult();
            }

        }

    }   // End_HttpParamConnThread

    protected boolean parsePinData(String myJSON){

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray datas = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i = 0; i< datas.length(); i++){
                JSONObject object = datas.getJSONObject(i);

                Travel t = new Travel();
                int no = object.getInt("no");       // schedule_no

                t.setSchNo(no);
                t.setTitle(object.getString(TAG_TITLE));

                String sdate = object.getString(TAG_SDATE);
                String edate = object.getString(TAG_EDATE);

                if( sdate == null || edate == null || object.getString("firstStation") == null || object.getString("lastStation") == null){
                    return false;
                }

                String[] s = sdate.split(" ");
                String[] e = edate.split(" ");
                t.setTxt_creationDate(s[0].replaceAll("-", "/"));

                String[] sdateArr = s[0].split("-");
                String[] edateArr = e[0].split("-");
                int sMonth = Integer.parseInt(sdateArr[1]);

                if( sMonth >= 5 && sMonth <= 9 ){
                    t.setPlanSeason("#여름");
                }else if( sMonth >= 10 & sMonth <= 3 ){
                    t.setPlanSeason("#겨울");
                }

                int day = Integer.parseInt(edateArr[2]) - Integer.parseInt(sdateArr[2]);

                t.setPlanTime("#"+(day-1)+"박"+day+"일");
                t.setBackground(R.drawable.hadong);    // 이미지 나중에 처리하기

                // user_no로 배경 이미지 random하게 뿌림
                settingBackground(t, no);

                t.setSetting(false);
                tr.add(t);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }// End_parsePinData

    private void showResult() {
        adapter.notifyDataSetChanged();
    }

    public void settingBackground(Travel t, int no) {       // user_no로 배경 이미지 random으로 뿌림
        int divideNum = no%12;

        switch ( divideNum ){
            case 0 :
                t.setBackground(R.drawable.seoul);
                break;
            case 1:
                t.setBackground(R.drawable.gapyeong);
                break;
            case 2 :
                t.setBackground(R.drawable.gangrueng);
                break;
            case 3:
                t.setBackground(R.drawable.andong);
                break;
            case 4 :
                t.setBackground(R.drawable.jeonju);
                break;
            case 5:
                t.setBackground(R.drawable.gyeongju);
                break;
            case 6 :
                t.setBackground(R.drawable.busan);
                break;
            case 7:
                t.setBackground(R.drawable.hadong);
                break;
            case 8 :
                t.setBackground(R.drawable.tongyeong);
                break;
            case 9:
                t.setBackground(R.drawable.sooncheon);
                break;
            case 10 :
                t.setBackground(R.drawable.boseong);
                break;
            case 11:
                t.setBackground(R.drawable.yeosoo);
                break;
        }
    }

 /*   public class HttpFavorConnThread extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path){
            // URL 연결이 구현될 부분
            URL url;
            String response = "";
            String CONNURL = path[0];
            String VALUE = "?user_no="+path[1];
            String VALUE2 = "&schedule_no="+path[2];

            HttpURLConnection conn = null;
            try {

                url = new URL(CONNURL+VALUE+VALUE2);
                Log.e(LOG_TAG, CONNURL+VALUE+VALUE2);
                conn = (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(3000);
                conn.setDoInput(true);

                Log.e("http response code", conn.getResponseCode()+"");
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                    Log.e(LOG_TAG, "연결 성공");
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림

                    while ((line = br.readLine()) != null) {// 서버의 응답을 읽어옴
                        response += line;
                    }

                    br.close();
                    conn.disconnect();
                    Log.e("RESPONSE", "The response is: " + response);
                }

            }catch (IOException e) {
                e.printStackTrace();
            }finally{
                conn.disconnect();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // UI 업데이트가 구현될 부분
            if(result==null) {
                //  로딩바 띄우기
                Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                return;
            }
            if( parsePinData(result) ){
                showResult();
            }
        }

    }   // End_HttpFavorConnThread*/

}

