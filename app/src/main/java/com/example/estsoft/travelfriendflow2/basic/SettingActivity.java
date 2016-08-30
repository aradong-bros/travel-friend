package com.example.estsoft.travelfriendflow2.basic;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;


import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SettingActivity extends AppCompatActivity {

    final int REQ_CODE_SELECT_IMAGE = 100;
    public String selectedImagePath;
    public ImageView imageView;
    String no;
    String name;
    String picture;
    String ip;

//    @Override
//    protected void onPause() {
//        super.onPause();
//        finish();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        final SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        try {
            JSONObject userData = new JSONObject(pref.getString("userData", ""));
            name = userData.getString("name");
            no = userData.getString("no");
            picture = userData.getString("picture");
            ip = pref.getString("ip","192.168.0.3");
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
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }

        });

        Button idChanger = (Button)findViewById(R.id.changeButton);
        idChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                alert.setTitle("아이디 변경");
                alert.setMessage("1 글자 이상 입력해주세요");

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


        Button setIPButton = (Button)findViewById(R.id.setIPButton);
        setIPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                alert.setTitle("IP 변경");

                final EditText input = new EditText(SettingActivity.this);
                input.setText(ip);
                alert.setView(input);

                alert.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        ip = input.getText().toString();
                        if(name.equals("")){
                            Toast.makeText(getApplicationContext(),"1글자 이상으로 만들어 주세요",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("ip", ip);
                        editor.commit();
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
                finish();
            }
        });

        if(isFBLoggedIn()){
            findViewById(R.id.WithdrawalButton).setVisibility(View.GONE);
        }

        ((Button)findViewById(R.id.WithdrawalButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isKakaoLoggedIn()) {
                    final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
                    AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                    alert.setMessage(appendMessage);
                    alert.setPositiveButton(getString(R.string.com_kakao_ok_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            UserManagement.requestUnlink(new UnLinkResponseCallback() {
                                @Override
                                public void onFailure(ErrorResult errorResult) {
                                    Logger.e(errorResult.toString());
                                }

                                @Override
                                public void onSessionClosed(ErrorResult errorResult) {
                                    Toast.makeText(getApplicationContext(), "탈퇴에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNotSignedUp() {
                                    Toast.makeText(getApplicationContext(), "가입되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onSuccess(Long userId) {
                                    Toast.makeText(getApplicationContext(), "탈퇴하였습니다.", Toast.LENGTH_SHORT).show();
                                    deleteUser(no);
                                    finish();
                                }
                            });
                            //dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton(getString(R.string.com_kakao_cancel_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            }
        });
    }

    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode==Activity.RESULT_OK)
            {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    selectedImagePath = getPath(data.getData());
                    File f = new File(selectedImagePath);
                    updatePicture(f);
                }
                catch (Exception e)		         {             e.printStackTrace();			}
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

    private void updatePicture(File f){

        class InsertData2 extends AsyncTask<File, Void, String> {
            //ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Picasso.with(getApplicationContext()).load(picture).transform(new CircleTransform()).into(imageView);
            }

            @Override
            protected String doInBackground(File... params) {

                try{
                    File f = params[0];
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    builder.addPart("picture",new FileBody(f));
                    InputStream inputStream = null;
                    HttpClient httpClient = AndroidHttpClient.newInstance("Android");
                    HttpPost httpPost = new HttpPost("http://222.239.250.207:8080/TravelFriendAndroid/user/profileUpload");
                    httpPost.setEntity(builder.build());
                    HttpResponse httpResponse  = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    inputStream = httpEntity.getContent();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;
                    while((line=bufferedReader.readLine())!=null){
                        stringBuilder.append(line+"\n");
                    }
                    inputStream.close();
                    picture = stringBuilder.toString();
                    insertToDatabase(name,picture,no);

                    return picture;
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData2 task = new InsertData2();
        task.execute(f);
    }

    private void deleteUser(String no){

        class InsertData2 extends AsyncTask<String, Void, String> {
            //ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String no = params[0];

                    String link="http://222.239.250.207:8080/TravelFriendAndroid/user/deleteUser";
                    String data  = URLEncoder.encode("no", "UTF-8") + "=" + URLEncoder.encode(no, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    SharedPreferences a = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = a.edit();
                    editor.putString("userData", "");
                    editor.commit();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                return "bye";
            }
        }

        InsertData2 task = new InsertData2();
        task.execute(no);
    }

}
