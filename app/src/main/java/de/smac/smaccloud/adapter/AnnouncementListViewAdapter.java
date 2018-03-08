package de.smac.smaccloud.adapter;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;

import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.DashboardActivity;
import de.smac.smaccloud.activity.UserCommentViewActivity;
import de.smac.smaccloud.activity.UserLikeViewActivity;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.ChannelsFragment;
import de.smac.smaccloud.fragment.MediaFragment;
import de.smac.smaccloud.model.Announcement;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.service.FCMMessagingService;
import de.smac.smaccloud.widgets.SquareImageView;

import static de.smac.smaccloud.fragment.MediaFragment.COMMENT_ACTIVITY_REQUEST_CODE;
import static de.smac.smaccloud.service.FCMMessagingService.KEY_DATA_BODY;

/**
 * Created by S Soft on 29-Dec-17.
 */

public class AnnouncementListViewAdapter extends BaseAdapter
{
    Activity activity;
    LayoutInflater inflater;
    ArrayList<Announcement> announcements;

    public AnnouncementListViewAdapter(Activity activity, ArrayList<Announcement> announcements)
    {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        this.announcements = announcements;
    }

    @Override
    public int getCount()
    {
        return announcements.size();
    }

    @Override
    public Object getItem(int position)
    {
        return announcements.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final MyViewHolder mViewHolder;

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.search_result_list_single, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        else
        {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        final Announcement currentAnnouncement = (Announcement) getItem(position);
        final Media currentMedia = new Media();
        User currentUser = new User();
        try
        {
            currentMedia.id = currentAnnouncement.associatedId;
            DataHelper.getMedia(activity, currentMedia);

            currentUser.id = currentAnnouncement.userId;
            DataHelper.getUser(activity, currentUser);

            if (currentAnnouncement.type.equals(FCMMessagingService.PUSH_TYPE_ADD_LIKE))
            {
                mViewHolder.txt_media_name.setText(currentMedia.name + " " + activity.getString(R.string.msg_like_by) + " " + currentUser.name);
            }
            else if (currentAnnouncement.type.equals(FCMMessagingService.PUSH_TYPE_ADD_COMMENT))
            {
                mViewHolder.txt_media_name.setText(currentMedia.name + " " + activity.getString(R.string.msg_comment_by) + " " + currentUser.name);
            }
            else
            {
                JSONObject jsonAnnouncementValue = new JSONObject(currentAnnouncement.value);
                if (jsonAnnouncementValue.has(KEY_DATA_BODY))
                {
                    mViewHolder.txt_media_name.setText(jsonAnnouncementValue.optString(KEY_DATA_BODY));
                }
            }

            if (currentMedia.id > 0)
            {
                Uri imageUri = Uri.parse(currentMedia.icon);
                Glide.with(activity).load(imageUri).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(mViewHolder.imageIcon);
                mViewHolder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else
            {
                Glide.with(activity).load(R.drawable.ic_logo).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(mViewHolder.imageIcon);
                mViewHolder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        mViewHolder.parentLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (currentAnnouncement.type.equals(FCMMessagingService.PUSH_TYPE_ADD_LIKE))
                {
                    if (currentMedia.isDownloaded == 1)
                    {
                        Intent userLikeIntent = new Intent(activity, UserLikeViewActivity.class);
                        userLikeIntent.putExtra(MediaFragment.EXTRA_MEDIA, currentMedia);
                        userLikeIntent.putExtra(MediaFragment.EXTRA_CHANNEL, new Channel());
                        activity.startActivity(userLikeIntent);
                        currentAnnouncement.isRead = true;
                        DataHelper.updateAnnouncement(activity, currentAnnouncement);
                    }
                    else
                    {
                        Helper.showSimpleDialog(activity, activity.getString(R.string.label_download_first_dialog));
                    }
                }
                else if (currentAnnouncement.type.equals(FCMMessagingService.PUSH_TYPE_ADD_COMMENT))
                {
                    if (currentMedia.isDownloaded == 1)
                    {
                        Intent userCommentIntent = new Intent(activity, UserCommentViewActivity.class);
                        userCommentIntent.putExtra(MediaFragment.EXTRA_MEDIA, currentMedia);
                        userCommentIntent.putExtra(MediaFragment.EXTRA_CHANNEL, new Channel());
                        activity.startActivityForResult(userCommentIntent, COMMENT_ACTIVITY_REQUEST_CODE);
                        currentAnnouncement.isRead = true;
                        DataHelper.updateAnnouncement(activity, currentAnnouncement);
                    }
                    else
                    {
                        Helper.showSimpleDialog(activity, activity.getString(R.string.label_download_first_dialog));
                    }
                }
                else if (currentAnnouncement.type.equals(FCMMessagingService.PUSH_TYPE_FORCE_SYNC))
                {
                    currentAnnouncement.isRead = true;
                    DataHelper.updateAnnouncement(activity, currentAnnouncement);
                }
                else if (currentAnnouncement.type.equals(FCMMessagingService.PUSH_TYPE_SYNC))
                {
                    activity.askForSync();
                }
                else if (currentAnnouncement.type.equals(FCMMessagingService.PUSH_TYPE_THEME_CHANGE))
                {
                    currentAnnouncement.isRead = true;
                    DataHelper.updateAnnouncement(activity, currentAnnouncement);
                }

                // Dismiss announcements list dialog from dashboard activity
                if (activity instanceof DashboardActivity)
                {
                    if (((DashboardActivity) activity).notificationDialog != null && ((DashboardActivity) activity).notificationDialog.getDialog() != null && ((DashboardActivity) activity).notificationDialog.getDialog().isShowing())
                    {
                        ((DashboardActivity) activity).notificationDialog.dismiss();

                    }

                    de.smac.smaccloud.base.Fragment dashFragment = (ChannelsFragment) activity.getSupportFragmentManager().findFragmentById(R.id.layoutFrame);
                    if (dashFragment != null && dashFragment instanceof ChannelsFragment)
                    {
                        ((ChannelsFragment) dashFragment).applyThemeColor();
                    }
                }
            }
        });

        boolean isTabletSize = activity.getResources().getBoolean(R.bool.isTablet);
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            if (isTabletSize)
            {
                mViewHolder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 8, Helper.getDeviceHeight(activity) / 12));
            }
            else
            {
                mViewHolder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 6, Helper.getDeviceHeight(activity) / 6));
            }
        }
        else
        {
            if (isTabletSize)
            {
                mViewHolder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 11, Helper.getDeviceHeight(activity) / 14));
            }
            else
            {
                mViewHolder.imageIcon.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 5, Helper.getDeviceHeight(activity) / 6));
            }
        }
        Helper.setupTypeface(mViewHolder.parentLayout1, Helper.robotoRegularTypeface);


        return convertView;
    }


    private class MyViewHolder
    {
        LinearLayout parentLayout, parentLayout1;
        SquareImageView imageIcon;
        TextView txt_media_name;

        public MyViewHolder(View convertView)
        {
            parentLayout = (LinearLayout) convertView.findViewById(R.id.parentLayout);
            parentLayout1 = (LinearLayout) convertView.findViewById(R.id.parentLayout1);
            imageIcon = (SquareImageView) convertView.findViewById(R.id.imageIcon);
            txt_media_name = (TextView) convertView.findViewById(R.id.txt_media_name);
        }
    }
}
