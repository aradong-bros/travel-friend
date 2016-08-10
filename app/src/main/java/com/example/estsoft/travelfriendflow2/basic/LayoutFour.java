package com.example.estsoft.travelfriendflow2.basic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.estsoft.travelfriendflow2.R;

public class LayoutFour extends Fragment {



    public static Fragment newInstance(Context context) {
        LayoutFour f = new LayoutFour();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.firststartview4, null);


        Button button = (Button)root.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity().getApplicationContext(),JoinActivity.class);
//                startActivity(intent);
                getActivity().finish();
            }
        });

        return root;
    }


}
