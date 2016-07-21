package com.example.estsoft.travelfriendflow2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AttractionActivity extends Activity {

    ArrayList<Reply> reply = new ArrayList<Reply>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction);

        reply.add(new Reply("아이디1","덧글내용1"));
        reply.add(new Reply("아이디2","덧글내용2"));
        reply.add(new Reply("아이디3","덧글내용3"));
        reply.add(new Reply("아이디4","덧글내용4"));

        MyAdapter2 adapter = new MyAdapter2(getApplicationContext(),R.layout.reply,reply);
        ListView lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(adapter);
    }

}

class MyAdapter2 extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<Reply> reply;
    LayoutInflater inf;

    public MyAdapter2(Context context, int layout, ArrayList<Reply> reply){
        this.context = context;
        this.layout = layout;
        this.reply = reply;
        this.inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return reply.size();
    }

    @Override
    public Object getItem(int position){
        return reply.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = inf.inflate(layout, null);
        }

        TextView id = (TextView)convertView.findViewById(R.id.idBox);
        TextView context = (TextView)convertView.findViewById(R.id.contextBox);

        Reply t = reply.get(position);
        id.setText(t.id);
        context.setText(t.context);
        return convertView;
    }
}

class Reply{
    String id = "";
    String context ="";
    public Reply(String id,String context){
        this.id = id;
        this.context = context;
    }

    public Reply(){}
}