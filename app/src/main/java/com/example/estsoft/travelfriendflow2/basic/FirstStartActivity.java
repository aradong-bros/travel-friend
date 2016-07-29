package com.example.estsoft.travelfriendflow2.basic;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;

import me.relex.circleindicator.CircleIndicator;

public class FirstStartActivity extends AppCompatActivity {

    private static final int MAX_VIEWS = 4;
    private ViewPager _mViewPager;
    private ViewPageAdapter _adapter;
    private Button _btn1, _btn2, _btn3, _btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firststart);
        setUpView();
        setTab();
    }

    private void setUpView(){
        _mViewPager = (ViewPager)findViewById(R.id.pager);
        _adapter = new ViewPageAdapter(getApplicationContext(),getSupportFragmentManager());
        _mViewPager.setAdapter(_adapter);
        _mViewPager.setCurrentItem(0);
        initButton();
    }

    private void setTab(){
        _mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                btnAction(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private  void btnAction(int action){
        switch(action){
            case 0: setButton(_btn1,"",30,30);
                setButton(_btn2,"",20,20);
                setButton(_btn3,"",20,20);
                setButton(_btn4,"",20,20);
                break;
            case 1: setButton(_btn1,"",20,20);
                setButton(_btn2,"",30,30);
                setButton(_btn3,"",20,20);
                setButton(_btn4,"",20,20);
                break;
            case 2:setButton(_btn1,"",20,20);
                setButton(_btn2,"",20,20);
                setButton(_btn3,"",30,30);
                setButton(_btn4,"",20,20);
                break;
            case 3:setButton(_btn1,"",20,20);
                setButton(_btn2,"",20,20);
                setButton(_btn3,"",20,20);
                setButton(_btn4,"",30,30);
                break;
        }
    }

    private void initButton(){
        _btn1 = (Button)findViewById(R.id.btn1);
        _btn2 = (Button)findViewById(R.id.btn2);
        _btn3 = (Button)findViewById(R.id.btn3);
        _btn4 = (Button)findViewById(R.id.btn4);
        setButton(_btn1,"",30,30);
        setButton(_btn2,"",20,20);
        setButton(_btn3,"",20,20);
        setButton(_btn4,"",20,20);
    }

    private void setButton(Button btn,String text,int h, int w){
        android.view.ViewGroup.LayoutParams params = btn.getLayoutParams();
        params.width = w;
        params.height = h;
        btn.setLayoutParams(params);

        btn.setWidth(w);
        btn.setHeight(h);
    }
}



//    //마지막 페이지 버튼
//    private View.OnClickListener mCloseButtonClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            int infoFirst = 1;
//            SharedPreferences a = getSharedPreferences("pref",MODE_PRIVATE);
//            SharedPreferences.Editor editor = a.edit();
//            editor.putInt("First",infoFirst);
//            editor.commit();
//
//            (Toast.makeText(getApplicationContext(),"저장완료 : ", Toast.LENGTH_LONG)).show();
//            finish();
//        }
//    };
//
//    private View.OnClickListener mLoginButtonClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
//            startActivity(intent);
//        }
//    };
//
//    private View.OnClickListener mJoinButtonClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(getApplicationContext(),JoinActivity.class);
//            startActivity(intent);
//        }
//    };





//        @Override
//        public Object instantiateItem(View pager, int position){
//            View v = null;
//
//            if(position == 0){
//                v = mInflater.inflate(R.layout.firststartview1,null);
//                v.findViewById(R.id.textView);
//            }else if(position == 1){
//                v = mInflater.inflate(R.layout.firststartview2, null);
//                v.findViewById(R.id.textView2);
//            }else if(position == 2){
//                v = mInflater.inflate(R.layout.firststartview3,null);
//                v.findViewById(R.id.textView3);
//            }else {
//                v = mInflater.inflate(R.layout.firststartview4,null);
////                v.findViewById(R.id.textView4);
//                v.findViewById(R.id.loginButton).setOnClickListener(mLoginButtonClick);
//                v.findViewById(R.id.joinButton).setOnClickListener(mJoinButtonClick);
//                v.findViewById(R.id.close).setOnClickListener(mCloseButtonClick);
//            }
//            ((ViewPager)pager).addView(v,0);
//            return v;
//        }
//
//        @Override
//        public void destroyItem(View pager, int position, Object view){
//            ((ViewPager)pager).removeView((View)view);
//        }
//
//
//        public boolean isViewFromObject(View pager,Object obj){
//            return pager == obj;
//        }
//    }


