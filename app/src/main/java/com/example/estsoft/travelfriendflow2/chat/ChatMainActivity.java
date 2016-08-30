package com.example.estsoft.travelfriendflow2.chat;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.CircleTransform;
import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.ChatData;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
public class ChatMainActivity extends Activity {

    private static String IP = "192.168.22.74";
    private final static int PORT = 10001;

    private String txtReceive;
    private Long regionReceive;
    private String userReceive;
    private String pictureReceive;
    private Long noReceive;
    private String timeReceive = "none";
    private String timeReceiveforCaller;
    private String previousDateforCaller = "none";
    private String previousDate = "none";

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private final Handler handler = new Handler();
    private EditText editText;

    private Long userNo;
    private String loginID;
    private Long regionNum;
    private String userImage;

    private ScrollView scrollView;

    private Long startFromDB = 0L;
    private Long beforeFromDB = 0L;
    boolean firstCall = true;



    private ImageView mProgressBar;
    private LinearLayout loading_layout;
    private AnimationDrawable animationDrawable;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (ois != null) ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (oos != null) {

                ChatData d = new ChatData();
                d.setUserNum(userNo);
                d.setId(loginID);
                d.setRegionNum(regionNum);
                d.setTxt("/quit");
                d.setImage(userImage);

                oos.writeObject(d);
                oos.flush();

                oos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_main);
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        IP = pref.getString("ip","");

        //loading page

        LayoutInflater inflater = getLayoutInflater();
        loading_layout = (LinearLayout)inflater.inflate(R.layout.custom_progressbar,null);
        addContentView(loading_layout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        loading_layout.setVisibility(View.GONE);


        mProgressBar = (ImageView)loading_layout.findViewById(R.id.imageview_custom_progress);
        mProgressBar.setVisibility(View.GONE);

        animationDrawable = (AnimationDrawable)mProgressBar.getDrawable();


        Intent intent = getIntent();
        regionNum = intent.getExtras().getLong("RegionNum");

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        GetListTask task = new GetListTask();
        task.execute(regionNum.toString(), "9999999", startFromDB.toString());

        try {
            JSONObject userData = new JSONObject(pref.getString("userData", ""));
            loginID = userData.getString("name");
            userNo = Long.parseLong(userData.getString("no"));
            userImage = userData.getString("picture");
        } catch (Exception e) {
            e.printStackTrace();
        }

        scrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });

        Button btn = (Button)findViewById(R.id.btn_callmore);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeFromDB = startFromDB;
                if(startFromDB!=-1L){
                    GetListTask task = new GetListTask();
                    task.execute(regionNum.toString(), startFromDB.toString(), "0");
                }
            }
        });


        TextView regionInformer = (TextView) findViewById(R.id.textview_region);
        int rgrg = Integer.parseInt(regionNum.toString());
        switch (rgrg) {
            case 1:
                regionInformer.setText("서울/경기(가평)");
                break;
            case 2:
                regionInformer.setText("강원(강릉)/충북/충남");
                break;
            case 3:
                regionInformer.setText("경북(안동,경주)/경남(부산,통영)");
                break;
            case 4:
                regionInformer.setText("전북(전주)/전남(보성,순천,여수,하동)");
                break;
        }

        editText = (EditText) findViewById(R.id.editText);

        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_background);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });

