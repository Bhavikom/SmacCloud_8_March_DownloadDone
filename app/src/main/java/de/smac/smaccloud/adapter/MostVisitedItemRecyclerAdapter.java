package de.smac.smaccloud.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.RecentItem;

public class MostVisitedItemRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<RecentItem> recentItems;


    public MostVisitedItemRecyclerAdapter(Context context, ArrayList<RecentItem> recentItems)
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
        View rootView = inflater.inflate(R.layout.most_visited_item_single, parent, false);
        return new CommentHolder(rootView);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)
    {

        final CommentHolder mostVisitedItemHolder = (CommentHolder) holder;
        RecentItem recentItem = recentItems.get(position);

        try
        {
            Channel channel = new Channel();
            channel.id = DataHelper.getChannelId(context, recentItem.id);
            DataHelper.getChannel(context, channel);
            mostVisitedItemHolder.txtChannelName.setText(channel.name);

            final Media media = new Media();
            media.id = recentItem.id;
            DataHelper.getMedia(context, media);
            mostVisitedItemHolder.txtFileName.setText(media.name);


            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    //dateFormat.parse(recentItem.visitTimestamp).getTime(),
                    Long.parseLong(recentItem.visitTimestamp),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

            mostVisitedItemHolder.labelVisit.setText(timeAgo);

            mostVisitedItemHolder.txtVisitCount.setText(String.valueOf(recentItem.visit));
            final Uri imageUri = Uri.parse(context.getFilesDir() + File.separator + media.id);

            {
                Glide.with(context)
                        .load(media.icon)
                        .asBitmap()
                        //.transform(new DocumentExifTransformation(activity, imageUri))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<Bitmap>()
                        {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation)
                            {
                                mostVisitedItemHolder.imgIcon.setImageBitmap(bitmap);
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
                                                mostVisitedItemHolder.imgIcon.setImageBitmap(bitmap);
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
            mostVisitedItemHolder.parentLayout.setOnClickListener(new View.OnClickListener()
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
        RelativeLayout parentLayout;
        ImageView imgIcon, imgVisibleIcon;
        TextView txtChannelName, txtFileName, labelVisit, txtVisitCount;

        public CommentHolder(View itemView)
        {
            super(itemView);
            itemView.setId(R.id.itemView);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.parentLayout);
            imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);
            imgVisibleIcon = (ImageView) itemView.findViewById(R.id.img_visible_count);
            txtChannelName = (TextView) itemView.findViewById(R.id.txtChannelName);
            txtFileName = (TextView) itemView.findViewById(R.id.txtFileName);
            labelVisit = (TextView) itemView.findViewById(R.id.labelVisit);
            txtVisitCount = (TextView) itemView.findViewById(R.id.txtVisitCount);
            imgVisibleIcon.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)));
            Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);

        }
    }
}
