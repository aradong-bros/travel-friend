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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estsoft.travelfriendflow2.R;
import com.example.estsoft.travelfriendflow2.mytravel.*;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class OthersPlanTableActivity extends AppCompatActivity {

    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<String>> mChildList = null;
    private ArrayList<String> mChildListContent1 = null;
    private ArrayList<String> mChildListContent2 = null;
    private ArrayList<String> mChildListContent3 = null;
    private ExpandableListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_plan_table);

        setLayout();

        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<String>>();
        mChildListContent1 = new ArrayList<String>();
        mChildListContent2 = new ArrayList<String>();
        mChildListContent3 = new ArrayList<String>();

        mGroupList.add("2016.08.23");
        mGroupList.add("서울");
        mGroupList.add("2016.08.24");
        mGroupList.add("서울");
        mGroupList.add("가평");

        mChildListContent1.add("1");
        mChildListContent1.add("2");
        mChildListContent1.add("3");

        mChildListContent2.add("4");
        mChildListContent2.add("5");
        mChildListContent2.add("6");

        mChildListContent3.add("7");
        mChildListContent3.add("8");
        mChildListContent3.add("9");

        mChildList.add(null);
        mChildList.add(mChildListContent1);
        mChildList.add(null);
        mChildList.add(mChildListContent2);
        mChildList.add(mChildListContent3);

        mListView.setAdapter(new BaseExpandableAdapter(this, mGroupList, mChildList));

    }

    private void setLayout(){
        mListView = (ExpandableListView)findViewById(R.id.elv_list);
    }

}

class BaseExpandableAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> groupList = null;
    private ArrayList<ArrayList<String>> childList = null;
    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;

    public BaseExpandableAdapter(Context c, ArrayList<String> groupList, ArrayList<ArrayList<String>> childList){

        super();
        this.inflater = LayoutInflater.from(c);
        this.groupList = groupList;
        this.childList = childList;
    }

    @Override
    public String getGroup(int groupPosition){
        return groupList.get(groupPosition);
    }

    @Override
    public int getGroupCount(){
        return groupList.size();
    }

    @Override
    public long getGroupId(int groupPosition){
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null){
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.test, parent, false);
            viewHolder.tv_groupName = (TextView)v.findViewById(R.id.tv_group);
//            viewHolder.iv_image = (ImageView)v.findViewById(R.id.iv_image);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)v.getTag();
        }

//        if(isExpanded){
//            viewHolder.iv_image.setBackgroundColor(Color.GREEN);
//        }else{
//            viewHolder.iv_image.setBackgroundColor(Color.WHITE);
//        }

        viewHolder.tv_groupName.setText(getGroup(groupPosition));

        return v;
    }

    @Override
    public String getChild(int groupPosition, int childPosition){
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition){
        if(childList.get(groupPosition)==null){
            return 0;
        }
        return childList.get(groupPosition).size();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition){
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null){
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.test, null);
            viewHolder.tv_childName = (TextView) v.findViewById(R.id.tv_child);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)v.getTag();
        }

        viewHolder.tv_childName.setText(getChild(groupPosition, childPosition));

        return v;
    }

    @Override
    public boolean hasStableIds(){return true;}

    @Override
    public boolean isChildSelectable(int groupPostion, int childPosition){return true;}



    class ViewHolder{
        public ImageView iv_image;
        public TextView tv_groupName;
        public TextView tv_childName;
    }
}