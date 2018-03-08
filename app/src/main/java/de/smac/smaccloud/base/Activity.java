package de.smac.smaccloud.base;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

import de.smac.smaccloud.R;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.ChannelFiles;
import de.smac.smaccloud.model.ChannelUser;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.MediaVersion;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserComment;
import de.smac.smaccloud.model.UserLike;
import de.smac.smaccloud.model.UserPreference;
import de.smac.smaccloud.service.FCMMessagingService;

import static de.smac.smaccloud.service.FCMMessagingService.ACTION_TYPE_CHANNEL_ASSIGNED;
import static de.smac.smaccloud.service.FCMMessagingService.ACTION_TYPE_CHANNEL_REMOVED;
import static de.smac.smaccloud.service.FCMMessagingService.ACTION_TYPE_CONTENT_UPDATED;
import static de.smac.smaccloud.service.FCMMessagingService.CHANNEL_UN_ASSIGNED;
import static de.smac.smaccloud.service.FCMMessagingService.KEY_DATA_ACTION_TYPE;

/**
 * Base class of all Activities which use into project
 */

@SuppressWarnings("unused")
public class Activity extends AppCompatActivity
{
    private static final int REQUEST_SYNC = 9999;
    public static Typeface robotoLightTypeface;
    public static NotificationIconValueChangeListener notificationIconValueChangeListener;

    protected static ActionBar actionBar;
    protected View parentLayout;
    protected Toolbar toolbar;
    protected Context context;
    protected NetworkService.NetworkBinder networkBinder;
    protected FragmentManager fragmentManager;
    String value;
    String lightBgColor;
    private Intent networkServiceIntent;
    private ServiceConnection networkConnection;
    private NetworkService.RequestCompleteCallback networkCallback;
    private ArrayList<UserLike> arrayListUserLikes;
    private ArrayList<UserComment> arrayListUserComments;
    private JSONArray jsonArrayUserLikes;
    private JSONArray jsonArrayUserComments;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        robotoLightTypeface = Typeface.createFromAsset(getAssets(), "fonts/roboto.regular.ttf");

