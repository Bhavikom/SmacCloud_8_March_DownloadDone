package de.smac.smaccloud.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.fragment.MediaFragment;
import de.smac.smaccloud.model.Announcement;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.ChannelFiles;
import de.smac.smaccloud.model.ChannelUser;
import de.smac.smaccloud.model.LocalizationData;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.MediaAllDownload;
import de.smac.smaccloud.model.MediaVersion;
import de.smac.smaccloud.model.RecentItem;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserComment;
import de.smac.smaccloud.model.UserLike;
import de.smac.smaccloud.model.UserPreference;
import de.smac.smaccloud.service.FCMMessagingService;

/**
 * This class is use to perform database related operations
 * It contain methods which are use to store, retrieve, remove database details
 */

public class DataHelper
{
    /**
     * Fields for Commons Database
     */

    //Constants
    public static final String USER_PREFERENCE_NAME_DATABASE_NAME = "DatabaseName";
    public static final String USER_PREFERENCE_NAME_LAST_SYNC_DATE = "LastSyncDate";
    public static final int USER_PREFERENCE_COUNT = 2;

    // User Preference Table
    public static final String TABLE_USER_PREFERENCE = "UserPreferences";
    public static final String USER_PREFERENCE_ID = "id";
    public static final String USER_PREFERENCE_NAME = "name";
    public static final String USER_PREFERENCE_VALUE = "value";
    public static final String USER_PREFERENCE_USER_ID = "userId";
    /**
     * Fields for individual database
     */
    //Localization table name
    public static final String TABLE_LOCALIZATION = "Localization";

    //Localization tables field
    public static final String LOCALIZATION_ID_PRIMERYKEY = "id";
    public static final String LOCALIZATION_CODE = "code";
    public static final String LOCALIZATION_DELETED_DATE = "deleted_date";
    public static final String LOCALIZATION_INSERTED_DATE = "inserted_date";
    public static final String LOCALIZATION_IS_DELETED = "is_deleted";
    public static final String LOCALIZATION_LANGUAGE = "language";
    public static final String LOCALIZATION_ID = "localization_id";
    public static final String LOCALIZATION_MESSAGE = "message";
    public static final String LOCALIZATION_TYPE = "type";
    public static final String LOCALIZATION_UPDATED_DATE = "update_date";


    public static final String TABLE_USER = "Users";
    public static final String USER_ID = "id";
    public static final String USER_ROLE_ID = "roleId";
    public static final String USER_TYPE = "type";
    public static final String USER_EMAIL = "email";
    public static final String USER_NAME = "name";
    public static final String USER_DESIGNATION = "designation";
    public static final String USER_ADDRESS = "address";
    public static final String USER_CONTACT = "contact";
    public static final String USER_INSERT_DATE = "insertDate";
    public static final String USER_UPDATE_DATE = "updateDate";
    public static final String USER_DELETE_DATE = "deleteDate";
    public static final String USER_IS_SYNCED = "isSynced";
    public static final String USER_ACCESS_TOKEN = "accessToken";

    public static final String TABLE_CHANNEL = "Channels";
    public static final String CHANNEL_CREATOR_ID = "creatorId";
    public static final String CHANNEL_ID = "id";
    public static final String CHANNEL_NAME = "name";
    public static final String CHANNEL_LOCATION = "location";
    public static final String CHANNEL_INSERT_DATE = "insertDate";
    public static final String CHANNEL_UPDATE_DATE = "updateDate";
    public static final String CHANNEL_DELETE_DATE = "deleteDate";
    public static final String CHANNEL_IS_SYNCED = "isSynced";
    public static final String CHANNEL_THUMBNAIL = "thumbnail";

    public static final String TABLE_CHANNEL_FILE = "ChannelFiles";
    public static final String CHANNEL_FILE_ID = "id";
    public static final String CHANNEL_FILE_CHANNEL_ID = "channelId";
    public static final String CHANNEL_FILE_VERSION_ID = "versionId";
    public static final String CHANNEL_FILE_FILE_ID = "fileId";
    public static final String CHANNEL_FILE_INSERT_DATE = "insertDate";
    public static final String CHANNEL_FILE_UPDATE_DATE = "updateDate";
    public static final String CHANNEL_FILE_DELETE_DATE = "deleteDate";
    public static final String CHANNEL_FILE_IS_SYNCED = "isSynced";

    public static final String TABLE_CHANNEL_USER = "ChannelUsers";
    public static final String CHANNEL_USER_ID = "id";
    public static final String CHANNEL_USER_ADDED_BY = "addedBy";
    public static final String CHANNEL_USER_USER_ID = "userId";
    public static final String CHANNEL_USER_CHANNEL_ID = "channelId";
    public static final String CHANNEL_USER_INSERT_DATE = "insertDate";
    public static final String CHANNEL_USER_UPDATE_DATE = "updateDate";
    public static final String CHANNEL_USER_DELETE_DATE = "deleteDate";
    public static final String CHANNEL_USER_IS_SYNCED = "isSynced";

    public static final String TABLE_MEDIA = "Media";
    public static final String MEDIA_ID = "id";
    public static final String MEDIA_UPDATE_DATE = "updateDate";
    public static final String MEDIA_IS_SYNCED = "isSynced";
    public static final String MEDIA_IS_DOWNLOADED = "isDownloaded";
    public static final String MEDIA_CURRENT_VERSION_ID = "currentVersionId";
    public static final String MEDIA_DELETE_DATE = "deleteDate";
    public static final String MEDIA_DESCRIPTION = "description";
    public static final String MEDIA_INSERT_DATE = "insertDate";
    public static final String MEDIA_LOCATION = "location";
    public static final String MEDIA_NAME = "name";
    public static final String MEDIA_PARENT_ID = "parentId";
    public static final String MEDIA_SIZE = "size";
    public static final String MEDIA_TYPE = "type";
    public static final String MEDIA_ATTACHABLE = "attachable";
    public static final String MEDIA_HAS_CONTENT = "hasContent";
    public static final String MEDIA_ICON = "icon";
    public static final String MEDIA_IS_DOWNLOADING = "isDownloading";

    public static final String TABLE_MEDIA_VERSION = "MediaVersions";
    public static final String MEDIA_VERSION_ID = "id";
    public static final String MEDIA_VERSION = "version";
    public static final String MEDIA_VERSION_FILE_ID = "fileId";
    public static final String MEDIA_VERSION_CREATOR_ID = "creatorId";
    public static final String MEDIA_VERSION_MODIFIER_ID = "modifierId";
    public static final String MEDIA_VERSION_INSERT_DATE = "insertDate";
    public static final String MEDIA_VERSION_UPDATE_DATE = "updateDate";
    public static final String MEDIA_VERSION_DELETE_DATE = "deleteDate";
    public static final String MEDIA_VERSION_IS_SYNCED = "isSynced";

    public static final String TABLE_COMMENT = "Comments";
    public static final String COMMENT_ID = "id";
    public static final String COMMENT_FILE_ID = "fileId";
    public static final String COMMENT = "comment";
    public static final String COMMENT_USER_ID = "userId";
    public static final String COMMENT_INSERT_DATE = "commentInsertDate";
    public static final String COMMENT_UPDATE_DATE = "updateDate";
    public static final String COMMENT_DELETE_DATE = "deleteDate";
    public static final String COMMENT_IS_SYNCED = "isSynced";

    public static final String TABLE_LIKE = "Likes";
    public static final String LIKE_ID = "id";
    public static final String LIKE_ASSOCIATED_ID = "associatedId";
    public static final String LIKE_TYPE = "type";
    public static final String LIKE_USER_ID = "userId";
    public static final String LIKE_INSERT_DATE = "insertDate";
    public static final String LIKE_UPDATE_DATE = "updateDate";
    public static final String LIKE_DELETE_DATE = "deleteDate";
    public static final String LIKE_IS_SYNCED = "isSynced";

    public static final String TABLE_MESSAGE = "Messages";
    public static final String MESSAGE_ID = "id";
    public static final String MESSAGE_SENDER_ID = "senderId";
    public static final String MESSAGE_SUBJECT = "subject";
    public static final String MESSAGE_BODY = "body";
    public static final String MESSAGE_INSERT_DATE = "insertDate";
    public static final String MESSAGE_UPDATE_DATE = "updateDate";
    public static final String MESSAGE_DELETE_DATE = "deleteDate";
    public static final String MESSAGE_IS_SYNCED = "isSynced";

    public static final String TABLE_MESSAGE_FILE = "MessageFiles";
    public static final String MESSAGE_FILE_ID = "id";
    public static final String MESSAGE_FILE_MESSAGE_ID = "messageId";
    public static final String MESSAGE_FILE_FILE_ID = "fileId";
    public static final String MESSAGE_FILE_FILE_VERSION_ID = "fileVersionId";
    public static final String MESSAGE_FILE_INSERT_DATE = "insertDate";
    public static final String MESSAGE_FILE_UPDATE_DATE = "updateDate";
    public static final String MESSAGE_FILE_DELETE_DATE = "deleteDate";
    public static final String MESSAGE_FILE_IS_SYNCED = "isSynced";

    public static final String TABLE_MESSAGE_RECEIVERS = "MessageReceivers";
    public static final String MESSAGE_RECEIVERS_ID = "id";
    public static final String MESSAGE_RECEIVERS_MESSAGE_ID = "messageId";
    public static final String MESSAGE_RECEIVERS_RECEIVER_ID = "receiverId";
    public static final String MESSAGE_RECEIVERS_TYPE = "type";
    public static final String MESSAGE_RECEIVERS_INSERT_DATE = "insertDate";
    public static final String MESSAGE_RECEIVERS_UPDATE_DATE = "updateDate";
    public static final String MESSAGE_RECEIVERS_DELETE_DATE = "deleteDate";
    public static final String MESSAGE_RECEIVERS_IS_SYNCED = "isSynced";

    public static final String TABLE_RECENT = "Recent";
    public static final String RECENT_ID = "Id";
    public static final String RECENT_VISIT = "Visit";
    public static final String RECENT_VISIT_TIMESTAMP = "VisitTimeStamp";
    public static final String RECENT_USER_ID = "UserId";
    public static final String RECENT_INSERT_DATE = "InsertDate";
    public static final String RECENT_UPDATE_DATE = "UpdateDate";
    public static final String RECENT_DELETE_DATE = "DeleteDate";

    public static final String TABLE_ANNOUNCEMENT = "Announcement";
    public static final String ANNOUNCEMENT_ID = "Id";
    public static final String ANNOUNCEMENT_TYPE = "Type";
    public static final String ANNOUNCEMENT_USER_ID = "UserId";
    public static final String ANNOUNCEMENT_ASSOCIATED_ID = "AssociatedId";
    public static final String ANNOUNCEMENT_VALUE = "Value";
    public static final String ANNOUNCEMENT_IS_READ = "IsRead";
    public static final String ANNOUNCEMENT_INSERTEDATE = "InsertDate";
    public static final String ANNOUNCEMENT_UPDATEDATE = "UpdateDate";
    public static final String ANNOUNCEMENT_DELETEDATE = "DeleteDate";


    public static String getInsertUserPreferencesSQL()
    {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_USER_PREFERENCE + "("
                + USER_PREFERENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_PREFERENCE_NAME + " TEXT NOT NULL, "
                + USER_PREFERENCE_VALUE + " TEXT NOT NULL, "
                + USER_PREFERENCE_USER_ID + " INTEGER NOT NULL)";
    }

    public static String getInsertLocalizationErrorCode()
    {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_LOCALIZATION + "("
                + LOCALIZATION_ID_PRIMERYKEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LOCALIZATION_CODE + "  INTEGER NOT NULL, "
                + LOCALIZATION_DELETED_DATE + " TEXT NOT NULL, "
                + LOCALIZATION_INSERTED_DATE + " TEXT NOT NULL, "
                + LOCALIZATION_IS_DELETED + " TEXT NOT NULL, "
                + LOCALIZATION_LANGUAGE + " TEXT NOT NULL,"
                + LOCALIZATION_ID + " TEXT NOT NULL,"
                + LOCALIZATION_MESSAGE + " TEXT NOT NULL, "
                + LOCALIZATION_TYPE + " TEXT NOT NULL, "
                + LOCALIZATION_UPDATED_DATE + " TEXT NOT NULL)";
    }

    public static void getUserPreference(Context context, UserPreference preference)
    {
        SQLiteDatabase db = CommonDatabase.getReadable(context);

        String whereClause = USER_PREFERENCE_USER_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(preference.userId)};

