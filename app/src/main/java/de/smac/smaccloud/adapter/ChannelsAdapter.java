package de.smac.smaccloud.adapter;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.model.Channel;

/**
 * This class user to bind channel in RecyclerView
 */
public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ChannelHolder>
{
    private boolean isTabletSize;
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Channel> channels;
    private LinearLayout parentLayout;
    private boolean isGrid;
    private OnClickListener clickListener;

    public ChannelsAdapter(Activity activity, ArrayList<Channel> channels)
    {
        this.activity = activity;
        this.channels = channels;
        this.inflater = LayoutInflater.from(this.activity);
    }

    @Override
    public ChannelHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View rootView = inflater.inflate(isGrid ? R.layout.partial_channel_item_grid : R.layout.partial_channel_item_list, parent, false);

        return new ChannelHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ChannelHolder holder, final int position)
    {
        final int finalPosition = position;
        Channel channel = channels.get(position);
        holder.labelName.setText(channel.name);
        if (clickListener != null)
        {
            View.OnClickListener onClickListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    switch (view.getId())
                    {
                        case R.id.compoundButtonDetail:
                            clickListener.onItemDetailClick(finalPosition, view);
                            break;

                        case R.id.itemView:
                            clickListener.onItemClick(finalPosition, view);
                            break;
                    }
                }
            };
            if (channel != null)
            {
                holder.compoundButtonDetail.setVisibility(View.GONE);
                if (channel.thumbnail != null)
                {
                    final Uri imageUri = Uri.parse(channel.thumbnail);
                    Glide.with(activity)
                            .load(imageUri)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>()
                            {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                                {
                                    holder.imageIcon.setImageBitmap(bitmap);
                                    holder.progressBarTemp.setVisibility(View.GONE);

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
                                                    holder.progressBarTemp.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onLoadFailed(Exception e, Drawable errorDrawable)
                                                {
                                                    super.onLoadFailed(e, errorDrawable);
                                                    holder.progressBarTemp.setVisibility(View.GONE);
                                                    holder.imageIcon.setImageBitmap(null);

                                                }
                                            });
                                }
                            });
                }
            }
            holder.compoundButtonDetail.setOnClickListener(onClickListener);
            holder.itemView.setOnClickListener(onClickListener);

            try
            {
                //holder.txt_number_of_media.setText(DataHelper.getCountMediaFromChannelId(activity, channel.id) + " " + activity.getString(R.string.label_medias));
                holder.txt_number_of_media.setText(String.valueOf(DataHelper.getCountRootMediaFromChannelId(activity, channel.id)) + " ");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            //holder.txt_share_with.setText(DataHelper.getUsersByChannelId(activity, channel.id) + " " + activity.getString(R.string.users));
            holder.txt_share_with.setText("" + DataHelper.getUsersByChannelId(activity, channel.id) + " ");

            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(channel.updateDate.getTime(),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy ", Locale.US);
            holder.txt_created_on.setText(sdf.format(channel.updateDate));

            isTabletSize = activity.getResources().getBoolean(R.bool.isTablet);
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                // Portrait Mode
                if (isTabletSize)
                {
                    holder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Helper.getDeviceHeight(activity) / 4));
                }
                else
                {
                    holder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Helper.getDeviceHeight(activity) / 3));
                }
            }
            else
            {
                // landscape Mode
                if (isTabletSize)
                {
                    holder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Helper.getDeviceHeight(activity) / 3));
                }
                else
                {
                    holder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Helper.getDeviceHeight(activity) / 3));
                }

            }
            Helper.setupTypeface(holder.parentLayout, Helper.robotoRegularTypeface);
        }
    }

    @Override
    public int getItemCount()
    {
        return channels.size();
    }

    public void setGrid(boolean isGrid)
    {
        this.isGrid = isGrid;
        notifyDataSetChanged();
    }

    public void setClickListener(OnClickListener clickListener)
    {
        this.clickListener = clickListener;
        notifyDataSetChanged();
    }

    public interface OnClickListener
    {
        public void onItemClick(int position, View view);

        public void onItemDetailClick(int position, View view);
    }

    class ChannelHolder extends RecyclerView.ViewHolder
    {
        View parentLayout;
        RoundedImageView imageIcon;
        TextView labelName;
        ImageView compoundButtonDetail;
        TextView txt_number_of_media;
        TextView txt_share_with;
        TextView txt_created_on;
        ProgressBar progressBarTemp;

        private ChannelHolder(View itemView)
        {
            super(itemView);
            itemView.setId(R.id.itemView);
            parentLayout = itemView;
            progressBarTemp = (ProgressBar) itemView.findViewById(R.id.progressTemp);
            imageIcon = (RoundedImageView) itemView.findViewById(R.id.imageIcon);
            labelName = (TextView) itemView.findViewById(R.id.labelName);
            compoundButtonDetail = (ImageView) itemView.findViewById(R.id.compoundButtonDetail);
            txt_number_of_media = (TextView) itemView.findViewById(R.id.txt_number_of_media);
            txt_share_with = (TextView) itemView.findViewById(R.id.txt_share_with);
            txt_created_on = (TextView) itemView.findViewById(R.id.txt_created_on);
        }
    }
}