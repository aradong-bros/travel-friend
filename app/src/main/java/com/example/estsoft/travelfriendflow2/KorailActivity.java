package com.example.estsoft.travelfriendflow2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class KorailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String activityName = getClass().getSimpleName();
        TextView tv = new TextView(this);
        tv.setText(activityName+ "화면");
        setContentView(tv);
    }

}
