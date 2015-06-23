package com.zari.matan.navigationdrawerexample.items;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.lyuzik.remoteimageview.RImageView;
import com.zari.matan.navigationdrawerexample.FeedAdapter;
import com.zari.matan.navigationdrawerexample.MainActivity;
import com.zari.matan.navigationdrawerexample.R;
import com.zari.matan.navigationdrawerexample.fragments.HomeFragment;
import com.zari.matan.navigationdrawerexample.helper.Utils;

import static com.zari.matan.navigationdrawerexample.MainActivity.mYoutubePlayer;

/**
 * Created by Matan on 5/25/2015
 */
public class YouTubeVideo implements FeedItem, View.OnClickListener, YouTubePlayer.PlaybackEventListener, YouTubePlayer.PlayerStateChangeListener {

    Context context;
    ItemData itemData;
    public ViewHolder holder;
    public static final String API_KEY = "AIzaSyAdaiI84wKJP0TZBkCz98EDxsIoNfe0tSc";
    public static YouTubePlayerSupportFragment mVideo;
    FragmentManager fragmentManager;
    private boolean isLoading;
    LinearLayout root;
    private static String thumbnailTag = null;
    private static String containerTag = null;
    public Activity activity;
    public boolean isPlaying;
    HomeFragment homeFragment;
    View homeLayout;
    FrameLayout current;
    private int position;
    private boolean isInitialized;


    public YouTubeVideo(Context context, ItemData itemData) {
        this.context = context;
        this.itemData = itemData;
        homeFragment = new HomeFragment();
        activity = (MainActivity) context;
        fragmentManager = ((MainActivity) context).getSupportFragmentManager();
        homeLayout = homeFragment.getViewInstance(context);
    }

