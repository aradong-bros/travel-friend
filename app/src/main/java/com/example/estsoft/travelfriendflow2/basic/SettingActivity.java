package com.example.estsoft.travelfriendflow2.basic;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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


import java.io.File;


public class SettingActivity extends AppCompatActivity {


    public String selectedImagePath;
    public ImageView imageView;

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        //프로필 사진 바꾸기
        imageView = (ImageView)findViewById(R.id.profile);
        Picasso.with(getApplicationContext()).load(selectedImagePath).transform(new CircleTransform()).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);
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

        Button dropoutButton = (Button)findViewById(R.id.dropoutButton);
        dropoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"회원탈퇴 되었습니다",Toast.LENGTH_LONG).show();
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
