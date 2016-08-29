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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.map.MapViewActivity;
import com.example.estsoft.travelfriendflow2.map.PinItem;
import com.squareup.picasso.Picasso;

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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
    private static Button btn_reply;
    public String sendLocation;        // 서버로 넘길 데이터
    private static Intent intent;

    ArrayList<Reply> reply = new ArrayList<Reply>();
    MyAdapter2 adapter;

    public String myNo;
    public String userName;
    public String userPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction);



        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        try {
            JSONObject userData = new JSONObject(pref.getString("userData", ""));
            myNo = userData.getString("no");
            userName = userData.getString("name");
            userPicture = userData.getString("picture");
        } catch (Exception e) {
            e.printStackTrace();
        }

        img_attraction = (ImageView)findViewById(R.id.img_attraction);
        txt_title = (TextView)findViewById(R.id.txt_title);
        txt_info = (TextView)findViewById(R.id.txt_info);
        txt_addr = (TextView)findViewById(R.id.txt_address);
        edt_reply = (EditText)findViewById(R.id.edt_reply);
        btn_reply = (Button)findViewById(R.id.btn_reply);
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
                    intent.putExtra("attrTitle",txt_title.getText().toString());
                }
                Log.e("선택된 값",no+"_"+sendLocation);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        adapter = new MyAdapter2(getApplicationContext(),R.layout.reply,reply, myNo);
        ListView lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(adapter);
        getList(no);

        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = edt_reply.getText().toString();
                addComment(myNo,intent.getStringExtra("no"),content);
                hideSoftKeyboard();
                edt_reply.setText("");
            }
        });

//        setListViewHeightBasedOnChildren(lv);
    }

