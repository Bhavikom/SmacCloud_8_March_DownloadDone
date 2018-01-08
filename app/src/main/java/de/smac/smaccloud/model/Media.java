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

/**
 * POJO(Plain Old Java Object) class for Media
 */
public class Media implements Parcelable
{
    public static final Creator<Media> CREATOR = new Creator<Media>()
    {
        public Media createFromParcel(Parcel source)
        {
            return new Media(source);
        }

        public Media[] newArray(int size)
        {
            return new Media[size];
        }
    };
    public int progress;
    public int id;
    public int parentId;
    public int currentVersionId;
    public String name;
    public String description;
    public String location;
    public String type;
    public long size;
    public boolean hasContent;
    public String attachable;
    public int likeCount;
    public int commentCount;
    public Date insertDate;
    public Date updateDate;
    public Date deleteDate;
    public int isDownloaded;
    public int isSynced;
    public int syncType;
    public MediaVersion currentVersion;
    public int channeId;
    public String icon;
    public int isDownloading;

    public Media()
    {
    }

    protected Media(Parcel in)
    {
        this.progress = in.readInt();
        this.id = in.readInt();
        this.parentId = in.readInt();
        this.currentVersionId = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.location = in.readString();
        this.type = in.readString();
        this.size = in.readLong();
        this.hasContent = in.readByte() != 0;
        this.attachable = in.readString();
        this.likeCount = in.readInt();
        this.commentCount = in.readInt();
        long tmpInsertDate = in.readLong();
        this.insertDate = tmpInsertDate == -1 ? null : new Date(tmpInsertDate);
        long tmpUpdateDate = in.readLong();
        this.updateDate = tmpUpdateDate == -1 ? null : new Date(tmpUpdateDate);
        long tmpDeleteDate = in.readLong();
        this.deleteDate = tmpDeleteDate == -1 ? null : new Date(tmpDeleteDate);
        this.isDownloaded = in.readInt();
        this.isSynced = in.readInt();
        this.syncType = in.readInt();
        this.currentVersion = in.readParcelable(MediaVersion.class.getClassLoader());
        this.channeId = in.readInt();
        this.icon = in.readString();
        this.isDownloading = in.readInt();
    }

    public static void parseListFromJson(JSONArray payloadJson, ArrayList<Media> mediaList) throws JSONException, ParseException
    {
        Log.e("Media", "Payload JSON length = " + payloadJson.length());
        for (int i = 0; i < payloadJson.length(); i++)
        {
            Media media = new Media();
            JSONObject mediaJson = payloadJson.getJSONObject(i);
            parseFromJson(mediaJson, media);
            mediaList.add(media);
        }
    }

    public static void parseFromJson(JSONObject mediaJson, Media media1) throws JSONException, ParseException
    {
        media1.attachable = mediaJson.optString("Attachable");
        media1.description = mediaJson.optString("Description");
        media1.id = mediaJson.optInt("Id");
        media1.location = mediaJson.optString("location");
        media1.name = mediaJson.optString("Name");
        media1.parentId = mediaJson.optInt("ParentId");
        media1.size = mediaJson.optInt("Size");
        media1.type = mediaJson.optString("Type");
        media1.isDownloaded = 0;
        media1.isSynced = 1;
        media1.currentVersion = new MediaVersion();
        media1.currentVersionId = mediaJson.optInt("CurrentVersionId");
        media1.syncType = mediaJson.optInt("SyncType");
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
       // dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String tempDate;
        tempDate = mediaJson.optString("InsertDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                media1.insertDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                media1.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = mediaJson.optString("UpdateDate");
        if (tempDate != null && !tempDate.isEmpty() && !(mediaJson.isNull("UpdateDate")))
        {
            try
            {
                media1.updateDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                media1.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = mediaJson.optString("DeleteDate");
        if (tempDate != null && !tempDate.isEmpty() && !(mediaJson.optString("DeleteData").equals("null")))
        {
            try
            {
                media1.deleteDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                media1.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        if (!media1.type.equals("folder"))
        {
            media1.currentVersion = new MediaVersion();
            MediaVersion.parseListFromJson(mediaJson.optJSONObject("CurrentVersion"), media1.currentVersion);
        }
        media1.icon = mediaJson.optString("Icon");
        Log.e("media1insertDate", media1.insertDate.toString());

    }

    public static void parseFromCursor(Cursor cursor, Media media) throws ParseException
    {
        if (cursor != null)
        {
            media.id = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_ID));
            media.name = cursor.getString(cursor.getColumnIndex(DataHelper.MEDIA_NAME));
            media.parentId = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_PARENT_ID));
            media.size = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_SIZE));
            media.type = cursor.getString(cursor.getColumnIndex(DataHelper.MEDIA_TYPE));
            media.currentVersionId = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_CURRENT_VERSION_ID));
            media.description = cursor.getString(cursor.getColumnIndex(DataHelper.MEDIA_DESCRIPTION));
            media.location = cursor.getString(cursor.getColumnIndex(DataHelper.MEDIA_LOCATION));
            media.attachable = cursor.getString(cursor.getColumnIndex(DataHelper.MEDIA_ATTACHABLE));
            media.isSynced = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_IS_SYNCED));
            media.isDownloaded = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_IS_DOWNLOADED));
           // SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.getDefault());
           // dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.MEDIA_INSERT_DATE));
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
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.MEDIA_UPDATE_DATE));
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
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.MEDIA_DELETE_DATE));
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
            media.icon = cursor.getString(cursor.getColumnIndex(DataHelper.MEDIA_ICON));
            media.isDownloading = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_IS_DOWNLOADING));
        }
        else
            media.id = -1;
    }

    public void populateUsingUserId(Context context) throws ParseException
    {
        if (id <= 0)
            throw new IllegalArgumentException("User Id cannot be zero or negative");
        DataHelper.getMedia(context, this);
    }

    public boolean add(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.addMedia(context, this);
    }

    public boolean saveChanges(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.updateMedia(context, this);
    }

    public boolean remove(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.removeMedia(context, this);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.progress);
        dest.writeInt(this.id);
        dest.writeInt(this.parentId);
        dest.writeInt(this.currentVersionId);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.location);
        dest.writeString(this.type);
        dest.writeLong(this.size);
        dest.writeByte(hasContent ? (byte) 1 : (byte) 0);
        dest.writeString(this.attachable);
        dest.writeInt(this.likeCount);
        dest.writeInt(this.commentCount);
        dest.writeLong(insertDate != null ? insertDate.getTime() : -1);
        dest.writeLong(updateDate != null ? updateDate.getTime() : -1);
        dest.writeLong(deleteDate != null ? deleteDate.getTime() : -1);
        dest.writeInt(this.isDownloaded);
        dest.writeInt(this.isSynced);
        dest.writeInt(this.syncType);
        dest.writeParcelable(this.currentVersion, 0);
        dest.writeInt(this.channeId);
        dest.writeString(this.icon);
        dest.writeInt(this.isDownloading);
    }


}
