package com.example.estsoft.travelfriendflow2.basic;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;

//어댑터
public class ViewPageAdapter extends FragmentPagerAdapter {
    private LayoutInflater mInflater;
    private Context _context;
    public static int totalPage = 4;

    public ViewPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        _context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch (position) {
            case 0:
                f = LayoutOne.newInstance(_context);
                break;
            case 1:
                f = LayoutTwo.newInstance(_context);
                break;
            case 2:
                f = LayoutThree.newInstance(_context);
                break;
            case 3:
                f = LayoutFour.newInstance(_context);
                break;
        }
        return f;
    }

    @Override
    public int getCount() {
        return totalPage;
    }
}