package de.smac.smaccloud.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;

/**
 * POJO(Plain Old Java Object) class for User Like
 */
public class UserLike implements Parcelable
{
    public static final Creator<UserLike> CREATOR = new Creator<UserLike>()
    {
        public UserLike createFromParcel(Parcel source)
        {
            return new UserLike(source);
        }

        public UserLike[] newArray(int size)
        {
            return new UserLike[size];
        }
    };
    public int id;
    public int associatedId;
    public int type;
    public int userId;
    public Date insertDate;
    public Date updateDate;
    public Date deleteDate;
    public int syncType;
    public int isSynced;
    public User user;

    public UserLike()
    {
    }

    protected UserLike(Parcel in)
    {
        this.id = in.readInt();
        this.associatedId = in.readInt();
        this.type = in.readInt();
        this.userId = in.readInt();
        long tmpInsertDate = in.readLong();
        this.insertDate = tmpInsertDate == -1 ? null : new Date(tmpInsertDate);
        long tmpUpdateDate = in.readLong();
        this.updateDate = tmpUpdateDate == -1 ? null : new Date(tmpUpdateDate);
        long tmpDeleteDate = in.readLong();
        this.deleteDate = tmpDeleteDate == -1 ? null : new Date(tmpDeleteDate);
        this.syncType = in.readInt();
        this.isSynced = in.readInt();

    }

    public static void parseListFromJson(JSONArray payloadJson, ArrayList<UserLike> userLikes) throws JSONException, ParseException
    {

        for (int i = 0; i < payloadJson.length(); i++)
        {
            UserLike userLike = new UserLike();
            JSONObject userLikeJson = payloadJson.getJSONObject(i);
            parseFromJson(userLikeJson, userLike);
            userLikes.add(userLike);

        }
    }

    public static void parseFromJson(JSONObject userLikeJson, UserLike userLike) throws JSONException, ParseException
    {
        userLike.id = userLikeJson.optInt("Id");
        userLike.associatedId = userLikeJson.optInt("AssociatedId");
        userLike.type = userLikeJson.optInt("Type");
        userLike.userId = userLikeJson.optInt("UserId");
        userLike.user = new User();
        if (userLikeJson.has("User") && !userLikeJson.isNull("User") && !userLikeJson.optString("User").equalsIgnoreCase("null"))
            User.parseFromJson(userLikeJson.optJSONObject("User"), userLike.user);
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String tempDate = userLikeJson.optString("InsertDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                userLike.insertDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                userLike.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = userLikeJson.optString("UpdateDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                userLike.updateDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                userLike.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = userLikeJson.optString("DeleteDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                userLike.deleteDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                userLike.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        userLike.syncType = userLikeJson.optInt("SyncType");
    }

    public static void parseFromCursor(Cursor cursor, UserLike userLike) throws ParseException
    {
        if (cursor != null)
        {
            userLike.id = cursor.getInt(cursor.getColumnIndex(DataHelper.LIKE_ID));
            userLike.associatedId = cursor.getInt(cursor.getColumnIndex(DataHelper.LIKE_ASSOCIATED_ID));
            userLike.type = cursor.getInt(cursor.getColumnIndex(DataHelper.LIKE_TYPE));
            userLike.userId = cursor.getInt(cursor.getColumnIndex(DataHelper.LIKE_USER_ID));
            userLike.isSynced = cursor.getInt(cursor.getColumnIndex(DataHelper.LIKE_IS_SYNCED));
            //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.LIKE_INSERT_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    userLike.insertDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    userLike.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.LIKE_UPDATE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    userLike.updateDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    userLike.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.LIKE_DELETE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    userLike.deleteDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    userLike.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }

        }
        else
            userLike.id = -1;
    }

    public boolean add(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.addUserLikes(context, this);
    }

    public boolean addOfflineLike(Context context)
    {
        return DataHelper.addUserLikes(context, this);
    }

    public boolean saveChanges(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.updateUserLikes(context, this);
    }

    public boolean remove(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.removeUserLikes(context, this);
    }

    public boolean removeoffline(Context context)
    {

        return DataHelper.removeUserOfflineLikes(context, this);
    }

    public JSONObject toJson() throws JSONException
    {
        JSONObject userLike = new JSONObject();
        userLike.put("UserId", this.userId);
        userLike.put("MediaId", this.associatedId);
        return userLike;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.id);
        dest.writeInt(this.associatedId);
        dest.writeInt(this.type);
        dest.writeInt(this.userId);
        dest.writeLong(insertDate != null ? insertDate.getTime() : -1);
        dest.writeLong(updateDate != null ? updateDate.getTime() : -1);
        dest.writeLong(deleteDate != null ? deleteDate.getTime() : -1);
        dest.writeInt(this.syncType);
        dest.writeInt(this.isSynced);

    }
}