        Cursor preferenceCursor = db.query(TABLE_USER_PREFERENCE, null, whereClause, whereArgs, null, null, null, String.valueOf(USER_PREFERENCE_COUNT));
        if (preferenceCursor != null)
        {
            if (preferenceCursor.moveToFirst())
                UserPreference.parseFromCursor(preferenceCursor, preference);
            else
                preference.userId = -1;

            preferenceCursor.close();
        }
        else
            preference.userId = -1;

    }

    public static boolean addUserPreference(Context context, UserPreference preference)
    {
        SQLiteDatabase db = CommonDatabase.getWritable(context);
        ContentValues userPreferenceValues = new ContentValues();
        userPreferenceValues.put(USER_PREFERENCE_NAME, USER_PREFERENCE_NAME_DATABASE_NAME);
        userPreferenceValues.put(USER_PREFERENCE_VALUE, preference.databaseName);
        userPreferenceValues.put(USER_PREFERENCE_USER_ID, preference.userId);
        long databaseNameRecordId = db.replace(TABLE_USER_PREFERENCE, null, userPreferenceValues);

        userPreferenceValues = new ContentValues();
        userPreferenceValues.put(USER_PREFERENCE_NAME, USER_PREFERENCE_NAME_LAST_SYNC_DATE);
        userPreferenceValues.put(USER_PREFERENCE_VALUE, preference.lastSyncDate);
        userPreferenceValues.put(USER_PREFERENCE_USER_ID, preference.userId);
        long lastSyncDateRecordId = db.replace(TABLE_USER_PREFERENCE, null, userPreferenceValues);

        return (databaseNameRecordId > 0 && lastSyncDateRecordId > 0);
    }

    public static boolean updateUserPreference(Context context, UserPreference preference)
    {
        SQLiteDatabase db = CommonDatabase.getWritable(context);

        ContentValues userPreferenceValues = new ContentValues();
        userPreferenceValues.put(USER_PREFERENCE_VALUE, preference.databaseName);
        String whereClause = USER_PREFERENCE_USER_ID + " = ? AND " + USER_PREFERENCE_NAME + " = ?";
        String[] whereArgs = new String[]{String.valueOf(preference.userId), USER_PREFERENCE_NAME_DATABASE_NAME};
        long databaseNameUpdateCount = db.update(TABLE_USER_PREFERENCE, userPreferenceValues, whereClause, whereArgs);

        userPreferenceValues = new ContentValues();
        userPreferenceValues.put(USER_PREFERENCE_VALUE, preference.lastSyncDate);
        whereClause = USER_PREFERENCE_USER_ID + " = ? AND " + USER_PREFERENCE_NAME + " = ?";
        whereArgs = new String[]{String.valueOf(preference.userId), USER_PREFERENCE_NAME_LAST_SYNC_DATE};
        long lastSyncDateUpdateCount = db.update(TABLE_USER_PREFERENCE, userPreferenceValues, whereClause, whereArgs);

        return (databaseNameUpdateCount > 0 || lastSyncDateUpdateCount > 0);
    }

    public static boolean removeUserPreference(Context context, UserPreference preference)
    {
        SQLiteDatabase db = CommonDatabase.getWritable(context);
        String whereClause = USER_PREFERENCE_USER_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(preference.userId)};
        long deleteCount = db.delete(TABLE_USER_PREFERENCE, whereClause, whereArgs);
        return (deleteCount > 0);
    }

    public static String getInsertUserSQL()
    {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_ROLE_ID + " INTEGER NOT NULL, "
                + USER_EMAIL + " TEXT NOT NULL, "
                + USER_NAME + " TEXT NOT NULL, "
                + USER_DESIGNATION + " TEXT, "
                + USER_ADDRESS + " TEXT, "
                + USER_CONTACT + " TEXT, "
                + USER_INSERT_DATE + " TEXT, "
                + USER_UPDATE_DATE + " TEXT, "
                + USER_DELETE_DATE + " TEXT, "
                + USER_TYPE + " INTEGER NOT NULL, "
                + USER_IS_SYNCED + " INTEGER,"
                + USER_ACCESS_TOKEN + " TEXT)";
    }

    public static void getUsers(Context context, ArrayList<User> users) throws ParseException
    {
        if (users == null)
            throw new IllegalArgumentException("ArrayList<User> cannot be null");
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor userCursor = db.query(TABLE_USER, null, null, null, null, null, null);
        if (userCursor != null)
        {
            if (userCursor.moveToFirst())
            {
                do
                {
                    User user = new User();
                    User.parseFromCursor(userCursor, user);
                    if (user.id != -1)
                    {
                        users.add(user);
                    }
                }
                while (userCursor.moveToNext());
            }
            userCursor.close();
        }
    }

    public static void getUser(Context context, User user) throws ParseException
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);

        String whereClause = USER_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(user.id)};
        Cursor userCursor = db.query(TABLE_USER, null, whereClause, whereArgs, null, null, null);
        if (userCursor != null)
        {
            if (userCursor.moveToFirst())
            {
                User.parseFromCursor(userCursor, user);
            }
            userCursor.close();
        }
    }

    public static int getUsersByChannelId(Context context, int channelId)
    {
        int users = 0;
        String qry = "select count(" + CHANNEL_USER_USER_ID + ") from " + TABLE_CHANNEL_USER + " where " + CHANNEL_USER_CHANNEL_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(channelId)};
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        Cursor cursor = db.rawQuery(qry, whereArgs);
        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                users = cursor.getInt(0);
            }
            cursor.close();
        }

        return users;
    }

    public static boolean addUser(Context context, User user)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues userValues = new ContentValues();
        userValues.put(USER_ID, user.id);
        userValues.put(USER_ROLE_ID, user.roleId);
        userValues.put(USER_TYPE, user.type);
        userValues.put(USER_EMAIL, user.email);
        userValues.put(USER_NAME, user.name);
        userValues.put(USER_DESIGNATION, user.designation);
        userValues.put(USER_ADDRESS, user.address);
        userValues.put(USER_CONTACT, user.contact);
        userValues.put(USER_IS_SYNCED, String.valueOf(user.isSynced));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (user.insertDate != null)
            userValues.put(USER_INSERT_DATE, Helper.getDateFormate().format(user.insertDate));
        if (user.updateDate != null)
            userValues.put(USER_UPDATE_DATE, Helper.getDateFormate().format(user.updateDate));
        if (user.deleteDate != null)
            userValues.put(USER_DELETE_DATE, Helper.getDateFormate().format(user.deleteDate));

        long addUserCount = db.replace(TABLE_USER, null, userValues);
        return (addUserCount > 0);
    }

    public static boolean updateUser(Context context, User user)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues userValues = new ContentValues();
        userValues.put(USER_ID, user.id);
        userValues.put(USER_ROLE_ID, user.roleId);
        userValues.put(USER_TYPE, user.type);
        userValues.put(USER_EMAIL, user.email);
        userValues.put(USER_NAME, user.name);
        userValues.put(USER_DESIGNATION, user.designation);
        userValues.put(USER_ADDRESS, user.address);
        userValues.put(USER_CONTACT, user.contact);
        userValues.put(USER_IS_SYNCED, String.valueOf(user.isSynced));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (user.insertDate != null)
            userValues.put(USER_INSERT_DATE, Helper.getDateFormate().format(user.insertDate));
        if (user.updateDate != null)
            userValues.put(USER_UPDATE_DATE, Helper.getDateFormate().format(user.updateDate));
        if (user.deleteDate != null)
            userValues.put(USER_DELETE_DATE, Helper.getDateFormate().format(user.deleteDate));

        String whereClause = USER_ID + " = ? AND " + USER_EMAIL + " = ?";
        String[] whereArgs = new String[]{String.valueOf(user.id), user.email};
        long addUserCount = db.replace(TABLE_USER, null, userValues); //, SQLiteDatabase.CONFLICT_REPLACE);
        return (addUserCount > 0);
    }

    public static boolean removeUser(Context context, User user)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        String whereClause = USER_ID + " = ? AND " + USER_EMAIL + " = ?";
        String[] whereArgs = new String[]{String.valueOf(user.id), user.email};
        long addUserCount = db.delete(TABLE_USER, whereClause, whereArgs);
        return (addUserCount > 0);
    }

    public static String getInsertChannelSQL()
    {
        return "CREATE TABLE IF NOT EXISTS  " + TABLE_CHANNEL + "("
                + CHANNEL_ID + " INTEGER  PRIMARY KEY  AUTOINCREMENT, "
                + CHANNEL_CREATOR_ID + " INTEGER NOT NULL, "
                + CHANNEL_NAME + " TEXT, "
                + CHANNEL_LOCATION + " TEXT, "
                + CHANNEL_IS_SYNCED + " INTEGER, "
                + CHANNEL_INSERT_DATE + " TEXT, "
                + CHANNEL_UPDATE_DATE + " TEXT, "
                + CHANNEL_DELETE_DATE + " TEXT,"
                + CHANNEL_THUMBNAIL + " TEXT)";
    }

    public static void getChannels(Context context, ArrayList<Channel> channels) throws ParseException
    {
        if (channels == null)
            throw new IllegalArgumentException("ArrayList<Channel> cannot be null");
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor channelCursor = db.query(TABLE_CHANNEL, null, null, null, null, null, null);
        if (channelCursor != null)
        {
            if (channelCursor.moveToFirst())
            {
                do
                {
                    Channel channel = new Channel();
                    Channel.parseFromCursor(channelCursor, channel);
                    if (channel.id != -1)
                    {
                        channel.creator = new User();
                        channel.creator.id = channel.creatorId;
                        getUser(context, channel.creator);
                        channels.add(channel);
                    }
                }
                while (channelCursor.moveToNext());
            }
            channelCursor.close();
        }
    }

    public static ArrayList<String> getChannelNames(Context context)
    {
        ArrayList<String> channelNames = new ArrayList<>();
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor channelCursor = db.query(TABLE_CHANNEL, new String[]{CHANNEL_NAME}, null, null, null, null, null);
        if (channelCursor != null)
        {
            if (channelCursor.moveToFirst())
            {
                do
                {
                    channelNames.add(channelCursor.getString(channelCursor.getColumnIndex(CHANNEL_NAME)));
                }
                while (channelCursor.moveToNext());
            }
            channelCursor.close();
        }
        return channelNames;
    }

    public static void getChannel(Context context, Channel channel) throws ParseException
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        String whereClause = CHANNEL_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(channel.id)};
        Cursor channelCursor = db.query(TABLE_CHANNEL, null, whereClause, whereArgs, null, null, null);

        if (channelCursor != null && channelCursor.moveToFirst())
        {
            Channel.parseFromCursor(channelCursor, channel);
            if (channel.id != -1)
            {
                channel.creator = new User();
                channel.creator.id = channel.creatorId;
                getUser(context, channel.creator);
            }
        }
        else
            channel.id = -1;
        if (channelCursor != null)
            channelCursor.close();
    }

    public static boolean addChannel(Context context, Channel channel)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues channelValues = new ContentValues();
        channelValues.put(CHANNEL_ID, channel.id);
        channelValues.put(CHANNEL_CREATOR_ID, channel.creatorId);
        channelValues.put(CHANNEL_NAME, channel.name);
        channelValues.put(CHANNEL_LOCATION, channel.location);
        channelValues.put(CHANNEL_IS_SYNCED, String.valueOf(channel.isSynced));
        channelValues.put(CHANNEL_THUMBNAIL, String.valueOf(channel.thumbnail));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (channel.insertDate != null)
            channelValues.put(CHANNEL_INSERT_DATE, Helper.getDateFormate().format(channel.insertDate));
        if (channel.updateDate != null)
            channelValues.put(CHANNEL_UPDATE_DATE, Helper.getDateFormate().format(channel.updateDate));
        if (channel.deleteDate != null)
            channelValues.put(CHANNEL_DELETE_DATE, Helper.getDateFormate().format(channel.deleteDate));
        long addChannelCount = db.replace(TABLE_CHANNEL, null, channelValues);
        return (addChannelCount > 0);
    }

    public static boolean updateChannel(Context context, Channel channel)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues channelValues = new ContentValues();
        channelValues.put(CHANNEL_CREATOR_ID, channel.creatorId);
        channelValues.put(CHANNEL_NAME, channel.name);
        channelValues.put(CHANNEL_LOCATION, channel.location);
        channelValues.put(CHANNEL_IS_SYNCED, String.valueOf(channel.isSynced));
        channelValues.put(CHANNEL_THUMBNAIL, String.valueOf(channel.thumbnail));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (channel.insertDate != null)
            channelValues.put(CHANNEL_INSERT_DATE, Helper.getDateFormate().format(channel.insertDate));
        if (channel.updateDate != null)
            channelValues.put(CHANNEL_UPDATE_DATE, Helper.getDateFormate().format(channel.updateDate));
        if (channel.deleteDate != null)
            channelValues.put(CHANNEL_DELETE_DATE, Helper.getDateFormate().format(channel.deleteDate));
        String whereClause = CHANNEL_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(channel.id)};
        long channelUpdateCount = db.update(TABLE_CHANNEL, channelValues, whereClause, whereArgs);
        return (channelUpdateCount > 0);
    }

    public static boolean removeChannel(Context context, Channel channel)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        String whereClause = CHANNEL_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(channel.id)};
        long removeChannelCount = db.delete(TABLE_USER, whereClause, whereArgs);
        return (removeChannelCount > 0);
    }

    public static String getInsertChannelFileSQL()
    {

        return "CREATE TABLE IF NOT EXISTS  " + TABLE_CHANNEL_FILE + "("
                + CHANNEL_FILE_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + CHANNEL_FILE_FILE_ID + " INTEGER NOT NULL,"
                + CHANNEL_FILE_CHANNEL_ID + " INTEGER,"
                + CHANNEL_FILE_IS_SYNCED + " INTEGER,"
                + CHANNEL_FILE_VERSION_ID + " INTEGER,"
                + CHANNEL_FILE_INSERT_DATE + " TEXT,"
                + CHANNEL_FILE_UPDATE_DATE + " TEXT,"
                + CHANNEL_FILE_DELETE_DATE + " TEXT " + ")";
    }

    public static void getChannelFiles(Context context, ArrayList<ChannelFiles> channelFiles) throws ParseException
    {
        if (channelFiles == null)
            throw new IllegalArgumentException("ArrayList<ChannelFiles> cannot be null");
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor channelFileCursor = db.query(TABLE_CHANNEL_FILE, null, null, null, null, null, null);
        if (channelFileCursor != null)
        {
            if (channelFileCursor.moveToFirst())
            {
                do
                {
                    ChannelFiles channelFile = new ChannelFiles();
                    ChannelFiles.parseFromCursor(channelFileCursor, channelFile);
                    if (channelFile.id != -1)
                    {
                        channelFiles.add(channelFile);
                    }
                }
                while (channelFileCursor.moveToNext());
            }
            channelFileCursor.close();
        }
    }

    public static void getChannelFile(Context context, ChannelFiles channelFile) throws ParseException
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        String whereClause = CHANNEL_FILE_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(channelFile.id)};
        Cursor channelFileCursor = db.query(TABLE_CHANNEL_FILE, null, whereClause, whereArgs, null, null, null);
        if (channelFileCursor != null)
        {
            if (channelFileCursor.moveToFirst())
            {
                ChannelFiles.parseFromCursor(channelFileCursor, channelFile);
                channelFileCursor.close();
            }
            channelFileCursor.close();
        }
        else
            channelFile.id = -1;
    }

    public static boolean addChannelFiles(Context context, ChannelFiles channelFiles)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues channelFilesValues = new ContentValues();
        channelFilesValues.put(CHANNEL_FILE_ID, channelFiles.id);
        channelFilesValues.put(CHANNEL_FILE_CHANNEL_ID, channelFiles.channelId);
        channelFilesValues.put(CHANNEL_FILE_VERSION_ID, channelFiles.fileVersionId);
        channelFilesValues.put(CHANNEL_FILE_FILE_ID, channelFiles.fileId);
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (channelFiles.insertDate != null)
            channelFilesValues.put(CHANNEL_FILE_INSERT_DATE, Helper.getDateFormate().format(channelFiles.insertDate));
        if (channelFiles.updateDate != null)
            channelFilesValues.put(CHANNEL_FILE_UPDATE_DATE, Helper.getDateFormate().format(channelFiles.updateDate));
        if (channelFiles.deleteDate != null)
            channelFilesValues.put(CHANNEL_FILE_DELETE_DATE, Helper.getDateFormate().format(channelFiles.deleteDate));
        long addChannelFilesCount = db.replace(TABLE_CHANNEL_FILE, null, channelFilesValues);
        return (addChannelFilesCount > 0);
    }

    public static boolean updateChannelFiles(Context context, ChannelFiles channelFiles)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues channelFilesValues = new ContentValues();
        channelFilesValues.put(CHANNEL_FILE_ID, channelFiles.id);
        channelFilesValues.put(CHANNEL_FILE_CHANNEL_ID, channelFiles.channelId);
        channelFilesValues.put(CHANNEL_FILE_VERSION_ID, channelFiles.fileVersionId);
        channelFilesValues.put(CHANNEL_FILE_FILE_ID, channelFiles.fileId);
        channelFilesValues.put(CHANNEL_FILE_IS_SYNCED, String.valueOf(channelFiles.isSynced));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (channelFiles.insertDate != null)
            channelFilesValues.put(CHANNEL_FILE_INSERT_DATE, Helper.getDateFormate().format(channelFiles.insertDate));
        if (channelFiles.updateDate != null)
            channelFilesValues.put(CHANNEL_FILE_UPDATE_DATE, Helper.getDateFormate().format(channelFiles.updateDate));
        if (channelFiles.deleteDate != null)
            channelFilesValues.put(CHANNEL_FILE_DELETE_DATE, Helper.getDateFormate().format(channelFiles.deleteDate));
        String whereClause = CHANNEL_FILE_FILE_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(channelFiles.fileId)};
        long updateChannelFilesCount = db.update(TABLE_CHANNEL_FILE, channelFilesValues, whereClause, whereArgs);
        return (updateChannelFilesCount > 0);
    }

    public static boolean removeChannelFiles(Context context, ChannelFiles channelFiles)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        String whereClause = CHANNEL_FILE_FILE_ID + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(channelFiles.fileId)};
        long addUserCount = db.delete(TABLE_CHANNEL_FILE, whereClause, whereArgs);
        return (addUserCount > 0);
    }

    public static String getInsertChannelUserSQL()
    {
        return "CREATE TABLE IF NOT EXISTS  " + TABLE_CHANNEL_USER + "("
                + CHANNEL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CHANNEL_USER_CHANNEL_ID + " INTEGER NOT NULL ,  "
                + CHANNEL_USER_USER_ID + " INTEGER NOT NULL,"
                + CHANNEL_USER_ADDED_BY + " INTEGER NOT NULL,"
                + CHANNEL_USER_INSERT_DATE + " TEXT,"
                + CHANNEL_USER_UPDATE_DATE + " TEXT,"
                + CHANNEL_USER_DELETE_DATE + " TEXT,"
                + CHANNEL_USER_IS_SYNCED + " INTEGER"
                + ")";
    }

    public static void getChannelUsers(Context context, ArrayList<ChannelUser> channelUsers) throws ParseException
    {
        if (channelUsers == null)
            throw new IllegalArgumentException("ArrayList<ChannelUsers> cannot be null");
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor channelUsersCursor = db.query(TABLE_CHANNEL_USER, null, null, null, null, null, null);
        if (channelUsersCursor != null)
        {
            if (channelUsersCursor.moveToFirst())
            {
                do
                {
                    ChannelUser channelUser = new ChannelUser();
                    channelUser.parseFromCursor(channelUsersCursor, channelUser);
                    if (channelUser.id != -1)
                    {
                        channelUsers.add(channelUser);
                    }
                }
                while (channelUsersCursor.moveToNext());
            }
            channelUsersCursor.close();
        }
    }

    public static void getChannelUser(Context context, ChannelUser channelUser) throws ParseException
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        String whereClause = CHANNEL_USER_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(channelUser.id)};
        Cursor channelUserCursor = db.query(TABLE_CHANNEL, null, whereClause, whereArgs, null, null, null);
        if (channelUserCursor != null)
        {
            if (channelUserCursor.moveToFirst())
            {
                ChannelUser.parseFromCursor(channelUserCursor, channelUser);
                channelUserCursor.close();
            }
            channelUserCursor.close();
        }
        else
            channelUser.id = -1;
    }

    public static boolean addChannelUsers(Context context, ChannelUser channelUser)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues channelUserValues = new ContentValues();
        channelUserValues.put(CHANNEL_USER_ID, channelUser.id);
        channelUserValues.put(CHANNEL_USER_ADDED_BY, channelUser.addedBy);
        channelUserValues.put(CHANNEL_USER_USER_ID, channelUser.userId);
        channelUserValues.put(CHANNEL_USER_CHANNEL_ID, channelUser.channelId);
        channelUserValues.put(CHANNEL_USER_IS_SYNCED, String.valueOf(channelUser.isSynced));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (channelUser.insertDate != null)
            channelUserValues.put(CHANNEL_USER_INSERT_DATE, Helper.getDateFormate().format(channelUser.insertDate));
        if (channelUser.updateDate != null)
            channelUserValues.put(CHANNEL_USER_UPDATE_DATE, Helper.getDateFormate().format(channelUser.updateDate));
        if (channelUser.deleteDate != null)
            channelUserValues.put(CHANNEL_USER_DELETE_DATE, Helper.getDateFormate().format(channelUser.deleteDate));
        long addChannelUsersCount = db.replace(TABLE_CHANNEL_USER, null, channelUserValues);
        return (addChannelUsersCount > 0);
    }

    public static boolean updateChannelUsers(Context context, ChannelUser channelUser)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues channelUserValues = new ContentValues();
        channelUserValues.put(CHANNEL_USER_ID, channelUser.id);
        channelUserValues.put(CHANNEL_USER_ADDED_BY, channelUser.addedBy);
        channelUserValues.put(CHANNEL_USER_USER_ID, channelUser.userId);
        channelUserValues.put(CHANNEL_USER_CHANNEL_ID, channelUser.channelId);
        channelUserValues.put(CHANNEL_USER_IS_SYNCED, String.valueOf(channelUser.isSynced));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (channelUser.insertDate != null)
            channelUserValues.put(CHANNEL_USER_INSERT_DATE, Helper.getDateFormate().format(channelUser.insertDate));
        if (channelUser.updateDate != null)
            channelUserValues.put(CHANNEL_USER_UPDATE_DATE, Helper.getDateFormate().format(channelUser.updateDate));
        if (channelUser.deleteDate != null)
            channelUserValues.put(CHANNEL_USER_DELETE_DATE, Helper.getDateFormate().format(channelUser.deleteDate));
        String whereClause = CHANNEL_USER_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(channelUser.id)};
        long updateChannelUsersCount = db.update(TABLE_CHANNEL_USER, channelUserValues, whereClause, whereArgs);
        return (updateChannelUsersCount > 0);
    }

    public static boolean removeChannelUsers(Context context, ChannelUser channelUser)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        String whereClause = CHANNEL_USER_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(channelUser.id)};
        long removeChannelUserCount = db.delete(TABLE_CHANNEL_USER, whereClause, whereArgs);
        return (removeChannelUserCount > 0);
    }

    public static String getInsertMediaSQL()
    {
        return "CREATE TABLE IF NOT EXISTS  " + TABLE_MEDIA + "("
                + MEDIA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MEDIA_NAME + " TEXT , "
                + MEDIA_SIZE + " INTEGER ,"
                + MEDIA_PARENT_ID + " INTEGER,"
                + MEDIA_CURRENT_VERSION_ID + " INTEGER , "
                + MEDIA_DESCRIPTION + " TEXT , "
                + MEDIA_LOCATION + " TEXT , "
                + MEDIA_ATTACHABLE + " TEXT , "
                + MEDIA_HAS_CONTENT + " TEXT , "
                + MEDIA_TYPE + " TEXT , "
                + MEDIA_IS_SYNCED + " INTEGER , "
                + MEDIA_IS_DOWNLOADED + " INTEGER , "
                + MEDIA_INSERT_DATE + " TEXT , "
                + MEDIA_UPDATE_DATE + " TEXT , "
                + MEDIA_DELETE_DATE + " TEXT ,"
                + MEDIA_IS_DOWNLOADING + " INTEGER ,"
                + MEDIA_ICON + " TEXT )";
    }

    public static void getAllMediaList(Context context, List<Media> mediaList) throws ParseException
    {
        if (mediaList == null)
            throw new IllegalArgumentException("ArrayList<MediaList> cannot be null");
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor mediaCursor = db.query(TABLE_MEDIA, null, null, null, null, null, null);
        if (mediaCursor != null)
        {
            if (mediaCursor.moveToFirst())
            {
                do
                {
                    Media media = new Media();
                    media.parseFromCursor(mediaCursor, media);
                    if (media.id != -1)
                    {
                        mediaList.add(media);
                    }
                }
                while (mediaCursor.moveToNext());
            }
            mediaCursor.close();
        }
    }

    public static int getCountRootMediaFromChannelId(Context context, int channelId)
    {
        int size = 0;
        SQLiteDatabase db = LocalDatabase.getReadable(context);

        String qry = "SELECT * FROM " + TABLE_MEDIA + " WHERE (" + MEDIA_ID + " IN (SELECT " + CHANNEL_FILE_FILE_ID + " FROM " + TABLE_CHANNEL_FILE + " WHERE " + CHANNEL_FILE_CHANNEL_ID + " = ?))";
        Cursor cur = db.rawQuery(qry, new String[]{String.valueOf(channelId)});
        if (cur != null && cur.moveToFirst())
        {
            do
            {
                try
                {
                    Media media = new Media();
                    media.id = cur.getInt(cur.getColumnIndex(DataHelper.MEDIA_ID));
                    getMedia(context, media);
                    if (media.type.equalsIgnoreCase(MediaFragment.FILETYPE_FOLDER))
                    {
                        size += getCountMediaFromChannelId(context, media.id);
                    }
                    else
                    {
                        size += 1;
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            while (cur.moveToNext());
        }
        if (cur != null)
            cur.close();
        return size;
    }

    public static int getCountMediaFromChannelId(Context context, int parentId)
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        int size = 0;
        String qry = "SELECT * FROM " + TABLE_MEDIA + " WHERE " + MEDIA_PARENT_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(parentId)};

        Cursor cur = db.rawQuery(qry, whereArgs);

        if (cur != null && cur.moveToFirst())
        {
            do
            {
                try
                {
                    Media media = new Media();
                    media.id = cur.getInt(cur.getColumnIndex(DataHelper.MEDIA_ID));
                    getMedia(context, media);
                    if (media.type.equalsIgnoreCase(MediaFragment.FILETYPE_FOLDER))
                    {
                        size += getCountMediaFromChannelId(context, media.id);
                    }
                    else
                    {
                        size += 1;
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            while (cur.moveToNext());
        }
        if (cur != null)
            cur.close();
        return size;
    }

    public static void getMedia(Context context, Media media) throws ParseException
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        String whereClause = MEDIA_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(media.id)};
        Cursor mediaCursor = db.query(TABLE_MEDIA, null, whereClause, whereArgs, null, null, null);
        if (mediaCursor != null && mediaCursor.moveToFirst())
        {
            if (mediaCursor.moveToFirst())
            {
                Media.parseFromCursor(mediaCursor, media);
            }
        }
        else
            media.id = -1;

        if (mediaCursor != null)
            mediaCursor.close();
    }

    public static boolean addMedia(Context context, Media media)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues mediaValues = new ContentValues();
        mediaValues.put(MEDIA_ID, media.id);
        mediaValues.put(MEDIA_NAME, media.name);
        mediaValues.put(MEDIA_IS_DOWNLOADED, media.isDownloaded);
        mediaValues.put(MEDIA_IS_DOWNLOADING, media.isDownloading);
        mediaValues.put(MEDIA_TYPE, media.type);
        mediaValues.put(MEDIA_SIZE, media.size);
        mediaValues.put(MEDIA_PARENT_ID, media.parentId);
        mediaValues.put(MEDIA_CURRENT_VERSION_ID, String.valueOf(media.currentVersionId));
        mediaValues.put(MEDIA_DESCRIPTION, media.description);
        mediaValues.put(MEDIA_LOCATION, media.location);
        mediaValues.put(MEDIA_ATTACHABLE, media.attachable);
        mediaValues.put(MEDIA_HAS_CONTENT, media.hasContent);
        mediaValues.put(MEDIA_IS_SYNCED, String.valueOf(media.isSynced));
        mediaValues.put(MEDIA_ICON, String.valueOf(media.icon));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (media.insertDate != null)
            mediaValues.put(MEDIA_INSERT_DATE, Helper.getDateFormate().format(media.insertDate));
        if (media.updateDate != null)
            mediaValues.put(MEDIA_UPDATE_DATE, Helper.getDateFormate().format(media.updateDate));
        if (media.deleteDate != null)
            mediaValues.put(MEDIA_DELETE_DATE, Helper.getDateFormate().format(media.deleteDate));
        long addMediaCount = db.replace(TABLE_MEDIA, null, mediaValues);
        return (addMediaCount > 0);
    }

    public static boolean isMediaExist(Context context, int mediaId)
    {
        boolean flag = false;
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        String whereClause = MEDIA_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(mediaId)};
        Cursor mediaCursor = db.query(TABLE_MEDIA, null, whereClause, whereArgs, null, null, null);
        if (mediaCursor != null && mediaCursor.getCount() > 0)
        {
            flag = true;
        }
        if (mediaCursor != null)
            mediaCursor.close();
        return flag;
    }

    public static boolean updateMedia(Context context, Media media)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        if (isMediaExist(context, media.id))
        {
            ContentValues mediaValues = new ContentValues();
            mediaValues.put(MEDIA_ID, media.id);
            mediaValues.put(MEDIA_NAME, media.name);
            mediaValues.put(MEDIA_IS_DOWNLOADED, media.isDownloaded);
            mediaValues.put(MEDIA_IS_DOWNLOADING, media.isDownloading);
            mediaValues.put(MEDIA_TYPE, media.type);
            mediaValues.put(MEDIA_SIZE, media.size);
            mediaValues.put(MEDIA_PARENT_ID, media.parentId);
            mediaValues.put(MEDIA_CURRENT_VERSION_ID, String.valueOf(media.currentVersionId));
            mediaValues.put(MEDIA_DESCRIPTION, media.description);
            mediaValues.put(MEDIA_LOCATION, media.location);
            mediaValues.put(MEDIA_ATTACHABLE, media.attachable);
            mediaValues.put(MEDIA_HAS_CONTENT, media.hasContent);
            mediaValues.put(MEDIA_IS_SYNCED, String.valueOf(media.isSynced));
            mediaValues.put(MEDIA_ICON, String.valueOf(media.icon));
            //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
            if (media.insertDate != null)
                mediaValues.put(MEDIA_INSERT_DATE, Helper.getDateFormate().format(media.insertDate));
            if (media.updateDate != null)
                mediaValues.put(MEDIA_UPDATE_DATE, Helper.getDateFormate().format(media.updateDate));
            if (media.deleteDate != null)
                mediaValues.put(MEDIA_DELETE_DATE, Helper.getDateFormate().format(media.deleteDate));
            String whereClause = MEDIA_ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(media.id)};
            long updateMediaCount = db.update(TABLE_MEDIA, mediaValues, whereClause, whereArgs);
            return (updateMediaCount > 0);
        }
        else
        {
            return addMedia(context, media);
        }
    }

    public static boolean updateAllMedia(Context context)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues mediaValues = new ContentValues();
        mediaValues.put(MEDIA_IS_DOWNLOADING, String.valueOf(1));
        String whereClause = MEDIA_IS_DOWNLOADED + " = ?";
        String[] whereArgs = new String[]{String.valueOf(0)};
        long updateMediaCount = db.update(TABLE_MEDIA, mediaValues, whereClause, whereArgs);
        return (updateMediaCount > 0);
    }

    public static boolean removeMedia(Context context, Media media)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        String whereClause = MEDIA_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(media.id)};
        long removeMediaCount = db.delete(TABLE_MEDIA, whereClause, whereArgs);
        return (removeMediaCount > 0);
    }

    public static boolean removeMediaFromAllTablesById(Context context, int id)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        long removeMediaCount = db.delete(TABLE_MEDIA, MEDIA_ID + " = ?", new String[]{String.valueOf(id)});
        long removeChannelFileCount = db.delete(TABLE_CHANNEL_FILE, CHANNEL_FILE_FILE_ID + " = ?", new String[]{String.valueOf(id)});
        long removeRecentCount = db.delete(TABLE_RECENT, RECENT_ID + "=  ?", new String[]{String.valueOf(id)});

        return (removeMediaCount > 0 && removeChannelFileCount > 0 && removeRecentCount > 0);
    }

    public static final long getAllDownloadSize(Context context)
    {
        long size = 0;
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        Cursor cur = db.rawQuery("SELECT SUM(" + MEDIA_SIZE + ") FROM " + TABLE_MEDIA + " where " + MEDIA_IS_DOWNLOADED + "= 0 and " + MEDIA_TYPE + " <> \'folder\'", null);
        if (cur != null && cur.moveToFirst())
        {
            size = cur.getInt(0);
        }
        if (cur != null)
            cur.close();
        return size;
    }

    public static final void getAllDownloadList(Context context, ArrayList<MediaAllDownload> downloadList)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_MEDIA + " where " + MEDIA_IS_DOWNLOADED + "= 0 and " + MEDIA_TYPE + " <> \'folder\'", null);

        if (cur != null && cur.moveToFirst())
        {
            do
            {
                MediaAllDownload mediaAllDownload = new MediaAllDownload();
                mediaAllDownload.mediaId = cur.getInt(cur.getColumnIndex(MEDIA_ID));
                mediaAllDownload.currentVersionId = cur.getInt(cur.getColumnIndex(MEDIA_CURRENT_VERSION_ID));
                downloadList.add(mediaAllDownload);
            }
            while (cur.moveToNext());
        }
        if (cur != null)
            cur.close();
    }

    public static final int getMediaParentId(Context context, int id)
    {
        int parentId = -1;
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        Cursor cur = db.rawQuery("SELECT " + MEDIA_PARENT_ID + "  FROM " + TABLE_MEDIA + " where " + MEDIA_ID + "= ?", new String[]{String.valueOf(id)});

        if (cur != null && cur.moveToFirst())
        {
            parentId = cur.getInt(cur.getColumnIndex(MEDIA_PARENT_ID));
        }
        if (cur != null)
            cur.close();
        return parentId;
    }

    public static void removeMediaPhysically(Activity activity, View parentLayout)
    {
        SQLiteDatabase db = LocalDatabase.getReadable(activity);
        Cursor cursor = db.query(TABLE_MEDIA, new String[]{MEDIA_ID}, MEDIA_IS_DOWNLOADED + " = ?", new String[]{String.valueOf("1")}, null, null, null);
        if (cursor != null && cursor.moveToFirst())
        {
            do
            {
                int fileId = cursor.getInt(cursor.getColumnIndex(MEDIA_ID));
                File mediaFile = new File(activity.getFilesDir() + File.separator + String.valueOf(fileId));
                if (mediaFile.exists())
                    mediaFile.delete();
            }
            while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
    }

    public static void closeLocalDatabase(Context context)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        db.close();
    }

    public static String getInsertMediaVersionSQL()
    {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_MEDIA_VERSION + "("
                + MEDIA_VERSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + MEDIA_VERSION + " INTEGER NOT NULL ,  "
                + MEDIA_VERSION_FILE_ID + " INTEGER , "
                + MEDIA_VERSION_CREATOR_ID + " INTEGER , "
                + MEDIA_VERSION_MODIFIER_ID + "  INTEGER , "
                + MEDIA_VERSION_IS_SYNCED + "  INTEGER , "
                + MEDIA_VERSION_INSERT_DATE + " TEXT , "
                + MEDIA_VERSION_UPDATE_DATE + " TEXT , "
                + MEDIA_VERSION_DELETE_DATE + " TEXT )";
    }

    public static void getMediaVersions(Context context, ArrayList<MediaVersion> mediaVersions) throws ParseException
    {
        if (mediaVersions == null)
            throw new IllegalArgumentException("ArrayList<MediaVersions> cannot be null");
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor mediaVersionCursor = db.query(TABLE_MEDIA_VERSION, null, null, null, null, null, null);
        if (mediaVersionCursor != null)
        {
            if (mediaVersionCursor.moveToFirst())
            {
                do
                {
                    MediaVersion mediaVersion = new MediaVersion();
                    MediaVersion.parseFromCursor(mediaVersionCursor, mediaVersion);
                    if (mediaVersion.id != -1)
                    {
                        mediaVersions.add(mediaVersion);
                    }
                }
                while (mediaVersionCursor.moveToNext());
            }
            mediaVersionCursor.close();
        }
    }

    public static void getMediaVersion(Context context, MediaVersion mediaVersion) throws ParseException
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        String whereClause = MEDIA_VERSION_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(mediaVersion.id)};
        Cursor mediaCursor = db.query(TABLE_MEDIA_VERSION, null, whereClause, whereArgs, null, null, null);
        if (mediaCursor != null)
        {
            if (mediaCursor.moveToFirst())
            {
                MediaVersion.parseFromCursor(mediaCursor, mediaVersion);
            }
            mediaCursor.close();
        }
        else
            mediaVersion.id = -1;
    }

    public static boolean addMediaVersion(Context context, MediaVersion mediaVersion)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues mediaversionValues = new ContentValues();
        mediaversionValues.put(MEDIA_VERSION_CREATOR_ID, mediaVersion.creatorId);
        mediaversionValues.put(MEDIA_VERSION_ID, mediaVersion.id);
        mediaversionValues.put(MEDIA_VERSION, mediaVersion.version);
        mediaversionValues.put(MEDIA_VERSION_FILE_ID, mediaVersion.fileId);
        mediaversionValues.put(MEDIA_VERSION_MODIFIER_ID, mediaVersion.modifierId);
        mediaversionValues.put(MEDIA_VERSION_IS_SYNCED, String.valueOf(mediaVersion.isSynced));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (mediaVersion.insertDate != null)
            mediaversionValues.put(MEDIA_VERSION_INSERT_DATE, Helper.getDateFormate().format(mediaVersion.insertDate));
        if (mediaVersion.updateDate != null)
            mediaversionValues.put(MEDIA_VERSION_UPDATE_DATE, Helper.getDateFormate().format(mediaVersion.updateDate));
        if (mediaVersion.deleteDate != null)
            mediaversionValues.put(MEDIA_VERSION_DELETE_DATE, Helper.getDateFormate().format(mediaVersion.deleteDate));
        long addMediaVersionCount = db.replace(TABLE_MEDIA_VERSION, null, mediaversionValues);
        return (addMediaVersionCount > 0);
    }

    public static boolean updateMediaVersion(Context context, MediaVersion mediaVersion)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues mediaversionValues = new ContentValues();
        mediaversionValues.put(MEDIA_VERSION_CREATOR_ID, mediaVersion.creatorId);
        mediaversionValues.put(MEDIA_VERSION_ID, mediaVersion.id);
        mediaversionValues.put(MEDIA_VERSION, mediaVersion.version);
        mediaversionValues.put(MEDIA_VERSION_FILE_ID, mediaVersion.fileId);
        mediaversionValues.put(MEDIA_VERSION_MODIFIER_ID, mediaVersion.modifierId);
        mediaversionValues.put(MEDIA_VERSION_IS_SYNCED, String.valueOf(mediaVersion.isSynced));
        // SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (mediaVersion.insertDate != null)
            mediaversionValues.put(MEDIA_VERSION_INSERT_DATE, Helper.getDateFormate().format(mediaVersion.insertDate));
        if (mediaVersion.updateDate != null)
            mediaversionValues.put(MEDIA_VERSION_UPDATE_DATE, Helper.getDateFormate().format(mediaVersion.updateDate));
        if (mediaVersion.deleteDate != null)
            mediaversionValues.put(MEDIA_VERSION_DELETE_DATE, Helper.getDateFormate().format(mediaVersion.deleteDate));
        String whereClause = MEDIA_VERSION_ID + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(mediaVersion.id)};
        //long updateMediaVersionCount = db.insertWithOnConflict(TABLE_MEDIA_VERSION, null, mediaversionValues, SQLiteDatabase.CONFLICT_REPLACE);
        long updateMediaVersionCount = db.replace(TABLE_MEDIA_VERSION, null, mediaversionValues);
        return (updateMediaVersionCount > 0);
    }

    public static boolean removeMediaVersion(Context context, MediaVersion mediaVersion)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        String whereClause = MEDIA_VERSION_ID + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(mediaVersion.id)};
        long removeMedisVersionCount = db.delete(TABLE_MEDIA_VERSION, whereClause, whereArgs);
        return (removeMedisVersionCount > 0);
    }

    public static boolean isCommentExist(Context context, int commentId)
    {
        boolean flag = false;
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_COMMENT + " WHERE " + COMMENT_ID + " = ?", new String[]{String.valueOf(commentId)});
        if (cursor != null && cursor.moveToFirst())
        {
            Log.e("TEST>>", "Number is comment count: " + cursor.getInt(0));
            if (cursor.getInt(0) > 0)
            {
                flag = true;
            }

        }
        if (cursor != null)
            cursor.close();
        return flag;
    }

    public static String getCommentTableQuery()
    {
        return "CREATE TABLE IF NOT EXISTS  " + TABLE_COMMENT + "("
                + COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COMMENT_FILE_ID + " INTEGER NOT NULL ,  "
                + COMMENT + " TEXT , "
                + COMMENT_USER_ID + " INTEGER , "
                + COMMENT_IS_SYNCED + "  INTEGER , "
                + COMMENT_INSERT_DATE + " TEXT , "
                + COMMENT_UPDATE_DATE + " TEXT , "
                + COMMENT_DELETE_DATE + " TEXT "
                + ")";
    }

    public static void getUserComments(Context context, ArrayList<UserComment> userComments) throws ParseException
    {

        SQLiteDatabase db = LocalDatabase.getReadable(context);
        String whereClause = COMMENT_IS_SYNCED + " = ?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor userCommentCursor = db.query(TABLE_COMMENT, null, whereClause, whereArgs, null, null, null);
        if (userCommentCursor != null && userCommentCursor.moveToFirst())
        {
            do
            {
                UserComment userComment = new UserComment();
                UserComment.parseFromCursor(userCommentCursor, userComment);
                User user = new User();
                user.id = userComment.userId;
                DataHelper.getUser(context, user);
                userComment.user = user;
                userComments.add(userComment);
            }
            while (userCommentCursor.moveToNext());
        }
        else
            userComments = null;

        if (userCommentCursor != null)
            userCommentCursor.close();

    }

    public static void getCommentsOnMedia(Context context, int id, ArrayList<UserComment> userComments, boolean isLimit) throws ParseException
    {
        ArrayList<UserComment> comments = new ArrayList<>();
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor commentCursor;
        if (isLimit)
            commentCursor = db.rawQuery("SELECT UC.*, U.* FROM 'Comments' as UC LEFT OUTER JOIN 'Users' as U ON UC.UserId = U.Id WHERE UC.FileId = " + id + " ORDER BY UC.id DESC LIMIT 0,5", null);
        else
            commentCursor = db.rawQuery("SELECT UC.*, U.* FROM 'Comments' as UC LEFT OUTER JOIN 'Users' as U ON UC.UserId = U.Id WHERE UC.FileId = " + id, null);

        if (commentCursor != null && commentCursor.moveToFirst())
        {
            commentCursor.moveToFirst();
            do
            {
                try
                {
                    UserComment userComment = new UserComment();
                    userComment.id = commentCursor.getInt(commentCursor.getColumnIndex(COMMENT_ID));
                    userComment.userId = commentCursor.getInt(commentCursor.getColumnIndex(COMMENT_USER_ID));
                    userComment.comment = commentCursor.getString(commentCursor.getColumnIndex(COMMENT));
                    userComment.user = new User();
                    userComment.user.id = commentCursor.getInt(commentCursor.getColumnIndex(USER_ID));
                    userComment.user.name = commentCursor.getString(commentCursor.getColumnIndex(USER_NAME));
                    userComment.user.contact = commentCursor.getString(commentCursor.getColumnIndex(USER_CONTACT));
                    userComment.user.email = commentCursor.getString(commentCursor.getColumnIndex(USER_EMAIL));
                    userComment.user.address = commentCursor.getString(commentCursor.getColumnIndex(USER_ADDRESS));
                    userComment.user.roleId = commentCursor.getInt(commentCursor.getColumnIndex(USER_ROLE_ID));
                    userComment.user.type = commentCursor.getInt(commentCursor.getColumnIndex(USER_TYPE));
                    userComment.user.designation = commentCursor.getString(commentCursor.getColumnIndex(USER_DESIGNATION));

                    String tempDate = commentCursor.getString(commentCursor.getColumnIndex(USER_INSERT_DATE));
                    if (tempDate != null && !tempDate.isEmpty())
                    {
                        try
                        {
                            userComment.user.insertDate = Helper.getDateFormate().parse(tempDate);
                        }
                        catch (ParseException px)
                        {
                            userComment.user.insertDate = Helper.getDateFormate().parse(tempDate);
                            //userComment.user.insertDate = dateFormat.parse(Helper.parseDate(dateFormat.toPattern(), new Date(tempDate)));
                        }
                    }
                    tempDate = commentCursor.getString(commentCursor.getColumnIndex(USER_UPDATE_DATE));
                    if (tempDate != null && !tempDate.isEmpty())
                    {
                        try
                        {
                            userComment.user.updateDate = Helper.getDateFormate().parse(tempDate);
                        }
                        catch (ParseException px)
                        {
                            userComment.user.insertDate = Helper.getDateFormate().parse(tempDate);
                            //userComment.user.updateDate = dateFormat.parse(Helper.parseDate(dateFormat.toPattern(), new Date(tempDate)));
                        }
                    }
                    tempDate = commentCursor.getString(commentCursor.getColumnIndex(USER_DELETE_DATE));
                    if (tempDate != null && !tempDate.isEmpty())
                    {
                        try
                        {
                            userComment.user.deleteDate = Helper.getDateFormate().parse(tempDate);
                        }
                        catch (ParseException px)
                        {
                            userComment.user.insertDate = Helper.getDateFormate().parse(tempDate);
                            //userComment.user.deleteDate = dateFormat.parse(Helper.parseDate(dateFormat.toPattern(), new Date(tempDate)));
                        }
                    }
                    tempDate = commentCursor.getString(commentCursor.getColumnIndex(COMMENT_INSERT_DATE));
                    if (tempDate != null && !tempDate.isEmpty())
                    {
                        try
                        {
                            userComment.insertDate = Helper.getDateFormate().parse(tempDate);
                        }
                        catch (ParseException px)
                        {
                            userComment.insertDate = Helper.getDateFormate().parse(tempDate);
                            //userComment.insertDate = dateFormat.parse(Helper.parseDate(dateFormat.toPattern(), new Date(tempDate)));
                        }
                    }
                    tempDate = commentCursor.getString(commentCursor.getColumnIndex(COMMENT_UPDATE_DATE));
                    if (tempDate != null && !tempDate.isEmpty())
                    {
                        try
                        {
                            userComment.updateDate = Helper.getDateFormate().parse(tempDate);
                        }
                        catch (ParseException px)
                        {
                            userComment.insertDate = Helper.getDateFormate().parse(tempDate);
                            //userComment.updateDate = dateFormat.parse(Helper.parseDate(dateFormat.toPattern(), new Date(tempDate)));
                        }
                    }
                    userComments.add(userComment);
                }
                catch (Exception e)
                {
                    Log.e(" ######## ", "exception : " + e.toString());
                }

            }
            while (commentCursor.moveToNext());
        }
        if (commentCursor != null)
            commentCursor.close();
    }

    public static int getMediaCommentCount(Context context, int id)
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor commentCursor = db.rawQuery("SELECT UC.*, U.* FROM 'Comments' as UC LEFT OUTER JOIN 'Users' as U ON UC.UserId = U.Id WHERE UC.FileId = " + id, null);
        if (commentCursor != null)
        {
            int count = commentCursor.getCount();
            commentCursor.close();
            return count;
        }
        return 0;
    }

    public static boolean addUserComments(Context context, UserComment userComment)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues userCommentsValues = new ContentValues();
        if (userComment.id > 0)
            userCommentsValues.put(COMMENT_ID, userComment.id);
        userCommentsValues.put(COMMENT_FILE_ID, userComment.fileId);
        userCommentsValues.put(COMMENT, userComment.comment);
        userCommentsValues.put(COMMENT_USER_ID, userComment.userId);
        userCommentsValues.put(COMMENT_IS_SYNCED, String.valueOf(userComment.isSynced));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (userComment.insertDate != null)
            userCommentsValues.put(COMMENT_INSERT_DATE, Helper.getDateFormate().format(userComment.insertDate));
        if (userComment.updateDate != null)
            userCommentsValues.put(COMMENT_UPDATE_DATE, Helper.getDateFormate().format(userComment.updateDate));
        if (userComment.deleteDate != null)
            userCommentsValues.put(COMMENT_DELETE_DATE, Helper.getDateFormate().format(userComment.deleteDate));
        long addUserCommentsCount = db.replace(TABLE_COMMENT, null, userCommentsValues);
        return (addUserCommentsCount > 0);
    }

    public static boolean updateUserComments(Context context, UserComment userComment)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues userCommentsValues = new ContentValues();
        userCommentsValues.put(COMMENT_ID, userComment.id);
        userCommentsValues.put(COMMENT_FILE_ID, userComment.fileId);
        userCommentsValues.put(COMMENT, userComment.comment);
        userCommentsValues.put(COMMENT_USER_ID, userComment.userId);
        userCommentsValues.put(COMMENT_IS_SYNCED, String.valueOf(userComment.isSynced));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (userComment.insertDate != null)
            userCommentsValues.put(COMMENT_INSERT_DATE, Helper.getDateFormate().format(userComment.insertDate));
        if (userComment.updateDate != null)
            userCommentsValues.put(COMMENT_UPDATE_DATE, Helper.getDateFormate().format(userComment.updateDate));
        if (userComment.deleteDate != null)
            userCommentsValues.put(COMMENT_DELETE_DATE, Helper.getDateFormate().format(userComment.deleteDate));
        String whereClause = COMMENT_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(userComment.id)};
        long updateUserCommentscount = db.update(TABLE_COMMENT, userCommentsValues, whereClause, whereArgs);
        return (updateUserCommentscount > 0);
    }

    public static boolean removeUserComments(Context context, UserComment userComment)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        String whereClause = COMMENT_ID + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(userComment.id)};
        long removeUserCommentsCount = db.delete(TABLE_COMMENT, whereClause, whereArgs);
        return (removeUserCommentsCount > 0);
    }

    public static boolean removeUserOfflineComments(Context context, UserComment userComment)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        String whereClause = COMMENT_IS_SYNCED + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(1)};
        long removeUserCommentsCount = db.delete(TABLE_COMMENT, whereClause, whereArgs);
        return (removeUserCommentsCount > 0);
    }

    public static String getLikeTableQuery()
    {
        return "CREATE TABLE IF NOT EXISTS  " + TABLE_LIKE + "("
                + LIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + LIKE_ASSOCIATED_ID + " INTEGER NOT NULL ,  "
                + LIKE_TYPE + " TEXT , "
                + LIKE_USER_ID + " INTEGER , "
                + LIKE_IS_SYNCED + "  INTEGER , "
                + LIKE_INSERT_DATE + " TEXT , "
                + LIKE_UPDATE_DATE + " TEXT , "
                + LIKE_DELETE_DATE + " TEXT "
                + ")";
    }

    public static void getUserLike(Context context, ArrayList<UserLike> userLikes) throws ParseException
    {

        SQLiteDatabase db = LocalDatabase.getReadable(context);
        String whereClause = LIKE_IS_SYNCED + " = ?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor userLikeCursor = db.query(TABLE_LIKE, null, whereClause, whereArgs, null, null, null);
        if (userLikeCursor != null && userLikeCursor.moveToFirst())
        {
            do
            {
                UserLike userLike = new UserLike();
                UserLike.parseFromCursor(userLikeCursor, userLike);
                userLikes.add(userLike);
            }
            while (userLikeCursor.moveToNext());
        }
        else
            userLikes = null;

        if (userLikeCursor != null)
            userLikeCursor.close();
    }

    public static boolean addUserLikes(Context context, UserLike userLike)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues userLikeValues = new ContentValues();
        userLikeValues.put(LIKE_ID, userLike.id);
        userLikeValues.put(LIKE_ASSOCIATED_ID, userLike.associatedId);
        userLikeValues.put(LIKE_TYPE, userLike.type);
        userLikeValues.put(LIKE_USER_ID, userLike.userId);
        userLikeValues.put(LIKE_IS_SYNCED, String.valueOf(userLike.isSynced));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (userLike.insertDate != null)
            userLikeValues.put(LIKE_INSERT_DATE, Helper.getDateFormate().format(userLike.insertDate));
        if (userLike.updateDate != null)
            userLikeValues.put(LIKE_UPDATE_DATE, Helper.getDateFormate().format(userLike.updateDate));
        if (userLike.deleteDate != null)
            userLikeValues.put(LIKE_DELETE_DATE, Helper.getDateFormate().format(userLike.deleteDate));
        long addUseLikeCount = db.replace(TABLE_LIKE, null, userLikeValues);
        return (addUseLikeCount > 0);
    }

    public static boolean updateUserLikes(Context context, UserLike userLike)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues userLikeValues = new ContentValues();
        userLikeValues.put(LIKE_ID, userLike.id);
        userLikeValues.put(LIKE_ASSOCIATED_ID, userLike.associatedId);
        userLikeValues.put(LIKE_TYPE, userLike.type);
        userLikeValues.put(LIKE_USER_ID, userLike.userId);
        userLikeValues.put(LIKE_IS_SYNCED, String.valueOf(userLike.isSynced));
        userLikeValues.put(LIKE_INSERT_DATE, String.valueOf(userLike.insertDate));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (userLike.insertDate != null)
            userLikeValues.put(LIKE_INSERT_DATE, Helper.getDateFormate().format(userLike.insertDate));
        if (userLike.updateDate != null)
            userLikeValues.put(LIKE_UPDATE_DATE, Helper.getDateFormate().format(userLike.updateDate));
        if (userLike.deleteDate != null)
            userLikeValues.put(LIKE_DELETE_DATE, Helper.getDateFormate().format(userLike.deleteDate));
        String whereClause = LIKE_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(userLike.id)};
        long updateUserLikecount = db.update(TABLE_LIKE, userLikeValues, whereClause, whereArgs);
        return (updateUserLikecount > 0);
    }

    public static boolean removeUserLikes(Context context, UserLike userLike)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        String whereClause = LIKE_ID + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(userLike.id)};
        long removeUserCommentsCount = db.delete(TABLE_LIKE, whereClause, whereArgs);
        return (removeUserCommentsCount > 0);
    }

    public static boolean removeUserOfflineLikes(Context context, UserLike userLike)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        String whereClause = LIKE_IS_SYNCED + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(1)};
        long removeUserCommentsCount = db.delete(TABLE_LIKE, whereClause, whereArgs);
        return (removeUserCommentsCount > 0);
    }

    public static String getInsertMessageReceiversSQL()
    {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGE_RECEIVERS + "("
                + MESSAGE_RECEIVERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + MESSAGE_RECEIVERS_MESSAGE_ID + " INTEGER NOT NULL ,  "
                + MESSAGE_RECEIVERS_RECEIVER_ID + " INTEGER , "
                + MESSAGE_RECEIVERS_TYPE + " TEXT , "
                + MESSAGE_RECEIVERS_IS_SYNCED + "  INTEGER , "
                + MESSAGE_RECEIVERS_INSERT_DATE + " TEXT , "
                + MESSAGE_RECEIVERS_UPDATE_DATE + " TEXT , "
                + MESSAGE_RECEIVERS_DELETE_DATE + " TEXT "
                + ")";
    }

    public static String getInsertMessageFileSQL()
    {
        return "CREATE TABLE IF NOT EXISTS  " + TABLE_MESSAGE_FILE + "("
                + MESSAGE_FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MESSAGE_FILE_MESSAGE_ID + " INTEGER NOT NULL ,  "
                + MESSAGE_FILE_FILE_ID + " INTEGER , "
                + MESSAGE_FILE_FILE_VERSION_ID + " INTEGER , "
                + MESSAGE_FILE_IS_SYNCED + "  INTEGER , "
                + MESSAGE_FILE_INSERT_DATE + " TEXT , "
                + MESSAGE_FILE_UPDATE_DATE + " TEXT , "
                + MESSAGE_FILE_DELETE_DATE + " TEXT "
                + ")";
    }

    public static String getInsertMessageSQL()
    {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGE + "("
                + MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + MESSAGE_SENDER_ID + " INTEGER NOT NULL ,  "
                + MESSAGE_SUBJECT + " TEXT , "
                + MESSAGE_BODY + " TEXT , "
                + MESSAGE_IS_SYNCED + "  INTEGER , "
                + MESSAGE_INSERT_DATE + " TEXT , "
                + MESSAGE_UPDATE_DATE + " TEXT , "
                + MESSAGE_DELETE_DATE + " TEXT "
                + ")";
    }

    public static Cursor fileFilter(String str, Context context)
    {
        Cursor cursor = null;
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        cursor = db.rawQuery("select * from Channel where ChannalName LIKE '%" + str + "%'", null);
        return cursor;
    }

    public static void getMediaListFromParent(Context context, int channelId, ArrayList<Media> media) throws ParseException
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor mediaCursor = db.rawQuery("SELECT M.*, CASE WHEN LikeCount IS NULL THEN 0 ELSE LikeCount END as Likes, CASE WHEN CommentCount IS NULL THEN 0 ELSE CommentCount END as Comments " +
                "FROM (SELECT * FROM " + TABLE_CHANNEL_FILE + " as CFI WHERE CFI.channelId =" + channelId + ") as CF " +
                "INNER JOIN (SELECT * FROM " + TABLE_MEDIA + " as MI WHERE MI.parentId = -1) as M " +
                "ON CF.fileId = M.id " +
                "LEFT OUTER JOIN (SELECT Count(*) as LikeCount, " + LIKE_ASSOCIATED_ID + " FROM " + TABLE_LIKE + " GROUP BY " + LIKE_ASSOCIATED_ID + ") as UL " +
                "ON M.id = UL.associatedId " +
                "LEFT OUTER JOIN (SELECT Count(*) as CommentCount, " + COMMENT_FILE_ID + " FROM  " + TABLE_COMMENT + " GROUP BY " + COMMENT_FILE_ID + ") as UC " +
                "ON M.id = UC.fileId", null);

        if (mediaCursor != null && mediaCursor.moveToFirst())
        {
            do
            {
                Media singleMedia = new Media();
                singleMedia.id = mediaCursor.getInt(mediaCursor.getColumnIndex(MEDIA_ID));
                singleMedia.name = mediaCursor.getString(mediaCursor.getColumnIndex(MEDIA_NAME));
                singleMedia.type = mediaCursor.getString(mediaCursor.getColumnIndex(MEDIA_TYPE));
                singleMedia.size = mediaCursor.getInt(mediaCursor.getColumnIndex(MEDIA_SIZE));
                singleMedia.currentVersionId = mediaCursor.getInt(mediaCursor.getColumnIndex(MEDIA_CURRENT_VERSION_ID));
                singleMedia.attachable = mediaCursor.getString(mediaCursor.getColumnIndex(MEDIA_ATTACHABLE));
                singleMedia.description = mediaCursor.getString(mediaCursor.getColumnIndex(MEDIA_DESCRIPTION));
                singleMedia.location = mediaCursor.getString(mediaCursor.getColumnIndex(MEDIA_LOCATION));
                singleMedia.parentId = mediaCursor.getInt(mediaCursor.getColumnIndex(MEDIA_PARENT_ID));
                singleMedia.hasContent = Boolean.parseBoolean(mediaCursor.getString(mediaCursor.getColumnIndex(MEDIA_HAS_CONTENT)));
                singleMedia.isDownloaded = mediaCursor.getInt(mediaCursor.getColumnIndex(MEDIA_IS_DOWNLOADED));
                singleMedia.isDownloading = mediaCursor.getInt(mediaCursor.getColumnIndex(MEDIA_IS_DOWNLOADING));
                singleMedia.likeCount = mediaCursor.getInt(mediaCursor.getColumnIndex("Likes"));
                singleMedia.commentCount = mediaCursor.getInt(mediaCursor.getColumnIndex("Comments"));
                //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.US);
                //SimpleDateFormat dateFormatGerman = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.GERMANY);
                //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                String tempDate = mediaCursor.getString(mediaCursor.getColumnIndex((MEDIA_INSERT_DATE)));
                if (tempDate != null && !tempDate.isEmpty())
                {
                    try
                    {
                        singleMedia.insertDate = Helper.getDateFormate().parse(tempDate);
                    }
                    catch (ParseException px)
                    {

                        singleMedia.insertDate = Helper.getDateFormate().parse(tempDate);
                        //singleMedia.insertDate = dateFormat.parse(Helper.parseDate(dateFormat.toPattern(), new Date(tempDate)));
                    }
                }
                tempDate = mediaCursor.getString(mediaCursor.getColumnIndex(MEDIA_UPDATE_DATE));
                if (tempDate != null && !tempDate.isEmpty())
                {
                    try
                    {
                        singleMedia.updateDate = Helper.getDateFormate().parse(tempDate);
                    }
                    catch (ParseException px)
                    {
                        singleMedia.insertDate = Helper.getDateFormate().parse(tempDate);
                        //singleMedia.updateDate = dateFormat.parse(Helper.parseDate(dateFormat.toPattern(), new Date(tempDate)));
                    }
                }
                tempDate = mediaCursor.getString(mediaCursor.getColumnIndex(MEDIA_DELETE_DATE));
                if (tempDate != null && !tempDate.isEmpty())
                {
                    try
                    {
                        singleMedia.deleteDate = Helper.getDateFormate().parse(tempDate);
                    }
                    catch (ParseException px)
                    {
                        singleMedia.insertDate = Helper.getDateFormate().parse(tempDate);
                        //singleMedia.deleteDate = dateFormat.parse(Helper.parseDate(dateFormat.toPattern(), new Date(tempDate)));
                    }
                }
                singleMedia.icon = mediaCursor.getString(mediaCursor.getColumnIndex(MEDIA_ICON));
                media.add(singleMedia);
            }
            while (mediaCursor.moveToNext());
        }
        if (mediaCursor != null)
            mediaCursor.close();
    }

    public static void getMediaListFromChannelId(Context context, int parentId, ArrayList<Media> mediaList) throws ParseException
    {
        Cursor medialistcursor = null;
        try
        {
            SQLiteDatabase db = LocalDatabase.getReadable(context);
            medialistcursor = db.rawQuery("SELECT M.*, CASE WHEN LikeCount IS NULL THEN 0 ELSE LikeCount END as Likes, CASE WHEN CommentCount IS NULL THEN 0 ELSE CommentCount END as Comments\n" +
                    "FROM (SELECT * FROM " + TABLE_MEDIA + " as MI WHERE MI.parentId = ? ) as M\n" +
                    "LEFT OUTER JOIN (SELECT Count(*) as LikeCount, associatedId FROM 'Likes' GROUP BY associatedId) as UL\n" +
                    "ON M.id = UL.associatedId\n" +
                    "LEFT OUTER JOIN (SELECT Count(*) as CommentCount, fileId FROM 'Comments' GROUP BY fileId) as UC\n" +
                    "ON M.id = UC.fileId", new String[]{String.valueOf(parentId)});

            {
                if (medialistcursor != null && medialistcursor.moveToFirst())
                {
                    mediaList.clear();
                    medialistcursor.moveToFirst();
                    do
                    {
                        Media media = new Media();
                        media.id = medialistcursor.getInt(medialistcursor.getColumnIndex(MEDIA_ID));
                        media.name = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_NAME));
                        media.type = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_TYPE));
                        media.size = medialistcursor.getInt(medialistcursor.getColumnIndex(MEDIA_SIZE));
                        media.currentVersionId = medialistcursor.getInt(medialistcursor.getColumnIndex(MEDIA_CURRENT_VERSION_ID));
                        media.attachable = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_ATTACHABLE));
                        media.description = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_DESCRIPTION));
                        media.location = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_LOCATION));
                        media.parentId = medialistcursor.getInt(medialistcursor.getColumnIndex(MEDIA_PARENT_ID));
                        media.hasContent = Boolean.parseBoolean(medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_HAS_CONTENT)));
                        media.isDownloaded = medialistcursor.getInt(medialistcursor.getColumnIndex(MEDIA_IS_DOWNLOADED));
                        media.isDownloading = medialistcursor.getInt(medialistcursor.getColumnIndex(MEDIA_IS_DOWNLOADING));
                        media.likeCount = medialistcursor.getInt(medialistcursor.getColumnIndex("Likes"));
                        media.commentCount = medialistcursor.getInt(medialistcursor.getColumnIndex("Comments"));
                        media.icon = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_ICON));
                        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        String tempDate = medialistcursor.getString(medialistcursor.getColumnIndex((MEDIA_INSERT_DATE)));
                        if (tempDate != null && !tempDate.isEmpty())
                        {
                            try
                            {
                                media.insertDate = Helper.getDateFormate().parse(tempDate);
                            }
                            catch (ParseException px)
                            {
                                media.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                            }
                        }
                        tempDate = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_UPDATE_DATE));
                        if (tempDate != null && !tempDate.isEmpty())
                        {
                            try
                            {
                                media.updateDate = Helper.getDateFormate().parse(tempDate);
                            }
                            catch (ParseException px)
                            {
                                media.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                            }
                        }
                        tempDate = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_DELETE_DATE));
                        if (tempDate != null && !tempDate.isEmpty())
                        {
                            try
                            {
                                media.deleteDate = Helper.getDateFormate().parse(tempDate);
                            }
                            catch (ParseException px)
                            {
                                media.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                            }
                        }
                        mediaList.add(media);
                    }
                    while ((medialistcursor.moveToNext()));
                }
               /* if (medialistcursor != null)
                    medialistcursor.close();*/
            }
        }
        catch (Exception e)
        {
            Log.e(" ######## ", " exception while fetching : " + e.toString());
        }
        finally
        {
            if (medialistcursor != null)
                medialistcursor.close();
        }


    }

    public static final int getChannelIdFromMediaID(Context context, int id)
    {
        int parentId = -1;
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        Cursor cur = db.rawQuery("SELECT " + CHANNEL_FILE_CHANNEL_ID + "  FROM " + TABLE_CHANNEL_FILE + " where " + MEDIA_VERSION_FILE_ID + "= ?", new String[]{String.valueOf(id)});

        if (cur != null && cur.moveToFirst())
        {
            parentId = cur.getInt(cur.getColumnIndex(CHANNEL_FILE_CHANNEL_ID));
        }
        if (cur != null)
            cur.close();
        return parentId;
    }

    public static ArrayList<Media> getDownloadMedia(Context context, ArrayList<String> id) throws ParseException
    {

        ArrayList<Media> medialist = new ArrayList<>();
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor medialistcursor = null;
        for (int i = 0; i < id.size(); i++)
        {
            medialistcursor = db.rawQuery("SELECT M. *, CASE WHEN LikeCount IS NULL THEN 0 ELSE LikeCount END as Likes, CASE WHEN CommentCount IS NULL THEN 0 ELSE CommentCount END as Comments\n" +
                    "FROM (SELECT * FROM 'Media' as MI WHERE MI.id=  " + id.get(i) + ")as M\n" +
                    "LEFT OUTER JOIN (SELECT Count(*) as LikeCount, associatedId FROM 'Likes' GROUP BY associatedId) as UL\n" +
                    "ON M.id = UL.associatedId\n" +
                    "LEFT OUTER JOIN (SELECT Count(*) as CommentCount, fileId FROM 'Comments' GROUP BY fileId) as UC\n" +
                    "ON M.id = UC.fileId", null);
            {
                if (medialistcursor.moveToFirst() && medialistcursor != null)
                {
                    medialistcursor.moveToFirst();
                    Media media = new Media();
                    media.id = medialistcursor.getInt(medialistcursor.getColumnIndex(MEDIA_ID));
                    media.name = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_NAME));
                    media.type = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_TYPE));
                    media.size = medialistcursor.getInt(medialistcursor.getColumnIndex(MEDIA_SIZE));
                    media.currentVersionId = medialistcursor.getInt(medialistcursor.getColumnIndex(MEDIA_CURRENT_VERSION_ID));
                    media.attachable = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_ATTACHABLE));
                    media.description = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_DESCRIPTION));
                    media.location = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_LOCATION));
                    media.parentId = medialistcursor.getInt(medialistcursor.getColumnIndex(MEDIA_PARENT_ID));
                    media.hasContent = Boolean.parseBoolean(medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_HAS_CONTENT)));
                    media.isDownloaded = medialistcursor.getInt(medialistcursor.getColumnIndex(MEDIA_IS_DOWNLOADED));
                    media.isDownloading = medialistcursor.getInt(medialistcursor.getColumnIndex(MEDIA_IS_DOWNLOADING));
                    media.likeCount = medialistcursor.getInt(medialistcursor.getColumnIndex("Likes"));
                    media.commentCount = medialistcursor.getInt(medialistcursor.getColumnIndex("Comments"));
                    //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.US);
                    //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String tempDate = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_INSERT_DATE));
                    if (tempDate != null && !tempDate.isEmpty())
                    {
                        try
                        {
                            media.insertDate = Helper.getDateFormate().parse(tempDate);
                        }
                        catch (ParseException px)
                        {
                            media.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                        }
                    }
                    tempDate = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_UPDATE_DATE));
                    if (tempDate != null && !tempDate.isEmpty())
                    {
                        try
                        {
                            media.updateDate = Helper.getDateFormate().parse(tempDate);
                        }
                        catch (ParseException px)
                        {
                            media.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                        }
                    }
                    tempDate = medialistcursor.getString(medialistcursor.getColumnIndex(MEDIA_DELETE_DATE));
                    if (tempDate != null && !tempDate.isEmpty())
                    {
                        try
                        {
                            media.deleteDate = Helper.getDateFormate().parse(tempDate);
                        }
                        catch (ParseException px)
                        {
                            media.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                        }
                    }
                }
            }
        }
        if (medialistcursor != null)
            medialistcursor.close();
        return medialist;
    }

    public static boolean checkLike(Context context, int associatedId, int userId)
    {
        boolean flag = false;
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor c;
        c = db.rawQuery("select * from " + TABLE_LIKE + " where " + LIKE_USER_ID + " = ? AND " + LIKE_ASSOCIATED_ID + " = ?", new String[]{String.valueOf(userId), String.valueOf(associatedId)});
        if (c != null && c.moveToFirst())
        {
            flag = true;
        }
        else
        {
            flag = false;
        }
        if (c != null)
            c.close();
        return flag;
    }


    public static int getChannelId(Context context, int mediaId)
    {
        int channelId = 0;
        int parentId = 0;
        Cursor c;
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        c = db.rawQuery("select * from " + TABLE_MEDIA + " where " + MEDIA_ID + " = ?", new String[]{String.valueOf(mediaId)});
        if (c != null && c.moveToFirst())
        {
            c.moveToFirst();
            parentId = c.getInt(c.getColumnIndex(MEDIA_PARENT_ID));
            mediaId = c.getInt(c.getColumnIndex(MEDIA_ID));
            if (parentId == -1)
            {
                c.close();
                c = db.rawQuery("select * from  " + TABLE_CHANNEL_FILE + " where " + CHANNEL_FILE_FILE_ID + " = ?", new String[]{String.valueOf(mediaId)});
                if (c != null && c.moveToFirst())
                {
                    c.moveToFirst();
                    channelId = c.getInt(c.getColumnIndex(CHANNEL_FILE_CHANNEL_ID));
                }
            }
            else
            {
                channelId = getChannelId(context, parentId);
            }
        }
        if (c != null)
            c.close();
        return channelId;
    }

    public static void getUserIdFromLike(Context context, ArrayList<UserLike> userLikes, int associatedId)
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        String whereClause = LIKE_ASSOCIATED_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(associatedId)};
        Cursor userLikeCursor = db.query(TABLE_LIKE, null, whereClause, whereArgs, null, null, null);
        if (userLikeCursor != null && userLikeCursor.moveToFirst())
        {
            do
            {
                try
                {
                    int id = userLikeCursor.getInt(userLikeCursor.getColumnIndex(LIKE_USER_ID));
                    User user = new User();
                    user.id = id;
                    getUser(context, user);

                    UserLike userLike = new UserLike();
                    userLike.userId = id;
                    userLike.user = user;
                    userLike.insertDate = Helper.getDateFormate().parse(userLikeCursor.getString(userLikeCursor.getColumnIndex(LIKE_INSERT_DATE)));
                    userLikes.add(userLike);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            while (userLikeCursor.moveToNext());
        }
        else
            userLikes = null;
        if (userLikeCursor != null)
            userLikeCursor.close();
    }

    public static int getMediaLikeCount(Context context, int associatedId)
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        String whereClause = LIKE_ASSOCIATED_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(associatedId)};
        Cursor userLikeCursor = db.query(TABLE_LIKE, null, whereClause, whereArgs, null, null, null);
        if (userLikeCursor != null)
        {
            int count = userLikeCursor.getCount();
            userLikeCursor.close();
            return count;
        }
        return 0;
    }

    public static String getRecentTableQuery()
    {
        return "CREATE TABLE IF NOT EXISTS  " + TABLE_RECENT + "("
                + RECENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + RECENT_VISIT + " INTEGER ,  "
                + RECENT_VISIT_TIMESTAMP + " TEXT , "
                + RECENT_USER_ID + " INTEGER , "
                + RECENT_INSERT_DATE + " TEXT , "
                + RECENT_UPDATE_DATE + " TEXT , "
                + RECENT_DELETE_DATE + " TEXT "
                + ")";
    }

    public static void insertRecentItem(Context context, int fileId, int userId)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        //c = db.rawQuery("select * from " + TABLE_LIKE + " where " + LIKE_USER_ID + " = ? AND " + LIKE_ASSOCIATED_ID + " = ?", new String[]{String.valueOf(associatedId), String.valueOf(userId)});
        Cursor checkRecentItem = db.rawQuery("SELECT " + RECENT_ID + " FROM  " + TABLE_RECENT + " WHERE " + RECENT_ID + " = ? AND " + RECENT_USER_ID + "= ?", new String[]{String.valueOf(fileId), String.valueOf(userId)});
        if (checkRecentItem != null)
        {
            if (checkRecentItem.getCount() == 0)
            {
                ContentValues recentItemValues = new ContentValues();
                recentItemValues.put(RECENT_ID, fileId);
                recentItemValues.put(RECENT_VISIT, 1);
                //recentItemValues.put(RECENT_VISIT_TIMESTAMP, Helper.parseDate(dateFormat.toPattern(), new Date()));
                recentItemValues.put(RECENT_VISIT_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
                recentItemValues.put(RECENT_USER_ID, userId);
                db.replace(TABLE_RECENT, null, recentItemValues);
            }
            else
            {
                Cursor totalVisitCursor = db.rawQuery("SELECT " + RECENT_VISIT + " FROM " + TABLE_RECENT + " WHERE " + RECENT_ID + " = ? AND " + RECENT_USER_ID + " = ?", new String[]{String.valueOf(fileId), String.valueOf(userId)});
                if (totalVisitCursor != null && totalVisitCursor.moveToFirst())
                {
                    int totalVisit = totalVisitCursor.getInt(totalVisitCursor.getColumnIndex(RECENT_VISIT));

                    ContentValues recentItemValues = new ContentValues();
                    recentItemValues.put(RECENT_VISIT, totalVisit + 1);
                    //recentItemValues.put(RECENT_VISIT_TIMESTAMP, Helper.parseDate(dateFormat.toPattern(), new Date()));
                    recentItemValues.put(RECENT_VISIT_TIMESTAMP, String.valueOf(System.currentTimeMillis()));

                    db.update(TABLE_RECENT, recentItemValues, RECENT_ID + "=? AND " + RECENT_USER_ID + "=?", new String[]{String.valueOf(fileId), String.valueOf(userId)});

                }
                if (totalVisitCursor != null)
                    totalVisitCursor.close();
            }
            checkRecentItem.close();
        }
    }

    public static void getRecentVisitedItems(Context context, ArrayList<RecentItem> recentItems) throws ParseException
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor recentItemCursor = db.rawQuery("SELECT * FROM " + TABLE_RECENT + " ORDER BY " + RECENT_VISIT_TIMESTAMP + " DESC", null);
        if (recentItemCursor != null && recentItemCursor.moveToFirst())
        {
            do
            {
                RecentItem recentItem = new RecentItem();
                RecentItem.parseFromCursor(recentItemCursor, recentItem);
                recentItems.add(recentItem);
            }
            while (recentItemCursor.moveToNext());
        }
        else
            recentItems = null;
        if (recentItemCursor != null)
            recentItemCursor.close();
    }

    public static void getMostVisitedItems(Context context, ArrayList<RecentItem> recentItems) throws ParseException
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor recentItemCursor = db.rawQuery("SELECT * FROM " + TABLE_RECENT + " ORDER BY " + RECENT_VISIT + " DESC", null);
        if (recentItemCursor != null && recentItemCursor.moveToFirst())
        {
            do
            {
                RecentItem recentItem = new RecentItem();
                RecentItem.parseFromCursor(recentItemCursor, recentItem);
                recentItems.add(recentItem);
            }
            while (recentItemCursor.moveToNext());
        }
        else
            recentItems = null;
        if (recentItemCursor != null)
            recentItemCursor.close();
    }

    public static int getNumberOfPopularity(Context context, int fileId, int userId)
    {
        int popularity = 0;
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor popularityCursor = db.rawQuery("SELECT " + RECENT_VISIT + " FROM " + TABLE_RECENT + " WHERE " + RECENT_ID + " = ? AND " + RECENT_USER_ID + " = ?", new String[]{String.valueOf(fileId), String.valueOf(userId)});
        if (popularityCursor != null && popularityCursor.moveToFirst())
        {
            popularity = popularityCursor.getInt(popularityCursor.getColumnIndex(RECENT_VISIT));
        }
        if (popularityCursor != null)
            popularityCursor.close();
        return popularity;
    }

    public static void fillLocalizationTable(Context context, ArrayList<LocalizationData> arrayList)
    {
        try
        {
            SQLiteDatabase db = CommonDatabase.getWritable(context);
            ContentValues contentValues = new ContentValues();

            for (int i = 0; i < arrayList.size(); i++)
            {
                contentValues.put(LOCALIZATION_CODE, arrayList.get(i).getCode());
                contentValues.put(LOCALIZATION_TYPE, arrayList.get(i).getType());
                contentValues.put(LOCALIZATION_DELETED_DATE, arrayList.get(i).getDeleteDate());
                contentValues.put(LOCALIZATION_INSERTED_DATE, arrayList.get(i).getInsertDate());
                contentValues.put(LOCALIZATION_UPDATED_DATE, arrayList.get(i).getUpdateDate());
                contentValues.put(LOCALIZATION_MESSAGE, arrayList.get(i).getMessage());
                contentValues.put(LOCALIZATION_LANGUAGE, arrayList.get(i).getLanguage());
                contentValues.put(LOCALIZATION_IS_DELETED, arrayList.get(i).getIsDeleted());
                contentValues.put(LOCALIZATION_ID, arrayList.get(i).getLocalizationId());

                long addLocalizationCount = db.replace(TABLE_LOCALIZATION, null, contentValues);
                //Log.e(" add value ", " add localization count : " + addLocalizationCount);

            }
        }
        catch (Exception e)
        {
            Log.e(" exception  ", " exception while adding : " + e.toString());
        }
    }

    public static String getLocalizationMessageFromCode(Context context, String code, String type)
    {
        Cursor cursor = null;
        try
        {
            String message = "";
            String lang = Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getLanguage();
            SQLiteDatabase db = CommonDatabase.getReadable(context);
            cursor = db.rawQuery("SELECT * FROM " + TABLE_LOCALIZATION + " WHERE " + LOCALIZATION_CODE + " = ? AND " + LOCALIZATION_LANGUAGE + " = ? AND " + LOCALIZATION_TYPE + " = ? ", new String[]{String.valueOf(code), String.valueOf(lang), String.valueOf(type)});
            if (cursor != null && cursor.moveToFirst())
            {
                message = cursor.getString(cursor.getColumnIndex(LOCALIZATION_MESSAGE));
            }
            if (cursor != null)
            {
                cursor.close();
            }
            return message;
        }
        catch (Exception e)
        {
            Log.e("", " exception while fetching : " + e.toString());
            if (cursor != null)
                cursor.close();
            return "";
        }

    }

    public static ArrayList<String> getLocalizationCountry(Context context, String lang, String type)
    {
        ArrayList<String> countryList = new ArrayList<>();
        Cursor cursor = null;
        try
        {
            SQLiteDatabase db = CommonDatabase.getReadable(context);
            cursor = db.rawQuery("SELECT " + LOCALIZATION_MESSAGE + " FROM " + TABLE_LOCALIZATION + " WHERE " + LOCALIZATION_LANGUAGE + " LIKE(?) AND " + LOCALIZATION_TYPE + " = ? ", new String[]{String.valueOf(lang), String.valueOf(type)});
            if (cursor != null && cursor.moveToFirst())
            {
                do
                {
                    countryList.add(cursor.getString(cursor.getColumnIndex(LOCALIZATION_MESSAGE)));
                }
                while (cursor.moveToNext());
            }
        }
        catch (Exception e)
        {
            Log.e("", " exception while fetching : " + e.toString());

        }
        if (cursor != null)
            cursor.close();
        return countryList;
    }

    public static int getCursorCountforLocalization(Context context)
    {
        Cursor cursor = null;
        int count = 0;
        String selectQuery = "SELECT  * FROM " + TABLE_LOCALIZATION;
        SQLiteDatabase db = CommonDatabase.getReadable(context);
        try
        {
            cursor = db.rawQuery(selectQuery, null);
            count = cursor.getCount();
        }
        catch (Exception e)
        {
            Log.e(" exception ", " getting cursor count  : " + e.toString());
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
        return count;
    }

    public static ContentValues addLocalizationRow(int code, String message, String language, String messageType)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCALIZATION_CODE, code);
        contentValues.put(LOCALIZATION_MESSAGE, message);
        contentValues.put(LOCALIZATION_LANGUAGE, language);
        contentValues.put(LOCALIZATION_TYPE, messageType);
        return contentValues;

    }

    public static String getAnnouncementTableQuery()
    {
        return "CREATE TABLE IF NOT EXISTS  " + TABLE_ANNOUNCEMENT + "("
                + ANNOUNCEMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + ANNOUNCEMENT_TYPE + " TEXT ,  "
                + ANNOUNCEMENT_USER_ID + " TEXT , "
                + ANNOUNCEMENT_ASSOCIATED_ID + " INTEGER , "
                + ANNOUNCEMENT_VALUE + " TEXT , "
                + ANNOUNCEMENT_IS_READ + " INTEGER , "
                + ANNOUNCEMENT_INSERTEDATE + " TEXT , "
                + ANNOUNCEMENT_UPDATEDATE + " TEXT , "
                + ANNOUNCEMENT_DELETEDATE + " TEXT "
                + ")";
    }

    public static boolean addAnnouncement(Context context, Announcement announcement)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues announcementValues = new ContentValues();
        announcementValues.put(ANNOUNCEMENT_TYPE, announcement.type);
        announcementValues.put(ANNOUNCEMENT_USER_ID, announcement.userId);
        announcementValues.put(ANNOUNCEMENT_ASSOCIATED_ID, announcement.associatedId);
        announcementValues.put(ANNOUNCEMENT_VALUE, announcement.value);
        announcementValues.put(ANNOUNCEMENT_IS_READ, announcement.isRead ? 1 : 0);
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (announcement.insertDate != null)
            announcementValues.put(ANNOUNCEMENT_INSERTEDATE, Helper.getDateFormate().format(announcement.insertDate));
        if (announcement.updateDate != null)
            announcementValues.put(ANNOUNCEMENT_UPDATEDATE, Helper.getDateFormate().format(announcement.updateDate));
        if (announcement.deleteDate != null)
            announcementValues.put(ANNOUNCEMENT_DELETEDATE, Helper.getDateFormate().format(announcement.deleteDate));


        if (!announcement.type.equalsIgnoreCase(FCMMessagingService.PUSH_TYPE_SYNC))
        {
            return db.replace(TABLE_ANNOUNCEMENT, null, announcementValues) > 0;
        }
        else
        {
            int id = getSyncAnnouncementId(context);
            if (id == -1)
            {
                return db.replace(TABLE_ANNOUNCEMENT, null, announcementValues) > 0;
            }
            else
            {
                announcement.id = id;
                return updateAnnouncement(context, announcement);
            }
        }

    }

    public static void getAnnouncementData(Context context, ArrayList<Announcement> announcements) throws ParseException
    {
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor announcementCursor = db.rawQuery("SELECT * FROM " + TABLE_ANNOUNCEMENT + " WHERE " + ANNOUNCEMENT_IS_READ + " = ? ORDER BY " + ANNOUNCEMENT_INSERTEDATE + " DESC", new String[]{"0"});
        if (announcementCursor != null)
        {
            if (announcementCursor.moveToFirst())
            {
                do
                {
                    Announcement announcement = new Announcement();
                    Announcement.parseFromCursor(announcementCursor, announcement);
                    announcements.add(announcement);
                }
                while (announcementCursor.moveToNext());
            }
            announcementCursor.close();
        }
    }

    public static int getAnnouncementCount(Context context)
    {
        int cnt = 0;
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor announcementCursor = db.rawQuery("SELECT * FROM " + TABLE_ANNOUNCEMENT + " WHERE " + ANNOUNCEMENT_IS_READ + " = ? ORDER BY " + ANNOUNCEMENT_INSERTEDATE + " DESC", new String[]{"0"});
        if (announcementCursor != null)
        {
            cnt = announcementCursor.getCount();
            announcementCursor.close();
        }
        return cnt;
    }


    public static int getSyncAnnouncementId(Context context)
    {
        int id = -1;
        SQLiteDatabase db = LocalDatabase.getReadable(context);
        Cursor announcementCursor = db.rawQuery("SELECT " + ANNOUNCEMENT_ID + " FROM " + TABLE_ANNOUNCEMENT + " WHERE " + ANNOUNCEMENT_TYPE + " = ?", new String[]{FCMMessagingService.PUSH_TYPE_SYNC});
        if (announcementCursor != null && announcementCursor.moveToFirst())
        {
            id = announcementCursor.getInt(announcementCursor.getColumnIndex(DataHelper.ANNOUNCEMENT_ID));
        }
        if (announcementCursor != null)
            announcementCursor.close();
        return id;
    }

    public static boolean updateAnnouncement(Context context, Announcement announcement)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);

        ContentValues announcementValues = new ContentValues();
        announcementValues.put(ANNOUNCEMENT_TYPE, announcement.type);
        announcementValues.put(ANNOUNCEMENT_USER_ID, announcement.userId);
        announcementValues.put(ANNOUNCEMENT_ASSOCIATED_ID, announcement.associatedId);
        announcementValues.put(ANNOUNCEMENT_VALUE, announcement.value);
        announcementValues.put(ANNOUNCEMENT_IS_READ, announcement.isRead ? 1 : 0);
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        if (announcement.insertDate != null)
            announcementValues.put(ANNOUNCEMENT_INSERTEDATE, Helper.getDateFormate().format(announcement.insertDate));
        if (announcement.updateDate != null)
            announcementValues.put(ANNOUNCEMENT_UPDATEDATE, Helper.getDateFormate().format(announcement.updateDate));
        if (announcement.deleteDate != null)
            announcementValues.put(ANNOUNCEMENT_DELETEDATE, Helper.getDateFormate().format(announcement.deleteDate));

        String whereClause = ANNOUNCEMENT_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(announcement.id)};
        long updateAnnouncementCount = db.update(TABLE_ANNOUNCEMENT, announcementValues, whereClause, whereArgs);
        return (updateAnnouncementCount > 0);
    }

    public static boolean updateSyncAnnouncementReadStatus(Context context, boolean isRead)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        ContentValues announcementValues = new ContentValues();

        announcementValues.put(ANNOUNCEMENT_IS_READ, isRead ? 1 : 0);
        String whereClause = ANNOUNCEMENT_TYPE + " = ?";
        String[] whereArgs = new String[]{FCMMessagingService.PUSH_TYPE_SYNC};
        long updateAnnouncementCount = db.update(TABLE_ANNOUNCEMENT, announcementValues, whereClause, whereArgs);
        return (updateAnnouncementCount > 0);
    }

    public static boolean removeAnnouncement(Context context, Announcement announcement)
    {
        SQLiteDatabase db = LocalDatabase.getWritable(context);
        String whereClause = ANNOUNCEMENT_ID + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(announcement.id)};
        long removeUserCommentsCount = db.delete(TABLE_ANNOUNCEMENT, whereClause, whereArgs);
        return (removeUserCommentsCount > 0);
    }

}