//    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            // pre-condition
//            return;
//        }
//
//        int totalHeight = 0;
//        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
//
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight;
//        listView.setLayoutParams(params);
//        listView.requestLayout();
//    }

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

                if( ("").equals(result)){
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
        Log.e("content",content+"");
        return content;

    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_reply.getWindowToken(), 0);
    }


    private void getList(String no){

        class InsertData extends AsyncTask<String, Void, JSONArray> {
            //ProgressDialog loading;

            @Override
            protected void onPostExecute(JSONArray s){
                super.onPostExecute(s);
                JSONArray jarray = s;
                if(jarray.length()==0){
                    Reply eachReply = new Reply();
                    eachReply.setContent("아직 리뷰가 없습니다.");
                    reply.add(eachReply);
                    adapter.notifyDataSetChanged();
                    return;
                }
                for(int i=0; i<jarray.length(); i++){
                    JSONObject obj = null;
                    try{

                        obj=jarray.getJSONObject(i);

                        String no = obj.getString("comment_no");
                        String userNo = obj.getString("user_no");
                        String id = obj.getString("user_name");
                        String content = obj.getString("content");
                        String date = obj.getString("date");
                        String picture = obj.getString("user_picture");

                        Reply eachReply = new Reply( no, userNo, id, content, date, picture);
                        //int count = adapter.getCount();
                        reply.add(eachReply);
                        adapter.notifyDataSetChanged();

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }

            @Override
            protected JSONArray doInBackground(String... params) {

                try{
                    String no = params[0];

                    String link="http://222.239.250.207:8080/TravelFriendAndroid/comment/postCommentList";
                    String data  = URLEncoder.encode("postList_no", "UTF-8") + "=" + URLEncoder.encode(no, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    JSONArray jarray = new JSONArray();
                    try {
                        jarray = new JSONObject(sb.toString()).getJSONArray("comments");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return jarray;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(no);
    }

    private void addComment(String userNo, String postListNo, String content){

        class InsertData extends AsyncTask<String, Void, JSONObject> {

            @Override
            protected void onPostExecute(JSONObject s){
                super.onPostExecute(s);
                JSONObject obj = s;
                try {
                    String no = obj.getString("no");
                    String userNo = obj.getString("user_no");
                    String id = userName;
                    String content = obj.getString("content");
                    String date = obj.getString("date");
                    String picture = userPicture;

                    Reply eachReply = new Reply(no, userNo, id, content, date, picture);
                    reply.add(eachReply);
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                try{
                    String user_no = params[0];
                    String postList_no = params[1];
                    String content = params[2];

                    String link="http://222.239.250.207:8080/TravelFriendAndroid/comment/insertComment";
                    String data  = URLEncoder.encode("user_no", "UTF-8") + "=" + URLEncoder.encode(user_no, "UTF-8");
                    data += "&" + URLEncoder.encode("postList_no","UTF-8") + "=" + URLEncoder.encode(postList_no,"UTF-8");
                    data += "&" + URLEncoder.encode("content","UTF-8") +"="+URLEncoder.encode(content,"UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    JSONObject job = new JSONObject();
                    try {
                        job = new JSONObject(sb.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return job;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(userNo,postListNo,content);
    }
}

class MyAdapter2 extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<Reply> reply;
    LayoutInflater inf;
    String myNo;
    MyAdapter2 adapterSelf;


    public MyAdapter2(){};

    public MyAdapter2(Context context, int layout, ArrayList<Reply> reply,String myNo){
        this.context = context;
        this.layout = layout;
        this.reply = reply;
        this.inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.myNo = myNo;
        this.adapterSelf = this;
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

        ImageView picture = (ImageView)convertView.findViewById(R.id.imageView);
        TextView id = (TextView)convertView.findViewById(R.id.userId);
        TextView content = (TextView)convertView.findViewById(R.id.content);
        TextView date = (TextView)convertView.findViewById(R.id.date);
        final Button delete = (Button)convertView.findViewById(R.id.deleteButton);


        final Reply t = reply.get(position);

        if(!myNo.equals(t.getUserNo())){
            delete.setVisibility(View.GONE);
        }

        final int pp = position;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteComment(t.no, adapterSelf, reply, pp);
            }
        });


        if(!t.picture.equals("")) {
            Picasso.with(context).load(t.picture).resize(100, 100).transform(new CircleTransform()).into(picture);
        }
        id.setText(t.id);
        content.setText(t.content);
        date.setText(t.date);

        return convertView;
    }

    private void deleteComment(String no, MyAdapter2 myAdapter2, ArrayList<Reply> reply, int positionNum){
        final MyAdapter2 adapter = myAdapter2;
        final ArrayList<Reply> replyList = reply;
        final int position = positionNum;

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                Toast.makeText(context,"결과:"+s,Toast.LENGTH_SHORT).show();
                if(s.equals("success")) {
                    Log.e("삭제완료", replyList.get(position).toString() + "완료 ㅊㅋㅊㅋ");
                    replyList.remove(position);
                    adapter.notifyDataSetChanged();
                }
            }


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String no = params[0];


                    String link="http://222.239.250.207:8080/TravelFriendAndroid/comment/deleteComment";
                    String data  = URLEncoder.encode("no", "UTF-8") + "=" + URLEncoder.encode(no, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    Log.e("deletRes is -->" , sb.toString()+"없음말고");
                    return sb.toString();

                } catch (Exception e) {

                    e.printStackTrace();
                    return "fail";
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(no);
    }
}

class Reply{

    String no = "";
    String userNo = "";
    String id = "";
    String content ="";
    String date ="";
    String picture = "";

    public Reply(String no, String userNo, String id, String content, String date, String picture){
        this.no=no;
        this.userNo = userNo;
        this.id = id;
        this.content = content;
        this.date = date;
        this.picture = picture;
    }

    public Reply(String id, String content){

        this.id = id;
        this.content = content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getUserNo(){
        return this.userNo;
    }

    public Reply(){}

    @Override
    public String toString() {
        return "Reply{" +
                "no='" + no + '\'' +
                ", userNo='" + userNo + '\'' +
                ", id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }
}


