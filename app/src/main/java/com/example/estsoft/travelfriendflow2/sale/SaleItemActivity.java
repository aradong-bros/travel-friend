package com.example.estsoft.travelfriendflow2.sale;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.lookaround.OthersPlanMapActivity;
import com.example.estsoft.travelfriendflow2.lookaround.OthersPlanTableActivity;

public class SaleItemActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_item);

        Intent intent;

        intent = getIntent();
        String title = intent.getStringExtra("title");

        TextView t = (TextView)findViewById(R.id.title);
        t.setText(title);

    }

}

