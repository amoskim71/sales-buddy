package com.ngocketit.saovietcrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.ngocketit.saovietcrm.R;

public class BaseSectionHeaderListItemView extends LinearLayout {
    private View headerView = null;
    
    public BaseSectionHeaderListItemView(Context context, AttributeSet attrs, int layout)
    {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(layout, this);
        headerView = findViewById(getSectionHeaderViewId());
    }
    
    protected int getSectionHeaderViewId() {
    	return R.id.listSectionHeaderContainerView;
    }

    public boolean onTouchEvent(MotionEvent evt) {
    	if (headerView == null) {
    		return super.onTouchEvent(evt);
    	}

        switch (evt.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float ex = evt.getX();
                float ey = evt.getY();
                
                int x = headerView.getLeft();
                int y = headerView.getTop();
                int w = headerView.getWidth();
                int h = headerView.getHeight();

                if (ex >= x && ex <= x + w && ey >= y && ey <= y + h)
                    return true;
            default:
                return super.onTouchEvent(evt);
        }
    }
}
