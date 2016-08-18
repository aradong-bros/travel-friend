package com.example.estsoft.travelfriendflow2.basic;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.CircleTransform;
import com.example.estsoft.travelfriendflow2.R;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.kakao.auth.Session;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class SettingActivity extends AppCompatActivity {


    public String selectedImagePath;
    public ImageView imageView;
    String no;
    String name;
    String picture;

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        try {
            JSONObject userData = new JSONObject(pref.getString("userData", ""));
            name = userData.getString("name");
            no = userData.getString("no");
            picture = userData.getString("picture");
        } catch (Exception e) {
            e.printStackTrace();
        }


        TextView nameTv = (TextView)findViewById(R.id.Text6);
        nameTv.setText(name);




        //프로필 사진 바꾸기
        imageView = (ImageView)findViewById(R.id.profile);
        Picasso.with(getApplicationContext()).load(picture).transform(new CircleTransform()).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);
            }

        });

        Button idChanger = (Button)findViewById(R.id.changeButton);
        idChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                alert.setTitle("아이디 변경");
                alert.setTitle("아이디를 변경해 주세요");

                final EditText input = new EditText(SettingActivity.this);
                input.setText(name);
                alert.setView(input);

                alert.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        name = input.getText().toString();
                        if(name.equals("")){
                            Toast.makeText(getApplicationContext(),"1글자 이상으로 만들어 주세요",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        insertToDatabase(name,picture,no);
                    }
                });
                alert.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        return;
                    }
                });

                alert.show();
            }
        });


        Button logoutButton = (Button)findViewById(R.id.logoutButton);
        assert logoutButton != null;
        assert logoutButton != null;
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFBLoggedIn()){
                    LoginManager.getInstance().logOut();
                    Toast.makeText(getApplicationContext(),"로그아웃 되었습니다.[Facebook]",Toast.LENGTH_SHORT).show();

                }
                if(isKakaoLoggedIn()){
                    UserManagement.requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            Log.e("kakao","logout");
                            //Session.getCurrentSession().close();
                        }
                    });
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    UserManagement.requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            Log.e("kakao","logout");
                            //Session.getCurrentSession().close();
                        }
                    });
                    Toast.makeText(getApplicationContext(),"로그아웃 되었습니다.[Kakao]",Toast.LENGTH_SHORT).show();
                }
                SharedPreferences a = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = a.edit();
                editor.putString("userData", null);
                editor.commit();
                //String loggedoutinfo = a.getString("userData","없음");
                //Log.e("logout success-----",loggedoutinfo);
                //startActivity(new Intent(getApplicationContext(),MainActivity.class));
                startActivity(new Intent(getApplicationContext(),JoinActivity.class));
            }
        });



    }

    public void onActivityResult(int requestCode,int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                Log.d("-->selectedImagePath : ",selectedImagePath+"");

                File f = new File(selectedImagePath);
                Picasso.with(getApplicationContext()).load(f).transform(new CircleTransform()).into(imageView);
            }
        }
    }

    public String getPath(Uri uri){
        if(uri == null){
            return null;
        }

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor != null){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }


    public boolean isFBLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public boolean isKakaoLoggedIn(){
        return Session.getCurrentSession().isOpened();
    }


    private void insertToDatabase(final String name, String picture, String no){

        class InsertData extends AsyncTask<String, Void, String> {
            //ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                TextView ttt = (TextView)findViewById(R.id.Text6);
                ttt.setText(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String name = (String)params[0];
                    String picture = (String)params[1];
                    String no = (String)params[2];

                    String link="http://222.239.250.207:8080/TravelFriendAndroid/user/modifyUser";
                    String data  = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    data += "&" + URLEncoder.encode("picture", "UTF-8") + "=" + URLEncoder.encode(picture, "UTF-8");
                    data += "&" + URLEncoder.encode("no", "UTF-8") + "=" + URLEncoder.encode(no, "UTF-8");

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

                    //Log.e("sb is ------------->",sb.toString());
                    JSONObject profileData = new JSONObject();
                    String nameforsend = name;
                    try {
                        JSONObject getFromDB = new JSONObject(sb.toString()).getJSONObject("userVo");
                        Log.e("getFromDB is -->",getFromDB.toString());

                        profileData.put("name", getFromDB.getString("name")).put("userID", getFromDB.getString("userID")).put("picture", getFromDB.getString("picture")).put("platform", getFromDB.getString("platform")).put("no",getFromDB.getString("no"));
                        nameforsend=getFromDB.getString("name");
                        SharedPreferences a = getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = a.edit();
                        editor.putString("userData", profileData.toString());
                        editor.commit();
                        //Log.e("profileData is -->",profileData.toString());
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }

                    return nameforsend;
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(name,picture, no);
    }


}
