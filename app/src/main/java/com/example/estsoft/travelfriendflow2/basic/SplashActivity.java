package com.example.estsoft.travelfriendflow2.basic;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.estsoft.travelfriendflow2.R;

public class SplashActivity extends AppCompatActivity {
    private static final int MILLISECONDS_DELAYED_FINISH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.splashLayout);
        relativeLayout.setBackground(new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(),R.drawable.splash)));

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleView(findViewById(R.id.splashLayout));
    }

    private void recycleView(View view) {
        if(view != null) {
            Drawable bg = view.getBackground();
            if(bg != null) {
                bg.setCallback(null);
                ((BitmapDrawable)bg).getBitmap().recycle();
                view.setBackgroundDrawable(null);
            }
        }
    }

}
