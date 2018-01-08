package de.smac.smaccloud.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.util.Date;

import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;

public class RecentItem implements Parcelable
{
    public static final Parcelable.Creator<RecentItem> CREATOR = new Parcelable.Creator<RecentItem>()
    {
        public RecentItem createFromParcel(Parcel source)
        {
            return new RecentItem(source);
        }

        public RecentItem[] newArray(int size)
        {
            return new RecentItem[size];
        }
    };

    public int id;
    public int visit;
    public String visitTimestamp;
    public int userId;
    public Date insertDate;
    public Date updateDate;
    public Date deleteDate;

    public RecentItem()
    {
    }

    protected RecentItem(Parcel in)
    {
        this.id = in.readInt();
        this.visit = in.readInt();
        this.visitTimestamp = in.readString();
        this.userId = in.readInt();
        long tmpInsertDate = in.readLong();
        this.insertDate = tmpInsertDate == -1 ? null : new Date(tmpInsertDate);
        long tmpUpdateDate = in.readLong();
        this.updateDate = tmpUpdateDate == -1 ? null : new Date(tmpUpdateDate);
        long tmpDeleteDate = in.readLong();
        this.deleteDate = tmpDeleteDate == -1 ? null : new Date(tmpDeleteDate);
    }

    public static void parseFromCursor(Cursor cursor, RecentItem recentItem) throws ParseException
    {
        if (cursor != null)
        {
            recentItem.id = cursor.getInt(cursor.getColumnIndex(DataHelper.RECENT_ID));
            recentItem.visit = cursor.getInt(cursor.getColumnIndex(DataHelper.RECENT_VISIT));
            recentItem.visitTimestamp = cursor.getString(cursor.getColumnIndex(DataHelper.RECENT_VISIT_TIMESTAMP));
            recentItem.userId = cursor.getInt(cursor.getColumnIndex(DataHelper.RECENT_USER_ID));
            //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.getDefault());
            //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.RECENT_INSERT_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    recentItem.insertDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    recentItem.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.RECENT_UPDATE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    recentItem.updateDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    recentItem.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.RECENT_DELETE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    recentItem.deleteDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    recentItem.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
        }
        else
            recentItem.id = -1;
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
        dest.writeInt(this.visit);
        dest.writeString(this.visitTimestamp);
        dest.writeInt(this.userId);
        dest.writeLong(insertDate != null ? insertDate.getTime() : -1);
        dest.writeLong(updateDate != null ? updateDate.getTime() : -1);
        dest.writeLong(deleteDate != null ? deleteDate.getTime() : -1);
    }
}
