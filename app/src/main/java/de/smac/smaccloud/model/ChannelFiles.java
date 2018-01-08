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
 * POJO(Plain Old Java Object) class for Channel File
 */
public class ChannelFiles implements Parcelable
{
    public static final Creator<ChannelFiles> CREATOR = new Creator<ChannelFiles>()
    {
        public ChannelFiles createFromParcel(Parcel source)
        {
            return new ChannelFiles(source);
        }

        public ChannelFiles[] newArray(int size)
        {
            return new ChannelFiles[size];
        }
    };
    public int id;
    public int channelId;
    public int fileVersionId;
    public int fileId;
    public int syncType;
    public Date insertDate;
    public Date updateDate;
    public Date deleteDate;
    public int isSynced;

    public ChannelFiles()
    {
    }

    protected ChannelFiles(Parcel in)
    {
        this.id = in.readInt();
        this.channelId = in.readInt();
        this.fileVersionId = in.readInt();
        this.fileId = in.readInt();
        this.syncType = in.readInt();
        long tmpInsertDate = in.readLong();
        this.insertDate = tmpInsertDate == -1 ? null : new Date(tmpInsertDate);
        long tmpUpdateDate = in.readLong();
        this.updateDate = tmpUpdateDate == -1 ? null : new Date(tmpUpdateDate);
        long tmpDeleteDate = in.readLong();
        this.deleteDate = tmpDeleteDate == -1 ? null : new Date(tmpDeleteDate);
        this.isSynced = in.readInt();
    }

    public static void parseListFromJson(JSONArray channelFileJson, ArrayList<ChannelFiles> channelFiles) throws JSONException, ParseException
    {
        for (int i = 0; i < channelFileJson.length(); i++)
        {
            ChannelFiles channelFile = new ChannelFiles();
            JSONObject channelFilesJson = channelFileJson.getJSONObject(i);
            parseFromJson(channelFilesJson, channelFile);
            channelFiles.add(channelFile);
        }

    }

    public static void parseFromJson(JSONObject channelFileJson, ChannelFiles channelFiles) throws JSONException, ParseException
    {
        channelFiles.id = channelFileJson.optInt("Id");
        channelFiles.channelId = channelFileJson.optInt("ChannelId");
        channelFiles.fileId = channelFileJson.optInt("FileId");
        channelFiles.fileVersionId = channelFileJson.optInt("FileVersionId");
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String tempDate = channelFileJson.optString("InsertDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                channelFiles.insertDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                channelFiles.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = channelFileJson.optString("UpdateDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                channelFiles.updateDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                channelFiles.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = channelFileJson.optString("DeleteDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                channelFiles.deleteDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                channelFiles.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        channelFiles.syncType = channelFileJson.optInt("SyncType");
    }

    public static void parseFromCursor(Cursor cursor, ChannelFiles channelFiles) throws ParseException
    {
        if (cursor != null)
        {
            channelFiles.id = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_FILE_ID));
            channelFiles.channelId = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_FILE_CHANNEL_ID));
            channelFiles.fileId = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_FILE_FILE_ID));
            channelFiles.fileVersionId = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_FILE_VERSION_ID));
            //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.getDefault());
            //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.CHANNEL_FILE_INSERT_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    channelFiles.insertDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    channelFiles.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.CHANNEL_FILE_UPDATE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    channelFiles.updateDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    channelFiles.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.CHANNEL_FILE_DELETE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    channelFiles.deleteDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    channelFiles.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            channelFiles.isSynced = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_FILE_IS_SYNCED));
        }
        else
            channelFiles.id = -1;
    }

    public boolean add(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.addChannelFiles(context, this);
    }

    public boolean saveChanges(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.updateChannelFiles(context, this);
    }

    public boolean remove(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.removeChannelFiles(context, this);
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
        dest.writeInt(this.channelId);
        dest.writeInt(this.fileVersionId);
        dest.writeInt(this.fileId);
        dest.writeInt(this.syncType);
        dest.writeLong(insertDate != null ? insertDate.getTime() : -1);
        dest.writeLong(updateDate != null ? updateDate.getTime() : -1);
        dest.writeLong(deleteDate != null ? deleteDate.getTime() : -1);
        dest.writeInt(this.isSynced);
    }
}
