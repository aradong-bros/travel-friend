package com.example.estsoft.travelfriendflow2.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.CircleTransform;
import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.ChatData;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class ChatMainActivity extends Activity{
    //private final static String IP = "115.68.116.235";

    private final static String IP = "192.168.22.74";
    private final static int PORT = 10001;
    private final static String LOGIN_ID="AndroidClient";

    private final static int ID = 1;

    private TextView lblReceive;
    private String txtReceive;

    private Long regionReceive;
    private String userReceive;
    private String pictureReceive;

    private Socket socket;
    private InputStream in;
    private OutputStream out;


    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private final Handler handler = new Handler();
    private EditText editText;
    private String loginID;
    private Long regionNum;
    private String userImage;
    private ScrollView scrollView;


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_main);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        try {
            JSONObject userData = new JSONObject(pref.getString("userData", ""));
            loginID = userData.getString("name");
            userImage = userData.getString("picture");
        }catch(Exception e){
            e.printStackTrace();
        }

        scrollView = (ScrollView)findViewById(R.id.scrollView) ;
        scrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
            }
        });

        Intent intent = getIntent();
        regionNum = intent.getExtras().getLong("RegionNum");
        TextView regionInformer = (TextView)findViewById(R.id.textview_region);
        int rgrg = Integer.parseInt(regionNum.toString());
        switch (rgrg){
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

        editText = (EditText)findViewById(R.id.editText);

        RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.main_background);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
            }
        });

// -- Thread for Connection
        Thread cThread = new Thread(){public void run() {
            try {
                connect(IP, PORT);
                Log.i("실행------------->","소켓 연결 시도");
            } catch (Exception e) {
                Log.e("error:------------->", "소켓연결 실패");
            }
        }
        };
        cThread.start();
    }

    private void connect(String ip, int port){
        int size;
        byte[] w = new byte[10240];
        txtReceive="";
        try{
            socket = new Socket(ip,port);
            Log.i("수신 소켓시작됨------------->",socket.toString());
            if(socket != null) {
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.flush();
                ChatData d= new ChatData();
                d.setId(loginID);
                d.setRegionNum(regionNum);
                d.setTxt("has entered");
                d.setImage(userImage);
                oos.writeObject(d);
                oos.flush();
                ChatData dd = new ChatData();
                while(socket != null && socket.isConnected()){

                    if((dd=(ChatData)ois.readObject())==null) continue;
                    txtReceive = new String(dd.getTxt());
                    userReceive = new String(dd.getId());
                    regionReceive = new Long(dd.getRegionNum());
                    pictureReceive = new String(dd.getImage());
                    handler.post(new Runnable(){
                        public void run() {

                            lblReceive = new TextView(getApplicationContext());
                            lblReceive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                            lblReceive.setTextColor(Color.rgb(0,0,0));

                            //프로필 사진 레이아웃
                            LinearLayout profileLay = new LinearLayout(getApplicationContext());
                            profileLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                            profileLay.setOrientation(LinearLayout.HORIZONTAL);

                            //대화명과 말풍선 레이아웃
                            LinearLayout commentBubbleLay = new LinearLayout(getApplicationContext());
                            commentBubbleLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                            commentBubbleLay.setOrientation(LinearLayout.VERTICAL);

                            TextView talkName = new TextView(getApplicationContext());

                            talkName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                            talkName.setGravity(Gravity.CENTER_HORIZONTAL);
                            talkName.setTextColor(Color.rgb(0,0,0));
                            talkName.setMaxLines(1);
                            talkName.setEllipsize(TextUtils.TruncateAt.END);

                            //말풍선 텍뷰 속성
                            LinearLayout.LayoutParams paramparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                            paramparam.bottomMargin = 25;
                            //본인 레이아웃 속성
                            LinearLayout.LayoutParams myparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                            myparam.rightMargin = 30;
                            //상대 레이아웃 속성
                            LinearLayout.LayoutParams yourparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                            yourparam.leftMargin= 30;

                            if(regionReceive.equals(regionNum)) {
                                talkName.setText(userReceive.toString());
                                //프로필 사진 선언
                                ImageView profile = new ImageView(getApplicationContext());
                                if (userReceive.equals(loginID)) {
                                    lblReceive.setBackgroundResource(R.drawable.mymessage22);
                                    //profile = addImageView(R.drawable.profile1);
                                    Picasso.with(getApplicationContext()).load(userImage).resize(120,120).transform(new CircleTransform()).into(profile);
                                    commentBubbleLay.addView(talkName);
                                    commentBubbleLay.addView(lblReceive);
                                    commentBubbleLay.setGravity(Gravity.RIGHT);
                                    profileLay.addView(commentBubbleLay);
                                    profileLay.addView(profile);
                                    profileLay.setGravity(Gravity.RIGHT | Gravity.TOP);
                                    talkName.setLayoutParams(myparam);
                                } else {
                                    lblReceive.setBackgroundResource(R.drawable.yourmessage22);
                                    //profile = addImageView(R.drawable.profile2);
                                    Picasso.with(getApplicationContext()).load(pictureReceive).resize(120,120).transform(new CircleTransform()).into(profile);
                                    profileLay.addView(profile);
                                    commentBubbleLay.addView(talkName);
                                    commentBubbleLay.addView(lblReceive);
                                    commentBubbleLay.setGravity(Gravity.LEFT);
                                    profileLay.addView(commentBubbleLay);
                                    profileLay.setGravity(Gravity.TOP);
                                    talkName.setLayoutParams(yourparam);
                                }
                                lblReceive.setLayoutParams(paramparam);
                                LinearLayout layout = (LinearLayout) findViewById(R.id.centerLayout);
                                layout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                    }
                                });

                                lblReceive.setText(txtReceive);
                                layout.addView(profileLay);

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
            try{
                if(ois!=null) ois.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            try{
                if(oos!=null) oos.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            try{
                if(socket!=null) socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void sendClicked(View v) throws Exception{
        if(editText.getText().toString().length()!=0  && !editText.getText().toString().equals(" ")){
            ChatData d = new ChatData();
            d.setRegionNum(regionNum);
            d.setId(loginID);
            d.setTxt(editText.getText().toString());
            d.setImage(userImage);
            oos.writeObject(d);
            oos.flush();
            editText.setText("");
        };
    }

    public ImageView addImageView(int drawable){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable);
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
        bitmap = getCroppedBitmap(bitmap);
        ImageView view = new ImageView(getBaseContext());
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        view.setImageBitmap(bitmap);
        return view;
    }

    //bitmap 동그랗게 얻기
    //http://stackoverflow.com/questions/11932805/cropping-circular-area-from-bitmap-in-android
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}