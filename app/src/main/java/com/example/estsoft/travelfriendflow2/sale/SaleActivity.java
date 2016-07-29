package com.example.estsoft.travelfriendflow2.sale;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.R;

import java.util.ArrayList;

public class SaleActivity extends Activity {

    ArrayList<SaleItem> saleItems = new ArrayList<SaleItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        String[] option = getResources().getStringArray(R.array.spinnerArray1);
        Spinner spin = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,option);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        String[] option2 = getResources().getStringArray(R.array.spinnerArray2);
        Spinner spin2 = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,option2);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(adapter2);


        saleItems.add(new SaleItem("제목", ContextCompat.getDrawable(this,R.drawable.attraction)));
        saleItems.add(new SaleItem("제목2", ContextCompat.getDrawable(this,R.drawable.attraction)));


        SaleItemAdapter saleItemAdapter = new SaleItemAdapter(getApplicationContext(),R.layout.saleitem,saleItems);
        ListView lv = (ListView)findViewById(R.id.salelist);
        lv.setAdapter(saleItemAdapter);

    }

}


class SaleItemAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<SaleItem> saleItems;
    LayoutInflater inf;

    public SaleItemAdapter(Context context, int layout, ArrayList<SaleItem> saleItems){
        this.context = context;
        this.layout = layout;
        this.saleItems = saleItems;
        this.inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return saleItems.size();
    }

    @Override
    public Object getItem(int position){
        return saleItems.get(position);
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

        TextView title = (TextView)convertView.findViewById(R.id.SaleTextBox);
        ImageView image = (ImageView)convertView.findViewById(R.id.SaleImage);

        SaleItem t = saleItems.get(position);
        title.setText(t.title);
        image.setImageDrawable(t.image);
        return convertView;
    }
}

class SaleItem{
    String title = "";
    Drawable image;
    public SaleItem(String title, Drawable image){
        this.title = title;
        this.image = image;
    }

    public SaleItem(){}
}