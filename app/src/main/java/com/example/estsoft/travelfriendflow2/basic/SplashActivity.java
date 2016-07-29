package com.example.estsoft.travelfriendflow2.basic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.estsoft.travelfriendflow2.R;

public class SplashActivity extends AppCompatActivity {
    private static final int MILLISECONDS_DELAYED_FINISH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // main
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        },MILLISECONDS_DELAYED_FINISH);

    }

}