    @Override
    public int getViewType() {
        return FeedAdapter.types.YOUTUBEVIDEO.ordinal();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.youtube_item_layout, null, false);
            holder = new ViewHolder();
            FrameLayout videoContainer = (FrameLayout) convertView.findViewById(R.id.youtube_video_container);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            RImageView cover = (RImageView) convertView.findViewById(R.id.youtubeVideoItemCover);
            ImageButton play = (ImageButton) convertView.findViewById(R.id.play);
            RImageView thumbnail = (RImageView) convertView.findViewById(R.id.thumbnail);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView text = (TextView) convertView.findViewById(R.id.text);
            root = (LinearLayout) convertView.findViewById(R.id.root);
            holder.root = root;
            holder.cover = cover;
            holder.thumbnail = thumbnail;
            holder.play = play;
            holder.name = name;
            holder.time = time;
            holder.text = text;
            holder.videoContainer = videoContainer;
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        int dp1 = Utils.getDP1();
        holder.cover.setTargetSize(35 * dp1 * 2, 35 * dp1 * 2);
        holder.cover.loadImageCircleBitmap(itemData.collData.cover);
        holder.time.setText(itemData.dateShort + " ago on " + context.getString(itemData.sourceStrId));
        holder.name.setText(itemData.collData.title);
        holder.thumbnail.setTargetSize(Utils.getScreenWidth(), 300 * dp1);
        holder.thumbnail.loadImageBitmap(itemData.thumbnail);
        holder.play.setOnClickListener(this);
        holder.play.setSoundEffectsEnabled(false);
        holder.text.setText(itemData.text);
        return convertView;

    }

    @Override
    public int getClickType() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        if(YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(context).name().equals( YouTubeInitializationResult.SUCCESS.name())) {
            if (mYoutubePlayer != null && mVideo != null) {
                Log.wtf("mYoutubePlayer", "!null");
                if (mYoutubePlayer.isPlaying() || isLoading || !isInitialized) {
                    Log.wtf("isPlaying() || isLoading", "true");

                    // setVideoLoadingAnimation(holder.thumbnail, 0, 1.0f);
                    // mYoutubePlayer.release();
                    mYoutubePlayer = null;
                    isPlaying = false;
                    removePlaying(mVideo);
                    mVideo = null;
                    isLoading = false;
                    if (containerTag != null && thumbnailTag != null) {
                        FrameLayout previousContainer = (FrameLayout) ((ListView) holder.root.getParent()).findViewWithTag(containerTag);
                        ImageView previousThumbnail = (ImageView) previousContainer.findViewById(R.id.thumbnail);
                        setVideoLoadingAnimation(previousThumbnail, 0, 1.0f);
                    }
                    HomeFragment.isFirstPlaying = false;
                }
            }
            HomeFragment.clickedPosition = position;
            holder.thumbnail.setTag(itemData.videoUrl);
            thumbnailTag = (String) holder.thumbnail.getTag();
            holder.videoContainer.setId(MainActivity.generateViewId());
            holder.videoContainer.setTag(itemData.videoUrl);
            containerTag = (String) holder.videoContainer.getTag();
            mVideo = YouTubePlayerSupportFragment.newInstance();
            isInitialized = false;
            final String videoUrl = itemData.videoUrl;
            showVideo(mVideo);
            mVideo.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                    isInitialized = true;
                    if (!wasRestored) {
                        mYoutubePlayer = youTubePlayer;
                        mYoutubePlayer.loadVideo(videoUrl);
                        mYoutubePlayer.setPlaybackEventListener(YouTubeVideo.this);
                        mYoutubePlayer.setPlayerStateChangeListener(YouTubeVideo.this);
                        mYoutubePlayer.setShowFullscreenButton(false);
                    } else
                        Log.e("wasRestored", "true");
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    Toast.makeText(context, youTubeInitializationResult.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Intent videoClient = new Intent(Intent.ACTION_VIEW);
            videoClient.setData(Uri.parse("https://www.youtube.com/watch?v=" + itemData.videoUrl));
            activity.startActivity(videoClient);

        }
    }

    @Override
    public void onPlaying() {
        isPlaying = true;
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onStopped() {
        mYoutubePlayer = null;
        isLoading = false;
        isPlaying = false;
        removePlaying(mVideo);
        setVideoLoadingAnimation(holder.thumbnail, 0, 1.0f);
    }

    @Override
    public void onBuffering(boolean b) {
        isLoading = true;
    }

    @Override
    public void onSeekTo(int i) {
    }

    @Override
    public void onLoading() {
        isLoading = true;
    }

    @Override
    public void onLoaded(String s) {
        if (holder != null) {
            setVideoLoadingAnimation(holder.thumbnail, 1.0f, 0);
        }
    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {
        isLoading = false;
        isPlaying = true;
    }

    @Override
    public void onVideoEnded() {
        if (holder != null) {
            isPlaying = false;
            removePlaying(mVideo);
            mYoutubePlayer = null;
        }

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

        if (errorReason.name().equals(YouTubePlayer.ErrorReason.USER_DECLINED_RESTRICTED_CONTENT.name())) {
            removePlaying(mVideo);
            mYoutubePlayer = null;
            Toast.makeText(context, R.string.youtube_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }


    private class ViewHolder {
        TextView name;
        FrameLayout videoContainer;
        RImageView cover;
        RImageView thumbnail;
        ImageButton play;
        TextView time;
        LinearLayout root;
        public TextView text;
    }


    public void setVideoLoadingAnimation(final View toAnimate, float startScale, final float endScale) {
        Animation anim = new ScaleAnimation(1f, 1f, startScale, endScale, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        anim.setFillAfter(true);
        anim.setDuration(300);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                toAnimate.setHasTransientState(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (endScale > 0)
                    toAnimate.setHasTransientState(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        toAnimate.startAnimation(anim);

    }

    public void removePlaying(YouTubePlayerSupportFragment video) {
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(video).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
        setVideoLoadingAnimation(holder.thumbnail, 0, 1.0f);
    }

    public void showVideo(final YouTubePlayerSupportFragment video) {
        removePlaying(video);
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        current = (FrameLayout) holder.root.findViewWithTag(itemData.videoUrl);
        transaction.add(current.getId(), video).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }


}
