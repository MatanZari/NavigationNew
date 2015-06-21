package com.zari.matan.navigationdrawerexample.helper;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Matan on 5/31/2015
 */
public class ListViewItemAnimation {

    ViewGroup itemBackground;
    Context context;
    View listItem;
    ListView mListView;
    SwipeCallback swipeCallback;
    int socialBgColor;

    public ListViewItemAnimation(Context context, View listItem, ListView listView, SwipeCallback SwipeCallback, ViewGroup itemBackground) {
        this.context = context;
        this.listItem = listItem;
        mListView = listView;
        this.swipeCallback = SwipeCallback;
        this.itemBackground = itemBackground;
    }

    public void setSocialBgColor(int socialBgColor) {
        this.socialBgColor = socialBgColor;
    }

    boolean mSwiping = false;
    boolean mItemPressed = false;
    private static final int SWIPE_DURATION = 350;



    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        float mDownX;
        private int mSwipeSlop = -1;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            listItem = v;
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(context).
                        getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {

                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    listItem.setAlpha(1);
                    listItem.setTranslationX(0);
                    if (itemBackground != null)
                        itemBackground.setBackgroundColor(Color.parseColor("#ffffff"));
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE: {

                    float x = event.getX() + listItem.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            mListView.requestDisallowInterceptTouchEvent(true);
                            itemBackground.setBackgroundColor(context.getResources().getColor(socialBgColor));
                        }
                    }
                    if (mSwiping) {
                        if (x < mDownX) {
                            listItem.setTranslationX((x - mDownX));
                        } else
                            mSwiping = false;
                    }
                }
                break;
                case MotionEvent.ACTION_UP: {
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {

                        float x = event.getX() + listItem.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean openUrl;
                        if (deltaXAbs > listItem.getWidth() / 3) {
                            // Greater than a quarter of the width - animate it out
                            fractionCovered = deltaXAbs / listItem.getWidth();
                            endX = deltaX < 0 ? -listItem.getWidth() : listItem.getWidth();
                            endAlpha = 0;
                            openUrl = true;
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / listItem.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            openUrl = false;
                        }
                        // Animate position and alpha of swiped item
                        // NOTE: This is a simplified version of swipe behavior, for the
                        // purposes of this demo about animation. A real version should use
                        // velocity (via the VelocityTracker class) to send the item off or
                        // back at an appropriate speed.

                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        mListView.setEnabled(false);
                        listItem.animate().setDuration(duration).
                                alpha(endAlpha).translationX(endX).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                listItem.setTranslationX(0);
                                if (openUrl) {
                                    swipeCallback.onSwipe();
                                    mListView.setEnabled(true);
                                    itemBackground.setBackgroundColor(0);
                                } else {
                                    mSwiping = false;
                                    mListView.setEnabled(true);

                                    itemBackground.setBackgroundColor(0);
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    } else {
                        v.performClick();
                        mListView.setEnabled(true);
                    }
                }
                mItemPressed = false;

                break;
                default:
                    return false;
            }
            return true;
        }
    };


    public interface SwipeCallback {
        void onSwipe();
    }


}
