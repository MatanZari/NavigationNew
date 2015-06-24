package com.zari.matan.navigationdrawerexample.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.lyuzik.remoteimageview.OnCompleteCB;
import com.lyuzik.remoteimageview.RGestureImageView;
import com.zari.matan.navigationdrawerexample.FeedAdapter;
import com.zari.matan.navigationdrawerexample.MainActivity;
import com.zari.matan.navigationdrawerexample.Network.Callback;
import com.zari.matan.navigationdrawerexample.Network.Executor;
import com.zari.matan.navigationdrawerexample.Network.MainHttpTask;
import com.zari.matan.navigationdrawerexample.Network.Request;
import com.zari.matan.navigationdrawerexample.R;
import com.zari.matan.navigationdrawerexample.helper.ExpendImage;
import com.zari.matan.navigationdrawerexample.helper.FragmentUiLifeCycleHelper;
import com.zari.matan.navigationdrawerexample.items.FeedItem;
import com.zari.matan.navigationdrawerexample.items.GifItem;
import com.zari.matan.navigationdrawerexample.items.ImageItem;
import com.zari.matan.navigationdrawerexample.items.ItemData;
import com.zari.matan.navigationdrawerexample.items.MP4VideoItem;
import com.zari.matan.navigationdrawerexample.items.PostItem;
import com.zari.matan.navigationdrawerexample.items.RSSPostItem;
import com.zari.matan.navigationdrawerexample.items.YouTubeVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.zari.matan.navigationdrawerexample.MainActivity.mMediaPlayer;
import static com.zari.matan.navigationdrawerexample.MainActivity.mYoutubePlayer;


/**
 * Created by Matan on 4/19/2015
 */

public class HomeFragment extends Fragment implements FragmentUiLifeCycleHelper, ExpendImage, AbsListView.OnScrollListener {

    public static final String TAG = HomeFragment.class.getName();
    public ListView listView;
    View v;
    MainActivity activity;
    FeedAdapter adapter;
    ArrayList<FeedItem> data;
    RelativeLayout zoomContainer;
    RGestureImageView gestureImageView;
    ProgressBar imagePreLoader;
    ProgressBar feedPreLoader;
    MainHttpTask httpTask;
    Executor executor;
    private boolean isPlayerReset;
    YouTubeVideo ytv;
    public static boolean isFirstPlaying;
    int firstVisible;
    public static int clickedPosition;
    public static int mp4ClickedPosition;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        Log.wtf(TAG, "onCreate");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.wtf(TAG, "onCreateView");
        v = inflater.inflate(R.layout.home_fragment_layout, container, false);
        listView = (ListView) v.findViewById(R.id.home_feed);
        zoomContainer = (RelativeLayout) v.findViewById(R.id.zoom_image_container);
        imagePreLoader = (ProgressBar) LayoutInflater.from(getActivity()).inflate(R.layout.image_preloader, zoomContainer, false);
        feedPreLoader = (ProgressBar) v.findViewById(R.id.feedPreLoader);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.wtf(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        if (activity.isInternetConnected()) {
            httpTask = new MainHttpTask(activity);
            Request request = new Request();
            request.urlStr = "http://wolflo.com/walls/system/nba/all?skip=20&limit=100";
            request.callback = homeCallback;
            executor = new Executor();
            executor.execute(httpTask, request);
            feedPreLoader.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onFragmentResumed() {

    }


    @Override
    public void openZoom(ItemData itemData) {

        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1.0f);
        alphaAnimation.setDuration(300);
        zoomContainer.removeAllViews();
        zoomContainer.addView(imagePreLoader);
        alphaAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
                zoomContainer.setVisibility(View.VISIBLE);


            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {

            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {

            }
        });
        zoomContainer.startAnimation(alphaAnimation);
        gestureImageView = (RGestureImageView) LayoutInflater.from(getActivity()).inflate(R.layout.expended_image, zoomContainer, false);
        gestureImageView.setTargetSize(500, 500);
        gestureImageView.setAdjustViewBounds(true);
        gestureImageView.loadImageBitmap(itemData.img, onCompleteCB, true);

    }


