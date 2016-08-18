package com.example.estsoft.travelfriendflow2.mytravel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.basic.MainActivity;
import com.example.estsoft.travelfriendflow2.thread.HttpMySetConnThread;
import com.example.estsoft.travelfriendflow2.thread.HttpParamConnThread;
import com.example.estsoft.travelfriendflow2.thread.Preference;

/**
 * MyTravelActivity -> MyTravelListActivity의 설정 버튼
 * */
public class MySettingActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MySettingActivity";
    private static String publicURL = "http://222.239.250.207:8080/TravelFriendAndroid/schedule/schModifyIsPublic";    // isPublic 수정
    private static String finishURL = "http://222.239.250.207:8080/TravelFriendAndroid/schedule/schModifyIsfinished";    // isFinished 수정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);

        Button save_btn = (Button)findViewById(R.id.save_btn);

        Intent intent = getIntent();
        final int schNo = intent.getIntExtra("schNo", -1);
        if( schNo == -1 ){
            Toast.makeText(getApplicationContext(), "Schedule No Error", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e(LOG_TAG, "Schedule NO :"+schNo);

        final ToggleButton tg_isPublic = (ToggleButton)findViewById(R.id.tg_isPublic);
        tg_isPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tg_isPublic.isChecked()){    // 공개 처리
                    new HttpMySetConnThread().execute(publicURL, schNo+"&isPublic=1");
                }else{  // 비공개 처리
                    new HttpMySetConnThread().execute(publicURL, schNo+"&isPublic=0");
                }
            }
        });


        final ToggleButton tg_isFinished = (ToggleButton)findViewById(R.id.tg_isFinished);
        tg_isFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tg_isFinished.isChecked()){  // 완성
                    new HttpMySetConnThread().execute(finishURL, schNo+"&isfinished=finished");
                }else{      // 미완성
                    new HttpMySetConnThread().execute(finishURL, schNo+"&isfinished=ongoing");
                }
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
