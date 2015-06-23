package com.zari.matan.navigationdrawerexample.helper;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Matan on 6/23/2015
 */
public class Drawer extends DrawerLayout {

    boolean shouldDispatchTouchEvent;

    public boolean shouldDispatchTouchEvent() {

        return shouldDispatchTouchEvent;
    }

    public void setShouldDispatchTouchEvent(boolean shouldDispatchTouchEvent) {
        this.shouldDispatchTouchEvent = shouldDispatchTouchEvent;
    }

    public Drawer(Context context) {
        super(context);
    }

    public Drawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Drawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !shouldDispatchTouchEvent;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return shouldDispatchTouchEvent && super.onInterceptTouchEvent(ev);
    }


}
