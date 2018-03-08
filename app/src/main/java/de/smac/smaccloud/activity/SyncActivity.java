package de.smac.smaccloud.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.michael.easydialog.EasyDialog;
import com.suke.widget.SwitchButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.ChannelFiles;
import de.smac.smaccloud.model.ChannelUser;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.MediaAllDownload;
import de.smac.smaccloud.model.MediaVersion;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserComment;
import de.smac.smaccloud.model.UserLike;
import de.smac.smaccloud.model.UserPreference;
import de.smac.smaccloud.service.DownloadService;

/**
 * User to sync data with server
 */
public class SyncActivity extends Activity
{
    public static final String IS_FROM_SETTING = "isFromSetting";
    public static final String KEY_MEDIA_SIZE = "MediaSize";
    public static final int REQUEST_SYNC = 4302;
    public String lastSyncDate;
    public TextView textViewTitle;
    public TextView textViewSubTitle, textViewDownloadFileContain;
    public LinearLayout parentLayout, linearInformation;
    public boolean isFullDownload = false;
    public int batteryStatus = 20;
    public ImageView imageViewSync, imageViewDownloadDemand, imageViewAutoDownload;
    public FrameLayout frameLayoutMiddle;
    public TextView textViewAutoDownload, textViewAutoDownloadTitle, textViewAutoDownloadInfo, textViewDownloadOnDemandTitle, textViewDownloadOnDemandInfo;
    ProgressDialog progressDialog;
    long mediaSize = 0;
    SwitchButton toggleButtonDownload;
    Button buttonDownload;
    Intent extras;
    Boolean isFromSetting = false;
    Activity activity;
    IntentFilter intentfilter;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (progressDialog != null)
            {
                progressDialog.dismiss();
            }
        }
    };
    private ArrayList<UserLike> arrayListUserLikes;
    private ArrayList<UserComment> arrayListUserComments;
    private JSONArray jsonArrayUserLikes, jsonArrayUserComments;
    private User user;
    private PreferenceHelper prefManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        Helper.retainOrientation(SyncActivity.this);
        prefManager = new PreferenceHelper(this);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        linearInformation = (LinearLayout) findViewById(R.id.linear_info);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewDownloadFileContain = (TextView) findViewById(R.id.txt_download_file_contain);
        textViewSubTitle = (TextView) findViewById(R.id.textViewSubTitle);
        textViewAutoDownload = (TextView) findViewById(R.id.txt_download_on_tap);
        textViewAutoDownloadTitle = (TextView) findViewById(R.id.txt_auto_download_info_title);
        textViewAutoDownloadInfo = (TextView) findViewById(R.id.txt_auto_download_info_des);
        textViewDownloadOnDemandTitle = (TextView) findViewById(R.id.txt_download_on_demand_info_title);
        textViewDownloadOnDemandInfo = (TextView) findViewById(R.id.txt_download_on_demand_info_des);
        imageViewSync = (ImageView) findViewById(R.id.imageViewSync);
        frameLayoutMiddle = (FrameLayout) findViewById(R.id.frameLayoutMiddle);
        imageViewDownloadDemand = (ImageView) findViewById(R.id.img_download_info);
        imageViewAutoDownload = (ImageView) findViewById(R.id.img_auto_download_info);
        toggleButtonDownload = (SwitchButton) findViewById(R.id.toggleAutoDownload);
        buttonDownload = (Button) findViewById(R.id.btnDownload);
        mediaSize = PreferenceHelper.getMediaSize(context);
        textViewDownloadFileContain.setText(getString(R.string.label_auto_download).concat(" ").concat(Helper.bytesConvertsToMb(mediaSize, context)));

        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        buttonDownload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Helper.isNetworkAvailable(context))
                {
                    UserPreference userPreference = new UserPreference();
                    userPreference.userId = user.id;
                    userPreference.populateUsingUserId(context);
                    String lastSyncDate = userPreference.lastSyncDate;
                    Helper.IS_DIALOG_SHOW = false;
                    progressDialog = new ProgressDialog(SyncActivity.this);
                    progressDialog.setMessage(getString(R.string.msg_please_wait_while_sync));
                    progressDialog.setCancelable(false);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                    frameLayoutMiddle.setVisibility(View.GONE);
                    linearInformation.setVisibility(View.GONE);

                    postNetworkRequest(REQUEST_SYNC, DataProvider.ENDPOINT_SYNC, DataProvider.Actions.SYNC,
                            RequestParameter.jsonArray("UserLikes", jsonArrayUserLikes), RequestParameter.jsonArray("UserComments", jsonArrayUserComments),
                            RequestParameter.urlEncoded("UserId", String.valueOf(userPreference.userId)), RequestParameter.urlEncoded("Org_Id", PreferenceHelper.getOrganizationId(context)), RequestParameter.urlEncoded("LastSyncDate", lastSyncDate));

                }
                else
                {
                    frameLayoutMiddle.setVisibility(View.VISIBLE);
                    linearInformation.setVisibility(View.VISIBLE);
                    Helper.showMessage(SyncActivity.this, false, getString(R.string.msg_please_check_your_connection));
                }
            }
        });

        toggleButtonDownload.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(SwitchButton switchButton, boolean isChecked)
            {
                if (isChecked)
                {
                    isFullDownload = true;
                    prefManager.saveFullDownloadMedia(true);

                    if (mediaSize > Helper.availableBlocks(context))
                    {
                        showNoFreeSpaceAvailableDialog();

                    }
                    else if (getBatteryLevel() <= batteryStatus)
                    {
                        showLowBatteryStatusDialog();
                    }
                    else
                    {
                        downloadAllFiles();
                    }

                }
                else
                {
                    prefManager.saveFullDownloadMedia(false);
                    isFullDownload = false;
                }
            }
        });

        extras = getIntent();
        if (extras != null)
        {
            if (extras.getExtras().containsKey(IS_FROM_SETTING))
                isFromSetting = extras.getExtras().getBoolean(IS_FROM_SETTING);
            if (extras.getExtras().containsKey(KEY_MEDIA_SIZE))
                mediaSize = getIntent().getExtras().getLong(KEY_MEDIA_SIZE);
        }

        try
        {
            if (!isFromSetting && actionBar != null)
            {
                actionBar.setHomeButtonEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);

            }
            UserPreference userPreference = new UserPreference();
            user = new User();
            user.id = PreferenceHelper.getUserContext(context);
            userPreference.userId = user.id;
            userPreference.populateUsingUserId(context);
            lastSyncDate = userPreference.lastSyncDate;
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
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        imageViewAutoDownload.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                showAutoDownloadDialog();

            }
        });
        imageViewDownloadDemand.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDownloadOnDemandDialog();

            }
        });

        Helper.setupTypeface(findViewById(R.id.parentLayout), Helper.robotoLightTypeface);



    }

    public void showDownloadOnDemandDialog()
    {
        final EasyDialog dialog = new EasyDialog(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.activity_download_on_demand, null);
        view.setLayoutParams(new RelativeLayout.LayoutParams(Helper.getDeviceWidth(activity) / 3, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView txtDownloadOnDownload = (TextView) view.findViewById(R.id.txt_download_on_demand);

        dialog.setLayout(view)
                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                .setBackgroundColor(activity.getResources().getColor(R.color.transparent_black_color))
                .setLocationByAttachedView(imageViewDownloadDemand)
                .setTouchOutsideDismiss(true)
                .setMatchParent(false)
                .show();

    }

    public void showAutoDownloadDialog()
    {
        final EasyDialog dialog = new EasyDialog(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.activity_auto_download, null);
        view.setLayoutParams(new RelativeLayout.LayoutParams(Helper.getDeviceWidth(activity) / 3, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView txtAutoDownload = (TextView) view.findViewById(R.id.txt_auto_download);
        dialog.setLayout(view)
                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                .setBackgroundColor(activity.getResources().getColor(R.color.transparent_black_color))
                .setLocationByAttachedView(imageViewAutoDownload)
                .setTouchOutsideDismiss(true)
                .setMatchParent(false)
                .show();

    }

    public void showLowBatteryStatusDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.app_title));
        alertDialog.setMessage(getString(R.string.battery_low_message) + "\n" + getString(R.string.download_message) + "\t" + Helper.bytesConvertsToMb(mediaSize, context) + "\t" + getString(R.string.download_message1) + "\n" + getString(R.string.download_message2) + "\t" + Helper.bytesConvertsToMb(Helper.availableBlocks(context), context));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.label_cancel),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        toggleButtonDownload.setChecked(false);
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    public void downloadAllFiles()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(getString(R.string.app_title));
        alertDialog.setMessage(getString(R.string.download_message) + " " + Helper.bytesConvertsToMb(mediaSize, context) + " " + getString(R.string.download_message1) + "\n" + getString(R.string.download_message2) + " " + Helper.bytesConvertsToMb(Helper.availableBlocks(context), context));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.label_cancel),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        toggleButtonDownload.setChecked(false);
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    public void showNoFreeSpaceAvailableDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.app_title));
        alertDialog.setMessage(context.getString(R.string.no_available_space_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        toggleButtonDownload.setChecked(false);
                        dialog.dismiss();

                    }
                });
        alertDialog.show();

    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        activity = this;
        arrayListUserLikes = new ArrayList<>();
        arrayListUserComments = new ArrayList<>();
        jsonArrayUserLikes = new JSONArray();
        jsonArrayUserComments = new JSONArray();
    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (isFromSetting)
        {
            if (item.getItemId() == android.R.id.home)
                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNetworkResponse(int requestCode, boolean status, final String response)
    {
        super.onNetworkResponse(requestCode, status, response);
        Helper.IS_DIALOG_SHOW = true;

        if (requestCode == REQUEST_SYNC)
        {
            if (status)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            JSONObject syncJson = new JSONObject(response);
                            int requestStatus = syncJson.optInt("Status");
                            if (requestStatus > 0)
                            {
                                notifySimple(syncJson.optString("Message"));
                            }
                            else
                            {

                                //  notifySimple(getString(R.string.msg_data_sync));
                                syncJson = syncJson.optJSONObject("Payload");
                                Log.e("payload", syncJson.toString());

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
                                        addCreator(channel.creator);
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
                                                    media.saveChanges(context);

                                                    break;

                                                case 3:
                                                    media.remove(context);
                                                    break;
                                            }
                                            Log.e("Media type", media.type + media.currentVersionId);
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
                                    if (isFullDownload)
                                    {
                                        //Helper.downloadAllFiles(SyncActivity.this, isFromSetting);

                                        final ArrayList<MediaAllDownload> arraylistDownloadList = new ArrayList<MediaAllDownload>();
                                        DataHelper.getAllDownloadList(activity, arraylistDownloadList);
                                        for (int i = 0; i < arraylistDownloadList.size(); i++)
                                        {
                                            int mediaParentId = arraylistDownloadList.get(i).mediaId;
                                            int rootMediaId;
                                            do
                                            {
                                                rootMediaId = mediaParentId;
                                                mediaParentId = DataHelper.getMediaParentId(activity, mediaParentId);
                                            }
                                            while (mediaParentId != -1);
                                            int channelId = DataHelper.getChannelIdFromMediaID(activity, rootMediaId);
                                            arraylistDownloadList.get(i).channelId = channelId;
                                        }


                                        for (int i = 0; i < arraylistDownloadList.size(); i++)
                                        {
                                            try
                                            {
                                                Media tempMedia = new Media();
                                                tempMedia.id = arraylistDownloadList.get(i).mediaId;
                                                DataHelper.getMedia(activity, tempMedia);
                                                tempMedia.isDownloading = 1;
                                                DataHelper.updateMedia(activity, tempMedia);
                                            }
                                            catch (Exception ex)
                                            {
                                                ex.printStackTrace();
                                            }
                                        }
                                        Intent starDownload = new Intent(activity, DownloadService.class);
                                        starDownload.putParcelableArrayListExtra("downloadlist", arraylistDownloadList);
                                        activity.startService(starDownload);

                                        if (!isFromSetting)
                                            startDashboardActivity();

                                        isFullDownload = false;
                                    }
                                    else if (!isFromSetting)
                                    {
                                        startDashboardActivity();
                                    }
                                    else
                                    {
                                        //finish();
                                    }

                                    PreferenceHelper.storeSyncStatus(context, true);

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
                            if (progressDialog != null)
                            {
                                progressDialog.dismiss();
                            }
                        }
                        catch (JSONException e)
                        {
                            if (progressDialog != null)
                            {
                                progressDialog.dismiss();
                            }
                            notifySimple(getString(R.string.msg_invalid_response_from_server));
                        }
                        handler.sendEmptyMessage(0);

                    }
                }).start();
            }
            else
            {
                if (progressDialog != null)
                {
                    progressDialog.dismiss();

                }
                frameLayoutMiddle.setVisibility(View.VISIBLE);
                linearInformation.setVisibility(View.VISIBLE);
                notifySimple(getString(R.string.msg_cannot_complete_request));
            }
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

    private void startDashboardActivity()
    {
        Intent dashboardIntent = new Intent(context, DashboardActivity.class);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dashboardIntent);
        finish();
    }

    public float getBatteryLevel()
    {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if (level == -1 || scale == -1)
        {
            return 50.0f;
        }
        return ((float) level / (float) scale) * 100.0f;
    }

}
