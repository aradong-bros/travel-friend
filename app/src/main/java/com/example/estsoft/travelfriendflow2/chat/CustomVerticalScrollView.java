package com.example.estsoft.travelfriendflow2.chat;

import android.content.Context;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

/**
 * 스크롤뷰가 가장자리에 다다랐는지 확인하기 위해 사용하는 커스텀 ScrollView
 * @author 이현정(g.littlepanda@gmail.com)
 */
public class CustomVerticalScrollView extends ScrollView {

    protected OnEdgeTouchListener onEdgeTouchListener;


    public static enum DIRECTION { TOP, BOTTOM, NONE };

    public CustomVerticalScrollView(Context context) {
        super(context);
    }

    public CustomVerticalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVerticalScrollView(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnEdgeTouchListener(OnEdgeTouchListener l){
        this.onEdgeTouchListener = l;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(onEdgeTouchListener != null){
            if(getScrollY()==0){
                onEdgeTouchListener.onEdgeTouch(DIRECTION.TOP);
            } else if((getScrollY() + getHeight())==computeVerticalScrollRange()){
                onEdgeTouchListener.onEdgeTouch(DIRECTION.BOTTOM);
            } else {
                onEdgeTouchListener.onEdgeTouch(DIRECTION.NONE);
            }
        }
    }

    public interface OnEdgeTouchListener {
        void onEdgeTouch(DIRECTION direction);
    }
}