// -- Thread for Connection
        Thread cThread = new Thread() {
            public void run() {
                try {
                    connect(IP, PORT);
                    Log.i("실행------------->", "소켓 연결 시도");
                } catch (Exception e) {
                    Log.e("error:------------->", "소켓연결 실패");
                }
            }
        };
        cThread.start();
    }

    private void connect(String ip, int port) {
        int size;
        byte[] w = new byte[10240];
        txtReceive = "";
        try {
            socket = new Socket(ip, port);
            Log.i("수신 소켓시작됨------------->", socket.toString());

            if (socket != null) {
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.flush();
                ChatData d = new ChatData();
                d.setUserNum(userNo);
                d.setId(loginID);
                d.setRegionNum(regionNum);
                d.setTxt("has entered");
                d.setImage(userImage);
                oos.writeObject(d);
                oos.flush();


                ChatData dd ;
                while (socket != null && socket.isConnected()) {

                    if ((dd = (ChatData) ois.readObject()) == null) continue;
                    if(!timeReceive.equals("none")) previousDate = timeReceive;
                    txtReceive = new String(dd.getTxt());
                    userReceive = new String(dd.getId());
                    regionReceive = new Long(dd.getRegionNum());
                    pictureReceive = new String(dd.getImage());
                    noReceive = new Long(dd.getNo());
                    timeReceive = new String(dd.getTime());


                    handler.post(new Runnable() {
                        public void run() {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date thisdate = simpleDateFormat.parse(timeReceive);
                                Date previous = new Date();
                                if(!previousDate.equals("0")) {
                                   previous = simpleDateFormat.parse(previousDate);
                                }
                                int thisday = thisdate.getDay();
                                int previousday = previous.getDay();
                                if(thisday!=previousday){
                                    LinearLayout newDayLay = new LinearLayout(getApplicationContext());
                                    newDayLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    newDayLay.setGravity(Gravity.CENTER_HORIZONTAL);
                                    newDayLay.setPadding(0,20,0,35);
                                    TextView tttt = new TextView(getApplicationContext());
                                    tttt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                    tttt.setTextColor(Color.rgb(0, 0, 0));
                                    tttt.setText("------------------"+simpleDateFormat.format(thisdate)+"------------------");
                                    newDayLay.addView(tttt);
                                    LinearLayout layout = (LinearLayout) findViewById(R.id.centerLayout);
                                    layout.addView(newDayLay);
                                }

                            }catch(Exception e){
                                e.printStackTrace();
                            }

                            String timeTv = "";
                            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                Date dddd = simpleDateFormat2.parse(timeReceive);
                                timeTv=String.format("%02d:%02d",dddd.getHours(),dddd.getMinutes());
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }

                            if (regionReceive.equals(regionNum)) {

                                LinearLayout kk = new LinearLayout(getApplicationContext());
                                LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );

                                if (userReceive.equals(loginID)) {
                                    kk = (LinearLayout)inflater.inflate(R.layout.mychat,null);
                                    ((TextView)kk.findViewById(R.id.chatbubble)).setText(txtReceive);
                                    ((TextView)kk.findViewById(R.id.time)).setText(timeTv);
                                } else {

                                    kk = (LinearLayout)inflater.inflate(R.layout.yourchat,null);
                                    Picasso.with(getApplicationContext()).load(pictureReceive).resize(120, 120).transform(new CircleTransform()).into(((ImageView)kk.findViewById(R.id.imageView)));
                                    ((TextView)kk.findViewById(R.id.userId)).setText(userReceive);
                                    ((TextView)kk.findViewById(R.id.chatbubble)).setText(txtReceive);
                                    ((TextView)kk.findViewById(R.id.time)).setText(timeTv);

                                }

                                LinearLayout layout = (LinearLayout) findViewById(R.id.centerLayout);
                                layout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                    }
                                });

                                layout.addView(kk);

                                scrollView.post(new Runnable() {
                                    public void run() {
                                        scrollView.fullScroll(View.FOCUS_DOWN);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ois != null) ois.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (oos != null) oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (socket != null) socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendClicked(View v) {
        if (editText.getText().toString().length() != 0 && !editText.getText().toString().equals(" ")) {
            if(oos!=null) {
                try {
                    ChatData d = new ChatData();
                    d.setUserNum(userNo);
                    d.setRegionNum(regionNum);
                    d.setId(loginID);
                    d.setTxt(editText.getText().toString());
                    d.setImage(userImage);
                    oos.writeObject(d);
                    oos.flush();
                    editText.setText("");
                } catch (Exception e) {
                    Toast.makeText(ChatMainActivity.this,"전송에 실패하였습니다.(사내망 와이파이 연결 필요)",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(ChatMainActivity.this,"서버에 연결되지 않았습니다.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    class GetListTask extends AsyncTask<String, Void, JSONArray> {
        JSONArray jarray;
        public JSONArray getJarray() {
            return jarray;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading_layout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            animationDrawable.start();
        }

        @Override
        protected void onPostExecute(JSONArray s) {
            super.onPostExecute(s);
            loading_layout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            animationDrawable.stop();

            JSONArray jarray = s;
            if (jarray == null) {
                startFromDB = -1L;
                return;
            }
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject obj = null;
                try {
                    obj = jarray.getJSONObject(i);
                    previousDateforCaller = (timeReceiveforCaller!="none"?timeReceiveforCaller:"0");
                    txtReceive = new String(obj.getString("content"));
                    userReceive = new String(obj.getString("name"));
                    pictureReceive = new String(obj.getString("picture"));
                    noReceive = new Long(obj.getLong("no"));
                    timeReceiveforCaller = new String(obj.getString("time"));
                    if(previousDate.equals("none")) previousDate = timeReceiveforCaller;
                    startFromDB = noReceive-1;


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date thisdate = simpleDateFormat.parse(timeReceiveforCaller);
                    Date previous = simpleDateFormat.parse(previousDateforCaller);
                    int thisday = thisdate.getDay();
                    int previousday = previous.getDay();
                    if (thisday != previousday) {
                        LinearLayout newDayLay = new LinearLayout(getApplicationContext());
                        newDayLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        newDayLay.setGravity(Gravity.CENTER_HORIZONTAL);
                        newDayLay.setPadding(0,20,0,35);
                        TextView tttt = new TextView(getApplicationContext());
                        tttt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                        tttt.setTextColor(Color.rgb(0, 0, 0));
                        tttt.setText("------------------"+simpleDateFormat.format(previous)+"------------------");
                        newDayLay.addView(tttt);
                        LinearLayout layout = (LinearLayout) findViewById(R.id.centerLayout);
                        layout.addView(newDayLay,1);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                String timeTv = "";
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date dddd = simpleDateFormat2.parse(timeReceiveforCaller);
                    timeTv=String.format("%02d:%02d",dddd.getHours(),dddd.getMinutes());
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                LinearLayout kk = new LinearLayout(getApplicationContext());
                LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );

                if (userReceive.equals(loginID)) {
                    kk = (LinearLayout)inflater.inflate(R.layout.mychat,null);
                    ((TextView)kk.findViewById(R.id.chatbubble)).setText(txtReceive);
                    ((TextView)kk.findViewById(R.id.time)).setText(timeTv);
                } else {
                    kk = (LinearLayout)inflater.inflate(R.layout.yourchat,null);
                    Picasso.with(getApplicationContext()).load(pictureReceive).resize(120, 120).transform(new CircleTransform()).into(((ImageView)kk.findViewById(R.id.imageView)));
                    ((TextView)kk.findViewById(R.id.userId)).setText(userReceive);
                    ((TextView)kk.findViewById(R.id.chatbubble)).setText(txtReceive);
                    ((TextView)kk.findViewById(R.id.time)).setText(timeTv);

                }

                LinearLayout layout = (LinearLayout) findViewById(R.id.centerLayout);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                });
                kk.setId(Integer.parseInt(noReceive.toString())+9999);
                layout.addView(kk, 1);
            }
            if(firstCall==true) {
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
                firstCall=false;
            }else{
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout layout = (LinearLayout)findViewById(Integer.parseInt(beforeFromDB.toString())+9999);
                        if(layout!=null) {
                            scrollView.scrollTo(0, layout.getBottom());
                        }
                    }
                });
            }
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String talk_no = (String) params[0];
                String no1 = (String) params[1];
                String no2 = (String) params[2];
                String link = "http://222.239.250.207:8080/TravelFriendAndroid/message/getSomeMessage";
                String data = URLEncoder.encode("talk_no", "UTF-8") + "=" + URLEncoder.encode(talk_no, "UTF-8");
                data += "&" + URLEncoder.encode("no1", "UTF-8") + "=" + URLEncoder.encode(no1, "UTF-8");
                data += "&" + URLEncoder.encode("no2", "UTF-8") + "=" + URLEncoder.encode(no2, "UTF-8");
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                jarray = new JSONArray();
                try {
                    jarray = new JSONObject(sb.toString()).getJSONArray("messageList");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jarray;
        }
    }

}