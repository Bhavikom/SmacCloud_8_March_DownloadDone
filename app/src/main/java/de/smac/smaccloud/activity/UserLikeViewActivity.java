package de.smac.smaccloud.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.json.JSONObject;

import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.LikeAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.MediaFragment;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Announcement;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserLike;
import de.smac.smaccloud.service.FCMMessagingService;

import static de.smac.smaccloud.service.FCMMessagingService.KEY_DATA_DATA_CONTENT;

/**
 * View like of file
 */
public class UserLikeViewActivity extends Activity
{

    private RecyclerView userLikeList;
    private LikeAdapter likeAdapter;
    private ArrayList<User> users;
    private ArrayList<UserLike> userLikes;
    private Media media;
    private Channel channel;
    private ArrayList<Integer> userId;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_user);
        Helper.retainOrientation(UserLikeViewActivity.this);
        media = this.getIntent().getParcelableExtra(MediaFragment.EXTRA_MEDIA);
        channel = this.getIntent().getParcelableExtra(MediaFragment.EXTRA_CHANNEL);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_material_vector);
            upArrow.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
        }

        DataHelper.getUserIdFromLike(context, userLikes, media.id);
        likeAdapter = new LikeAdapter(context, userLikes);
        userLikeList.setAdapter(likeAdapter);
        likeAdapter.notifyDataSetChanged();

        // Clear notification related to this file
        try
        {
            ArrayList<Announcement> announcements = new ArrayList<>();
            DataHelper.getAnnouncementData(context, announcements);
            for (Announcement announcement : announcements)
            {
                if (announcement.type.equalsIgnoreCase(FCMMessagingService.PUSH_TYPE_ADD_LIKE))
                {
                    JSONObject jsonAnnouncementValue = new JSONObject(announcement.value);
                    if (jsonAnnouncementValue.has(KEY_DATA_DATA_CONTENT))
                    {
                        UserLike userLike = new UserLike();
                        UserLike.parseFromJson(new JSONObject(jsonAnnouncementValue.getString(KEY_DATA_DATA_CONTENT)), userLike);
                        if (userLike.associatedId == media.id)
                        {
                            announcement.isRead = true;
                            DataHelper.updateAnnouncement(context, announcement);
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        userLikeList = (RecyclerView) findViewById(R.id.listUser);
        userId = new ArrayList<>();
        users = new ArrayList<>();
        userLikes = new ArrayList<>();
        user = new User();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        userLikeList.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void bindEvents()
    {
        super.bindEvents();
    }
}
