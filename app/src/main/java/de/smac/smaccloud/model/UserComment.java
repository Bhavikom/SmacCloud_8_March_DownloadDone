package de.smac.smaccloud.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;

import static de.smac.smaccloud.R.string.date;

/**
 * POJO(Plain Old Java Object) class for User Comment
 */
public class UserComment implements Parcelable
{
    public static final Creator<UserComment> CREATOR = new Creator<UserComment>()
    {
        public UserComment createFromParcel(Parcel source)
        {
            return new UserComment(source);
        }

        public UserComment[] newArray(int size)
        {
            return new UserComment[size];
        }
    };
    public int id;
    public String comment;
    public int fileId;
    public int userId;
    public Date insertDate;
    public Date updateDate;
    public Date deleteDate;
    public int syncType;
    public int isSynced;
    public Media file;
    public User user;

    public UserComment()
    {
    }

    protected UserComment(Parcel in)
    {
        this.comment = in.readString();
        this.id = in.readInt();
        this.fileId = in.readInt();
        this.userId = in.readInt();
        long tmpInsertDate = in.readLong();
        this.insertDate = tmpInsertDate == -1 ? null : new Date(tmpInsertDate);
        long tmpUpdateDate = in.readLong();
        this.updateDate = tmpUpdateDate == -1 ? null : new Date(tmpUpdateDate);
        long tmpDeleteDate = in.readLong();
        this.deleteDate = tmpDeleteDate == -1 ? null : new Date(tmpDeleteDate);
        this.syncType = in.readInt();
        this.isSynced = in.readInt();
        this.file = in.readParcelable(Media.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static void parseListFromJson(JSONArray payloadJson, ArrayList<UserComment> userComments) throws JSONException, ParseException
    {

        for (int i = 0; i < payloadJson.length(); i++)
        {
            UserComment userComment = new UserComment();
            JSONObject userCommentJson = payloadJson.getJSONObject(i);
            parseFromJSon(userCommentJson, userComment);
            userComments.add(userComment);
        }
    }

    public static void parseFromJSon(JSONObject userCommentJson, UserComment userComment) throws JSONException, ParseException
    {
        userComment.comment = userCommentJson.optString("Comment");
        userComment.id = userCommentJson.optInt("Id");
        userComment.userId = userCommentJson.optInt("UserId");
        userComment.fileId = userCommentJson.optInt("FileId");
        userComment.isSynced = 0;
        if (userCommentJson.has("User") && !userCommentJson.isNull("User"))
        {
            User user = new User();
            User.parseFromJson(userCommentJson.optJSONObject("User"), user);
        }

        /* date format to parse in UTC format*/
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String tempDate = userCommentJson.optString("InsertDate"); /// getting time from server
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                /* converting tempDate to insertDate with UTC time format */
                userComment.insertDate = Helper.getDateFormate().parse(tempDate); // it will store in database
                Log.e(" while comment "," inserted date : "+userComment.insertDate);
            }
            catch (ParseException px)
            {
                userComment.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = userCommentJson.optString("UpdateDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                userComment.updateDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                userComment.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = userCommentJson.optString("DeleteDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                userComment.deleteDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                userComment.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        userComment.syncType = userCommentJson.optInt("SyncType");
    }

    public static void parseFromCursor(Cursor cursor, UserComment userComment) throws ParseException
    {
        if (cursor != null)
        {
            userComment.comment = cursor.getString(cursor.getColumnIndex(DataHelper.COMMENT));
            userComment.id = cursor.getInt(cursor.getColumnIndex(DataHelper.COMMENT_ID));
            userComment.userId = cursor.getInt(cursor.getColumnIndex(DataHelper.COMMENT_USER_ID));
            userComment.isSynced = cursor.getInt(cursor.getColumnIndex(DataHelper.COMMENT_IS_SYNCED));
            userComment.fileId = cursor.getInt(cursor.getColumnIndex(DataHelper.COMMENT_FILE_ID));
            //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.getDefault());
            //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.COMMENT_INSERT_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    userComment.insertDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    userComment.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.COMMENT_UPDATE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    userComment.updateDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    userComment.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.COMMENT_DELETE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    userComment.deleteDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    userComment.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }

        }
        else
            userComment.id = -1;
    }

    public boolean add(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.addUserComments(context, this);
    }

    public boolean addOfflineComments(Context context)
    {
        return DataHelper.addUserComments(context, this);
    }

    public boolean saveChanges(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.updateUserComments(context, this);
    }

    public boolean remove(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.removeUserComments(context, this);
    }

    public boolean removeOffline(Context context)
    {
        return DataHelper.removeUserOfflineComments(context, this);
    }

    public JSONObject toJson() throws JSONException
    {
        JSONObject userComment = new JSONObject();
        userComment.put("UserId", this.userId);
        userComment.put("MediaId", this.fileId);
        userComment.put("Comment", this.comment);
        return userComment;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.comment);
        dest.writeInt(this.id);
        dest.writeInt(this.fileId);
        dest.writeInt(this.userId);
        dest.writeLong(insertDate != null ? insertDate.getTime() : -1);
        dest.writeLong(updateDate != null ? updateDate.getTime() : -1);
        dest.writeLong(deleteDate != null ? deleteDate.getTime() : -1);
        dest.writeInt(this.syncType);
        dest.writeInt(this.isSynced);
        dest.writeParcelable(this.file, 0);
        dest.writeParcelable(this.user, 0);
    }
}
