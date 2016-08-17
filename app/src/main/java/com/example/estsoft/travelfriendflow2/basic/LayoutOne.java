package com.example.estsoft.travelfriendflow2.basic;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.estsoft.travelfriendflow2.R;

public class LayoutOne extends Fragment {
    private ViewGroup root;

    public static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.firststartview1, null);
        ImageView imageView = (ImageView)root.findViewById(R.id.img_sample1);
        imageView.setBackgroundDrawable(new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(),R.drawable.phone_sample)));

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycleView(root.findViewById(R.id.img_sample1));
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
