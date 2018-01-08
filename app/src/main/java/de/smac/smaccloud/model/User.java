package de.smac.smaccloud.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;

/**
 * POJO(Plain Old Java Object) class for User
 */
public class User implements Parcelable
{

    public static final Creator<User> CREATOR = new Creator<User>()
    {
        public User createFromParcel(Parcel source)
        {
            return new User(source);
        }

        public User[] newArray(int size)
        {
            return new User[size];
        }
    };
    public int id;
    public int roleId;
    public int type;
    public String email;
    public String name;
    public String designation;
    public String address;
    public String contact;
    public Date insertDate;
    public Date updateDate;
    public Date deleteDate;
    public int syncType;
    public int isSynced;

    public User()
    {
    }

    protected User(Parcel in)
    {
        this.id = in.readInt();
        this.roleId = in.readInt();
        this.type = in.readInt();
        this.email = in.readString();
        this.name = in.readString();
        this.designation = in.readString();
        this.address = in.readString();
        this.contact = in.readString();
        long tmpInsertDate = in.readLong();
        this.insertDate = tmpInsertDate == -1 ? null : new Date(tmpInsertDate);
        long tmpUpdateDate = in.readLong();
        this.updateDate = tmpUpdateDate == -1 ? null : new Date(tmpUpdateDate);
        long tmpDeleteDate = in.readLong();
        this.deleteDate = tmpDeleteDate == -1 ? null : new Date(tmpDeleteDate);
        this.syncType = in.readInt();
        this.isSynced = in.readInt();
    }

    public static void parseFromJson(JSONObject userJson, User user) throws JSONException, ParseException
    {
        user.id = userJson.optInt("Id");
        user.roleId = userJson.optInt("RoleId");
        user.type = userJson.optInt("Type");
        user.email = userJson.optString("Email");
        user.name = userJson.optString("Name");
        user.designation = userJson.optString("Designation");
        user.address = userJson.optString("Address");
        user.contact = userJson.optString("Contact");
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
       // dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String tempDate = userJson.optString("InsertDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                user.insertDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                user.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = userJson.optString("UpdateDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                user.updateDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                user.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = userJson.optString("DeleteDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                user.deleteDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                user.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        user.syncType = userJson.optInt("SyncType");
    }

    public static void parseFromCursor(Cursor cursor, User user) throws ParseException
    {
        if (cursor != null)
        {
            user.id = cursor.getInt(cursor.getColumnIndex(DataHelper.USER_ID));
            user.roleId = cursor.getInt(cursor.getColumnIndex(DataHelper.USER_ROLE_ID));
            user.type = cursor.getInt(cursor.getColumnIndex(DataHelper.USER_TYPE));
            user.email = cursor.getString(cursor.getColumnIndex(DataHelper.USER_EMAIL));
            user.name = cursor.getString(cursor.getColumnIndex(DataHelper.USER_NAME));
            user.designation = cursor.getString(cursor.getColumnIndex(DataHelper.USER_DESIGNATION));
            user.address = cursor.getString(cursor.getColumnIndex(DataHelper.USER_ADDRESS));
            user.contact = cursor.getString(cursor.getColumnIndex(DataHelper.USER_CONTACT));
            user.isSynced = cursor.getInt(cursor.getColumnIndex(DataHelper.USER_IS_SYNCED));
            //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a",Locale.US);
            //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.USER_INSERT_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    user.insertDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    user.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.USER_UPDATE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    user.updateDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    user.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.USER_DELETE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    user.deleteDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    user.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }

        }
        else
            user.id = -1;
    }

    public void populateUsingId(Context context) throws ParseException
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        DataHelper.getUser(context, this);
    }

    public boolean add(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.addUser(context, this);
    }

    public boolean saveChanges(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.updateUser(context, this);
    }

    public boolean remove(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.removeUser(context, this);
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
        dest.writeInt(this.roleId);
        dest.writeInt(this.type);
        dest.writeString(this.email);
        dest.writeString(this.name);
        dest.writeString(this.designation);
        dest.writeString(this.address);
        dest.writeString(this.contact);
        dest.writeLong(insertDate != null ? insertDate.getTime() : -1);
        dest.writeLong(updateDate != null ? updateDate.getTime() : -1);
        dest.writeLong(deleteDate != null ? deleteDate.getTime() : -1);
        dest.writeInt(this.syncType);
        dest.writeInt(this.isSynced);
    }
}