package de.smac.smaccloud.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.DashboardActivity;
import de.smac.smaccloud.activity.SplashActivity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Announcement;
import de.smac.smaccloud.model.UserComment;
import de.smac.smaccloud.model.UserLike;

import static com.google.android.gms.internal.zzahn.runOnUiThread;
import static de.smac.smaccloud.base.Activity.notificationIconValueChangeListener;

public class FCMMessagingService extends FirebaseMessagingService
{
    public static final String KEY_DATA_TITLE = "title";
    public static final String KEY_DATA_BODY = "body";
    public static final String KEY_DATA_DO_LOGOUT = "doLogout";
    public static final String KEY_DATA_SHOULD_DISABLE_APP_ = "shouldDisableApp";
    public static final String KEY_DATA_ACTION_TYPE = "actionType";
    public static final String KEY_DATA_DATA_CONTENT = "datacontent";

    public static final int ACTION_TYPE_CHANNEL_ASSIGNED = 1801;
    public static final int CHANNEL_UN_ASSIGNED = 1802;
    public static final int ACTION_TYPE_CHANNEL_REMOVED = 1803;
    public static final int ACTION_TYPE_MEDIA_COMMENT = 1804;
    public static final int ACTION_TYPE_CONTENT_UPDATED = 1805;
    public static final int ACTION_TYPE_MEDIA_LIKE = 1806;
    public static final int ACTION_TYPE_THEME_CHANGE = 1807;

    public static final String PUSH_TYPE_ADD_LIKE = "ADD_LIKE";
    public static final String PUSH_TYPE_SYNC = "SYNC";
    public static final String PUSH_TYPE_FORCE_SYNC = "FORCE_SYNC";
    public static final String PUSH_TYPE_ADD_COMMENT = "ADD_COMMENT";
    public static final String PUSH_TYPE_THEME_CHANGE = "THEME_CHANGE";

    public static final String KEY_NOTIFICATION_DATA = "NotificationData";
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";
    public static FCMPushReceiveListener fcmPushReceiveListener;
    public static CommentPushReceiveListener commentPushReceiveListener;
    public static ThemeChangeNotificationListener themeChangeNotificationListener;

