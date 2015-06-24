package com.zari.matan.navigationdrawerexample.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyuzik.remoteimageview.RImageView;
import com.zari.matan.navigationdrawerexample.FeedAdapter;
import com.zari.matan.navigationdrawerexample.R;
import com.zari.matan.navigationdrawerexample.fragments.HomeFragment;
import com.zari.matan.navigationdrawerexample.helper.ListViewItemAnimation;
import com.zari.matan.navigationdrawerexample.helper.Utils;

/**
 * Created by Matan on 5/18/2015
 */
public class RSSPostItem implements FeedItem, ListViewItemAnimation.SwipeCallback {
    ItemData itemData;
    Context context;
    ViewHolder holder;
    int dp = Utils.getDP1();
    HomeFragment homeFragment;
    View homeFragmentLayout;
    ListView homeFeed;
    public RSSPostItem(ItemData itemData, Context context) {
        this.itemData = itemData;
        this.context = context;
        homeFragment = new HomeFragment();
        homeFragmentLayout = homeFragment.getViewInstance(context);
        homeFeed = (ListView) homeFragmentLayout.findViewById(R.id.home_feed);

    }

    @Override
    public int getViewType() {
        return FeedAdapter.types.RSSPOST.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {

        if (convertView == null){
            convertView = inflater.inflate(R.layout.rss_post_item_layout,null,false);
            holder = new ViewHolder();
            holder.cover = (RImageView) convertView.findViewById(R.id.postItemCover);
            holder.image = (RImageView) convertView.findViewById(R.id.postImage);
            holder.name = (TextView) convertView.findViewById(R.id.postName);
            holder.title = (TextView) convertView.findViewById(R.id.postTitle);
            holder.desc = (TextView) convertView.findViewById(R.id.postDesc);
            holder.time = (TextView) convertView.findViewById(R.id.postTime);
            holder.postContent = (LinearLayout) convertView.findViewById(R.id.postContent);
            holder.itemBackground = (FrameLayout) convertView.findViewById(R.id.itemBackground);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(itemData.collData.title);
        holder.time.setText(itemData.dateShort + " ago on "+context.getString(itemData.sourceStrId));
        holder.title.setText(itemData.text);
        holder.desc.setText(itemData.description);
        holder.cover.setTargetSize(dp * 35 *2, dp * 35 *2);
        holder.cover.loadImageCircleBitmap(itemData.collData.cover);
        if (itemData.img != null){
            holder.image.setTargetSize(100*dp*2, 100*dp*2);
            holder.image.loadImageBitmap(itemData.img);
        }
        if (itemData.externalUrl != null){
            ListViewItemAnimation itemAnimation = new ListViewItemAnimation(context,holder.postContent,homeFeed,this,holder.itemBackground);
            itemAnimation.setSocialBgColor(R.color.rss);
            holder.postContent.setOnTouchListener(itemAnimation.mTouchListener);
        }
        return convertView;
    }

    @Override
    public int getClickType() {
        return 0;
    }

    @Override
    public void onSwipe() {
        Toast.makeText(context,"Hello",Toast.LENGTH_SHORT).show();
    }


    private class ViewHolder{
        TextView title;
        TextView desc;
        TextView time;
        TextView name;
        RImageView cover;
        RImageView image;
        LinearLayout postContent;
        FrameLayout itemBackground;
    }
}
