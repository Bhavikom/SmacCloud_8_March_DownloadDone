package de.smac.smaccloud.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.UserPreference;

/**
 * This class is used to create local database in SQLite
 */
public class LocalDatabase extends SQLiteOpenHelper
{
    private static int forUserId;
    private static int DATABASE_VERSION = 1;
    private static LocalDatabase _database;

    private LocalDatabase(Context context, UserPreference preference)
    {
        super(context, preference.databaseName, null, DATABASE_VERSION);
    }

    public static LocalDatabase newInstance(Context context)
    {
        if (!PreferenceHelper.hasUserContext(context))
            throw new IllegalStateException("Cannot access system without logging in");
        UserPreference preference = new UserPreference();
        preference.userId = PreferenceHelper.getUserContext(context);
        preference.populateUsingUserId(context);
        if (TextUtils.isEmpty(preference.databaseName))
            throw new IllegalStateException("Database has not been created for user");
        forUserId = preference.userId;
        return new LocalDatabase(context, preference);
    }

    public static SQLiteDatabase getWritable(Context context) {
        UserPreference userPreference = new UserPreference();
        userPreference.userId = PreferenceHelper.getUserContext(context);
        DataHelper.getUserPreference(context, userPreference);

        if (_database == null) {
            _database = LocalDatabase.newInstance(context);
        } else if (!userPreference.databaseName.equalsIgnoreCase(_database.getDatabaseName())) {
            _database = LocalDatabase.newInstance(context);
        }

        /*if (forUserId != PreferenceHelper.getUserContext(context) || _database == null){

        }*/
        return _database.getWritableDatabase();
    }

    public static SQLiteDatabase getReadable(Context context) {
        UserPreference userPreference = new UserPreference();
        userPreference.userId = PreferenceHelper.getUserContext(context);
        DataHelper.getUserPreference(context, userPreference);

        if (_database == null) {
            _database = LocalDatabase.newInstance(context);
        } else if (!userPreference.databaseName.equalsIgnoreCase(_database.getDatabaseName())) {
            _database = LocalDatabase.newInstance(context);
        }
        /*if (forUserId != PreferenceHelper.getUserContext(context) || _database == null)
            _database = LocalDatabase.newInstance(context);*/
        return _database.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DataHelper.getInsertUserSQL());
        db.execSQL(DataHelper.getInsertMediaSQL());
        db.execSQL(DataHelper.getInsertMediaVersionSQL());
        db.execSQL(DataHelper.getInsertChannelSQL());
        db.execSQL(DataHelper.getInsertChannelUserSQL());
        db.execSQL(DataHelper.getInsertChannelFileSQL());
        db.execSQL(DataHelper.getInsertMessageSQL());
        db.execSQL(DataHelper.getCommentTableQuery());
        db.execSQL(DataHelper.getLikeTableQuery());
        db.execSQL(DataHelper.getInsertMessageFileSQL());
        db.execSQL(DataHelper.getInsertMessageReceiversSQL());
        db.execSQL(DataHelper.getRecentTableQuery());
        db.execSQL(DataHelper.getAnnouncementTableQuery());
      //  DataHelper.fillLocalizationTable(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }



}