package com.example.estsoft.travelfriendflow2;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ImageView imageView = (ImageView)findViewById(R.id.profile);
        Picasso.with(getApplicationContext()).load("https://scontent.xx.fbcdn.net/v/t1.0-1/p160x160/13256226_995591200566299_3486062778365119937_n.jpg?oh=f7c9bcdb95f75616a7c113659995f9c3&oe=5836967E").transform(new CircleTransform()).into(imageView);

        Button logoutButton = (Button)findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"로그아웃 되었습니다.",Toast.LENGTH_SHORT).show();
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


}
