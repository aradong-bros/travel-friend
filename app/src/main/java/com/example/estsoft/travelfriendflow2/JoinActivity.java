package com.example.estsoft.travelfriendflow2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        int infoFirst = 1;
        SharedPreferences a = getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = a.edit();
        editor.putInt("First",infoFirst);
        editor.commit();

        //이메일로 회원가입
        Button startWithEmailButton = (Button)findViewById(R.id.startWithEmailbutton);
        startWithEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getApplicationContext(),JoinWithEmailActivity.class);
                startActivity(intent);
            }
        });

        //페이스북으로 회원가입
        Button startWithFacebookButton = (Button)findViewById(R.id.startWithFacebookButton);
        startWithFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"페이스북으로 회원가입 되었습니다",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }


}