    OnCompleteCB onCompleteCB = new OnCompleteCB() {
        @Override
        public void complete() {
            zoomContainer.removeAllViews();
            zoomContainer.addView(gestureImageView);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            gestureImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeZoomMode();
                }
            });
        }
    };


    private void closeZoomMode() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                zoomContainer.setVisibility(View.GONE);
            }
        });
        zoomContainer.startAnimation(alphaAnimation);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    Callback homeCallback = new Callback() {
        @Override
        public void handleResponse(String response) {
            try {
                if (response != null) {
                    activity.sp.edit().putString("savedResult", response).apply();
                }
                if (feedPreLoader.getVisibility() == View.VISIBLE) {
                    feedPreLoader.setVisibility(View.GONE);
                }
                JSONArray items = new JSONArray(response);
                if (data == null)
                    data = new ArrayList<>();
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    ItemData itemData = ItemData.parsUrl(getActivity(), item);

                    assert itemData != null;
                    if (itemData.type.equals("image")) {

                        if (!itemData.img.contains(".gif"))
                            data.add(new ImageItem(activity, itemData, HomeFragment.this));

                        else
                            data.add(new GifItem(activity, itemData));

                    } else if (itemData.type.equals("video") && !itemData.source.equals("youtube") && !itemData.source.equals("vimeo")) {
                        MP4VideoItem mp4VideoItem = new MP4VideoItem(activity, itemData);
                        mp4VideoItem.setPosition(i);
                        data.add(mp4VideoItem);
                    } else if (itemData.type.equals("video") && itemData.source.equals("youtube")) {
                        YouTubeVideo youTubeVideo = new YouTubeVideo(activity, itemData);
                        youTubeVideo.setPosition(i);
                        data.add(youTubeVideo);
                    } else if (itemData.type.equals("post")) {
                        if (itemData.source.equals("rss"))
                            data.add(new RSSPostItem(itemData, activity));
                        else
                            data.add(new PostItem(activity, itemData));
                    }
                }
                if (adapter == null) {

                    adapter = new FeedAdapter(activity, 0, data);
                    listView.setAdapter(adapter);
                    listView.setOnScrollListener(HomeFragment.this);
                } else {
                    adapter.notifyDataSetChanged();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        for (int i = 0; i < visibleItemCount; i++) {
            View listItem = view.getChildAt(i);
            int viewType = adapter.getItemViewType(firstVisibleItem);

            if (firstVisibleItem > 0) {
                if (adapter.getItem(firstVisibleItem - 1) instanceof MP4VideoItem) {
                    ((MP4VideoItem) adapter.getItem(firstVisibleItem - 1)).disableClick = mMediaPlayer != null;
                }
            }
            if (adapter.getItem(firstVisibleItem + 1) instanceof MP4VideoItem) {
                ((MP4VideoItem) adapter.getItem(firstVisibleItem + 1)).disableClick = mMediaPlayer != null;
            }

            if (viewType == FeedAdapter.types.MP4VIDEO.ordinal()) {

                MP4VideoItem item = (MP4VideoItem) adapter.getItem(firstVisibleItem);
                item.disableClick = mMediaPlayer != null;
                if (listItem.getTop() < -listItem.getHeight() * 0.75 && mMediaPlayer != null) {
                    if (mp4ClickedPosition == firstVisibleItem) {
                        item.onCompletion(mMediaPlayer);
                        mMediaPlayer = null;

                    }
                    else
                        return;
                }
            } else if (mYoutubePlayer != null
                    && viewType == FeedAdapter.types.YOUTUBEVIDEO.ordinal()) {
                if (firstVisible != firstVisibleItem) {
                    firstVisible = firstVisibleItem;
                    isFirstPlaying = true;
                }
                ytv = (YouTubeVideo) adapter.getItem(firstVisibleItem);
                if (listItem.getTop() < -listItem.getHeight() / 2) {
                    if (isFirstPlaying && clickedPosition == firstVisibleItem) {
                        ytv.onVideoEnded();
                        isFirstPlaying = false;
                    }
                }
                else
                    return;
            }
            else
                return;
        }
    }

    @SuppressLint("InflateParams")
    public View getViewInstance(Context context) {

        return LayoutInflater.from(context).inflate(R.layout.home_fragment_layout, null, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.wtf(TAG, "onDestroyView");
        adapter = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.wtf(TAG, "onActivityCreated");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.wtf(TAG, "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.wtf(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.wtf(TAG, "onResume");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.wtf(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.wtf(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.wtf(TAG, "onStop");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.wtf(TAG, "onLowMemory");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.wtf(TAG, "onDestroy");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.wtf(TAG, "onDetach");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.wtf(TAG, "onAttach");
    }
}