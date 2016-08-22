package com.example.estsoft.travelfriendflow2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.map.MapViewActivity;
import com.example.estsoft.travelfriendflow2.map.PinItem;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AttractionActivity extends Activity {
    private static final String LOG_TAG = "AttractionActivity";
    private static final String URL = "http://222.239.250.207:8080/TravelFriendAndroid/android/getDetailData/";

    private static final String TAG_RESULTS = "allAtrVo";
    private static final String TAG_NO = "no";
    private static final String TAG_TITLE = "name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_INFO = "info";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_CATEGORY ="category";

    private JSONArray datas = null;
    private static TextView txt_title, txt_info, txt_addr;
    private static EditText edt_reply;
    private static ImageView img_attraction;
    private static Button btn_like;
    public String sendLocation;        // 서버로 넘길 데이터
    private static Intent intent;

    ArrayList<Reply> reply = new ArrayList<Reply>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction);

        img_attraction = (ImageView)findViewById(R.id.img_attraction);
        txt_title = (TextView)findViewById(R.id.txt_title);
        txt_info = (TextView)findViewById(R.id.txt_info);
        txt_addr = (TextView)findViewById(R.id.txt_address);
        edt_reply = (EditText)findViewById(R.id.edt_reply);
        btn_like = (Button)findViewById(R.id.btn_like);
        hideSoftKeyboard(); // 키보드 숨김

        // ---
        intent = getIntent();
        String usage = intent.getStringExtra("usage");

        if( ("result").equals(usage) ){
            Log.e("usage","null");
            btn_like.setVisibility(View.GONE);
        }

        final String no = intent.getStringExtra("no");
        Log.e("no",no);
        if(no == null){
            Log.e(LOG_TAG,"Incorrect Input : no");
        }
        Log.e(LOG_TAG,URL+no);
        fetchData(URL+no);     // DB에서 상세정보 가져오기

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 이미지변화를 통해 선택했다는 UI 뿌려주기

                if( sendLocation == null ){
                    Log.e(LOG_TAG, "location == null");
                }else{
                    intent.putExtra("no", no);
                    intent.putExtra("location", sendLocation);
                }
                Log.e("선택된 값",no+"_"+sendLocation);

                setResult(RESULT_OK, intent);
                finish();
            }
        });



//        reply.add(new Reply("아이디1","덧글내용1"));
//        reply.add(new Reply("아이디2","덧글내용2"));
//        reply.add(new Reply("아이디3","덧글내용3"));
//        reply.add(new Reply("아이디4","덧글내용4"));
//
//        MyAdapter2 adapter = new MyAdapter2(getApplicationContext(),R.layout.reply,reply);
//        ListView lv = (ListView)findViewById(R.id.listview);
//        lv.setAdapter(adapter);
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
                Log.e(LOG_TAG,result);
                PinItem item = parsePinData(result);

                if(item != null){
                    showResult(item);
                }

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    protected PinItem parsePinData(String myJSON){
        PinItem pinItem = new PinItem();

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONObject object = jsonObj.getJSONObject(TAG_RESULTS);
            Log.e(LOG_TAG, object.toString());

            pinItem.no = object.getString(TAG_NO);
            pinItem.title = object.getString(TAG_TITLE);
            pinItem.picture = object.getString(TAG_PICTURE);

            String location = object.getString(TAG_LOCATION);
            String[] arr = location.split(",");
            pinItem.latitude = (arr[0] != null ? Double.parseDouble(arr[0]) : null );
            pinItem.longitude = (arr[1] != null ? Double.parseDouble(arr[1]) : null );

            sendLocation = Double.toString(pinItem.latitude)+","+Double.toString(pinItem.longitude);

            pinItem.info = object.getString(TAG_INFO);

            pinItem.category = object.getString(TAG_CATEGORY);
            pinItem.address = object.getString(TAG_ADDRESS);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return pinItem;
    }


    private void showResult(PinItem item) {
        Drawable drawable = createDrawableFromUrl(item.picture);
        if(drawable != null){
            img_attraction.setImageDrawable(drawable);
        }else{
            img_attraction.setImageResource(R.drawable.noimage);
        }
        txt_title.setText(item.title);
        txt_addr.setText(item.address);

        item.info = item.info.replaceAll("<(/)?[bB][rR](\\s)*(/)?>","\n").replaceAll("strong","h2");// info의 <br>태그 삭제 및 <strong>태그 변환
        txt_info.setText( Html.fromHtml(item.info));
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

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_reply.getWindowToken(), 0);
    }

}

class MyAdapter2 extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<Reply> reply;
    LayoutInflater inf;

    public MyAdapter2(Context context, int layout, ArrayList<Reply> reply){
        this.context = context;
        this.layout = layout;
        this.reply = reply;
        this.inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return reply.size();
    }

    @Override
    public Object getItem(int position){
        return reply.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = inf.inflate(layout, null);
        }

        TextView id = (TextView)convertView.findViewById(R.id.idBox);
        TextView context = (TextView)convertView.findViewById(R.id.contextBox);

        Reply t = reply.get(position);
        id.setText(t.id);
        context.setText(t.context);
        return convertView;
    }
}

class Reply{
    String id = "";
    String context ="";
    public Reply(String id,String context){
        this.id = id;
        this.context = context;
    }

    public Reply(){}
}