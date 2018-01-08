package de.smac.smaccloud.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.util.Date;

import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;

/**
 * Created by S Soft on 29-Dec-17.
 */

public class Announcement implements Parcelable
{
    public static final Creator<Announcement> CREATOR = new Creator<Announcement>()
    {
        public Announcement createFromParcel(Parcel source)
        {
            return new Announcement(source);
        }

        public Announcement[] newArray(int size)
        {
            return new Announcement[size];
        }
    };
    public int id;
    public String type;
    public int userId;
    public int associatedId;
    public String value;
    public boolean isRead;
    public Date insertDate;
    public Date updateDate;
    public Date deleteDate;

    public Announcement()
    {
    }

    protected Announcement(Parcel in)
    {
        this.id = in.readInt();
        this.type = in.readString();
        this.userId = in.readInt();
        this.associatedId = in.readInt();
        this.value = in.readString();
        this.isRead = in.readByte() != 0;
        long tmpInsertDate = in.readLong();
        this.insertDate = tmpInsertDate == -1 ? null : new Date(tmpInsertDate);
        long tmpUpdateDate = in.readLong();
        this.updateDate = tmpUpdateDate == -1 ? null : new Date(tmpUpdateDate);
        long tmpDeleteDate = in.readLong();
        this.deleteDate = tmpDeleteDate == -1 ? null : new Date(tmpDeleteDate);
    }

    public static void parseFromCursor(Cursor cursor, Announcement announcement) throws ParseException
    {
        if (cursor != null)
        {
            announcement.id = cursor.getInt(cursor.getColumnIndex(DataHelper.ANNOUNCEMENT_ID));
            announcement.type = cursor.getString(cursor.getColumnIndex(DataHelper.ANNOUNCEMENT_TYPE));
            announcement.userId = cursor.getInt(cursor.getColumnIndex(DataHelper.ANNOUNCEMENT_USER_ID));
            announcement.associatedId = cursor.getInt(cursor.getColumnIndex(DataHelper.ANNOUNCEMENT_ASSOCIATED_ID));
            announcement.value = cursor.getString(cursor.getColumnIndex(DataHelper.ANNOUNCEMENT_VALUE));
            announcement.isRead = cursor.getInt(cursor.getColumnIndex(DataHelper.ANNOUNCEMENT_IS_READ)) == 1;
            //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.getDefault());
            //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.RECENT_INSERT_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    announcement.insertDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    announcement.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.RECENT_UPDATE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    announcement.updateDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    announcement.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.RECENT_DELETE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    announcement.deleteDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    announcement.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
        }
        else
            announcement.id = -1;
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
        dest.writeString(this.type);
        dest.writeInt(this.userId);
        dest.writeInt(this.associatedId);
        dest.writeString(this.value);
        dest.writeByte(this.isRead ? (byte) 1 : (byte) 0);
        dest.writeLong(insertDate != null ? insertDate.getTime() : -1);
        dest.writeLong(updateDate != null ? updateDate.getTime() : -1);
        dest.writeLong(deleteDate != null ? deleteDate.getTime() : -1);
    }
}
