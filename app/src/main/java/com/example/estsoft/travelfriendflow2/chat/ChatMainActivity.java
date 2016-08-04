package com.example.estsoft.travelfriendflow2.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.chat.ChatData;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
public class ChatMainActivity extends Activity{
    //private final static String IP = "115.68.116.235";

    private final static String IP = "192.168.230.4";
    private final static int PORT = 10001;
    private final static String LOGIN_ID="AndroidClient";

    private final static int ID = 1;

    private TextView lblReceive;
    private String txtReceive;

    private Long regionReceive;
    private Long userReceive;

    private Socket socket;
    private InputStream in;
    private OutputStream out;


    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private final Handler handler = new Handler();
    private EditText editText;
    private String loginID;
    private Long regionNum;
    private ScrollView scrollView;


    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_main);
//        RelativeLayout layout = (RelativeLayout)findViewById(R.id.centerLayout);
        //layout.setBackgroundColor(Color.rgb(200, 200, 200));


        scrollView = (ScrollView)findViewById(R.id.scrollView) ;
        scrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
            }
        });



        Intent intent = getIntent();
        loginID = intent.getExtras().getString("ID");
        regionNum = intent.getExtras().getLong("RegionNum");

        editText = (EditText)findViewById(R.id.editText);

        RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.main_background);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
            }
        });

// -- label for messages
//        lblReceive = new TextView(this);
//        //lblReceive.setId(ID);
//        lblReceive.setText("");
//        lblReceive.setTextSize(16.0f);
//        lblReceive.setTextColor(Color.rgb(0,0,0));
//        //RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
//        //lblReceive.setLayoutParams(param1);
//        layout.addView(lblReceive);


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
                //in = socket.getInputStream();
                //out = socket.getOutputStream();

                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.flush();

                ChatData d= new ChatData();
                d.setId(Long.parseLong(loginID));
                d.setRegionNum(regionNum);
                d.setTxt("has entered");


                oos.writeObject(d);
                oos.flush();

                //PrintWriter pw = new PrintWriter( new OutputStreamWriter( out ) );
                //pw.println(loginID);
                //pw.flush() ;

                ChatData dd = new ChatData();
                while(socket != null && socket.isConnected()){

                    if((dd=(ChatData)ois.readObject())==null) continue;
                    //size = in.read(w);
                    //if(size<=0)continue;

//                    if(obj instanceof ChatData)
//                        dd = (ChatData)obj;
//                    else continue;

                    txtReceive = new String(dd.getTxt());
                    Log.e("txt",txtReceive);
                    userReceive = new Long(dd.getId());
                    Log.e("user",userReceive.toString());
                    regionReceive = new Long(dd.getRegionNum());
                    Log.e("region",regionReceive.toString());


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


                            //String idid = "";
                            //Long regionregion = 0L;
                            //int endofID = txtReceive.indexOf("%");
                            //int endofRegion = txtReceive.indexOf("$");

                            //if(endofRegion>0){
                            //    regionregion = Long.parseLong(txtReceive.substring(endofID+1,endofRegion));
                            //}

                            Log.e("before if","123123");
                            if(regionReceive.equals(regionNum)) {
                                Log.e("after if","12312312312311231723971239");
                                talkName.setText(userReceive.toString());

                                //프로필 사진 선언
                                ImageView profile;

                                if (userReceive.equals(Long.parseLong(loginID))) {
                                    lblReceive.setBackgroundResource(R.drawable.mymessage22);
                                    profile = addImageView(R.mipmap.ic_launcher);       // 원래 피카츄
                                    commentBubbleLay.addView(talkName);
                                    commentBubbleLay.addView(lblReceive);
                                    commentBubbleLay.setGravity(Gravity.RIGHT);
                                    profileLay.addView(commentBubbleLay);
                                    profileLay.addView(profile);
                                    profileLay.setGravity(Gravity.RIGHT | Gravity.TOP);
                                    talkName.setLayoutParams(myparam);
                                } else {
                                    lblReceive.setBackgroundResource(R.drawable.yourmessage22);
                                    profile = addImageView(R.mipmap.ic_launcher);  // 원래 피카츄
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


//                            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                            addLay = (LinearLayout) inflater.inflate(R.layout.chatting_message, addLay);
//                            TextView taav = (TextView)addLay.findViewById(R.id.chatmessage);
//                            taav.setText(bb);


                                // 자동 스크롤 되는 부분
                                //참조 http://egloos.zum.com/Mitcehll/v/999007

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
        } catch (Exception ex) {Log.e("socket",ex.toString());
            ex.printStackTrace();}
    }


    public void sendClicked(View v) throws Exception{
        //PrintWriter pw = new PrintWriter( new OutputStreamWriter( out ) );

        Log.e("111",editText.getText().toString());
        if(editText.getText().toString().length()!=0  && !editText.getText().toString().equals(" ")){

            //pw.println(regionNum+"$"+editText.getText());
            //pw.flush();
            ChatData d = new ChatData();
            d.setRegionNum(regionNum);
            d.setId(Long.parseLong(loginID));
            d.setTxt(editText.getText().toString());

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