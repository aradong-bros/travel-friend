package com.example.estsoft.travelfriendflow2.lookaround;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.mytravel.Travel;
import com.example.estsoft.travelfriendflow2.thread.HttpFavorConnThread;
import com.example.estsoft.travelfriendflow2.thread.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LookAroundActivity extends Activity {
    private static final String LOG_TAG = "LookAroundActivity";
    private static String othAllSrchURL = "http://222.239.250.207:8080/TravelFriendAndroid/schedule/schSelectByOther";    // 다른 사용자 글 전체 조회
    private static String searchFavoriteURL = "http://222.239.250.207:8080/TravelFriendAndroid/favorite/selectFavoriteList";  // Favorite Table Search

    private static final String FAVORITE_TAG_RESULTS="favoriteList";
    private static final String TAG_RESULTS="schList";
    private static final String TAG_TITLE="title";
    private static final String TAG_SDATE="startDate";
    private static final String TAG_EDATE="endDate";

    ArrayList<Travel> tr = new ArrayList<Travel>();
    private ArrayList<Integer> favorSchNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookaround);

    }

    @Override
    protected void onResume() {
        super.onResume();

        tr.clear(); // 초기화
        Preference pf = new Preference(this);
        new HttpParamConnThread().execute(searchFavoriteURL, pf.getUserNo());
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
                conn.setConnectTimeout(5000); // 타임아웃
                conn.setDoInput(true);

                int responseCode = conn.getResponseCode();
                Log.e("http response code", responseCode+"");

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

                    return response;

                }else{
                    Log.e(LOG_TAG, "서버 접속 실패");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }catch (IOException e) {
                e.printStackTrace();
            }finally{
                conn.disconnect();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals(null)) {
                //  로딩바 띄우기
                Toast.makeText(getApplicationContext(), "네트워크가 원활하지 않습니다.\n 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                return;
            }

            String str = result.substring(2,4);     // sc or fa
            if( str.equals("fa") ){

               if( parsePinFavorData(result) ) {
                   Preference pf = new Preference(getApplicationContext());
                   new HttpParamConnThread().execute(othAllSrchURL, pf.getUserNo());
               }

            }else if( str.equals("sc") ){

                if( parsePinData(result) )
                    showResult();

            }

        }

    }   // End_HttpParamConnThread

    protected boolean parsePinData(String myJSON){

        // tr.add(new Travel("여행가쟈","2016/04/21","4박5일","여름",R.drawable.hadong));
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray datas = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i = 0; i< datas.length(); i++){
                JSONObject object = datas.getJSONObject(i);

                Travel t = new Travel();
                int no = object.getInt("no");

                for(int k=0; k<favorSchNo.size(); k++){
                    if( no == favorSchNo.get(k) )
                        t.setHeart(true);
                }

                t.setSchNo(no);
                t.setTitle(object.getString(TAG_TITLE));

                String sdate = object.getString(TAG_SDATE);
                String edate = object.getString(TAG_EDATE);

                if( sdate.equals(null)|| edate.equals(null) ){              // <<<
                    return false;
                }

                String[] s = sdate.split(" ");
                String[] e = edate.split(" ");
                t.setTxt_creationDate(s[0].replaceAll("-", "/"));

                String[] sdateArr = s[0].split("-");
                String[] edateArr = e[0].split("-");
                int sMonth = Integer.parseInt(sdateArr[1]);    // month

                if( sMonth >= 5 && sMonth <= 9 ){
                    t.setPlanSeason("여름");
                }else if( sMonth >= 10 & sMonth <= 3 ){
                    t.setPlanSeason("겨울");
                }

                int day = Integer.parseInt(edateArr[2]) - Integer.parseInt(sdateArr[2]);
                t.setPlanTime((day-1)+"박"+day+"일");

                t.setBackground(R.drawable.hadong);    // 이미지 나중에 처리하기
                tr.add(t);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }   // End_parsePinData

    private void showResult() {

            MyAdapter adapter = new MyAdapter(getApplicationContext(),R.layout.row,tr);
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

    }

    protected boolean parsePinFavorData(String myJSON){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray datas = jsonObj.getJSONArray(FAVORITE_TAG_RESULTS);
            favorSchNo = new ArrayList<Integer>(datas.length());

            for(int i = 0; i< datas.length(); i++){
                JSONObject object = datas.getJSONObject(i);
                int no = object.getInt("no");
                favorSchNo.add(no);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }   // End_parsePinFavorData

}

class MyAdapter extends BaseAdapter{
    Context context;
    int layout;
    ArrayList<Travel> tr;
    LayoutInflater inf;
    String insertFavoriteURL = "http://222.239.250.207:8080/TravelFriendAndroid/favorite/insertFavoriteData";  // Favorite Table Insert
    String deleteFavoriteURL = "http://222.239.250.207:8080/TravelFriendAndroid/favorite/deleteFavoriteData";  // Favorite Table Delete

    public MyAdapter(Context context, int layout, ArrayList<Travel> tr){
        this.context = context;
        this.layout = layout;
        this.tr = tr;
        this.inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return tr.size();
    }

    @Override
    public Object getItem(int position){
        return tr.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = inf.inflate(layout, null);
        }

        TextView title = (TextView)convertView.findViewById(R.id.lookAroundTextBox);
        TextView txt_creationDate = (TextView)convertView.findViewById(R.id.txt_creationDate);
        TextView plan_time = (TextView)convertView.findViewById(R.id.plan_time);
        TextView plan_season = (TextView)convertView.findViewById(R.id.plan_season);
        LinearLayout background = (LinearLayout)convertView.findViewById(R.id.row_layout);
        ImageView heart = (ImageView)convertView.findViewById(R.id.heart);

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.setSelected(!v.isSelected());
                Preference pf = new Preference(context);

                if( v.isSelected() ){ // true:♥
                    new HttpFavorConnThread().execute(insertFavoriteURL,  pf.getUserNo(), tr.get(position).getSchNo()+"");
                }else{      // false:♡
                    new HttpFavorConnThread().execute(deleteFavoriteURL, pf.getUserNo(), tr.get(position).getSchNo()+"");
                }

            }
        });

        Travel t = tr.get(position);
        title.setText(t.getTitle());
        txt_creationDate.setText(t.getTxt_creationDate());
        plan_time.setText(t.getPlanTime());
        plan_season.setText(t.getPlanSeason());
        background.setBackgroundResource(t.getBackground());
        heart.setSelected(t.isHeart());

        return convertView;
    }
}
