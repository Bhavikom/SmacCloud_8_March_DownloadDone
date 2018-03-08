package de.smac.smaccloud.adapter;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.MediaActivity;
import de.smac.smaccloud.activity.MediaAttachmentActivity;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.widgets.SquareImageView;

import static de.smac.smaccloud.activity.ShareActivity.REQUEST_CODE_MEDIA_ATTACHMENT;

public class ChannelAttachAdapter extends RecyclerView.Adapter<ChannelAttachAdapter.ChannelHolder>
{
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Channel> channels;


    public ChannelAttachAdapter(Activity activity, ArrayList<Channel> channels)
    {
        this.activity = activity;
        this.channels = channels;
        this.inflater = LayoutInflater.from(this.activity);
    }

    @Override
    public ChannelHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View rootView = inflater.inflate(R.layout.activity_channal_atteach_list_single, parent, false);
        return new ChannelHolder(rootView);
    }


    @Override
    public void onBindViewHolder(final ChannelHolder holder, final int position)
    {
        Channel channel = channels.get(position);
        holder.labelName.setText(channel.name);
        boolean isTabletSize = activity.getResources().getBoolean(R.bool.isTablet);
        holder.imageViewNext.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(activity)));
        Helper.setupTypeface(holder.parentLayout, Helper.robotoRegularTypeface);
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            if (isTabletSize)
            {
                holder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 8, Helper.getDeviceHeight(activity) / 12));
            }
            else
            {

                holder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 5, Helper.getDeviceHeight(activity) / 6));
            }
        }
        else
        {
            if (isTabletSize)
            {
                holder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 11, Helper.getDeviceHeight(activity) / 14));
            }
            else
            {

                holder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 5, Helper.getDeviceHeight(activity) / 6));
            }
        }
        if (channel != null)
        {

            if (channel.thumbnail != null)
            {
                final Uri imageUri = Uri.parse(channel.thumbnail);
                Glide.with(activity)
                        .load(imageUri)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<Bitmap>()
                        {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                            {
                                holder.imageIcon.setImageBitmap(bitmap);
                                holder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable)
                            {
                                super.onLoadFailed(e, errorDrawable);
                                Glide.with(activity)
                                        .load(imageUri)
                                        .asBitmap()
                                        .error(R.drawable.ic_channel)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(new SimpleTarget<Bitmap>()
                                        {
                                            @Override
                                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                                            {
                                                holder.imageIcon.setImageBitmap(bitmap);
                                                holder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                            }

                                            @Override
                                            public void onLoadFailed(Exception e, Drawable errorDrawable)
                                            {
                                                super.onLoadFailed(e, errorDrawable);
                                                if (e != null && e.getMessage() != null)
                                                    Log.e("Glide", e.getMessage());
                                            }
                                        });

                            }
                        });
            }
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Channel channel = channels.get(position);
                Intent mediaIntent = new Intent(activity, MediaAttachmentActivity.class);
                mediaIntent.putExtra(MediaActivity.EXTRA_CHANNEL, channel);
                mediaIntent.putExtra(MediaActivity.EXTRA_PARENT, -1);
                activity.startActivityForResult(mediaIntent, REQUEST_CODE_MEDIA_ATTACHMENT);
                activity.overridePendingTransition(0, 0);


            }
        });
        //   holder.labelName.setTypeface(Helper.robotoRegularTypeface);

    }

    @Override
    public int getItemCount()
    {
        return channels.size();
    }

    class ChannelHolder extends RecyclerView.ViewHolder
    {
        LinearLayout parentLayout;
        SquareImageView imageIcon;
        TextView labelName;
        ImageView imageViewNext;

        private ChannelHolder(View itemView)
        {
            super(itemView);
            itemView.setId(R.id.itemView);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
            imageIcon = (SquareImageView) itemView.findViewById(R.id.imageIcon);
            labelName = (TextView) itemView.findViewById(R.id.txt_channel_name);
            imageViewNext = (ImageView) itemView.findViewById(R.id.img_next);


        }
    }
}