        fragmentManager = getSupportFragmentManager();
        context = this;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        bindService(networkServiceIntent, networkConnection, BIND_IMPORTANT);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unbindService(networkConnection);
    }

    protected final void setupToolBar()
    {
        toolbar = (Toolbar) findViewById(R.id.navigationBar);
        if (toolbar != null)
        {
            setSupportActionBar(toolbar);
            actionBar = getSupportActionBar();
            if (actionBar != null)
            {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    protected final void refreshLayoutTypeface()
    {
        Helper.setupTypeface(getWindow().getDecorView(), robotoLightTypeface);

    }

    @Override
    public final void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);
        parentLayout = findViewById(R.id.parentLayout);
        setupToolBar();
        initializeComponents();
        bindEvents();
        refreshLayoutTypeface();
        updateParentThemeColor();
        /*FCMMessagingService.themeChangeNotificationListener = new FCMMessagingService.ThemeChangeNotificationListener()
        {
            @Override
            public void onThemeChangeNotificationReceived()
            {
                updateParentThemeColor();
            }
        };*/


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateParentThemeColor();
    }

    protected void initializeComponents()
    {
        networkServiceIntent = new Intent(context, NetworkService.class);
        networkConnection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder)
            {
                networkBinder = (NetworkService.NetworkBinder) binder;
                onNetworkReady();
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                networkBinder = null;
            }
        };

        FCMMessagingService.fcmPushReceiveListener = new FCMMessagingService.FCMPushReceiveListener()
        {
            @Override
            public void onFCMPushReceived(String title, String body, Map<String, String> arrayMapData)
            {
                if (!isFinishing())
                {
                    if (arrayMapData != null)
                    {
                        if (arrayMapData.containsKey(KEY_DATA_ACTION_TYPE) && TextUtils.isDigitsOnly(arrayMapData.get(KEY_DATA_ACTION_TYPE)))
                        {
                            int type = Integer.parseInt(arrayMapData.get(KEY_DATA_ACTION_TYPE));
                            switch (type)
                            {
                                case ACTION_TYPE_CHANNEL_ASSIGNED:
                                case CHANNEL_UN_ASSIGNED:
                                case ACTION_TYPE_CHANNEL_REMOVED:
                                    syncServiceCall();
                                    break;
                                case ACTION_TYPE_CONTENT_UPDATED:
                                    askForSync();
                                    break;
                            }

                        }
                    }
                }
            }
        };

    }

    protected void bindEvents()
    {
        networkCallback = new NetworkService.RequestCompleteCallback()
        {
            @Override
            public void onRequestComplete(int requestCode, boolean status, String payload)
            {
                onNetworkResponse(requestCode, status, payload);
            }
        };
    }

    public final void notifySimple(String message)
    {
        if (parentLayout != null)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
        else
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public void postNetworkRequest(int requestCode, String url, String action, RequestParameter... requestParameters)
    {
        if (networkBinder != null)
        {
            networkBinder.postWrappedJSONRequest(this, requestCode, url, action, networkCallback, requestParameters);
        }
    }

    /*protected void navigateToFragment(int containerId, Fragment fragment)
    {
        navigateToFragment(containerId, fragment, true);
    }*/

    protected void navigateToFragment(int containerId, Fragment fragment, boolean addToBackStack)
    {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack)
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    public void updateParentThemeColor()
    {
        if (parentLayout != null)
        {

            lightBgColor = PreferenceHelper.getAppBackColor(context);
            if (lightBgColor != null && lightBgColor.length() > 0)
            {
                lightBgColor = lightBgColor.substring(1, lightBgColor.length());
                String value = "#40" + lightBgColor;
                parentLayout.setBackgroundColor(Color.parseColor(value));
            }
            else
            {
                lightBgColor = "#000000";
                lightBgColor = lightBgColor.substring(1, lightBgColor.length());
                String value = "#40" + lightBgColor;
                parentLayout.setBackgroundColor(Color.parseColor(value));
            }

        }
        else if (findViewById(android.R.id.content) != null)
        {
            lightBgColor = PreferenceHelper.getAppBackColor(context);
            if (lightBgColor != null && lightBgColor.length() > 0)
            {
                lightBgColor = lightBgColor.substring(1, lightBgColor.length());
                String value = "#40" + lightBgColor;
                findViewById(android.R.id.content).setBackgroundColor(Color.parseColor(value));
            }
            else
            {
                lightBgColor = "#000000";
                lightBgColor = lightBgColor.substring(1, lightBgColor.length());
                String value = "#40" + lightBgColor;
                findViewById(android.R.id.content).setBackgroundColor(Color.parseColor(value));
            }

        }
    }

    protected void onNetworkReady()
    {

    }

    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        if (requestCode == REQUEST_SYNC)
        {
            Helper.IS_DIALOG_SHOW = true;
            if (status)
            {
                try
                {

                    JSONObject syncJson = new JSONObject(response);
                    int requestStatus = syncJson.optInt("Status");
                    if (requestStatus == 0)
                    {

                        /*notifySimple(getString(R.string.msg_data_sync));*/

                        syncJson = syncJson.optJSONObject("Payload");
                        Log.e("TEST>>", syncJson.toString());

                        if (!(arrayListUserLikes.size() <= 0))
                        {
                            for (UserLike userLike : arrayListUserLikes)
                            {
                                userLike.removeoffline(context);
                            }
                        }
                        if (!(arrayListUserComments.size() <= 0))
                        {
                            for (UserComment userComment : arrayListUserComments)
                            {
                                userComment.removeOffline(context);
                            }
                        }
                        ArrayList<Channel> arraylistChannels = new ArrayList<>();
                        JSONArray channelJsonArray = syncJson.optJSONArray("Channels");
                        try
                        {
                            Channel.parseListFromJson(channelJsonArray, arraylistChannels);
                            for (Channel channel : arraylistChannels)
                            {
                                switch (channel.syncType)
                                {
                                    case 1:
                                        channel.add(context);
                                        break;

                                    case 2:
                                        channel.saveChanges(context);
                                        break;

                                    case 3:
                                        channel.remove(context);
                                        break;
                                }

                            }

                            if (syncJson.has("Media") && !syncJson.isNull("Media"))
                            {
                                JSONArray mediaJsonArray = syncJson.optJSONArray("Media");
                                ArrayList<Media> arraylistMediallist = new ArrayList<>();
                                Media.parseListFromJson(mediaJsonArray, arraylistMediallist);
                                for (Media media : arraylistMediallist)
                                {
                                    switch (media.syncType)
                                    {
                                        case 1:
                                            media.add(context);

                                            break;
                                        case 2:
                                            Media oldMedia = new Media();
                                            oldMedia.id = media.id;
                                            DataHelper.getMedia(context, oldMedia);
                                            media.isDownloaded = oldMedia.isDownloaded;
                                            media.isDownloading = oldMedia.isDownloading;
                                            media.saveChanges(context);

                                            break;

                                        case 3:
                                            media.remove(context);
                                            break;
                                    }
                                    if (!(media.type.equals("folder")))
                                    {
                                        addMediaVersion(media.currentVersion);
                                    }

                                }
                            }

                            if (syncJson.has("ChannelFiles") && !syncJson.isNull("ChannelFiles"))
                            {
                                JSONArray channelFilesJsonArray = syncJson.optJSONArray("ChannelFiles");
                                ArrayList<ChannelFiles> arraylistChhannelFiles = new ArrayList<>();
                                ChannelFiles.parseListFromJson(channelFilesJsonArray, arraylistChhannelFiles);
                                for (ChannelFiles channelFile : arraylistChhannelFiles)
                                {
                                    switch (channelFile.syncType)
                                    {
                                        case 1:
                                            channelFile.add(context);
                                            break;

                                        case 2:
                                            channelFile.saveChanges(context);
                                            break;

                                        case 3:
                                            channelFile.remove(context);
                                            break;
                                    }
                                }
                            }

                            if (syncJson.has("ChannelUsers") && !syncJson.isNull("ChannelUsers"))
                            {
                                JSONArray channelUsersJsonArray = syncJson.optJSONArray("ChannelUsers");
                                ArrayList<ChannelUser> arraylistChannelUsers = new ArrayList<>();
                                ChannelUser.parseListFromJson(channelUsersJsonArray, arraylistChannelUsers);
                                for (ChannelUser channelUser : arraylistChannelUsers)
                                {
                                    switch (channelUser.syncType)
                                    {
                                        case 1:
                                            channelUser.add(context);
                                            break;
                                        case 2:
                                            channelUser.saveChanges(context);
                                            break;
                                        case 3:
                                            channelUser.remove(context);
                                            break;
                                    }
                                }
                            }

                            if (syncJson.has("UserComments") && !syncJson.isNull("UserComments"))
                            {
                                JSONArray userCommentsJsonArray = syncJson.optJSONArray("UserComments");
                                ArrayList<UserComment> arraylistuserComments = new ArrayList<>();
                                UserComment.parseListFromJson(userCommentsJsonArray, arraylistuserComments);
                                for (UserComment usercomment : arraylistuserComments)
                                {
                                    switch (usercomment.syncType)
                                    {
                                        case 1:
                                            if (!DataHelper.isCommentExist(context, usercomment.id))
                                                usercomment.add(context);
                                            break;
                                        case 2:
                                            usercomment.saveChanges(context);
                                            break;
                                        case 3:
                                            usercomment.remove(context);
                                            break;
                                    }
                                }
                            }

                            if (syncJson.has("UserLikes") && !syncJson.isNull("UserLikes"))
                            {
                                JSONArray userLikeJsonArray = syncJson.optJSONArray("UserLikes");
                                ArrayList<UserLike> arraylistuserLikes = new ArrayList<>();
                                UserLike.parseListFromJson(userLikeJsonArray, arraylistuserLikes);
                                for (UserLike userLike : arraylistuserLikes)
                                {
                                    switch (userLike.syncType)
                                    {
                                        case 1:
                                            userLike.add(context);
                                            break;
                                        case 2:
                                            userLike.saveChanges(context);
                                            break;
                                        case 3:
                                            userLike.remove(context);
                                            break;
                                    }
                                    addCreator(userLike.user);
                                }
                            }

                            UserPreference userPreference = new UserPreference();
                            userPreference.userId = PreferenceHelper.getUserContext(context);
                            userPreference.populateUsingUserId(context);
                            userPreference.lastSyncDate = syncJson.optString("LastSyncDate");
                            userPreference.saveChanges(context);
                            Log.e("lastsync", userPreference.lastSyncDate);
                            User user = new User();
                            user.id = PreferenceHelper.getUserContext(context);
                            user.populateUsingId(context);
                            userPreference.userId = user.id;
                            PreferenceHelper.storeSyncStatus(context, true);
                            DataHelper.updateSyncAnnouncementReadStatus(context, true);
                            if (notificationIconValueChangeListener != null)
                            {
                                notificationIconValueChangeListener.onNotificationIconValueChanged();
                            }

                            Helper.showSimpleDialog(this, getString(R.string.sync_data_update_sucessfully));
                        }
                        catch (JSONException | ParseException e)
                        {
                            e.printStackTrace();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void askForSync()
    {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(context.getString(R.string.app_title));
        dialog.setMessage(context.getString(R.string.sync_update_dialog));
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        syncServiceCall();
                    }
                });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.label_cancel),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        DataHelper.updateSyncAnnouncementReadStatus(Activity.this, false);
                        if (notificationIconValueChangeListener != null)
                        {
                            notificationIconValueChangeListener.onNotificationIconValueChanged();
                        }
                        dialog.cancel();
                    }
                });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void syncServiceCall()
    {
        if (Helper.isNetworkAvailable(context))
        {
            arrayListUserLikes = new ArrayList<>();
            arrayListUserComments = new ArrayList<>();
            jsonArrayUserLikes = new JSONArray();
            jsonArrayUserComments = new JSONArray();

            UserPreference userPreference = new UserPreference();
            userPreference.userId = PreferenceHelper.getUserContext(context);
            userPreference.populateUsingUserId(context);
            String lastSyncDate = userPreference.lastSyncDate;
            try
            {
                DataHelper.getUserLike(context, arrayListUserLikes);
                DataHelper.getUserComments(context, arrayListUserComments);
                for (UserLike like : arrayListUserLikes)
                {
                    JSONObject likeJson = like.toJson();
                    int channelId = DataHelper.getChannelId(context, like.associatedId);
                    likeJson.put("ChannelId", channelId);
                    jsonArrayUserLikes.put(likeJson);
                }
                for (UserComment userComment : arrayListUserComments)
                {
                    JSONObject commentJson = userComment.toJson();
                    int channelId = DataHelper.getChannelId(context, userComment.fileId);
                    commentJson.put("ChannelId", channelId);
                    jsonArrayUserComments.put(commentJson);
                    Log.e("TEST SYNC Comment>>", commentJson.toString());
                }
            }
            catch (Exception ex)
            {
                Log.e("TEST>>", ex.getMessage(), ex);
            }
            Helper.IS_DIALOG_SHOW = false;

            postNetworkRequest(REQUEST_SYNC,
                    DataProvider.ENDPOINT_SYNC,
                    DataProvider.Actions.SYNC,
                    RequestParameter.jsonArray("UserLikes", jsonArrayUserLikes), RequestParameter.jsonArray("UserComments", jsonArrayUserComments), RequestParameter.urlEncoded("UserId", String.valueOf(userPreference.userId)), RequestParameter.urlEncoded("LastSyncDate", lastSyncDate)
            );
        }
    }

    protected void addMediaVersion(MediaVersion currentVersion)
    {
        if (currentVersion != null)
        {
            switch (currentVersion.syncType)
            {
                case 0:
                    currentVersion.saveChanges(context);
                    addCreator(currentVersion.creator);
                    break;
                case 1:
                    currentVersion.add(context);
                    addCreator(currentVersion.creator);
                    break;
                case 2:
                    currentVersion.saveChanges(context);
                    addCreator(currentVersion.creator);
                    break;
                case 3:
                    currentVersion.remove(context);
                    addCreator(currentVersion.creator);
                    break;
            }
        }
    }

    protected void addCreator(User creator)
    {
        switch (creator.syncType)
        {
            case 0:
                creator.saveChanges(context);
                break;
            case 1:
                creator.add(context);
                break;
            case 2:
                creator.saveChanges(context);
                break;
            case 3:
                creator.remove(context);
                break;
        }
    }

    public interface NotificationIconValueChangeListener
    {
        public void onNotificationIconValueChanged();
    }

}