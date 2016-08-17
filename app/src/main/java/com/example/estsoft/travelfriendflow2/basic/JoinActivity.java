package com.example.estsoft.travelfriendflow2.basic;


import android.content.Context;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class JoinActivity extends AppCompatActivity {

    //페이스북 용
    CallbackManager callbackManager;
    private LoginButton loginButton;

    //카카오톡 용
    private SessionCallback mKakaocallback;
    private com.kakao.usermgmt.LoginButton kakaoLogin;
    //private String profileUrl;


    String email, name, gender, userId, token, picture, platform;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_join);


        int infoFirst = 1;
        SharedPreferences a = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = a.edit();
        editor.putInt("First", infoFirst);
        editor.commit();

        //facebook
        loginButton = (LoginButton) findViewById(R.id.startWithFacebookButton);
        loginButton.setReadPermissions(Arrays.asList("email"));
        callbackManager = CallbackManager.Factory.create();


        //페이스북 key 가져오기
        try {
            PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(
                    "com.example.estsoft.travelfriendflow2", //앱의 패키지 명
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        //카카오톡 키 가져오기
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("keyhash: ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch(PackageManager.NameNotFoundException e){}
        catch(NoSuchAlgorithmException e){}


        if(isFBLoggedIn()){
            //Toast.makeText(getApplicationContext(),"FB로그인 되어 있음",Toast.LENGTH_LONG).show();
            //startActivity(new Intent(JoinActivity.this,MainActivity.class));
            finish();
        }
        if(isKakaoLoggedIn()){
            //Toast.makeText(getApplicationContext(),"Kakao already logged in",Toast.LENGTH_LONG).show();
            //startActivity(new Intent(JoinActivity.this,MainActivity.class));
            finish();
        }


//        mKakaocallback = new SessionCallback();
//        com.kakao.auth.Session.getCurrentSession().addCallback(mKakaocallback);
//        com.kakao.auth.Session.getCurrentSession().checkAndImplicitOpen();
//        com.kakao.auth.Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, JoinActivity.this);

        Button kakaoLogin = (Button)findViewById(R.id.customkakao);
        kakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isKakaoLogin();
            }
        });

//카카오톡 로그아웃 (임시)
//        Button startWithEmailButton = (Button) findViewById(R.id.startWithEmailbutton);
//        startWithEmailButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(getApplicationContext(), JoinWithEmailActivity.class);
////                startActivity(intent);
//                UserManagement.requestLogout(new LogoutResponseCallback() {
//                    @Override
//                    public void onCompleteLogout() {
//
//                    }
//                });
//            }
//        });


        //페이스북으로 회원가입
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        //Log.e("TAG", "페이스북 로그인 결과" + response.toString());
                        try {
                            userId = object.getString("id");
                            name = object.getString("name");
                            JSONObject jsonObject = new JSONObject(object.getString("picture"));
                            picture =  jsonObject.getJSONObject("data").getString("url");
                            platform = "facebook";

                            Log.e("facebook login info--->", userId + name + picture);

                            insertToDatabase(name, userId, picture, platform);
                            //gender = object.getString("gender");
                            //email = object.getString("email");
//                            handler.sendEmptyMessage(0);



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,picture");
                request.setParameters(parameters);
                request.executeAsync();
                //userId= loginResult.getAccessToken().getUserId();
                //token = loginResult.getAccessToken().getToken();





                //Toast.makeText(getApplicationContext(), "페이스북으로 접속합니다", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                //info.setText("Login attemt canceled");
            }

            @Override
            public void onError(FacebookException error) {
                //info.setText("Login attempt failed");
            }
        });
    }

    private void isKakaoLogin() {
        // 카카오 세션을 오픈한다
        mKakaocallback = new SessionCallback();
        com.kakao.auth.Session.getCurrentSession().addCallback(mKakaocallback);
        com.kakao.auth.Session.getCurrentSession().checkAndImplicitOpen();
        com.kakao.auth.Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, JoinActivity.this);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Log.d("TAG", "세션 오픈됨");
            // 사용자 정보를 가져옴, 회원가입 미가입시 자동가입 시킴
            KakaorequestMe();

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Log.d("TAG", exception.getMessage());
            }
        }
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void KakaorequestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                int ErrorCode = errorResult.getErrorCode();
                int ClientErrorCode = -777;

                if (ErrorCode == ClientErrorCode) {
                    Toast.makeText(getApplicationContext(), "카카오톡 서버의 네트워크가 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("TAG", "오류로 카카오로그인 실패 ");
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("TAG", "오류로 카카오로그인 실패 ");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Session.getCurrentSession().checkAccessTokenInfo();
                //Toast.makeText(JoinActivity.this, "success!!" + userProfile, Toast.LENGTH_LONG).show();
                picture = userProfile.getThumbnailImagePath();
                userId = String.valueOf(userProfile.getId());
                name = userProfile.getNickname();
                platform = "kakao";

                insertToDatabase(name, userId, picture, platform);
                Log.e("kakao login info----", userProfile.toString());
                //Toast.makeText(getApplicationContext(), "카카오톡으로 접속합니다", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //startActivity(intent);
                finish();
            }

            @Override
            public void onNotSignedUp() {
                // 자동가입이 아닐경우 동의창
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
//                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(0);
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)
                && callbackManager.onActivityResult(requestCode, resultCode, data)
                ) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void insertToDatabase(final String name, String userID, String picture, String platform){

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(JoinActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String name = (String)params[0];
                    String userID = (String)params[1];
                    String picture = (String)params[2];
                    String platform = (String)params[3];


                    String link="http://222.239.250.207:8080/TravelFriendAndroid/user/insertUser";
                    String data  = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    data += "&" + URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                    data += "&" + URLEncoder.encode("picture", "UTF-8") + "=" + URLEncoder.encode(picture, "UTF-8");
                    data += "&" + URLEncoder.encode("platform", "UTF-8") + "=" + URLEncoder.encode(platform, "UTF-8");

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
                    try {
                        JSONObject getFromDB = new JSONObject(sb.toString()).getJSONObject("userVo");
                        Log.e("getFromDB is -->",getFromDB.toString());

                        profileData.put("name", getFromDB.getString("name")).put("userID", getFromDB.getString("userID")).put("picture", getFromDB.getString("picture")).put("platform", getFromDB.getString("platform")).put("no",getFromDB.getString("no"));

                        SharedPreferences a = getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = a.edit();
                        editor.putString("userData", profileData.toString());
                        editor.commit();
                        //Log.e("profileData is -->",profileData.toString());
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }

                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(name,userID, picture, platform);
    }


    //facebook login 확인
    public boolean isFBLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public boolean isKakaoLoggedIn(){
        return Session.getCurrentSession().isOpened();
    }

}
