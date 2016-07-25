package com.example.estsoft.travelfriendflow2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {

    Intent intent;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);
        intent = new Intent(ChatActivity.this, ChatMainActivity.class);
        bt = (Button)findViewById(R.id.button_idcheck);

        Button rbt1 = (Button)findViewById(R.id.button_region_no1);
        Button rbt2 = (Button)findViewById(R.id.button_region_no2);
        Button rbt3 = (Button)findViewById(R.id.button_region_no3);
        Button rbt4 = (Button)findViewById(R.id.button_region_no4);


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = (TextView)findViewById(R.id.editText_id);
                String id = tv.getText().toString();
                id = id.replaceAll("\\p{Space}","");
                if(id.length()==0 || id.equals("") || id.equals(" ")){
                    return;
                }
                tv.setEnabled(false);
                bt.setEnabled(false);
                intent.putExtra("ID", id);
            }
        });

        rbt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt.isEnabled()){
                    Toast.makeText(getApplicationContext(),"아이디 먼저 설정해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Long regionNum = 1L;
                intent.putExtra("RegionNum",regionNum);
                startActivity(intent);
                //finish();
                //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
            }
        });

        rbt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt.isEnabled()){
                    Toast.makeText(getApplicationContext(),"아이디 먼저 설정해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Long regionNum = 2L;
                intent.putExtra("RegionNum",regionNum);
                startActivity(intent);
                //finish();
                //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
            }
        });

        rbt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt.isEnabled()){
                    Toast.makeText(getApplicationContext(),"아이디 먼저 설정해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Long regionNum = 3L;
                intent.putExtra("RegionNum",regionNum);
                startActivity(intent);
                //finish();
                //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
            }
        });

        rbt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt.isEnabled()){
                    Toast.makeText(getApplicationContext(),"아이디 먼저 설정해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Long regionNum = 4L;
                intent.putExtra("RegionNum",regionNum);
                startActivity(intent);
                //finish();
                //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setIdsss(View v){
        TextView tv = (TextView)findViewById(R.id.editText_id);
        String id = tv.getText().toString();
        id = id.replaceAll("\\p{Space}","");
        if(id.length()==0 || id.equals("") || id.equals(" ")){
            return;
        }
        tv.setEnabled(false);
        bt.setEnabled(false);
        intent.putExtra("ID", id);
        return;
    }

    public void setRegionsss(View v){
        if(bt.isEnabled()){
            Toast.makeText(getApplicationContext(),"아이디 먼저 설정해주세요",Toast.LENGTH_SHORT).show();
            return;
        }

        int viewId = v.getId();
        Long regionNum = 1L;
        switch(viewId){
            case R.id.button_region_no1:
                regionNum = 1L;
                break;
            case R.id.button_region_no2:
                regionNum = 2L;
                break;
            case R.id.button_region_no3:
                regionNum = 3L;
                break;
            case R.id.button_region_no4:
                regionNum = 4L;
                break;
        }
        intent.putExtra("RegionNum",regionNum);
        startActivity(intent);
        finish();
        return;
        //Toast.makeText(getApplicationContext(),""+viewId,Toast.LENGTH_SHORT).show();
    }

}
