package de.smac.smaccloud.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import de.smac.smaccloud.data.DataHelper;

/**
 * POJO(Plain Old Java Object) class for User Preference
 */
public class UserPreference implements Parcelable
{
    public static final Parcelable.Creator<UserPreference> CREATOR = new Parcelable.Creator<UserPreference>()
    {
        public UserPreference createFromParcel(Parcel source)
        {
            return new UserPreference(source);
        }

        public UserPreference[] newArray(int size)
        {
            return new UserPreference[size];
        }
    };
    public String databaseName;
    public String lastSyncDate;
    public int userId;

    public UserPreference()
    {
    }

    protected UserPreference(Parcel in)
    {
        this.databaseName = in.readString();
        this.lastSyncDate = in.readString();
        this.userId = in.readInt();
    }

    public static void parseFromCursor(Cursor cursor, UserPreference preference)
    {
        if (cursor.getCount() > 0 && cursor.getCount() % DataHelper.USER_PREFERENCE_COUNT == 0)
        {
            preference.userId = cursor.getInt(cursor.getColumnIndex(DataHelper.USER_PREFERENCE_USER_ID));
            for (int i = 0; i < DataHelper.USER_PREFERENCE_COUNT; i++)
            {
                String preferenceName = cursor.getString(cursor.getColumnIndex(DataHelper.USER_PREFERENCE_NAME));
                switch (preferenceName)
                {
                    case DataHelper.USER_PREFERENCE_NAME_DATABASE_NAME:
                        preference.databaseName = cursor.getString(cursor.getColumnIndex(DataHelper.USER_PREFERENCE_VALUE));
                        break;

                    case DataHelper.USER_PREFERENCE_NAME_LAST_SYNC_DATE:
                        preference.lastSyncDate = cursor.getString(cursor.getColumnIndex(DataHelper.USER_PREFERENCE_VALUE));
                        break;
                }
                cursor.moveToNext();
            }
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.databaseName);
        dest.writeString(this.lastSyncDate);
        dest.writeInt(this.userId);
    }

    public void populateUsingUserId(Context context)
    {
        if (userId <= 0)
            throw new IllegalArgumentException("User Id cannot be zero or negative");
        DataHelper.getUserPreference(context, this);
    }

    public boolean add(Context context)
    {
        if (TextUtils.isEmpty(databaseName))
            throw new IllegalArgumentException("Database Name cannot be null or empty");
        if (TextUtils.isEmpty(lastSyncDate))
            throw new IllegalArgumentException("Last Sync Date cannot be null or empty");
        if (userId <= 0)
            throw new IllegalArgumentException("User Id cannot be zero or negative");
        return DataHelper.addUserPreference(context, this);
    }

    public boolean saveChanges(Context context)
    {
        if (TextUtils.isEmpty(databaseName))
            throw new IllegalArgumentException("Database Name cannot be null or empty");
        if (TextUtils.isEmpty(lastSyncDate))
            throw new IllegalArgumentException("Last Sync Date cannot be null or empty");
        if (userId <= 0)
            throw new IllegalArgumentException("User Id cannot be zero or negative");
        return DataHelper.updateUserPreference(context, this);
    }

    public boolean remove(Context context)
    {
        if (userId <= 0)
            throw new IllegalArgumentException("User Id cannot be zero or negative");
        return DataHelper.removeUserPreference(context, this);
    }
}