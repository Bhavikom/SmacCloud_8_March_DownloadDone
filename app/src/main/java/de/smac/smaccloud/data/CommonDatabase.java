package de.smac.smaccloud.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class is used to create common database in SQLite
 */
public class CommonDatabase extends SQLiteOpenHelper
{
    private static String DATABASE_NAME = "Common.sqlite";
    private static int DATABASE_VERSION = 1;
    private static CommonDatabase _database;

    public CommonDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DataHelper.getInsertUserPreferencesSQL());
        db.execSQL(DataHelper.getInsertLocalizationErrorCode());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    public static SQLiteDatabase getWritable(Context context)
    {
        if (_database == null)
            _database = new CommonDatabase(context);
        return _database.getWritableDatabase();
    }

    public static SQLiteDatabase getReadable(Context context)
    {
        if (_database == null)
            _database = new CommonDatabase(context);
        return _database.getReadableDatabase();
    }

}