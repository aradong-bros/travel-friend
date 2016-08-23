package com.example.estsoft.travelfriendflow2.lookaround;

import android.app.TabActivity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.mytravel.*;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class OthersPlanTableActivity extends AppCompatActivity {

    ArrayList<Plan> plan = new ArrayList<Plan>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_plan_table);

        plan.add(new Plan(1,"plan 1-1"));
        plan.add(new Plan(1,"plan 1-2"));
        plan.add(new Plan(1,"plan 1-3"));
        plan.add(new Plan(2,"plan 2-1"));
        plan.add(new Plan(2,"plan 2-2"));
        plan.add(new Plan(3,"plan 3-1"));
        plan.add(new Plan(4,"plan 4-1"));
        plan.add(new Plan(4,"plan 4-2"));
        plan.add(new Plan(4,"plan 4-3"));
        plan.add(new Plan(4,"plan 4-4"));

        TextView dayTitle;

        LinearLayout topLL = (LinearLayout)findViewById(R.id.layout);
        LinearLayout parent = (LinearLayout)findViewById(R.id.parent);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        int maxIndex = 0;
        for(int i=0; i<plan.size(); i++){
            Plan temp = plan.get(i);
            if(maxIndex < temp.getDay()){
                maxIndex = temp.getDay();
            }
        }

        for(int num=1; num<=maxIndex; num++){
            int count = 0;
            for(int j=0; j<plan.size(); j++){
                if(plan.get(j).getDay() == num){
                    count ++;
                }
            }
            dayTitle = new TextView(OthersPlanTableActivity.this);



            dayTitle.setText("DAY "+num);
            dayTitle.setGravity(Gravity.CENTER);
            dayTitle.setTextColor(Color.parseColor("#FFFFFF"));
            dayTitle.setTextSize(20);
            dayTitle.setPadding(1,1,1,1);
            dayTitle.setHeight(count * 151);
            if(num%2==0) {
                dayTitle.setBackgroundColor(Color.parseColor("#eb9b00"));
            }else{
                dayTitle.setBackgroundColor(Color.parseColor("#eb6900"));
            }
            topLL.addView(dayTitle);
        }

        PlanAdapter adapter = new PlanAdapter(getApplicationContext(),R.layout.plan,plan);
        ListView lv = (ListView)findViewById(R.id.planlist);
//        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
        lv.setAdapter(adapter);

        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

}


class PlanAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<Plan> plan;
    LayoutInflater inf;

    public PlanAdapter(Context context, int layout, ArrayList<Plan> plan){
        this.context = context;
        this.layout = layout;
        this.plan = plan;
        this.inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount(){
        return plan.size();
    }

    @Override
    public Object getItem(int position){
        return plan.get(position);
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

        TextView title = (TextView)convertView.findViewById(R.id.plantitle);
        Plan p = plan.get(position);
        title.setText(p.title);

        return convertView;
    }
}

class Plan{
    String title = "";
    int day ;

    public Plan(int day,String title){ this.day = day; this.title = title; }
    public Plan(){}

    public int getDay() {
        return day;
    }
}