    String TAG = "SMAC CLOUD";
    Context context;
    ArrayList<UserLike> arrayListUserLikes;
    ArrayList<UserComment> arrayListUserComments;
    JSONArray jsonArrayUserLikes;
    // [END receive_message]
    JSONArray jsonArrayUserComments;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage)
    {
        context = this;

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (PreferenceHelper.hasUserContext(context) && remoteMessage.getData().size() > 0)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Announcement announcement = new Announcement();
                    announcement.value = new Gson().toJson(remoteMessage.getData());
                    announcement.isRead = false;
                    announcement.insertDate = Calendar.getInstance().getTime();

                    String messageTitle = getString(R.string.app_name);
                    String messageBody = getString(R.string.app_name);
                    int actionType = -1;

                    if (remoteMessage.getData().containsKey(KEY_DATA_TITLE))
                        messageTitle = remoteMessage.getData().get(KEY_DATA_TITLE);
                    if (remoteMessage.getData().containsKey(KEY_DATA_BODY))
                        messageBody = remoteMessage.getData().get(KEY_DATA_BODY);
                    if (remoteMessage.getData().containsKey(KEY_DATA_ACTION_TYPE) && TextUtils.isDigitsOnly(remoteMessage.getData().get(KEY_DATA_ACTION_TYPE)))
                        actionType = Integer.parseInt(remoteMessage.getData().get(KEY_DATA_ACTION_TYPE));

                    switch (actionType)
                    {
                        case ACTION_TYPE_CHANNEL_ASSIGNED:
                        case CHANNEL_UN_ASSIGNED:
                        case ACTION_TYPE_CHANNEL_REMOVED:
                            announcement.type = PUSH_TYPE_FORCE_SYNC;
                            announcement.userId = -1;
                            announcement.associatedId = -1;
                            if (fcmPushReceiveListener != null)
                            {
                                fcmPushReceiveListener.onFCMPushReceived(messageTitle, messageBody, remoteMessage.getData());
                            }
                            break;
                        case ACTION_TYPE_MEDIA_COMMENT:
                            if (remoteMessage.getData().containsKey(KEY_DATA_DATA_CONTENT))
                            {
                                try
                                {
                                    UserComment newUserComment = new UserComment();
                                    UserComment.parseFromJSon(new JSONObject(remoteMessage.getData().get(KEY_DATA_DATA_CONTENT)), newUserComment);

                                    announcement.type = PUSH_TYPE_ADD_COMMENT;
                                    announcement.userId = newUserComment.userId;
                                    announcement.associatedId = newUserComment.fileId;

                                    if (DataHelper.addUserComments(context, newUserComment))
                                    {
                                        Log.e("TEST>>", "User comment added successfully");

                                        if (commentPushReceiveListener != null)
                                        {
                                            commentPushReceiveListener.onCommentPushReceived();
                                        }
                                    }
                                    else
                                    {
                                        Log.e("TEST>>", "Fail to add user comment");
                                    }
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                            break;

                        case ACTION_TYPE_CONTENT_UPDATED:
                            announcement.type = PUSH_TYPE_SYNC;
                            announcement.userId = -1;
                            announcement.associatedId = -1;
                            announcement.isRead = true;

                            boolean foreground = false;
                            try
                            {
                                foreground = new Helper.ForegroundCheckTask().execute(context).get();
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                            if (foreground)
                            {
                                if (fcmPushReceiveListener != null)
                                {
                                    fcmPushReceiveListener.onFCMPushReceived(messageTitle, messageBody, remoteMessage.getData());
                                }
                                else
                                {
                                    Toast.makeText(context, "onFCMPushReceived is NULL!!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                sendNotification(messageTitle, messageBody, remoteMessage.getData());
                            }
                            break;

                        case ACTION_TYPE_MEDIA_LIKE:
                            if (remoteMessage.getData().containsKey(KEY_DATA_DATA_CONTENT))
                            {
                                try
                                {
                                    UserLike newUserLike = new UserLike();
                                    UserLike.parseFromJson(new JSONObject(remoteMessage.getData().get(KEY_DATA_DATA_CONTENT)), newUserLike);

                                    announcement.type = PUSH_TYPE_ADD_LIKE;
                                    announcement.userId = newUserLike.userId;
                                    announcement.associatedId = newUserLike.associatedId;

                                    if (DataHelper.addUserLikes(context, newUserLike))
                                    {
                                        Log.e("TEST>>", "User like added successfully");
                                    }
                                    else
                                    {
                                        Log.e("TEST>>", "Fail to add user like");
                                    }
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                            break;

                        case ACTION_TYPE_THEME_CHANGE:
                            // TODO: 1/19/2018 Manage theme change here
                            announcement.type = PUSH_TYPE_THEME_CHANGE;
                            announcement.userId = -1;
                            announcement.associatedId = -1;
                            if (remoteMessage.getData().containsKey(KEY_DATA_DATA_CONTENT))
                            {
                                try
                                {
                                    JSONObject jsonThemeData = new JSONObject(remoteMessage.getData().get(KEY_DATA_DATA_CONTENT));
                                    JSONObject jsonTheme = jsonThemeData.getJSONObject("Result");
                                    if (jsonTheme.has("Icon"))
                                    {
                                        PreferenceHelper.storeAppIcon(context, jsonTheme.optString("Icon"));
                                    }
                                    if (jsonTheme.has("AppColor"))
                                    {
                                        PreferenceHelper.storeAppColor(context, jsonTheme.optString("AppColor"));
                                    }
                                    if (jsonTheme.has("AppBackColor"))
                                    {
                                        PreferenceHelper.storeAppBackColor(context, jsonTheme.optString("AppBackColor"));
                                    }
                                    if (jsonTheme.has("AppFontColor"))
                                    {
                                        PreferenceHelper.storeAppFontColor(context, jsonTheme.optString("AppFontColor"));
                                    }
                                    if (jsonTheme.has("AppFont"))
                                    {
                                        PreferenceHelper.storeAppFontName(context, jsonTheme.optString("AppFont"));
                                    }
                                }
                                catch (JSONException jsonEx)
                                {
                                    jsonEx.printStackTrace();
                                }
                            }
                            break;

                        default:
                            announcement.type = "";
                            break;
                    }
                    DataHelper.addAnnouncement(context, announcement);

                    if (notificationIconValueChangeListener != null)
                    {
                        notificationIconValueChangeListener.onNotificationIconValueChanged();
                    }
                    if (themeChangeNotificationListener != null)
                    {
                        themeChangeNotificationListener.onThemeChangeNotificationReceived();
                    }
                }
            });
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    private void sendNotification(String messageTitle, String messageBody, Map<String, String> arrayMapData)
    {
        Intent intent;
        if (PreferenceHelper.hasUserContext(context))
        {
            intent = new Intent(this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(KEY_NOTIFICATION_DATA, new Gson().toJson(arrayMapData));
        }
        else
        {
            intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public interface FCMPushReceiveListener
    {
        public void onFCMPushReceived(String title, String body, Map<String, String> arrayMapData);
    }

    public interface CommentPushReceiveListener
    {
        public void onCommentPushReceived();
    }

    public interface ThemeChangeNotificationListener
    {
        public void onThemeChangeNotificationReceived();
    }
}
