package com.example.estsoft.travelfriendflow2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import org.json.JSONObject;

import java.util.Arrays;

public class JoinActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private TextView info;
    private LoginButton loginButton;


    private SessionCallback mKakaocallback;
    private com.kakao.usermgmt.LoginButton kakaoLogin;
    private String profileUrl;

    String email, name, gender, userId, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_join);


        info=(TextView)findViewById(R.id.textView);
        loginButton = (LoginButton)findViewById(R.id.startWithFacebookButton);
        loginButton.setReadPermissions(Arrays.asList("email"));


        callbackManager = CallbackManager.Factory.create();

        int infoFirst = 1;
        SharedPreferences a = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = a.edit();
        editor.putInt("First", infoFirst);
        editor.commit();

        //페이스북 key 가져오기
//        try {
//            PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(
//                    "com.example.estsoft.travelfriendflow2", //앱의 패키지 명
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//        } catch (NoSuchAlgorithmException e) {
//        }




        //카카오톡 키 가져오기
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.i("keyhash: ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        }
//        catch(PackageManager.NameNotFoundException e){}
//        catch(NoSuchAlgorithmException e){}

        //getAppKeyHash();
        mKakaocallback = new SessionCallback();
        com.kakao.auth.Session.getCurrentSession().addCallback(mKakaocallback);
        com.kakao.auth.Session.getCurrentSession().checkAndImplicitOpen();


//        kakaoLogin = (com.kakao.usermgmt.LoginButton)findViewById(R.id.startWithKakaoButton);
//        kakaoLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //isKakaoLogin();
//                Log.d("kakao login----------","login button CLICKED!!!!");
//            }
//        });



        //카카오톡 로그아웃 (임시)
        Button startWithEmailButton = (Button) findViewById(R.id.startWithEmailbutton);
        startWithEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), JoinWithEmailActivity.class);
//                startActivity(intent);
                UserManagement.requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {

                    }
                });
            }
        });



        //페이스북으로 회원가입
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {



                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Log.e("TAG","페이스북 로그인 결과"+response.toString());
                        try {
//                            JSONObject responseJSON = new JSONObject(response.toString());
//                            Log.e("------------------", responseJSON.toString());
//                            JSONObject graphJSON = responseJSON.getJSONObject("graphObject");

                            Log.e("tttt",object.toString());
                            Log.e("aaaaa", object.getString("email")+object.getString("name")+object.getString("gender"));
                            email = object.getString("email");
                            name = object.getString("name");
                            gender = object.getString("gender");

//                            handler.sendEmptyMessage(0);

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();

                userId= loginResult.getAccessToken().getUserId();
                token = loginResult.getAccessToken().getToken();




                Toast.makeText(getApplicationContext(),"페이스북으로 회원가입 되었습니다",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                info.setText("Login attemt canceled");
            }

            @Override
            public void onError(FacebookException error) {
                info.setText("Login attempt failed");
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
            Log.d("TAG" , "세션 오픈됨");
            // 사용자 정보를 가져옴, 회원가입 미가입시 자동가입 시킴
            KakaorequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Log.d("TAG" , exception.getMessage());
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
                    Log.d("TAG" , "오류로 카카오로그인 실패 ");
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("TAG" , "오류로 카카오로그인 실패 ");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Toast.makeText(JoinActivity.this,"success!!"+userProfile,Toast.LENGTH_LONG).show();
                profileUrl = userProfile.getProfileImagePath();
                userId = String.valueOf(userProfile.getId());
                name = userProfile.getNickname();

                Log.i("kakao login info----",""+userProfile);

                //handler.sendEmptyMessage(0);
            }

            @Override
            public void onNotSignedUp() {
                // 자동가입이 아닐경우 동의창
            }
        });
    }



//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//
//            info.setText(
//                    "User ID: "
//                            + userId
//                            +"\nAuth Token: "
//                            +token +
//                            "\nname: "
//                            +name + "\nemail: " +email + "\ngender: "+gender
//                    +"\nprofileUrl"+profileUrl
//            );
//
//        }
//    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }


//    private void getAppKeyHash() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                Log.d("Hash key", something);
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            Log.e("name not found", e.toString());
//        }
//    }
}



