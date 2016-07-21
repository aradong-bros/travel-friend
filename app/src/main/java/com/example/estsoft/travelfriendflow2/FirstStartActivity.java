package com.example.estsoft.travelfriendflow2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class FirstStartActivity extends AppCompatActivity {

    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firststart);

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new PagerAdapterClass(getApplicationContext()));

    }

    private View.OnClickListener mCloseButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int infoFirst = 1;
            SharedPreferences a = getSharedPreferences("pref",MODE_PRIVATE);
            SharedPreferences.Editor editor = a.edit();
            editor.putInt("First",infoFirst);
            editor.commit();

            (Toast.makeText(getApplicationContext(),"저장완료 : ", Toast.LENGTH_LONG)).show();
            finish();
        }
    };

    private View.OnClickListener mLoginButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener mJoinButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(),JoinActivity.class);
            startActivity(intent);
        }
    };

    private class PagerAdapterClass extends PagerAdapter{
        private LayoutInflater mInflater;

        public PagerAdapterClass(Context c){
            super();
            mInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount(){
            return 4;
        }

        @Override
        public Object instantiateItem(View pager, int position){
            View v = null;

            if(position == 0){
                v = mInflater.inflate(R.layout.firststartview1,null);
                v.findViewById(R.id.textView);
            }else if(position == 1){
                v = mInflater.inflate(R.layout.firststartview2, null);
                v.findViewById(R.id.textView2);
            }else if(position == 2){
                v = mInflater.inflate(R.layout.firststartview3,null);
                v.findViewById(R.id.textView3);
            }else {
                v = mInflater.inflate(R.layout.firststartview4,null);
//                v.findViewById(R.id.textView4);
                v.findViewById(R.id.loginButton).setOnClickListener(mLoginButtonClick);
                v.findViewById(R.id.joinButton).setOnClickListener(mJoinButtonClick);
                v.findViewById(R.id.close).setOnClickListener(mCloseButtonClick);
            }
            ((ViewPager)pager).addView(v,0);
            return v;
        }

        @Override
        public void destroyItem(View pager, int position, Object view){
            ((ViewPager)pager).removeView((View)view);
        }


        public boolean isViewFromObject(View pager,Object obj){
            return pager == obj;
        }
    }


}
