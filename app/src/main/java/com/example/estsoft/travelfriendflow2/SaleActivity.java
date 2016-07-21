package com.example.estsoft.travelfriendflow2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SaleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String activityName = getClass().getSimpleName();
        TextView tv = new TextView(this);
        tv.setText(activityName+ "화면");
        setContentView(tv);
    }

}
