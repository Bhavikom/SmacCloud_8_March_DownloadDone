package de.smac.smaccloud.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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

import java.io.File;
import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.RecentItem;

public class RecentItemRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<RecentItem> recentItems;


    public RecentItemRecyclerAdapter(Context context, ArrayList<RecentItem> recentItems)
    {
        this.context = context;
        this.recentItems = recentItems;
        this.inflater = LayoutInflater.from(this.context);
    }

    public void addMoreData(ArrayList<RecentItem> recentItems)
    {
        this.recentItems = recentItems;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View rootView = inflater.inflate(R.layout.recent_item_single, parent, false);
        return new CommentHolder(rootView);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)
    {

        final CommentHolder recentItemHolder = (CommentHolder) holder;
        RecentItem recentItem = recentItems.get(position);


       /* recentItemHolder.txtFileName.setTypeface(Helper.robotoBoldTypeface);
        recentItemHolder.txtChannelName.setTypeface(Helper.robotoRegularTypeface);
        recentItemHolder.txtLastOpenTime.setTypeface(Helper.robotoRegularTypeface);*/

        try
        {
            Channel channel = new Channel();
            channel.id = DataHelper.getChannelId(context, recentItem.id);
            DataHelper.getChannel(context, channel);
            recentItemHolder.txtChannelName.setText(channel.name);

            final Media media = new Media();
            media.id = recentItem.id;
            DataHelper.getMedia(context, media);
            recentItemHolder.txtFileName.setText(media.name);

            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    //dateFormat.parse(recentItem.visitTimestamp).getTime(),
                    Long.parseLong(recentItem.visitTimestamp),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            recentItemHolder.txtLastOpenTime.setText(timeAgo);
            final Uri imageUri = Uri.parse(context.getFilesDir() + File.separator + media.id);
            {
                Glide.with(context)
                        //.load(new File(String.valueOf(imageUri)))
                        .load(media.icon)
                        .asBitmap()
                        //.transform(new DocumentExifTransformation(activity, imageUri))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<Bitmap>()
                        {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                            {
                                recentItemHolder.imgIcon.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable)
                            {
                                super.onLoadFailed(e, errorDrawable);
                                Glide.with(context)
                                        .load(imageUri)
                                        .asBitmap()
                                        .error(R.drawable.ic_file_icon)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(new SimpleTarget<Bitmap>()
                                        {
                                            @Override
                                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                                            {
                                                recentItemHolder.imgIcon.setImageBitmap(bitmap);
                                            }

                                            @Override
                                            public void onLoadFailed(Exception e, Drawable errorDrawable)
                                            {
                                                super.onLoadFailed(e, errorDrawable);
                                                if (e.getMessage() != null)
                                                    Log.e("Glide", e.getMessage());
                                            }
                                        });
                            }
                        });
            }
            recentItemHolder.parentLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Helper.openFile(context, media);
                }
            });

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }

    @Override
    public int getItemCount()
    {
        return recentItems.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder
    {
        LinearLayout parentLayout;
        ImageView imgIcon;
        TextView txtChannelName, txtFileName, txtLastOpenTime;


        private CommentHolder(View itemView)
        {
            super(itemView);
            itemView.setId(R.id.itemView);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
            imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);
            txtChannelName = (TextView) itemView.findViewById(R.id.txtChannelName);
            txtFileName = (TextView) itemView.findViewById(R.id.txtFileName);
            txtLastOpenTime = (TextView) itemView.findViewById(R.id.txtLastOpenTime);
            Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);
        }
    }
}
