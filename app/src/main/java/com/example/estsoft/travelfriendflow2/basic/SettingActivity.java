package com.example.estsoft.travelfriendflow2.basic;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //SharedPreferences a = getSharedPreferences("pref", MODE_PRIVATE);
        //String loggedoutinfo = a.getString("userData","없음");
        //Log.e("current info-----",loggedoutinfo);


        ImageView imageView = (ImageView)findViewById(R.id.profile);
        Picasso.with(getApplicationContext()).load("https://scontent.xx.fbcdn.net/v/t1.0-9/12011354_171091463233969_4930354003965117617_n.jpg?oh=f14da56919c0cb11290d1427135cc785&oe=58293D19").transform(new CircleTransform()).into(imageView);

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

        Button dropoutButton = (Button)findViewById(R.id.dropoutButton);
        dropoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"회원탈퇴 되었습니다",Toast.LENGTH_LONG).show();
            }
        });


    }

    public void dialogChangePassword(View view) {
        LayoutInflater inflater = getLayoutInflater();
        final View customView = inflater.inflate(R.layout.dialog_password, null);

        new android.app.AlertDialog.Builder(this).
                setTitle("비밀 번호 변경").
                setView(customView).
                setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextView tv = (TextView) customView.findViewById(R.id.password);
                        String password = tv.getText().toString();
                        Log.d("-------->", password);
                    }
                }).
                show();
    }


    public boolean isFBLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public boolean isKakaoLoggedIn(){
        return Session.getCurrentSession().isOpened();
    }


}
