package com.example.estsoft.travelfriendflow2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class LookAroundActivity extends Activity {

    ArrayList<Travel> tr = new ArrayList<Travel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookaround);

        tr.add(new Travel("제목"));
        tr.add(new Travel("제목2"));
        tr.add(new Travel("제목3"));
        tr.add(new Travel("제목4"));
        tr.add(new Travel("제목5"));
        tr.add(new Travel("제목6"));

        MyAdapter adapter = new MyAdapter(getApplicationContext(),R.layout.row,tr);
        ListView lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"234",Toast.LENGTH_SHORT).show();
            }
        });

    }

}

class MyAdapter extends BaseAdapter{
    Context context;
    int layout;
    ArrayList<Travel> tr;
    LayoutInflater inf;

    public MyAdapter(Context context, int layout, ArrayList<Travel> tr){
        this.context = context;
        this.layout = layout;
        this.tr = tr;
        this.inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return tr.size();
    }

    @Override
    public Object getItem(int position){
        return tr.get(position);
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

        TextView title = (TextView)convertView.findViewById(R.id.lookAroundTextBox);

        Travel t = tr.get(position);
        title.setText(t.title);
        return convertView;
    }
}

class Travel{
    String title = "";
    public Travel(String title){
        this.title = title;
    }

    public Travel(){}
}