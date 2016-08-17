package com.example.estsoft.travelfriendflow2.mytravel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.estsoft.travelfriendflow2.R;

public class MySettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);

//        final ToggleButton tg_isPublic = (ToggleButton)findViewById(R.id.tg_isPublic);
//        tg_isPublic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(tg_isPublic.isChecked()){
//                    Toast.makeText(MySettingActivity.this, tg_isPublic.isChecked()+"if문", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(MySettingActivity.this, "else문", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }
}
